package coinone.message;


/**
 * enum 값은 반드시 1부터 순서대로 증가 시켜야 함
 */
public enum PacketType {
	
	NONE(0),

	//클라이언트 => 서버 단방향
	HEARTBEAT(1),	//keep-allive
	SECRETKEY(2),	//TLS를 사용하지 않고 자체 보안을 사용할 경우 암호화된 사설키 보냄
	REQUEST_ONLY(3),	//호출만 하고 응답은 받지 않는 유형
	
	//서버 => 클라이언트 단방향 push
	SUBSCRIBE(4),
	
	//요청·응답
	REQUEST(5),	//클라이언트 => 서버
	RESPONSE(6),	//서버 => 클라이언트, REQUEST 타입 패킷에 대한 응답 패킷
	 
	//클라이언트 => 서버 => 클라이언트, 서버가 패킷을 받아 그대로 클라이언트로 브로드캐스팅 
	RELAY(7),

	//클라이언트 => 서버, 비동기 요청에 대한 응답
	ASYNC_REQUEST(8),
	ASYNC_RESPONSE(9);
	        
	static final PacketType[] TYPES;
	static {
		PacketType[] types = PacketType.values();
		TYPES = new PacketType[types.length];
		for(int i = 0; i < types.length; i++)
			TYPES[i] = types[i];
	}
	
	public final int value;
	PacketType(int value) {
		this.value = value;
	}

	public static PacketType of(int value) {
		try {
			return TYPES[value];
		} catch(Exception ex) {
			throw new RuntimeException("not exist PacketType - " + value); //TODO change ErrorCodeException	
		}
	}
	
}
