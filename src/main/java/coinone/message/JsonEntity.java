package coinone.message;

import io.vertx.core.buffer.Buffer;
import lombok.ToString;

@ToString(doNotUseGetters=true)
public class JsonEntity extends Entity {

	final BufferInSegment _in;
	
	public JsonEntity(BufferInSegment in) {
		_in = in;
	}

	@Override
	public byte[] asBytes() {
		if(_bodyBytes != null)
			return _bodyBytes;
		
		return (_bodyBytes = _in.asBytes());
	}

	@Override
	public String asString() {
		if(_bodyString != null)
			return _bodyString;
		
		return (_bodyString = _in.asString(CHARSET_DEFAULT));
	}

	@Override
	public Buffer asBuffer() {
		return _in.getBuffer();
	}

}
