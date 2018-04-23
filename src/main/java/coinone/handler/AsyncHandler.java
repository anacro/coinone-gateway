package coinone.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import coinone.Action;
import coinone.AppConfiguration;
import coinone.Request;
import coinone.Response;
import coinone.SocketContext;

public abstract class AsyncHandler {
	
	protected static final Logger LOG = LoggerFactory.getLogger(AsyncHandler.class);
	
	protected AsyncHandler _next; // next 요청
	protected AsyncHandler _prev; // next 응답
	
	@Autowired protected AppConfiguration _cofig;	
	
	public void setRequestHandler(AsyncHandler next) {
		_next = next; 
	}
	
	public void setResponseHandler(AsyncHandler prev) {
		_prev = prev; 
	}	
	
	protected void fireHandleRequest(SocketContext context, Object o) {
		try {
			handleRequest(context, o);			
		} catch(Exception ex) {
			if(o instanceof Request) {				
				Request request = (Request) o;
				handleExceptionAndFireHandleResponse(request.getResponse(), ex);
		
			} else {
				if(ex instanceof RuntimeException)
					throw (RuntimeException) ex;
				else
					throw new RuntimeException(ex);
			}
		}
	}	

	private void handleExceptionAndFireHandleResponse(Response response, Exception ex) {
		response.addException(ex);
		if(_prev != null)
			_prev.fireHandleResponse(response);
	}	

	protected void fireHandleResponse(Response response) {
		try {
			if(ignoreError(response))
				_prev.fireHandleResponse(response);					
			else
				handleResponse(response);			
		} catch(Exception ex) {
			handleExceptionAndFireHandleResponse(response, ex);			
		}
	}

	// false => 에러가 있어도 handleResponse 실행
	protected boolean ignoreError(Response response) {
		return response.isError();
	}

	protected void handleRequest(SocketContext context, Object o) throws Exception {
		if(_next == null)
			throw new RuntimeException("not register next handler");
		
		_next.fireHandleRequest(context, o);
	}
	
	protected void handleResponse(Response response) throws Exception {
		if(_prev == null)
			throw new RuntimeException("current handler is first");
		
		_prev.fireHandleResponse(response);
	}
	
	protected final void maybeRunContext(SocketContext context, Action action) {
		if(context.isOnContextThread())
			try {
				action.apply();
			} catch(Throwable ex) {
				_cofig.errorLogger().error(context.toString(), ex);
			}
		else
			runContext(context, action);
	}

	protected final void runContext(SocketContext context, Action action) {
		context.runOnContext(() -> {
			try {
				action.apply();
			} catch(Throwable ex) {
				_cofig.errorLogger().error(context.toString(), ex);
			}
		});
	}	
	
}
