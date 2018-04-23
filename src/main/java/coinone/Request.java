package coinone;

import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import coinone.message.MethodType;
import coinone.message.Protocol;
import coinone.message.packet.Packet;
import coinone.message.packet.RequestPacket;
import coinone.session.Session;

public class Request {

	private final SocketContext _context;
	private final RequestPacket _packet;
	private final Response _response;
	
	public Request(SocketContext context, RequestPacket p) {
		_context = context;
		_packet = p;
		_response = new Response(this);
	}

	public String getUrl() {
		return _packet.protocol.url;
	}
	
	public MethodType getHttpMethod() {
		return _packet.protocol.method;
	}	

	public Protocol getProtocol() {	
		return _packet.protocol;
	}
	
	public int getCallId() {
		return _packet.callId;
	}
	
	public String getBodyAsString() {
		return _packet.body.asString();
	}

	public Buffer getBodyAsBuffer() {
		return _packet.body.asBuffer();
	}	

	public byte[] getBodyAsBytes() {
		return _packet.body.asBytes();
	}

	public MultiMap getBodyAsMultiMap() {
		return _packet.body.asMultiMap();	
	}

	public Response getResponse() {
		return _response;
	}

	public SocketContext getContext() {
		return _context;
	}

	public Session getSession() {
		return _context.getSession();
	}

	public Packet getPacket() {
		Packet p = _packet;
		
		if(p == null) {
			synchronized(this) {
				p = _packet;
			}
		}
		
		return p;
	}

	
}
