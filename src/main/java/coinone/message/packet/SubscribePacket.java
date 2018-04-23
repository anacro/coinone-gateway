package coinone.message.packet;

import lombok.ToString;
import coinone.message.Entity;
import coinone.message.PacketType;
import coinone.message.Protocol;


@ToString(doNotUseGetters=true, callSuper=true)
public class SubscribePacket extends Packet {

	public SubscribePacket(Protocol protocol, Entity body) {
		super(PacketType.SUBSCRIBE, protocol, body);
	}

}
