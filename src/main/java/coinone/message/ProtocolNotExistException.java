/**
 * 
 */
package coinone.message;

import coinone.ErrorCodeException;
import coinone.ErrorCodes;

@SuppressWarnings("serial")
public class ProtocolNotExistException extends ErrorCodeException {

	/**
	 * @param errorCode
	 */
	public ProtocolNotExistException(String errorMsg) {
		super(ErrorCodes.AlreadyLogin, errorMsg);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param errorCode
	 * @param placeHolderValues
	 */
	public ProtocolNotExistException(int errorCode, Object... placeHolderValues) {
		super(errorCode, placeHolderValues);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param errorCode
	 * @param errorMsg
	 */
	public ProtocolNotExistException(int errorCode, String errorMsg) {
		super(errorCode, errorMsg);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 * @param errorCode
	 */
	public ProtocolNotExistException(Throwable cause, int errorCode) {
		super(cause, errorCode);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 * @param errorCode
	 * @param placeHolderValues
	 */
	public ProtocolNotExistException(Throwable cause, int errorCode,
			Object... placeHolderValues) {
		super(cause, errorCode, placeHolderValues);
		// TODO Auto-generated constructor stub
	}

}
