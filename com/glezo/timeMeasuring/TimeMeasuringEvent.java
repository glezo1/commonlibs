package com.glezo.timeMeasuring;

import java.util.Date;

public class TimeMeasuringEvent 
{
	private Date	event_timestamp;
	private String	activity_name;
	private String	event_type;	// "start", "stop"
	
	public TimeMeasuringEvent(Date event_timestamp,String activity_name,String event_type)
	{
		this.event_timestamp	=event_timestamp;
		this.activity_name		=activity_name;
		this.event_type			=event_type;
	}
	public Date		getEventTimestamp()	{	return this.event_timestamp;	}
	public String	getActivityName()	{	return this.activity_name;		}
	public String	getEventType()		{	return this.event_type;			}
}
