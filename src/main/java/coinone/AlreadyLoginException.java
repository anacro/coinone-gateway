/**
 * 
 */
package coinone;

@SuppressWarnings("serial")
public class AlreadyLoginException extends ErrorCodeException {

	/**
	 * @param errorCode
	 */
	public AlreadyLoginException(String errorMsg) {
		super(ErrorCodes.AlreadyLogin, errorMsg);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param errorCode
	 * @param placeHolderValues
	 */
	public AlreadyLoginException(int errorCode, Object... placeHolderValues) {
		super(errorCode, placeHolderValues);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param errorCode
	 * @param errorMsg
	 */
	public AlreadyLoginException(int errorCode, String errorMsg) {
		super(errorCode, errorMsg);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 * @param errorCode
	 */
	public AlreadyLoginException(Throwable cause, int errorCode) {
		super(cause, errorCode);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 * @param errorCode
	 * @param placeHolderValues
	 */
	public AlreadyLoginException(Throwable cause, int errorCode,
			Object... placeHolderValues) {
		super(cause, errorCode, placeHolderValues);
		// TODO Auto-generated constructor stub
	}

}
