package coinone.message.packet;

import coinone.message.Entity;
import coinone.message.PacketType;
import coinone.message.Protocol;


public class ResponsePacket extends RequestPacket {

	public final int status;
	
	public ResponsePacket(Protocol protocol, Entity body, int callId, int status) {
		super(PacketType.RESPONSE, protocol, body, callId);
		this.status = status;
	}

}
