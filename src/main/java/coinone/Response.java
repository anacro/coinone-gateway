package coinone;

import io.vertx.core.buffer.Buffer;

import java.util.ArrayList;
import java.util.List;

public class Response {
	
	private final Request _request;
	private Buffer _buffer;
	private List<Throwable> _errors;

	public Response(Request request) {
		_request = request;
	}

	public void addException(Throwable ex) {
		if (_errors == null)
			_errors = new ArrayList<>();
		
		_errors.add(ex);
	}

	public boolean isError() {
		return _errors != null ? true : false;
	}

	public List<Throwable> getErrors() {
		return _errors;
	}

	public Request getRequest() {
		return _request;
	}

	public Buffer getBuffer() {
		return _buffer;
	}
	
	public void setBuffer(Buffer buffer) {
		_buffer = buffer;		
	}

}
