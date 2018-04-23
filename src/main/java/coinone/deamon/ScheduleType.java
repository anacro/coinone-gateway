package coinone.deamon;

public enum ScheduleType {
	FixedRate, FixedDelay, SimpleTrigger, CrontTrigger;
	
	public static final String INITIAL_DELAY_SECONDS = "initialDelay";
	public static final String PERIOD_SECONDS = "period";
	
	public static ScheduleType of(String type) {
		switch(type) {
			case "FixedRate" :
				return FixedRate;
				
			case "FixedDelay" :
				return FixedDelay;
				
			case "SimpleTrigger" :
				return SimpleTrigger;
				
			case "CrontTrigger" :
				return CrontTrigger;
				
			default :
				throw new IllegalArgumentException("type");
		}
	}
	
}
