package coinone.client;

import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutor;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coinone.Const;
import coinone.SocketException;
import coinone.SocketException.SocketError;
import coinone.VertxUtils;
import coinone.message.BufferInSegment;
import coinone.message.BufferOutSegment;
import coinone.message.IPacketMessage;
import coinone.message.Protocol;
import coinone.message.packet.Packet;
import coinone.message.packet.ResponsePacket;
import coinone.message.packet.SubscribePacket;

public class Client {
	protected static final Logger LOG = LoggerFactory.getLogger(Client.class);
	
	public static final int CONNECT_TIMEOUT = 4 * 1000;
	private static final int RESPONSE_TIMEOUT = Const.ENV_DEBUG ? 1000 * 60 * 10 : 1000 * 10;	
	
	private final Map<Integer, SingleEmitter<ResponsePacket>> _responseMap = new ConcurrentHashMap<>(); //key:callId
	private final Map<Short, Consumer<SubscribePacket>> _subscribeMap =  new ConcurrentHashMap<>(); //key:protocol
	private final AtomicInteger _transation = new AtomicInteger();
	private final EventExecutor _executor = new DefaultEventExecutorGroup(1).next();	
	private final NetClient _netClient;
	private final AtomicReference<NetSocket> _socketRef = new AtomicReference<>();
	
	private final String _host; 
	private final int _port;
	private final IPacketMessage _packetMessage;
		
	public Client(NetClient netClient, IPacketMessage m, String host, int port) {
		_netClient = netClient;
		_packetMessage = m;
		_host = host;
		_port = port;		
	}	
		
	public void subscribe(Protocol protocol, Consumer<SubscribePacket> handler) {
		_subscribeMap.put(protocol.id, handler);
	}	
	
	public final void colse() {
		_netClient.close();
	}
	
	public final Single<ResponsePacket> call(Protocol protocol, Object entity) {
		if(LOG.isTraceEnabled())
			LOG.trace("call message, protocol:{}, entity:{}", protocol, entity);
		
		return Single.create(s -> {
			Integer callId = null;
			try {
				//패킷생성
				callId = _transation.incrementAndGet();				
				BufferOutSegment m = encode(protocol, callId, entity);
				if(LOG.isTraceEnabled()) LOG.trace("encoded message, protocol:{}, callId:{}, BufferOutSegment:{}", protocol, callId, m);				
								
				_responseMap.put(callId, s);
				
				send0(m);
				
				final Integer ik = callId;
				if(RESPONSE_TIMEOUT > 0) {
					_executor.schedule(() -> { // 제한시간 까지 응답이 오지 않으면 타임아웃 발생 시킴
						SingleEmitter<ResponsePacket> e = _responseMap.remove(ik);
						if(e != null) //응답이 왔으면 이미 삭제됨
							e.onError(new TimeoutException("timeout - " + RESPONSE_TIMEOUT));
					}, RESPONSE_TIMEOUT, TimeUnit.MILLISECONDS);
				}
			} catch(Exception ex) {
				LOG.error("protocol:" + protocol, ex);
				
				if(callId != null)
					_responseMap.remove(callId);
				
				s.onError(ex);
			}			
		});		
	}
	
	public final void send(Protocol protocol, byte[] body) {
		if(LOG.isTraceEnabled())
			LOG.trace("send message, protocol:{}, array:{}", protocol, body);		
		
		_executor.submit(() -> {
			try {
				//패킷생성				
				BufferOutSegment m = encode(protocol, NumberUtils.INTEGER_ZERO, body);
				if(LOG.isTraceEnabled())
					LOG.trace("encoded message, protocol:{}, BufferOutSegment:{}", protocol, m);				
								
				send0(m);
			} catch(Exception ex) {
				LOG.error("protocol:" + protocol, ex);
			}
		});
	}

	private void send0(BufferOutSegment message) {						
		getSocket().write(Buffer.buffer(message.getByteBuf()));
		if(LOG.isTraceEnabled()) LOG.trace("send - " + message);
	}

	private NetSocket getSocket() {
		NetSocket s = _socketRef.get();
		if(s == null)
			s = connect();
		return s;
	}

	private NetSocket connect() {
		CountDownLatch waiter = new CountDownLatch(1);
		
		_netClient.connect(_port, _host, e -> {
			if(false == e.succeeded()) {
				disposeSocket();
				waiter.countDown();
				LOG.error("fail connect socket, " + toString(), e.cause());
				return;
			}
			
			try {
				NetSocket socket = e.result();
				LOG.info("success connect socket - {}, {}", socket.writeHandlerID(), _host + ":" + _port);
				
				socket.closeHandler(avoid -> {
					disposeSocket();
					LOG.info("client socket onClose - " + toString());
				});
				
				socket.exceptionHandler(ex -> {						
					disposeSocket();
					if(SocketException.isIgnorableSocketError(ex)) {
						LOG.info("client socket onError : " + ex.getMessage() + ", " + toString());							
					} else {
						LOG.error("client socket onError : " + toString(), ex);
					}					
				});
				
				socket.handler(VertxUtils.createRecordParser(socket.writeHandlerID(), this::onMessage));
				_socketRef.set(socket);
			} catch(Exception ex) {
				disposeSocket();
				LOG.error("success connect socket, but fail connect handling" + toString(), ex);				
			} finally {
				waiter.countDown();
			}
		});
		
		NetSocket s = null;
		try {			
			waiter.await(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
			s = _socketRef.get();
			if(s == null)
				throw new SocketException(SocketError.ConnectionRefused, _host + ":" + _port);			
		} catch (InterruptedException e) {
			s = _socketRef.get();
			if(s == null)
				throw new SocketException(SocketError.TimedOut, _host + ":" + _port);
		}		
		return s;
	}
	
	private void disposeSocket() {
		_socketRef.set(null);
	}
	
	private void onMessage(Buffer buffer) {		
		try {
			BufferInSegment m = new BufferInSegment(buffer);
			Packet packet = _packetMessage.parse(m);			
			if(LOG.isTraceEnabled())
				LOG.trace("protocol - {}, {}", packet, m);
			
			switch(packet.type) {
				case SUBSCRIBE :
					onMessageSubscribe((SubscribePacket) packet);					
					break;
					
				case RESPONSE :
					onMessageResponse((ResponsePacket) packet);					
					break;
					
				default :
					LOG.warn("not supported packet type - " + packet);
			}			
		} catch(Exception ex) {
			LOG.error("fail onMessage", ex);
		}
	}

	private void onMessageResponse(ResponsePacket packet) {		
		SingleEmitter<ResponsePacket> e = _responseMap.remove(packet.callId);
		if(e != null) { // Promise is null => 타임아웃 처리 되었음
			int status = packet.status;
			if(status == 0) {
				e.onSuccess(packet);
			} else {						
				e.onError(new ResponseErrorException(status, packet.body.asString()));
			}
		}
	} 

	private void onMessageSubscribe(SubscribePacket packet) {
		Consumer<SubscribePacket> notifier = _subscribeMap.get(packet.protocol.id);
		if(notifier != null) {
			notifier.accept(packet);
		} else {
			LOG.warn("not register subscribe Handler, protocol:" + packet.protocol.id);
		}
	}
	
	private BufferOutSegment encode(Protocol protocol, Integer callId, Object entity) {
		return _packetMessage.request(protocol, callId, entity);
	}
	
	@Override
	public String toString() {		
		return super.toString() + "[" + _host + ":" + _port + "]";		
	}
	
}