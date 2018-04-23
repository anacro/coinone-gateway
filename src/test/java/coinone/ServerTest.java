package coinone;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import coinone.client.Client;
import coinone.client.CoinoneClient;
import coinone.message.JsonPacketCodec;
import coinone.message.PacketMessage;
import coinone.message.Protocol;
import coinone.message.Protocols;

public class ServerTest {
		
	static CoinoneClient CLIENT;
		

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		MockApplication.main(new String[0]);
		Application.main(new String[0]);
		
		Thread.sleep(1000);
		
		Vertx vertx = Vertx.vertx(new VertxOptions().setBlockedThreadCheckInterval(1000 * 60 * 10));	   		
		NetClient netClient = vertx.createNetClient(new NetClientOptions());	
		String host = "127.0.0.1";
		int port = 30000;
				
		CLIENT = new CoinoneClient(
				new Client(netClient, new PacketMessage(new JsonPacketCodec()), host, port),
				AppConfiguration.coinoneProps()
		);
		
		CLIENT.subscribe(Protocols.PUB_TICKER, json -> {
			System.out.println("on Subscribe => " + Protocols.PUB_TICKER + " - " + json);
		});
		
/*		JsonObject e = new JsonObject().put(Params.APP_ID, CLIENT.getAppId());		
		Protocol p = Protocols.AUTH_REQUEST_TOKEN;
		JsonObject r = CLIENT.call(p, e).blockingGet();
		System.out.println("on Response => " + p + " - " + r);
		
		String rToken = r.getString(Result.REQUEST_TOKEN);
		e.put(Params.REQUEST_TOKEN, rToken).put(Params.app_secret, CLIENT.getAppSecret());
		p = Protocols.AUTH_ACCESS_TOKEN;
		r = CLIENT.call(p, e).blockingGet();
		System.out.println("on Response => " + p + " - " + r);
		CLIENT.setAccessToken(r.getString(Result.ACCESS_TOKEN));*/
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Thread.sleep(1000 * 10);
		
		if(CLIENT != null)
			CLIENT.close();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBALANCE() throws InterruptedException, ExecutionException {
		JsonObject e = new JsonObject();
		
		Protocol p = Protocols.ACC_BALANCE;
		JsonObject r = CLIENT.call(p, e).blockingGet();
		System.out.println("on Response => " + p + " - " + r);
		
		r = CLIENT.call(p, e).blockingGet();
		System.out.println("on Response => " + p + " - " + r);		
	}

}
