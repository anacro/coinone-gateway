package coinone.message;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Protocols {
	
	protected static final Logger LOG = LoggerFactory.getLogger(Protocols.class);

	private static final Map<Short, Protocol> PROTOCOLS = new HashMap<>();
	
	
	public static final short AUTH_REQUEST_TOKEN_ID = 10;		
	public static final short AUTH_ACCESS_TOKEN_ID = 11;
	
	public static final short ACC_BALANCE_ID = 20;
	public static final short ACC_DAILY_BALANCE_ID = 21;
	
	
	public static final short PUB_TICKER_ID = 31;
	
	
	public static final Protocol AUTH_REQUEST_TOKEN = new Protocol(AUTH_REQUEST_TOKEN_ID, "/account/login/", MethodType.Get);
	
	public static final Protocol AUTH_ACCESS_TOKEN = new Protocol(AUTH_ACCESS_TOKEN_ID, "/oauth/access_token/", MethodType.PostForm);
	
	public static final Protocol ACC_BALANCE = new Protocol(ACC_BALANCE_ID, "/v2/account/balance/", MethodType.PostJson);
	
	public static final Protocol ACC_DAILY_BALANCE = new Protocol(ACC_DAILY_BALANCE_ID, "/v2/account/daily_balance/", MethodType.PostJson);

	public static final Protocol PUB_TICKER = new Protocol(PUB_TICKER_ID, "/ticker/", MethodType.Get);

	
	
	
	static {
		synchronized(PROTOCOLS) {
			for(Field field : Protocols.class.getDeclaredFields()) {			
				try {
					if(field.getType() == Protocol.class) {
						Protocol p = (Protocol) field.get(null);
						PROTOCOLS.put(p.id, p);
					}
				} catch (Exception ex) {
					LOG.error("field must allow short constant - " + field, ex);			
				}
			}
		}
	}
	
	
	public static Protocol of(Short id) {
		Protocol p = PROTOCOLS.get(id);
		if(p == null)
			throw new ProtocolNotExistException(id);
		
		return p;
	}
	
	private Protocols() {}
	
}
