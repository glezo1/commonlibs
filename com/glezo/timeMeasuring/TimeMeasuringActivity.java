package com.glezo.timeMeasuring;

public class TimeMeasuringActivity 
{
	private String	activity_name;
	private	long	time_elapsed;
	private boolean	is_running;
	
	public TimeMeasuringActivity(String activity_name,long time_elapsed,boolean is_running)
	{
		this.activity_name	=activity_name;
		this.time_elapsed	=time_elapsed;
		this.is_running		=is_running;
	}
	public String	getActivityName()	{	return this.activity_name;	}
	public long		getTimeElapsed()	{	return this.time_elapsed;	}
	public boolean	isRunning()			{	return this.is_running;		}
	public boolean	equals(Object o)
	{
		if(o==null)
		{
			return false;
		}
		else if(o instanceof String)
		{
			return this.activity_name.equals(o);
		}
		else if(!(o instanceof TimeMeasuringEvent))
		{
			return false;
		}
		else
		{
			TimeMeasuringActivity oo=(TimeMeasuringActivity)o;
			return this.activity_name.equals(oo.activity_name);
		}
	}
	
}
