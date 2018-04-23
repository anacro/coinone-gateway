package coinone.component;

import static coinone.ApiHelper.isSuccess;
import io.reactivex.Single;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import coinone.ApiInvoker;
import coinone.AppConfiguration;
import coinone.Request;
import coinone.SocketContext;
import coinone.SocketHandlerManager;
import coinone.deamon.IBlockDispatcher;
import coinone.handler.NetSocketHandler;
import coinone.message.BufferOutSegment;
import coinone.message.IPacketMessage;
import coinone.message.Protocols;
import coinone.message.packet.MultiMapPacket;

@Component
public class TickerPublisher implements IBlockDispatcher {
	
	@Autowired ApiInvoker _invoker;
	
	@Autowired SocketHandlerManager _manager;
	
	@Autowired AppConfiguration _config;
	
	@Autowired IPacketMessage _message;
	
	Single<Buffer> _rxRequest;
	
	Buffer _buffer;	
	
	@PostConstruct
	public void init() {
/*		List<String> all = Arrays.asList("btc", "bch", "eth", "etc");
		List<Single<Buffer>> list = new ArrayList<>(all.size());
	
		for(String curency : all) {
			list.add(packet(curency));	
		}	
		
		_rxRequest = Single.zip(list, args -> {
			JsonArray a = new JsonArray();
			JsonObject r = new JsonObject().put("r", a);
			
			for(Object o : args) {
				JsonObject j = ((Buffer) o).toJsonObject();
				if(isSuccess(j)) {
					a.add(j);
				}
			}
			return r.toBuffer();
		});		*/
		
		_rxRequest = packet("all");				
//				.map(b -> {
//					JsonObject j = b.toJsonObject();
//					if(! isSuccess(j)) {
//						throw new RuntimeException("result => " + j);
//					}
//					return b;
//				});
	}
	
	private Single<Buffer> packet(String currency) {
		MultiMapPacket p = new MultiMapPacket(Protocols.PUB_TICKER, MultiMap.caseInsensitiveMultiMap().add("currency", currency));		
		Request req = new Request(SocketContext.dummy(), p);
		return _invoker.invoke(req);		
	}

	@Override
	public void dispatch() throws InterruptedException {		
		Buffer b = _rxRequest.blockingGet();
		JsonObject j = b.toJsonObject();
		if(! isSuccess(j)) {
			_config.errorLogger().error("fail get ticker - " + j);
			return;
		}
		
		if(_buffer == null || b.equals(_buffer) == false) {		
			_buffer = b;

			BufferOutSegment m = forSubscribe(b);
			for(NetSocketHandler h : _manager.all()) {
				h.getContext().send(m);
			}			
		}
		
//		_rxRequest.subscribe(
//				buffer -> {
//					for(NetSocketHandler h : _manager.all()) {
//						h.getContext().send(forSubscribe(buffer));
//					}
//				},
//				ex -> {
//					_config.errorLogger().error("fail ticker - " + Protocols.PUB_TICKER, ex);
//				}
//			);		
	}

	private BufferOutSegment forSubscribe(Buffer b) {		
		return _message.subscribe(Protocols.PUB_TICKER, b);
	}		


}
