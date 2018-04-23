package coinone.client;

import static coinone.ApiHelper.errorCode;
import static coinone.ApiHelper.errorMessage;
import static coinone.ApiHelper.isSuccess;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;

import java.util.Properties;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coinone.Params;
import coinone.message.MethodType;
import coinone.message.Protocol;

public class CoinoneClient {
	
	static final Logger LOG = LoggerFactory.getLogger(CoinoneClient.class);
		
	public final Client client;
	
	final String _appId;
	final String _appSecret;
	String _accessToken;	
	
	public CoinoneClient(Client client, Properties props) {
		this.client = client;
		_appId = props.getProperty(Params.APP_ID);
		_appSecret = props.getProperty(Params.APP_SECRET);
		_accessToken = props.getProperty(Params.ACCESS_TOKEN);
	}

	public Single<JsonObject> call(Protocol p, JsonObject e) {		
		return client.call(p, forV2(p, e))
				.map(packet -> {
					JsonObject j = packet.body.asJsonObject();
					
					if(isSuccess(j))
						return j;
						
					throw new ResponseErrorException(Integer.parseInt(errorCode(j)), p + " - " + errorMessage(j));					
				});
	}	

	public void subscribe(Protocol p, Consumer<JsonObject> handler) {
		client.subscribe(p, packet -> {
			handler.accept(packet.body.asJsonObject());
		});
	}	

	private JsonObject forV2(Protocol p, JsonObject j) {
		if(p.method == MethodType.PostJson) {
			if(_accessToken == null)
				throw new IllegalStateException("accessToken is null");
			
			j
			.put(Params.ACCESS_TOKEN, _accessToken)
			.put(Params.NONCE, System.currentTimeMillis());			
		}
		
		if(LOG.isDebugEnabled())
			LOG.debug(p + " => " + j);
		
		return j;
	}

	public void close() {
		if(client != null)
			client.colse();
	}

	public void setAccessToken(String token) {
		_accessToken = token;		
	}

	public String getAppId() {
		return _appId;
	}

	public String getAppSecret() {
		return _appSecret;
	}
	
}
