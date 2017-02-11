package com.glezo.timeMeasuring;

public class TimeMeasuringEventExecution 
{
	private TimeMeasuringEvent	event;
	private long				time_elapsed;
	
	public TimeMeasuringEventExecution(TimeMeasuringEvent event,long time_elapsed)
	{
		this.event			=event;
		this.time_elapsed	=time_elapsed;
	}
	public TimeMeasuringEvent	getEvent()				{	return this.event;			}
	public long					getTimeElapsed()		{	return this.time_elapsed;	}
	public void					addTimeElapsed(long l)	{	this.time_elapsed+=l;		}	
}
