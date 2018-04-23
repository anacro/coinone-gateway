package coinone.deamon;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduleRunner extends AbstractRunner {
	
	private final ScheduledExecutorService _scheduler = Executors.newSingleThreadScheduledExecutor();	
		
	private final ScheduleType _scheduleType;
	private final long _initialDelay;
	private final long _period;
	private final IBlockDispatcher _dispatcher;
		
	public ScheduleRunner(IBlockDispatcher dispatcher, ScheduleType scheduleType, long initialDelayMillis, long periodMillis) {
		_dispatcher = dispatcher;
		_scheduleType = scheduleType;
		_initialDelay = initialDelayMillis;
		_period = periodMillis;		
	}
	
	protected IDispatcher getDispatcher() {
		return _dispatcher;
	}	

	@Override
	protected void doStart() throws Exception {
		Runnable command = () -> {
			try {
				_dispatcher.dispatch();															
			} catch(Exception ex) {
				errorLogger().error("fail dispatch - " + _dispatcher, ex);
			} catch(Throwable t) {
				errorLogger().error("Throwable dispatch - " + _dispatcher, t);				
				//TODO 디스패쳐를 새로 생성함
			}
		};		
		
		if(_scheduleType == ScheduleType.FixedRate)
			_scheduler.scheduleAtFixedRate(command, _initialDelay, _period, TimeUnit.MILLISECONDS);
		else
			_scheduler.scheduleWithFixedDelay(command, _initialDelay, _period, TimeUnit.MILLISECONDS);		
	}

	@Override
	protected void doStop() throws Exception {
		try { _scheduler.shutdownNow(); } catch(Throwable t) {}
	}

	@Override
	protected void execute(Runnable command) {
		_scheduler.execute(command);		
	}

	@Override
	protected ExecutorService getExecutor() {
		return _scheduler;
	}
	
}

