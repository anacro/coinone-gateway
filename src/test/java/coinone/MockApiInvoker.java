package coinone;


import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

import java.util.List;

import coinone.message.Protocol;
import coinone.message.Protocols;

public class MockApiInvoker extends ApiInvoker {

	public MockApiInvoker(WebClient c) {
		super(c);
	}
	
	@Override
	protected void sendForm(Request req, HttpRequest<Buffer> hr, Handler<AsyncResult<HttpResponse<Buffer>>> cb) {
	    onResponse(req, cb);
	}	
	
	@Override
	protected void send(Request req, HttpRequest<Buffer> hr, Handler<AsyncResult<HttpResponse<Buffer>>> cb) {
	    onResponse(req, cb);
	}
	
	@Override
	protected void sendJson(Request req, HttpRequest<Buffer> hr, Handler<AsyncResult<HttpResponse<Buffer>>> cb) {
		onResponse(req, cb);
	}

	private void onResponse(Request req, Handler<AsyncResult<HttpResponse<Buffer>>> cb) {
		Future<HttpResponse<Buffer>> fut = Future.future();		
		JsonObject j = result(req);
	    fut.complete(new HttpClientResponse(j));
//		fut.fail(new RuntimeException("error test"));
	    cb.handle(fut);
	}
	
	private JsonObject result(Request req) {
		Protocol p = req.getProtocol();
		
		switch(p.id.shortValue()) {
			case Protocols.AUTH_REQUEST_TOKEN_ID :
				return successJsonObject().put(Params.REQUEST_TOKEN, "request_token");
//				return errorJsonObject();
				
			case Protocols.AUTH_ACCESS_TOKEN_ID :
				return successJsonObject().put(Params.ACCESS_TOKEN, "b48144d5-ed96-471f-8214-75cxxxx0a7d4");
				
			case Protocols.ACC_BALANCE_ID :
				return successJsonObject()
						.put("btc", new JsonObject()
										.put("avail", "344.33703699")
										.put("balance", "344.33703699")
						);
				
			case Protocols.PUB_TICKER_ID :
				return successJsonObject()
						.put("currency", req.getBodyAsMultiMap().get("currency"))
						.put("first", "13045000");
				
			default :
				return new JsonObject();
		}
	}

	private JsonObject successJsonObject() {
		return new JsonObject()
			.put("errorCode", "0")
			.put("result", "success");
	}
	
	private JsonObject errorJsonObject() {
		return new JsonObject()
			.put("errorCode", "-1")
			.put("result", "error");
	}


	static class HttpClientResponse implements HttpResponse<Buffer> {
		Buffer _buffer;
		
		public HttpClientResponse(JsonObject j) {
			_buffer = j.toBuffer();
		}

		@Override
		public HttpVersion version() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int statusCode() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String statusMessage() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MultiMap headers() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getHeader(String headerName) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MultiMap trailers() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getTrailer(String trailerName) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List cookies() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Buffer body() {
			return _buffer;
		}

		@Override
		public Buffer bodyAsBuffer() {
			return _buffer;
		}

		@Override
		public JsonArray bodyAsJsonArray() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}
