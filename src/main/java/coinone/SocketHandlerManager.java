package coinone;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import coinone.handler.AsyncHandlerPipeline;
import coinone.handler.NetSocketHandler;

@Component
public class SocketHandlerManager {
	
	private final Map<String, NetSocketHandler> _handlerMap = new ConcurrentHashMap<>();
	
	@Autowired private AsyncHandlerPipeline _pipeline;
	
	public void createHandler(SocketContext context) {
		NetSocketHandler handler = new NetSocketHandler(context, this, _pipeline);
		_handlerMap.put(handler.getId(), handler);
		
		handler.fireOnOpen();
	}	

	public void recycle(NetSocketHandler handler) {
		_handlerMap.remove(handler.getContext().getSocketId());
	}

	public Collection<NetSocketHandler> all() {
		return Collections.unmodifiableCollection(_handlerMap.values());		
	}

}
