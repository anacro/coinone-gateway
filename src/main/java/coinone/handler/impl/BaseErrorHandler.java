package coinone.handler.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import coinone.AppConfiguration;
import coinone.ErrorCodeException;
import coinone.ErrorCodes;
import coinone.Request;
import coinone.Response;
import coinone.handler.AsyncHandler;
import coinone.handler.IErrorHandler;
import coinone.message.BufferOutSegment;
import coinone.message.IPacketMessage;

public class BaseErrorHandler extends AsyncHandler implements IErrorHandler {
	
	@Autowired IPacketMessage _message;
	
	@Autowired AppConfiguration _config;
	
	@Override
	protected boolean ignoreError(Response response) {
		return response.isError() == false;
	}
		
	@Override
	protected void handleResponse(Response response) throws Exception {				
		List<Throwable> errors = response.getErrors();
		
		Throwable rootCause = errors.get(0);
		_config.errorLogger().error(response.toString(), rootCause);
		
		mapException(rootCause, response);		
		
		for(int i = 1, len = errors.size(); i < len; i++) {
			Throwable cause = errors.get(i);
			_config.errorLogger().error("cause" + i, cause);
		}		
		
		super.handleResponse(response);		
	}
		
	protected void mapException(Throwable rootCause, Response response) {		
		if(response.getRequest().getPacket().isRequest())
				sendErrorResponse(response, rootCause);
	}

	protected void sendErrorResponse(Response response, Throwable cause) {
		try {
			Request req = response.getRequest();
			
			int errorCode = cause instanceof ErrorCodeException ? ((ErrorCodeException) cause).getErrorCode() : ErrorCodes.UNHANDLE;			
			BufferOutSegment message = _message.responseFail(
					req.getProtocol(), 
					req.getCallId(), 
					errorCode,
					StringUtils.abbreviate(cause.getMessage(), 100));
						
			req.getContext().send(message);
			
		} catch(Exception ex) {
			_config.errorLogger().error(ex.getMessage(), ex);
		}
	}

}
