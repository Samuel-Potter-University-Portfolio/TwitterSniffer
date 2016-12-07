package potters.pof.stats;

public class Ticker 
{
	private final long INTERNAL_TICK_TIME = 300;
	private long last_update_epoch;
	
	protected static long GetEpoch()
	{
		return System.currentTimeMillis()/1000;
	}
	
	public void Update()
	{
		long current_epoch = GetEpoch();
		Tick(current_epoch);
		
		if(current_epoch - last_update_epoch > INTERNAL_TICK_TIME)
		{
			InternalTick(current_epoch);
			last_update_epoch = current_epoch;
		}
	}

	protected void Tick(final long current_epoch){}
	protected void InternalTick(final long current_epoch){}
}
