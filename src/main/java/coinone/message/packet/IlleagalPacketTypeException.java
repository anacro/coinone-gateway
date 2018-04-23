package coinone.message.packet;

import coinone.ErrorCodeException;
import coinone.ErrorCodes;

@SuppressWarnings("serial")
public class IlleagalPacketTypeException extends ErrorCodeException {

	public IlleagalPacketTypeException(String message) {
		super(ErrorCodes.IllegalPacketType, message);
	}

}
