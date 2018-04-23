package coinone;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;

import coinone.message.HMAC;

public class ApiInvoker {	
	
	final WebClient _client;	

	@Autowired Signature _signature;
	
	public ApiInvoker(WebClient c) {
		_client = c;
	}
	
	public Single<Buffer> invoke(Request req) {
		switch(req.getHttpMethod()) {
			case Get :
				return httpGet(req);
				
			case PostForm :
				return postForm(req);
				
			case PostJson :
				return postJson(req);				
				
			default :
				throw new UnsupportedOperationException();
		}		
	}
	
	private Single<Buffer> httpGet(Request req) {
		return Single.create(s -> {
			MultiMap m = req.getBodyAsMultiMap();
			
			HttpRequest<Buffer> r = _client.get(req.getUrl());
			 
			 for(String key : m.names()) {
				 r.addQueryParam(key, m.get(key));
			 }
			 
			 send(req, r, end(s));
		});
	}
	
	protected void send(Request req, HttpRequest<Buffer> hr, Handler<AsyncResult<HttpResponse<Buffer>>> cb) {
		hr.send(cb);
	}

	private Single<Buffer> postForm(Request req) {		
		return Single.create(s -> {
			HttpRequest<Buffer> r = makeFormRequest(req);
			sendForm(req, r, end(s));		
		});
	}
	
	protected void sendForm(Request req, HttpRequest<Buffer> hr, Handler<AsyncResult<HttpResponse<Buffer>>> cb) {
		hr.sendForm(req.getBodyAsMultiMap(), cb);	
	}

	private HttpRequest<Buffer> makeFormRequest(Request req) {
		return _client.post(req.getUrl());
	}
	
	private Single<Buffer> postJson(Request req) {
		return Single.create(s -> {
			HttpRequest<Buffer> r = makeJsonRequest(req);
			sendJson(req, r, end(s));			
		});
	}
	
	protected void sendJson(Request req, HttpRequest<Buffer> hr, Handler<AsyncResult<HttpResponse<Buffer>>> cb) {
		hr.sendJson(req.getBodyAsBuffer(), cb);		
	}

	private HttpRequest<Buffer> makeJsonRequest(Request req) {
		String base64 = payload(req.getBodyAsBytes());
		return _client	
		.post(req.getUrl())
		.putHeader("X-COINONE-PAYLOAD", base64)
		.putHeader("X-COINONE-SIGNATURE", signature(base64));
	}
	
	private Handler<AsyncResult<HttpResponse<Buffer>>> end(SingleEmitter<Buffer> s) {
		return ar -> {
		    if (ar.succeeded())
		    	s.onSuccess(ar.result().body());
		    else
		    	s.onError(ar.cause());
		  };
	}

	private String signature(String xCoinonePayload) {
		if(xCoinonePayload == null)
			return null;
		
		return _signature.s(xCoinonePayload);		
	}

	private String payload(byte[] payload) {
		if(payload == null || payload.length == 0)
			return null;
		
		return new String(Base64.encodeBase64(payload));
	}	
	
	static class Signature {
		final HMAC _hmac;
		
		public Signature(HMAC hmac) {
			_hmac = hmac;
		}

		public String s(String xCoinonePayload) {
			try {
				return bytesToHex(_hmac.digest(xCoinonePayload.getBytes()));
			} catch (InvalidKeyException | NoSuchAlgorithmException ex) {
				throw new RuntimeException(ex);
			}
		}		

		private String bytesToHex(byte[] a) {
			StringBuilder sb = new StringBuilder(a.length * 2);
	        
			for(int i = 0; i < a.length; i++)
				sb.append(Integer.toString((a[i] & 0xff) + 0x100, 16).substring(1));
			
	        return sb.toString();
		}
		
	}
	
}
