package coinone.component;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import coinone.AppConfiguration;
import coinone.SocketContext;
import coinone.SocketHandlerManager;
import coinone.session.Session;

@Component
public class ServerVerticle extends AbstractVerticle {
	
	protected static final Logger LOG = LoggerFactory.getLogger(ServerVerticle.class);
	
	private final AtomicBoolean _isClosed = new AtomicBoolean();
	
	@Autowired private AppConfiguration _configuration;
	@Autowired private SocketHandlerManager _manager;
	
	private NetServer _server;
		
	@Override
	public void start(final Future<Void> startFuture) throws Exception {		
		NetServerOptions options = new NetServerOptions();
							
		vertx.createNetServer(options)			
		.connectHandler(this::createSocketHandler)
		.listen(_configuration.port(), ar -> {
			if(ar.succeeded()) {
				_server = ar.result();
			} else {
				_configuration.errorLogger().error("fail listen - port:" + _configuration.port(), ar.cause());
				
				startFuture.fail(ar.cause());
			}		
		});		
	}
	
	@Override
	public void stop() {
		if(_server != null) {
			 if(_isClosed.compareAndSet(false, true)) {
				 _server.close();
			 }
		}
	}	

	private void createSocketHandler(NetSocket socket) {
		SocketContext c = new SocketContext(vertx.getOrCreateContext(), socket, new Session(socket.writeHandlerID()));
		_manager.createHandler(c);
	}
	
}
