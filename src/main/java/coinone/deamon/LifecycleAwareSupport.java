package coinone.deamon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LifecycleAwareSupport {
	
	static final Logger LOG = LoggerFactory.getLogger(LifecycleAwareSupport.class);
	
	private LifecycleAwareSupport() {}

	public static void start(Object target) throws Exception {
		if(target instanceof LifecycleAware)
			((LifecycleAware) target).start();		
	}
	
	public static void stop(Object target) {
		if(target instanceof LifecycleAware) {
			try {
				((LifecycleAware) target).stop();
			} catch(Throwable t) {
				LOG.error(target.toString(), t);
			}
		}
	}	
	
}
