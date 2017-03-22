package com.glezo.executableInterfaces.rsf;

import java.util.ArrayList;

import com.glezo.ipv4.Ipv4;

public class RSF_Scanner_Result 
{
	private Ipv4				ip;
	private ArrayList<String>	exploits_vulnerable_to;
	
	public RSF_Scanner_Result(Ipv4 ip,ArrayList<String> exploits)
	{
		this.ip						=ip;
		this.exploits_vulnerable_to	=exploits;
	}
	public Ipv4					getIp()						{	return this.ip;						}
	public ArrayList<String>	getExploitsVulnerableTo()	{	return this.exploits_vulnerable_to;	}
}
