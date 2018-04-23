package coinone;

import static coinone.Const.PACKET_LENGTH_FIELD_SIZE;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

import java.util.Arrays;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class VertxUtils {

	static final Logger LOG = LoggerFactory.getLogger(VertxUtils.class);
	
	public static RecordParser createRecordParser(final String socketId, Consumer<Buffer> onMessage) {
		final RecordParser parser = RecordParser.newFixed(PACKET_LENGTH_FIELD_SIZE);								
		parser.setOutput(new Handler<Buffer>() {
			final String _socketId = socketId;			
			int size = -1;
			
			@Override
			public void handle(Buffer buff) {
				if (size == -1) {
					if(LOG.isTraceEnabled())
						LOG.trace(_socketId + " - Packet Receive, size: " + Arrays.toString(buff.getByteBuf().array()));
					
		            size = buff.getInt(0);					
		            parser.fixedSizeMode(size);
		        } else {
					if(LOG.isTraceEnabled())
						LOG.trace(_socketId + " - Packet Receive, data: " + Arrays.toString(buff.getByteBuf().array()));
					
		        	onMessage.accept(buff);
		        	parser.fixedSizeMode(PACKET_LENGTH_FIELD_SIZE);
		            size = -1;
		        }
			}
		});
		return parser;
	}
	
	
}
