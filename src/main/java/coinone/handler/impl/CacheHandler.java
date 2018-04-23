package coinone.handler.impl;

import static coinone.ApiHelper.isSuccess;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import coinone.Request;
import coinone.Response;
import coinone.SocketContext;
import coinone.handler.AsyncHandler;
import coinone.message.Protocol;
import coinone.message.Protocols;
import coinone.session.CacheNotExistException;


public class CacheHandler extends AsyncHandler {
			
	@Override
	protected void handleRequest(SocketContext context, Object o) throws Exception {
		Request req = (Request) o;
		Protocol p = req.getProtocol();
		
		if(! isCachable(p.id)) {
			super.handleRequest(context, o);
			return;
		}
		
		req.getSession().getCache(p.id).subscribe(
			buffer -> {
				maybeRunContext(context, () -> {					
					Response res = req.getResponse();
					res.setBuffer(buffer);
					super.handleResponse(res);											
				});
			},
			ex -> {
				if(false == ex instanceof CacheNotExistException)
					_cofig.errorLogger().error("fail getCache", ex);
				
				maybeRunContext(context, () -> super.handleRequest(context, o));
			}	
		);
	}
	
	@Override
	protected void handleResponse(Response response) throws Exception {
		Request req = response.getRequest();
		Protocol p = req.getProtocol();
				
		if(isCachable(p.id)) {
			Buffer b = response.getBuffer();			
			JsonObject j = b.toJsonObject();			
			if(isSuccess(j))
				req.getSession()
				.putCache(p.id, b, p.ttl)
				.subscribe(
					o -> {},
					ex -> _cofig.errorLogger().error("fail put chache - " + j, ex)
				);
		}	
		
		super.handleResponse(response);
	}	

	private boolean isCachable(short id) {
		return id ==  Protocols.ACC_BALANCE_ID
				|| id == Protocols.ACC_DAILY_BALANCE_ID;
	}

	
}
