package coinone.client;

import coinone.ErrorCodeException;

@SuppressWarnings("serial")
public class ResponseErrorException extends ErrorCodeException {

	public ResponseErrorException(int errorCode, String message) {
		super(errorCode, message);
	}
		
}
