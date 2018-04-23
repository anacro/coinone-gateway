package coinone.session;

import io.vertx.core.buffer.Buffer;

public class CacheObject {
	
	final long _expired;
	final Buffer _buffer;

	public CacheObject(Buffer buffer, long ttl) {
		_expired = System.currentTimeMillis() + ttl;
		_buffer = buffer;
	}

	public boolean expired() {
		return System.currentTimeMillis() > _expired;
	}

	public Buffer get() {
		return _buffer;
	}

}
