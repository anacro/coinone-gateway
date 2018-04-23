package coinone.message;

import java.lang.reflect.Type;

public interface IPacketCodec {

	public byte[] encode(Object entity);

	public Entity decode(BufferInSegment in, Type type);

}
