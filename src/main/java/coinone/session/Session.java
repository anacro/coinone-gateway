package coinone.session;

import static coinone.Exceptions.CACHE_NOT_EXIST;
import io.reactivex.Single;
import io.vertx.core.buffer.Buffer;

import java.util.HashMap;
import java.util.Map;

public class Session {
	
	private Map<Short, CacheObject> _map = new HashMap<>();
	
	@SuppressWarnings("unused")
	private final String _sessId;
	
	public Session(String sessId) {
		_sessId = sessId;
	}

	public void onClose() {
		_map.clear();
	}

	public Single<Buffer> getCache(Short id) {
		return Single.create(s -> {
			CacheObject o = _map.get(id);
			if(o == null) {
				s.onError(CACHE_NOT_EXIST);
				return;
			}
			
			if(o.expired()) {
				_map.remove(id);
				s.onError(CACHE_NOT_EXIST);
				return;
			}
			
			s.onSuccess(o.get());
		});
	}

	public Single<Buffer> putCache(Short id, Buffer buffer, long ttl) {
		return Single.create(s -> {
			CacheObject o = new CacheObject(buffer, ttl);
			_map.put(id, o);
			s.onSuccess(o.get());
		});		
	}


}
