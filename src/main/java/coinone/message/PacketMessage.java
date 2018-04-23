package coinone.message;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import coinone.message.packet.IlleagalPacketTypeException;
import coinone.message.packet.Packet;
import coinone.message.packet.RequestPacket;
import coinone.message.packet.ResponsePacket;
import coinone.message.packet.SubscribePacket;



public class PacketMessage implements IPacketMessage {
		
	//메터데이터
	public static final int FIELD_SIZE_OF_MSG = 4; //메시지의 길이를 담는 필드 사이즈
	public static final int FIELD_SIZE_OF_MSG_AND_META = 5; // FIELD_SIZE_OF_MESSAGE + PacketType + PacketOptions
	
	//헤더
	public static final int SUBSCRIBE_HEADER_SIZE = 2; // 프로토콜(2)	
	public static final int REQUEST_HEADER_SIZE = 6; // 프로토콜(2) + 호출ID(4)
	public static final int RESPONSE_HEADER_SIZE = 10; // 프로토콜(2) + 호출ID(4) + 응답상태(4)	

	public static final int SUBSCRIBE_FIXED_SIZE = FIELD_SIZE_OF_MSG_AND_META + SUBSCRIBE_HEADER_SIZE;	
	public static final int REQUEST_FIXED_SIZE = FIELD_SIZE_OF_MSG_AND_META + REQUEST_HEADER_SIZE;
	public static final int RESPONSE_FIXED_SIZE = FIELD_SIZE_OF_MSG_AND_META + RESPONSE_HEADER_SIZE;
	
	public static final int SUBSCRIBE_BUFFER_SIZE = 128;
	public static final int REQUEST_BUFFER_SIZE = 256;
	public static final int RESPONSE_BUFFER_SIZE = 512;
	
	private final IPacketCodec _codec;
	
	public PacketMessage(IPacketCodec codec) {
		_codec = codec;
	}

	@Override
	public BufferOutSegment request(Protocol protocol, int callId, Object entity) {
		BufferOutSegment message = newMessage(entity == null ? REQUEST_FIXED_SIZE : REQUEST_BUFFER_SIZE, FIELD_SIZE_OF_MSG_AND_META);
		message.writeShort(protocol.id);
		message.writeInt(callId);
		return build(message, PacketType.REQUEST, entity);
	}
	
	@Override
	public BufferOutSegment responseSuccess(Protocol protocol, int callId, Object entity) {
		return response0(protocol, callId, Packet.RESPONSE_SUCCESS, entity);
	}

	@Override
	public BufferOutSegment responseFail(Protocol protocol, int callId, int errorCode, String errMsg) {
		return response0(protocol, callId, errorCode, errMsg);
	}	

	@Override
	public BufferOutSegment subscribe(Protocol protocol, Object entity) {
		BufferOutSegment message = newMessage(entity == null ? SUBSCRIBE_FIXED_SIZE : RESPONSE_BUFFER_SIZE, FIELD_SIZE_OF_MSG_AND_META);
		message.writeShort(protocol.id);
		return build(message, PacketType.SUBSCRIBE, entity);
	}	
	
	private BufferOutSegment response0(Protocol protocol, int callId, int status, Object entity) {		
		BufferOutSegment message = newMessage(entity == null ? RESPONSE_FIXED_SIZE : RESPONSE_BUFFER_SIZE, FIELD_SIZE_OF_MSG_AND_META);		
		message.writeShort(protocol.id);
		message.writeInt(callId);
		
		if(status == Packet.RESPONSE_SUCCESS) {
			message.writerIndex(RESPONSE_FIXED_SIZE); //상태코드는 자동 0으로 초기화
			return build(message, PacketType.RESPONSE, entity);
		} else {			
			return buildErrorResponse(status, (String) entity, message);
		}		
	}	
	
	public BufferOutSegment newMessage(int capacity, int offsetofPayload) {		
		BufferOutSegment message = new BufferOutSegment(capacity);

		message.writerIndex(offsetofPayload);		

		return message;
	}
	
	private BufferInSegment unwrapMeta(BufferInSegment message) {	
		PacketOptions options = options(message.readByte());		
		
		if(options.hasFlag(PacketOptions.Compression))
			return decompress(message);		
		
		return message;
	}	

