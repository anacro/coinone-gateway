package coinone;

import java.text.MessageFormat;
import java.util.Arrays;

@SuppressWarnings("serial")
public class ErrorCodeException extends RuntimeException {
	
	private final int _errorCode;
	
	private final String _errorMessage;	

	public ErrorCodeException(final int errorCode) {
		super();
		
		_errorCode = errorCode;
    	_errorMessage = getErrorMessage(errorCode);
	}
	
	public ErrorCodeException(final int errorCode, final Object ... placeHolderValues) {
		super();
		
		_errorCode = errorCode;
    	_errorMessage = getErrorMessage(errorCode, placeHolderValues);
	}
	
	public ErrorCodeException(final int errorCode, String errorMsg) {
		super();
		_errorCode = errorCode;
    	_errorMessage = "[" + errorCode + "]" + errorMsg;
	}

	public ErrorCodeException(final Throwable cause, final int errorCode) {
		super(cause);
		
		_errorCode = errorCode;
    	_errorMessage = getErrorMessage(errorCode);		
	}
	
	public ErrorCodeException(final Throwable cause, final int errorCode, final Object ... placeHolderValues) {
		super(cause);
		
		_errorCode = errorCode;
    	_errorMessage = getErrorMessage(errorCode, placeHolderValues);		
	}	
	
	@Override    
    public final String getMessage() {
        return _errorMessage;
    }		

	protected String getErrorMessage(int errorCode) {
		return "error_code:" + String.valueOf(errorCode);
	}	
	
	protected String getErrorMessage(int errorCode, Object ... args) {
		return MessageFormat.format("error_code:{0,number,#}, error_message:{1}", _errorCode, Arrays.toString(args));		
	}

	public int getErrorCode() {
		return _errorCode;
	}

}
