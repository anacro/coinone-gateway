package coinone.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.vertx.core.buffer.Buffer;

import java.io.OutputStream;

public class BufferOutSegment extends OutputStream {

	private final ByteBuf _byteBuf;
	
	public BufferOutSegment() {
		_byteBuf = Unpooled.buffer(256);
	}	
	
	public BufferOutSegment(int initialCapacity) {
		_byteBuf = Unpooled.buffer(initialCapacity);
	}
	
	public BufferOutSegment(byte[] array) {
		_byteBuf = Unpooled.wrappedBuffer(array);
	}

	public BufferOutSegment(ByteBuf buffer) {
		_byteBuf = buffer;
	}

	public ByteBuf getByteBuf() {
		return _byteBuf;
	}

	@Override
	public void write(int b) {
		_byteBuf.writeByte(b);
	}
	
	@Override
    public void write(byte b[], int off, int len) {
		_byteBuf.writeBytes(b, off, len);
    }
	
	public byte[] array() {
		return _byteBuf.array();
	}
	
	public void writeBytes(byte[] src) {
		_byteBuf.writeBytes(src, 0, src.length);
	}
	
	public void writeBytes(byte[] src, int srcIndex, int length) {
		_byteBuf.writeBytes(src, srcIndex, length);
	}
	
	public void writeShort(int value) {
		_byteBuf.writeShort(value);
	}
	
	public void writeInt(int value) {
		_byteBuf.writeInt(value);
	}	

	public void setInt(int index, int value) {
		_byteBuf.setInt(index, value);
	}

	public void setShort(int index, short value) {
		_byteBuf.setShort(index, value);		
	}
	
	public void setByte(int index, int value) {
		_byteBuf.setByte(index, value);
	}
	
	public int size() {
		return _byteBuf.readableBytes();
	}
	
	public void writerIndex(int writerIndex) {
		_byteBuf.writerIndex(writerIndex);		
	}

	public int writerIndex() {
		return _byteBuf.writerIndex();
	}
	
	@Override
	public String toString() {
		return "BufferOutSegment - ridx:" + _byteBuf.readerIndex() + ", widx:" + writerIndex() + ", array:" + toString(array(), writerIndex() -1);		
	}

    public String toString(byte[] a, int iMax) {
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

	public Buffer getBuffer() {
		return Buffer.buffer(_byteBuf);
	}

    
}
