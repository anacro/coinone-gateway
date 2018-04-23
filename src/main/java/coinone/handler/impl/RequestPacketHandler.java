package coinone.handler.impl;

import org.springframework.beans.factory.annotation.Autowired;

import coinone.Request;
import coinone.Response;
import coinone.SocketContext;
import coinone.handler.AsyncHandler;
import coinone.message.BufferInSegment;
import coinone.message.IPacketMessage;
import coinone.message.packet.Packet;
import coinone.message.packet.RequestPacket;

public class RequestPacketHandler extends AsyncHandler {
	
	@Autowired IPacketMessage _message;

	@Override
	public void handleRequest(SocketContext context, Object o) throws Exception {
		Packet packet = _message.parse((BufferInSegment) o);
		
		if(packet.isRequest()) {
			Request request = new Request(context, (RequestPacket) packet);
			o = request;
		}		
		
		super.handleRequest(context, o);
	}

	@Override
	public void handleResponse(Response response) throws Exception {
		Request request = response.getRequest();
		
		if(request.getPacket().isRequest()) {
			if(false == response.isError()) {
				request.getContext().send(						
						_message.responseSuccess(
								request.getProtocol(), 
								request.getCallId(),								 
								response.getBuffer())
						);
			}
		}
		
		super.handleResponse(response);
	}	
	
}
