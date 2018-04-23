package coinone.message.packet;

import lombok.ToString;
import coinone.message.Entity;
import coinone.message.PacketType;
import coinone.message.Protocol;

@ToString(doNotUseGetters=true)
public class Packet {
	
	public static final int RESPONSE_SUCCESS = 0;
	
	public final PacketType type;
	public final Protocol protocol;
	public final Entity body;
	
	public Packet(PacketType type, Protocol protocol, Entity body) {
		this.type = type;
		this.protocol = protocol;
		this.body = body;
	}

	public boolean isRequest() {
		return this.type == PacketType.REQUEST;
	}	
	
	

}
