package coinone;

import io.vertx.core.Vertx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import coinone.component.ServerVerticle;

@EnableMockAppConfig
@SpringBootApplication(scanBasePackages={ "coinone.component" })
public class MockApplication {
	
	@Autowired private ServerVerticle _server;
	@Autowired private Vertx _vertx;
	
	public static void main(String[] args) {
		SpringApplication.run(MockApplication.class, args);
	}	  
  
	@EventListener
	 void deployVerticles(ApplicationReadyEvent event) {
		_vertx.deployVerticle(_server);
	}	 

}
