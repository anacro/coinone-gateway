package coinone;

import io.vertx.core.Vertx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import coinone.component.ServerVerticle;


@EnableAppConfig
@SpringBootApplication( scanBasePackages = { "coinone.component" } )
public class Application {
	
	@Autowired private ServerVerticle _server;
	@Autowired private Vertx _vertx;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
			
/*		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
	    	try {
	    		_server.stop();
			} catch (Exception e) {
				// TODO 로그
				e.printStackTrace(System.out);
			}
		}));	*/	  
	}
	  
  
	@EventListener
	void deployVerticles(ApplicationReadyEvent event) {
		_vertx.deployVerticle(_server);
	}	 

}
