package coinone.message;

import io.vertx.core.buffer.Buffer;

public class BufferEntity extends Entity {

	private final Buffer _buffer;
	
	public BufferEntity(Buffer buffer) {
		_buffer = buffer;
	}

	@Override
	public String asString() {
		if(_bodyString != null)
			return _bodyString;
		
		byte[] a = _buffer.getBytes();
		return (_bodyString = asString(a));
	}

	@Override
	public byte[] asBytes() {
		if(_bodyBytes != null)
			return _bodyBytes;
		
		return (_bodyBytes = _buffer.getBytes());
	}

	@Override
	public Buffer asBuffer() {
		return _buffer;
	}


}
