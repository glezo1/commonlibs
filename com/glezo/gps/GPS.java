package com.glezo.gps;

//Double, since we might allow null altitude (network-provider location) 

public class GPS 
{
	private Double	latitude;
	private Double	longitude;
	private Double	altitude;
	
	public GPS(Double latitude,Double longitude,Double altitude)
	{
		this.latitude	=latitude;
		this.longitude	=longitude;
		this.altitude	=altitude;
	}
	public Double	getLatitude()	{	return this.latitude;	}
	public Double	getLongitude()	{	return this.longitude;	}
	public Double	getAltitude()	{	return this.altitude;	}
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
			GPS oo=(GPS)o;
			return this.latitude==oo.latitude && this.longitude==oo.longitude && this.altitude==oo.altitude;
		}
	}	
}
