package com.glezo.wifi_locator;

import java.util.Date;

import com.glezo.gps.GPS;
import com.glezo.mac.Mac;

public class Wifi_Observation 
{
	private Mac		observer_mac;
	private GPS		observer_location;
	private Mac		observed_mac;
	private double	observed_strength; //in decibels
	private Date	observation_timestamp;
	
	public Wifi_Observation(Mac observer,GPS location,Mac observed,double strength,Date date)
	{
		this.observed_mac			=observer;
		this.observer_location		=location;
		this.observed_mac			=observed;
		this.observed_strength		=strength;
		this.observation_timestamp	=date;
	}
	public Mac		getObserverMac()			{	return this.observer_mac;			}
	public GPS		getObserverLocation()		{	return this.observer_location;		}
	public Mac		getObservedMac()			{	return this.observed_mac;			}
	public double	getObservedStregnth()		{	return this.observed_strength;		}
	public Date		getObservationTimestamp()	{	return this.observation_timestamp;	}
	public boolean	equals(Object o)
	{
		if(o==null)
		{
			return false;
		}
		else
		{
			if(this.getClass()!=o.getClass())
			{
				return false;
			}
			Wifi_Observation oo=(Wifi_Observation)o;
			return 	this.observer_mac.equals(oo.observer_mac)
					&& this.observer_location.equals(oo.observer_location)
					&& this.observed_mac.equals(oo.observed_mac)
					&& this.observed_strength==oo.observed_strength
					&& this.observation_timestamp.equals(oo.observation_timestamp);
		}
	}	
	
	
}
