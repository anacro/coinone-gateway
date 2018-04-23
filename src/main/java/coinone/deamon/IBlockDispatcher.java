package coinone.deamon;

public interface IBlockDispatcher extends IDispatcher {
	
	void dispatch() throws InterruptedException;
	
}
