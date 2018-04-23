package coinone.message;

import coinone.ErrorCodeException;
import coinone.ErrorCodes;

@SuppressWarnings("serial")
public class CompressException extends ErrorCodeException {

	public CompressException(String message, Throwable cause) {
		super(cause, ErrorCodes.Compress, message);
	}

}
