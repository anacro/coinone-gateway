package coinone.message;

import lombok.ToString;
import coinone.Const;


@ToString(doNotUseGetters=true)
public class Protocol {

	public final Short id;
	
	public final String url;
	
	public final MethodType method;

	public final long ttl;
	
	public Protocol(int id, String url, MethodType method, long ttl) {	
		this.id = new Short((short) id);
		this.url = url;
		this.method = method;
		this.ttl = ttl;
	}
	
	public Protocol(int id, String url, MethodType method) {	
		this(id, url, method, Const.CACHE_TTL);
	}
	
}
