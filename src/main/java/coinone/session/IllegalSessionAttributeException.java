package coinone.session;

import coinone.ErrorCodeException;
import coinone.ErrorCodes;

@SuppressWarnings("serial")
public class IllegalSessionAttributeException extends ErrorCodeException {

	public IllegalSessionAttributeException(String errorMsg) {
		super(ErrorCodes.IllegalSessionAttribute, errorMsg);
	}	

}
