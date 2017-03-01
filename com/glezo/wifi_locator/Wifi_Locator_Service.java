package com.glezo.wifi_locator;

import java.util.ArrayList;
import java.util.Collection;
import com.glezo.version.Version;

public abstract class Wifi_Locator_Service 
{
	public String	algorithm_name;
	public Version	algorithm_version;
	public String	algorithm_description;
	
	public Wifi_Locator_Service(String algorithm_name,Version algorithm_version,String algorithm_description)
	{
		this.algorithm_name			=algorithm_name;
		this.algorithm_version		=algorithm_version;
		this.algorithm_description	=algorithm_description;
	}
	public String	getAlgorithmName()			{	return this.algorithm_name;			}
	public Version	getAlgorithmVersion()		{	return this.algorithm_version;		}
	public String	getAlgorithmDescription()	{	return this.algorithm_description;	}
	public abstract ArrayList<Wifi_Location> infere_position(Collection<Wifi_Observation> seeds);
}
