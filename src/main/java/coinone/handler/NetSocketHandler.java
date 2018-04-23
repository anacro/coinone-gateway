package coinone.handler;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coinone.SocketContext;
import coinone.SocketException;
import coinone.SocketHandlerManager;
import coinone.VertxUtils;
import coinone.message.BufferInSegment;
import coinone.session.Session;

public class NetSocketHandler {

	protected static final Logger LOG = LoggerFactory.getLogger(NetSocketHandler.class);
	
	protected final SocketContext _socketContext;
		
	protected final SocketHandlerManager _manager;
		
	protected final AsyncHandlerPipeline _handlerPipeline;
	
	public NetSocketHandler(SocketContext context, SocketHandlerManager manager, AsyncHandlerPipeline pipeline) {		
		_manager = manager;
		_handlerPipeline = pipeline;
		_socketContext = context;
		registerHandlers(_socketContext);
	}
	
	public final String getId() {
		return _socketContext.getSocketId();
	}
	
	@SuppressWarnings("unchecked")
	public final <T extends SocketHandlerManager> T getManager() {
		return (T) _manager;
	}
	
	public final String getName() {
		return getClass().getSimpleName() + "_" + _socketContext.getSocketId();
	}

	public final SocketContext getContext() {
		return _socketContext;
	}


	protected Handler<Void> getCloseHandler() {
		return aVoid -> fireOnClose();
	}

	protected Handler<Throwable> getExceptionHandler() {
		return throwable -> fireOnError(throwable);
	}	
	
	protected final void fireOnError(Throwable throwable) {
		try {
			onError(throwable);
		} catch(Throwable t) {
			LOG.error("fail onError", t);
		}		
	}	
	
	public final void fireOnOpen() {
		onOpen();
	}

	public final void fireOnClose() {
		//1. 소켓컨텍스트제거 - 종료 플래그 on			
		try {
			if(!_socketContext.dispose())
				return; //이미 종료 되었음
		} catch(Throwable t) {
			LOG.warn("fail dispose transport", t);
		}		
		
		//2. 소켓 핸들러 onClose 이벤트 호출
		try {
			onClose();
		} catch(Throwable t) {
			LOG.error("fail onClose", t);
		}
					
		//3. 세션 종료 이벤트 호출
		try {
			getSession().onClose();
		} catch(Throwable t) {
			//TODO 에러 핸들링
			LOG.error("fail onClose Event of Session", t);				
		}
		
		//4. 소켓 핸들러 제거
		try {
			_manager.recycle(this);
		} catch(Throwable t) {
			LOG.error("fail recycle WebSocketHandler", t);
		}
	}	
	
	private Session getSession() {
		return _socketContext.getSession();
	}
	
	protected void registerHandlers(final SocketContext context) {
		NetSocket socket = context.getSocket();		
		
		socket.exceptionHandler(getExceptionHandler());		
		socket.closeHandler(getCloseHandler());
		registerReceiveHandler(socket);
	}
	
	protected void registerReceiveHandler(final NetSocket socket) {		
		socket.handler(
				VertxUtils.createRecordParser(socket.writeHandlerID(), this::fireOnMessage)
				);
	}

	private final void fireOnMessage(Buffer buffer) {
		onMessage(new BufferInSegment(buffer));
	}
		
	public void onOpen() {
		if(LOG.isDebugEnabled())
			LOG.debug("client:[{}, {}]", _socketContext.remoteAddress(), _socketContext.getSocketId());
	}
	
	public void onClose() {
		if(LOG.isDebugEnabled())
			LOG.debug("client:[{}, {}]", _socketContext.remoteAddress(), _socketContext.getSocketId());
	}
	
	public void onError(Throwable ex) {
		if(SocketException.isIgnorableSocketError(ex))
			LOG.debug("client:[{}, {}], {}", _socketContext.remoteAddress(), _socketContext.getSocketId(), ex.getMessage());				
		else
			LOG.error(MessageFormat.format("client:[{0}, {1}]", _socketContext.remoteAddress(), _socketContext.getSocketId()), ex);			
	}
	
	public void onMessage(BufferInSegment message) {
		if(LOG.isTraceEnabled())
			LOG.trace("client[{}, {}], {}", _socketContext.remoteAddress(), _socketContext.getSocketId(), message);
		
		_handlerPipeline.process(_socketContext, message);	
	}
	
}