	private BufferOutSegment build(BufferOutSegment message, PacketType type, Object entity) {
		PacketOptions option = PacketOptions.None;	
		
		if(entity != null) {
			byte[] a = entity instanceof byte[] 
					? (byte[]) entity
					: _codec.encode(entity);
			
			message.writeBytes(a);
			
			if(needCompress(message)) {
				message = compress(message, FIELD_SIZE_OF_MSG_AND_META);
				option = PacketOptions.Compression;
			}
		}

		message.setInt(0, message.size() - FIELD_SIZE_OF_MSG);
		message.setByte(FIELD_SIZE_OF_MSG, metadata(type, option.value));
		return message;
	}
	
	private BufferOutSegment buildErrorResponse(int errorCode, String errorMsg, BufferOutSegment message) {
		return buildErrorResponse(PacketType.RESPONSE, errorCode, errorMsg, message);
	}
	
	private BufferOutSegment buildErrorResponse(PacketType type, int errorCode, String errorMsg, BufferOutSegment message) {
		message.writeInt(errorCode);
		if(errorMsg != null)
			message.writeBytes(errorMsg.getBytes(StandardCharsets.UTF_8));

		message.setInt(0, message.size() - FIELD_SIZE_OF_MSG);
		message.setByte(FIELD_SIZE_OF_MSG, metadata(type, PacketOptions.None.value));
		return message;
	}	

	public PacketOptions options(int value) {
		return PacketOptions.of(value & 0b0000_1111);
	}
	
	public int typeValue(BufferInSegment message) {
		return message.getByte(0) >>> 4;
	}	
	
	public int metadata(PacketType type, int options) {
		return (type.value << 4) | options;
	}
	
	private BufferOutSegment compress(BufferOutSegment message, int offset) {
		int length = message.size() - offset; //압축해야할 바이트 길이
		try {
			BufferOutSegment out = new BufferOutSegment(length);
			out.writerIndex(offset); //메타데이터(사이즈 + 타입 + 옵션) 영역이후 부터 압축된 바이트를 write
			
			try(GZIPOutputStream os = new GZIPOutputStream(out, length)) {
				os.write(message.array(), offset, length);
			}
			
			return out;
		} catch(Exception ex) {
			throw new CompressException(message.toString() + ", offset:" + offset + ", length:" + length , ex);
		}
	}
	
	private BufferInSegment decompress(BufferInSegment message) {
		try {
			int readSize = 0;
			int capacity = message.readableBytes() * 2;
			
			BufferInSegment in = new BufferInSegment(capacity);
			
			byte[] buff = new byte[capacity];			
			try(GZIPInputStream is = new GZIPInputStream(message, capacity)) {			
				while((readSize = is.read(buff)) != -1) {
					in.wrieBytes(buff, 0, readSize);
				}
			}
			
			return in;
		} catch(Exception ex) {
			throw new DecompressException(message.toString(), ex);
		}					
	}		
	
	private boolean needCompress(BufferOutSegment message) {
		return message.size() > 1450; // 이더넷 MTU 사이즈(1500) - TCP/IP 헤더 사이즈(40)	
	}

	public Entity decode(BufferInSegment in, Type type) {
		return _codec.decode(in, type);
	}

	public Packet parse(BufferInSegment message) {
		PacketType type = PacketType.of(typeValue(message));
		BufferInSegment in = unwrapMeta(message);
		
		switch(type) {
			case SUBSCRIBE :
				return parseSubscribe(in);
				
			case RESPONSE :
				return parseResponse(in);
				
			case REQUEST :
				return parseRequest(in);
				
			default :
				throw new IlleagalPacketTypeException(type.toString());
		}
	}	

	private SubscribePacket parseSubscribe(BufferInSegment in) {
		Protocol p = Protocols.of(in.readShort());		
		Entity body = decode(in, Entity.class);
		return new SubscribePacket(p, body);
	}

	private RequestPacket parseRequest(BufferInSegment in) {
		Protocol p = Protocols.of(in.readShort());
		int callId = in.readInt();
		Entity body = decode(in, Entity.class);
		return new RequestPacket(p, body, callId);		
	}

	private ResponsePacket parseResponse(BufferInSegment in) {
		Protocol p = Protocols.of(in.readShort());
		int callId = in.readInt();
		int status = in.readInt();
		Entity body = decode(in, Entity.class);
		return new ResponsePacket(p, body, callId, status);
	}
	
}
