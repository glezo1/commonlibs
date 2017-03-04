package com.glezo.ipv4Range;

import java.util.ArrayList;

import com.glezo.ipv4.Ipv4;

/*
	child classes:
		Ipv4Range_begin_end			specified by a start and end ip
		Ipv4Range_custom_list		list of ips
		Ipv4Range_CIDR
*/
public abstract class Ipv4Range implements Comparable<Ipv4Range>
{
	protected Ipv4							first_ip;
	protected Ipv4							last_ip;
	protected Ipv4							current_ip;
	protected long							number_of_ips;
	protected boolean						hasNext;
	protected String						asnumber_or_description;
	
	public Ipv4								getFirstIp()					{	return this.first_ip;					}
	public Ipv4								getLastIp()						{	return this.last_ip;					}
	public long								getNumberOfIpsInRange()			{	return this.number_of_ips;				}		
	public boolean							hasNext()						{	return this.hasNext;					}
	public String							getASNumber_or_description()	{	return this.asnumber_or_description;	}

	abstract public String					getNextIpAsString();
	abstract public Ipv4					getNextIp();
	abstract public boolean					ipBelongsToRange(Ipv4 ip);
	abstract public ArrayList<Ipv4Range>	decompose_in_ranges_of_size(int size);
	abstract public ArrayList<Ipv4Range>	toCIDR();
	abstract public ArrayList<String>		toCIDRStrings(); 
	abstract public String					toString();
	abstract public ArrayList<String>		to_braa_masscan_string();
	abstract public void					reset();
	public void								setASNumber(String asn)			{	this.asnumber_or_description=asn;			}
	
	public int								compareTo(Ipv4Range o)			{	return this.first_ip.compareTo(o.first_ip);	} 
	public boolean							equals(Object o)
	{
		if(this == o)
		{
			return true;
		}
		else if( (o instanceof Ipv4Range_begin_end)|| (o instanceof Ipv4Range_custom_list) || (o instanceof Ipv4Range_CIDR))
		{
			Ipv4Range or=(Ipv4Range)o;
			return (this.first_ip.equals(or.first_ip)) && (this.last_ip.equals(or.last_ip) && this.number_of_ips==or.number_of_ips); 
		}
		else
		{
			return false;
		}
	}
	
}
