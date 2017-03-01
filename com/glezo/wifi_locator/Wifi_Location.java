package com.glezo.wifi_locator;

import java.util.ArrayList;

import com.glezo.gps.GPS;
import com.glezo.mac.Mac;

public class Wifi_Location 
{
	private GPS							wifi_position;
	private Mac							wifi_bssid;
	private ArrayList<Wifi_Observation>	seeds;
	private Wifi_Locator_Service		algorithm;
	private double						accuracy_heuristic;
	
	public Wifi_Location(GPS position,Mac bssid,ArrayList<Wifi_Observation> seeds,Wifi_Locator_Service algorithm,double accuracy_heuristic)
	{
		this.wifi_position		=position;
		this.wifi_bssid			=bssid;
		this.seeds				=seeds;
		this.algorithm			=algorithm;
		this.accuracy_heuristic	=accuracy_heuristic;
	}
	public GPS							getWifiPosition()		{	return this.wifi_position;		}
	public Mac							getWifiBssid()			{	return this.wifi_bssid;			}
	public ArrayList<Wifi_Observation>	getSeeds()				{	return this.seeds;				}
	public Wifi_Locator_Service			getWifiLocatorService()	{	return this.algorithm;			}
	public double						getAccuracyHeuristic()	{	return this.accuracy_heuristic;	}
}
