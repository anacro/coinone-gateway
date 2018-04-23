package coinone;

@FunctionalInterface
public interface Action {
	
	void apply() throws Exception;

}
