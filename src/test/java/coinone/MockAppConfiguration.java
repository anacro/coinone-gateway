package coinone;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import org.springframework.context.annotation.Configuration;

@Configuration
public class MockAppConfiguration extends AppConfiguration {

	@Override
	protected ApiInvoker apiInvoker0(final Vertx vertx) {
		WebClientOptions options = new WebClientOptions()
		.setDefaultHost("127.0.0.1")
		.setDefaultPort(8443);
		return new MockApiInvoker(WebClient.create(vertx, options));
	}
	
}
