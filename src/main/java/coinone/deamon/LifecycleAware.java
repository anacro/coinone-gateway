package coinone.deamon;



public interface LifecycleAware {

	public void start() throws Exception;
	
	public void stop() throws Exception;
	
	public LifecycleState getLifecycleState();
	
}
