package coinone.handler.impl;

import io.vertx.core.buffer.Buffer;

import org.springframework.beans.factory.annotation.Autowired;

import coinone.ApiInvoker;
import coinone.Request;
import coinone.Response;
import coinone.SocketContext;
import coinone.handler.AsyncHandler;



public class ApiInvokerHandler extends AsyncHandler {
	
	@Autowired ApiInvoker _invoker;	
	
	@Override
	public void handleRequest(SocketContext context, Object o) throws Exception {
		Request req = (Request) o;		
		
		try {
			_invoker.invoke(req).subscribe(
					buffer -> maybeRunContext(context, () -> success0(req, buffer)),
					ex -> maybeRunContext(context, () -> error0(req, ex))			
			);
		} catch(Throwable ex) {
			maybeRunContext(context, () -> error0(req, ex));
		}
	}

	private void success0(Request req, Buffer buffer) throws Exception {
		Response res = req.getResponse();
		res.setBuffer(buffer);
		handleResponse(res);
	}
	
	private void error0(Request req, Throwable ex) throws Exception {
		req.getResponse().addException(ex);
		handleResponse(req.getResponse());
	}
	
}
