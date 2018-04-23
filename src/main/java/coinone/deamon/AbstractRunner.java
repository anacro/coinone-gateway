package coinone.deamon;

import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import coinone.AppConfiguration;

public abstract class AbstractRunner implements LifecycleAware {
	
	protected static final Logger LOG = LoggerFactory.getLogger(AbstractRunner.class);
	
	protected volatile LifecycleState _lifecycleState = LifecycleState.NONE;

	@Autowired protected AppConfiguration _config;
	
	public AbstractRunner() {		
	}
	
	@PostConstruct 
	public void init() {
		doConfigure(_config);
	}

	@Override
	public synchronized void start() throws Exception {
		if(_lifecycleState == LifecycleState.START)
			return;
		
		if(LOG.isInfoEnabled())
			LOG.info("try Start " + getDispatcher());			
		
		LifecycleAwareSupport.start(getDispatcher());
		
		doStart();
		
		setLifecycleState(LifecycleState.START);
	}
	
	@Override
	public synchronized void stop() throws Exception {
		if(_lifecycleState == LifecycleState.STOP)
			return;		

		if(LOG.isInfoEnabled())
			LOG.info("try Stop " + getDispatcher());
		
		doStop();
		
		LifecycleAwareSupport.stop(getDispatcher());	
		
		setLifecycleState(LifecycleState.STOP);		
	}

	@Override                    
	public LifecycleState getLifecycleState() {
		return _lifecycleState;
	}
	
	public final void setLifecycleState(LifecycleState state) {
		_lifecycleState = state;
	}
	
	protected final Logger errorLogger() {
		return _config.errorLogger();
	}
	
	protected void doConfigure(AppConfiguration config) {}
	
	protected abstract void doStart() throws Exception;
	protected abstract void doStop() throws Exception;
	protected abstract IDispatcher getDispatcher();
	protected abstract void execute(Runnable command);
	protected abstract ExecutorService getExecutor();

}
