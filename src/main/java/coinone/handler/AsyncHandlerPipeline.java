package coinone.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;

import coinone.Response;
import coinone.SocketContext;

public class AsyncHandlerPipeline extends AsyncHandler {
	
	protected final Logger _errorLogger;
	
	private final AsyncHandler _startHandler;	
	
	private AsyncHandlerPipeline(AsyncHandler startHandler, Logger errorLogger) {
		_startHandler = startHandler;
		_errorLogger = errorLogger;
		
		_startHandler.setResponseHandler(this);
	}		

	public void process(SocketContext context, Object o) {		
		try {
			_startHandler.fireHandleRequest(context, o); 
		} catch(Exception ex) {
			getErrorLogger().error(context + ", " + o, ex);
		}
	}
	
	private Logger getErrorLogger() {
		return _errorLogger != null ? _errorLogger : LOG;
	}

	@Override
	protected void fireHandleResponse(Response response) {
		if(LOG.isTraceEnabled()) LOG.trace("finish request pipeline process");
	}

	public static class Builder {
		
		private final List<AsyncHandler> _handlers;
		
		Logger _errorLogger;
		
		public Builder() {
			_handlers = new ArrayList<>();			
		}
		
		public Builder addHandlers(AsyncHandler[] handlers) {
			if(handlers == null || handlers.length == 0)
				throw new IllegalArgumentException("not register handler");
			
			for(AsyncHandler handler : handlers) {
				_handlers.add(handler);	
			}
			
			return this;			
		}
		
		public Builder addHandler(AsyncHandler handler) {
			_handlers.add(handler);
			return this;
		}
		
		public AsyncHandlerPipeline build() {
			if(_handlers.size() == 0)
				throw new RuntimeException("not register handler");
			
			AsyncHandler startHandler = _handlers.get(0);		
			
			boolean registeredErrorHandler = false;
			AsyncHandler prev = null;
			for(AsyncHandler handler : _handlers) {
				if(handler instanceof IErrorHandler) {
					registeredErrorHandler = true;
				}
				
				handler.setResponseHandler(prev);
				prev = handler;
			}
			
			if(! registeredErrorHandler)
				throw new IllegalStateException("not register ErrorHandler");
			
			AsyncHandler next = null;						
			Collections.reverse(_handlers);
			for(AsyncHandler handler : _handlers) {
				handler.setRequestHandler(next);
				next = handler;
			}
			
			return new AsyncHandlerPipeline(startHandler, _errorLogger);
		}

		public Builder setErrorLogger(Logger errorLogger) {
			_errorLogger = errorLogger;
			return this;
		}		
	}

}
