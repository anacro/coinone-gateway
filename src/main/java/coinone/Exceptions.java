package coinone;

import io.netty.util.internal.EmptyArrays;
import coinone.session.CacheNotExistException;

public abstract class Exceptions {
	
	public static final CacheNotExistException CACHE_NOT_EXIST = new CacheNotExistException();
	
	static {
		CACHE_NOT_EXIST.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
	}

}
