package coinone;

import io.vertx.core.Context;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.util.concurrent.atomic.AtomicBoolean;

import coinone.message.BufferOutSegment;
import coinone.session.Session;

public class SocketContext {
	
	private final AtomicBoolean _isClosed = new AtomicBoolean();
	private final NetSocket _socket;
	private final Session _session;
	private final Context _context;
	private final Thread _contextThread;
	
	public SocketContext(Context context, NetSocket socket, Session session) {
		_context = context;
		_socket = socket;
		_session = session;
		_contextThread = Thread.currentThread();
	}	

	public String getSocketId() {
		return _socket.writeHandlerID();
	}

	/**
	 * @return false => 이미 종료 되었음
	 */
	public boolean dispose() {
		return _isClosed.compareAndSet(false, true);
	}

	public String remoteAddress() {
		return _socket.remoteAddress().toString();
	}

	public NetSocket getSocket() {
		return _socket;
	}

	public void send(BufferOutSegment message) {
		send(message.getBuffer());
	}
	
	public void send(Buffer buffer) {
		_socket.write(buffer);
	}

	public Session getSession() {
		return _session;
	}
	
	public void runOnContext(Runnable action) {
		_context.runOnContext(v -> action.run());
	}

	public static SocketContext dummy() {
		return new SocketContext(null, null, null);
	}

	public boolean isOnContextThread() {
		return _contextThread == Thread.currentThread();
	}
	
	@Override
	public String toString() {
		return "SocketContext[" + getSocketId() + "], " + remoteAddress(); 
	}
	
}
