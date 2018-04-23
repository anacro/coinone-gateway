package coinone.message;

import coinone.message.packet.Packet;

public interface IPacketMessage {

	Packet parse(BufferInSegment o);

	BufferOutSegment responseSuccess(Protocol protocol, int callId, Object entity);
	
	BufferOutSegment responseFail(Protocol protocol, int callId, int status, String errMsg);

	BufferOutSegment request(Protocol protocol, int callId, Object entity);

	BufferOutSegment subscribe(Protocol pubTicker, Object entity);

}
