package coinone.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.vertx.core.buffer.Buffer;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class BufferInSegment extends InputStream {
	
	private final Buffer _buffer;
	private final ByteBuf _byteBuf;

	public BufferInSegment(Buffer buffer) {
		_buffer = buffer;
		_byteBuf = buffer.getByteBuf().unwrap().unwrap();
	}

	public BufferInSegment(int capacity) {
		_byteBuf = Unpooled.buffer(capacity);
		_buffer = Buffer.buffer(_byteBuf);
	}

	@Override
	public int read() {
		return _byteBuf.isReadable() ? (_byteBuf.readByte() & 0xff) : -1;
	}
	
	@Override
	public int read(byte b[], int off, int len) {        
        int n = _byteBuf.readableBytes();
        if(n <= 0)
        	return -1;
        
        int k = len;
        if(n < k)
        	k = n;      
        
    	_byteBuf.readBytes(b, off, k);
    	return k;
	}
	
	@Override
    public long skip(long length) {
		int n = _byteBuf.readableBytes();
		if(n <= 0)
			return 0;
		
		int k = (int)length;		
		if(n < k)
			k = n;
		
        _byteBuf.skipBytes(k);
        return k;
    }	
	
	public byte[] array() {
		return _byteBuf.array();
	}
	
	public int readerIndex() {
		return _byteBuf.readerIndex();
	}

	public void readerIndex(int readerIndex) {
		_byteBuf.readerIndex(readerIndex);
	}	
	
	public void readBytes(byte[] dst, int dstIndex, int length) {
        _byteBuf.readBytes(dst, dstIndex, length);
    }	

	public void readBytes(byte[] dst) {
		_byteBuf.readBytes(dst);
	}
	
	public byte readByte() {
		return _byteBuf.readByte();
	}
	
	public short readShort() {
		return _byteBuf.readShort();
	}

	public int readInt() {
		return _byteBuf.readInt();
	}
	
	public long readLong() {
		return _byteBuf.readLong();
	}	
	
	public int readableBytes() {
		return _byteBuf.readableBytes();
	}
	
	public boolean isReadable() {
		return _byteBuf.isReadable();
	}

	public void wrieBytes(byte[] src, int srcIndex, int length) {
		_byteBuf.writeBytes(src, srcIndex, length);		
	}

	public void wrieBytes(byte[] src) {
		_byteBuf.writeBytes(src, 0, src.length);		
	}

	public void skipBytes(int length) {
		_byteBuf.skipBytes(length);
	}
	
	public Buffer getBuffer() {
		return _buffer;
	}
	
	public byte getByte(int index) {
		return _byteBuf.getByte(index);
	}
	
	public byte[] getBytes(int index, int length) {
		byte[] dst = new byte[length];
		_byteBuf.getBytes(index, dst, 0, length);
		return dst;
	}	
	
	@Override
	public String toString() {
		return "BufferInSegment - readerIndex:" + readerIndex() + ", writerIndex:" + _byteBuf.writerIndex() + ", " + _buffer;
	}

	public byte[] asBytes() {		
		int len = _byteBuf.readableBytes();
		if(len == 0)
			return ArrayUtils.EMPTY_BYTE_ARRAY;
		
		byte[] dst = new byte[len];		
		System.arraycopy(_byteBuf.array(), _byteBuf.readerIndex(), dst, 0, len);
		return dst;
	}

	public String asString(Charset charset) {
		int len = _byteBuf.readableBytes();
		if(len == 0)
			return StringUtils.EMPTY;	
		
		return new String(_byteBuf.array(), _byteBuf.readerIndex(), len, charset);		
	}

}
