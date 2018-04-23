package coinone.message;

import coinone.ErrorCodeException;
import coinone.ErrorCodes;

@SuppressWarnings("serial")
public class DecompressException extends ErrorCodeException {

	public DecompressException(String msg, Throwable cause) {
		super(cause, ErrorCodes.Decompress, msg);
	}
	
}
