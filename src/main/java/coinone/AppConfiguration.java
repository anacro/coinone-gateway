package coinone;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

import coinone.ApiInvoker.Signature;
import coinone.component.TickerPublisher;
import coinone.deamon.ScheduleRunner;
import coinone.deamon.ScheduleType;
import coinone.handler.AsyncHandler;
import coinone.handler.AsyncHandlerPipeline;
import coinone.handler.impl.ApiInvokerHandler;
import coinone.handler.impl.BaseErrorHandler;
import coinone.handler.impl.CacheHandler;
import coinone.handler.impl.RequestPacketHandler;
import coinone.message.HMAC;
import coinone.message.JsonPacketCodec;
import coinone.message.PacketMessage;

@Configuration
@PropertySource(value = { "classpath:application.properties" })
public class AppConfiguration {
		
	static final Logger ERR_LOGGER = LoggerFactory.getLogger("ERROR"); //logback 설정파일에 에러로그명 정의 해야 함
	
	@Autowired Environment _environment;

	public int port() {		
		return _environment.getProperty("server.port", Integer.class, 30000);
	}
	
	public String coinoneHost() {
		return _environment.getProperty("coinone.host", "api.coinone.co.kr");
	}
	
	public int coinonePort() {		
		return _environment.getProperty("coinone.port", Integer.class, 443);
	}
	
	public String tickerScheduleType() {
		return _environment.getProperty("ticker.scheduleType", ScheduleType.FixedDelay.name());
	}	

	public int tickerInitialDelaySeconds() {
		return _environment.getProperty("ticker.InitialDelaySeconds", Integer.class, 3);
	}	

	public int tickerPeriodSeconds() {
		return _environment.getProperty("ticker.PeriodSeconds", Integer.class, 3);
	}		

	public Logger errorLogger() {
		return ERR_LOGGER;
	}
	
	@Bean
	@Autowired
	public Vertx vertx() {
		VertxOptions options = Const.ENV_DEBUG 
				? new VertxOptions().setBlockedThreadCheckInterval(1000 * 60 * 5)
				: new VertxOptions();
		
		return Vertx.vertx(options);
	}
	
	@Bean
	@Autowired
	public AsyncHandlerPipeline pipeline(final BaseErrorHandler exHandler, final RequestPacketHandler packetHandler, final ApiInvokerHandler apiCallHandler, final CacheHandler cacheHandler) {
		AsyncHandler[] handlers = new AsyncHandler[] {
					exHandler
				,	packetHandler
				,	cacheHandler
				,	apiCallHandler
		};
		
		return new AsyncHandlerPipeline.Builder()
			.setErrorLogger(errorLogger())
			.addHandlers(handlers).build();
	}
	
	@Bean
	@Autowired
	public SocketHandlerManager socketHandlerManager() {
		return new SocketHandlerManager();
	}
	
	@Bean
	@Autowired 
	public BaseErrorHandler exceptionMapperHandler() {
		return new BaseErrorHandler();
	} 
	
	@Bean
	@Autowired
	public RequestPacketHandler requestPacketHandler() {
		return new RequestPacketHandler();
	}
	
	@Bean
	@Autowired
	public ApiInvokerHandler apiCallHandler() {
		return new ApiInvokerHandler();
	}
	
	@Bean
	@Autowired
	public CacheHandler cacheHandler() {
		return new CacheHandler();
	}
	
	@Bean
	@Autowired
	public PacketMessage packetMessage() {
		return new PacketMessage(new JsonPacketCodec());
	}
	  
	@Bean
	@Autowired
	public ApiInvoker apiInvoker(final Vertx vertx) {		
		return apiInvoker0(vertx);
	}

	protected ApiInvoker apiInvoker0(final Vertx vertx) {
		WebClientOptions options = new WebClientOptions()
		.setDefaultHost(coinoneHost())
		.setDefaultPort(coinonePort())
		.setSsl(true);
		return new ApiInvoker(WebClient.create(vertx, options));
	}
	
	@Bean
	@Autowired
	public Signature signature() throws Exception {
		return new Signature(new HMAC(coinoneProps().getProperty(Params.APP_SECRET).toUpperCase(), "HmacSHA512"));
	}
	
	@Bean(name="tickerRunner", initMethod="start", destroyMethod="stop")
	@Autowired
	public ScheduleRunner tickerRunner(final TickerPublisher ticker) {
		return new ScheduleRunner(ticker, ScheduleType.of(tickerScheduleType()), tickerInitialDelaySeconds() * 1000L, tickerPeriodSeconds() * 1000L);
	}
		
	public static Properties coinoneProps() throws Exception {		
		ClassPathResource resource = new ClassPathResource("coinone.properties");
		InputStream in = resource.getInputStream();
		
		int len = in.available();
		ByteArrayOutputStream out = new ByteArrayOutputStream(len * 2);
		
		byte[] buf = new byte[2048];
		int r;
		while ((r = in.read(buf)) != -1) {
			out.write(buf, 0, r);
        }		
		in.close();
		
		CipherTemplate cipher = new CipherTemplate("AES", "prkhtzh3r7weplpeob3t53qkn");
		byte[] data = cipher.decrypt(out.toByteArray());	
		
		Properties p = new Properties();
		p.load(new ByteArrayInputStream(data));		
		return p;
	}
	
}
