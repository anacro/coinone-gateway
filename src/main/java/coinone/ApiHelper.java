package coinone;
import io.vertx.core.json.JsonObject;


public abstract class ApiHelper {
	
	public static boolean isSuccess(JsonObject j) {
		return "0".equals(errorCode(j));
	}

	public static String errorCode(JsonObject j) {		
		return j.getString("errorCode");
	}
	
	public static String errorMessage(JsonObject j) {		
		return j.getString("errorMessage");
	}
	
	public static String result(JsonObject j) {
		return j.getString("result");
	}

}
