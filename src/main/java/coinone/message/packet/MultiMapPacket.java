package coinone.message.packet;

import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

import java.util.Map;

import coinone.message.Entity;
import coinone.message.PacketType;
import coinone.message.Protocol;

public class MultiMapPacket extends RequestPacket {
	
	public MultiMapPacket(Protocol protocol, MultiMap map) {
		super(PacketType.NONE, protocol, new MultiMapEntity(map), 0);		
	}
	
	static class MultiMapEntity extends Entity {
		MultiMap _map;
		
		public MultiMapEntity(MultiMap map) {
			_map = map;
		}
		
		@Override
		public MultiMap asMultiMap() {
			return _map;
		}
		
		@Override
		public JsonObject asJsonObject() {
			if(_bodyJson != null)
				return _bodyJson;
			
			JsonObject j = new JsonObject();
			for(Map.Entry<String, String> e : _map) {
				j.put(e.getKey(), e.getValue());
			}
			
			return j;
		}

		@Override
		public byte[] asBytes() {
			if(_bodyBytes != null)
				return _bodyBytes;
			
			return (_bodyBytes = asString().getBytes(CHARSET_DEFAULT));
		}

		@Override
		public String asString() {
			if(_bodyString != null)
				return _bodyString;
			
			return (_bodyString = asJsonObject().toString());
		}

		@Override
		public Buffer asBuffer() {
			return asJsonObject().toBuffer();
		}
		
	}

}
