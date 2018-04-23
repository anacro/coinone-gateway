package coinone.message;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

import java.lang.reflect.Type;

import org.springframework.util.Assert;

public class JsonPacketCodec implements IPacketCodec {
	
	@Override
	public byte[] encode(Object entity) {
		if(entity == null)
			return null;
		
		if(entity instanceof byte[])
			return (byte[]) entity;
		
		if(entity instanceof Buffer) {
			return ((Buffer) entity).getByteBuf().array();
		}
		
		if(entity instanceof JsonObject)
			return ((JsonObject) entity).toBuffer().getByteBuf().array();
		
		throw new RuntimeException("not suppored type, entity:" + entity);
	}

	@Override
	public Entity decode(BufferInSegment in, Type type) {
		Assert.notNull(in, "in is null");
		Assert.notNull(type, "type is null");
		
		int length = in.readableBytes();
		if(length == 0)
			return Entity.EMPTY;
		
		try {
			return new JsonEntity(in);
		} catch(Exception ex) {
			throw new RuntimeException(in.toString(), ex); //TODO ErrorCodeException	
		}
	} 

}
