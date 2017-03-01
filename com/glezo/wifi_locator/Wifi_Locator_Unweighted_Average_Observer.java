package com.glezo.wifi_locator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.glezo.gps.GPS;
import com.glezo.mac.Mac;
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
		//result shall be an arraylist: one item per distinct observed router
		ArrayList<Wifi_Location> result=new ArrayList<Wifi_Location>();
		
		HashMap<Mac,ArrayList<Wifi_Observation>> observed_map=new HashMap<Mac,ArrayList<Wifi_Observation>>();
		for(Iterator<Wifi_Observation> iterator = seeds.iterator(); iterator.hasNext();)
		{
			Wifi_Observation current_observation = (Wifi_Observation) iterator.next();
			Mac current_observed_mac=current_observation.getObservedMac();
			ArrayList<Wifi_Observation> seeds_data=observed_map.get(current_observed_mac);
			if(seeds_data==null)
			{
				seeds_data=new ArrayList<Wifi_Observation>();
				seeds_data.add(current_observation);
				observed_map.put(current_observed_mac,seeds_data);
			}
			else
			{
				seeds_data.add(current_observation);
			}
		}
		Collection<ArrayList<Wifi_Observation>> collection=observed_map.values();
		Iterator<ArrayList<Wifi_Observation>> iterator=collection.iterator();
		while(iterator.hasNext())
		{
			//arraylist of each and every observed mac
			ArrayList<Wifi_Observation> current_router_data=iterator.next();
			Mac current_router_bssid=current_router_data.get(0).getObservedMac();
			int num=0;
			double lat=0;
			double alt=0;
			double lon=0;
			for(int i=0;i<current_router_data.size();i++)
			{
				num++;
				Double lat_aux=current_router_data.get(i).getObserverLocation().getLatitude();
				Double alt_aux=current_router_data.get(i).getObserverLocation().getAltitude();
				Double lon_aux=current_router_data.get(i).getObserverLocation().getLongitude();
				if(lat_aux!=null)	{	lat+=lat_aux;	}
				if(alt_aux!=null)	{	alt+=alt_aux;	}
				if(lon_aux!=null)	{	lon+=lon_aux;	}
			}
			lat/=num;
			alt/=num;
			lon/=num;
			result.add(new Wifi_Location(new GPS(lat,lon,alt),current_router_bssid,current_router_data,this,num));
		}
		return result;
	}

}
