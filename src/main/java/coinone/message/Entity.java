package coinone.message;

import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class Entity {
	
	public static final Charset CHARSET_DEFAULT = StandardCharsets.UTF_8;

	public static final Entity EMPTY = new EmptyEntity();
	
	public static final MultiMap EMPTY_MULTI_MAP = MultiMap.caseInsensitiveMultiMap();
	
	public static final Buffer EMPTY_BUFFER = Buffer.buffer(new byte[0]);
	
	public static final JsonObject EMPTY_JSON_OBJECT = new JsonObject();	
	
	protected String _bodyString;
	protected byte[] _bodyBytes;
	protected JsonObject _bodyJson;
	
	public String asString() {
		return _bodyString;
	}
	
	public byte[] asBytes() {
		return _bodyBytes;
	}
	
	public abstract Buffer asBuffer();

	public JsonObject asJsonObject() {
		if(_bodyJson != null)
			return _bodyJson;
		
		return new JsonObject(asBuffer());
//		return new JsonObject(asString());
	}

	public MultiMap asMultiMap() {
		JsonObject j = asJsonObject();
		if(j.size() == 0)
			return EMPTY_MULTI_MAP;
		
		MultiMap form = MultiMap.caseInsensitiveMultiMap();		
		for(Map.Entry<String, Object> e : j) {
			form.add(e.getKey(), valueOf(e.getValue()));
		}
		
		return form;
	}
	
	private String valueOf(Object value) {
		// TODO JsonArray		
		if(value == null)
			return null;
		
		return value.toString();
	}
	
	protected final String asString(byte[] a) {
		return string(a);
	}
	
	public static String string(byte[] a) {
		return new String(a, CHARSET_DEFAULT);
	}
	
	public static byte[] bytes(String s) {
		return s.getBytes(CHARSET_DEFAULT);
	}
	
	static class EmptyEntity extends Entity {		

		@Override
		public String asString() {
			return StringUtils.EMPTY;
		}

		@Override
		public byte[] asBytes() {
			return ArrayUtils.EMPTY_BYTE_ARRAY;
		}

		@Override
		public Buffer asBuffer() {
			return EMPTY_BUFFER;
		}

		@Override
		public JsonObject asJsonObject() {
			return EMPTY_JSON_OBJECT;
		}
		
		public MultiMap asMultiMap() {
			return EMPTY_MULTI_MAP;
		}
	}

}
