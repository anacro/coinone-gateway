package coinone.message.packet;

import coinone.message.Entity;
import coinone.message.PacketType;
import coinone.message.Protocol;


public class RequestPacket extends Packet {
	
	public final int callId;
	
	public RequestPacket(Protocol protocol, Entity body, int callId) {
		super(PacketType.REQUEST, protocol, body);
		this.callId = callId;
	}	
	
	protected RequestPacket(PacketType type, Protocol protocol, Entity body, int callId) {
		super(type, protocol, body);
		this.callId = callId;
	}

}
