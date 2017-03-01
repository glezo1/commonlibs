package com.glezo.wifi_locator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.glezo.version.Version;

public class Wifi_Locator_Unweighted_Average_Observer extends Wifi_Locator_Service 
{
	private static final String		algorithm_name="unweighted_average_observer";
	private static final Version	algorithm_version=new Version("0.1"); 
	private static final String		algorithm_description="Unweighted average, even between different observers";
	
	public Wifi_Locator_Unweighted_Average_Observer()
	{
		super(algorithm_name,algorithm_version,algorithm_description);
	}
	public ArrayList<Wifi_Location> infere_position(Collection<Wifi_Observation> seeds) 
	{
		ArrayList<Wifi_Observation> unprocessed=new ArrayList<Wifi_Observation>();
		for(Iterator<Wifi_Observation> iterator = seeds.iterator(); iterator.hasNext();)
		{
			Wifi_Observation current_observation = (Wifi_Observation) iterator.next();
			unprocessed.add(current_observation);
		}
		//TODO!
		
		
		return null;
	}

}
