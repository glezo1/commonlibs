package com.glezo.ipv4Range;

import java.util.ArrayList;

import com.glezo.ipv4.Ipv4;
import com.glezo.ipv4.UnparseableIpv4Exception;

public class Ipv4Range_CIDR  extends Ipv4Range
{
	private int	mask_length;
	
	public Ipv4Range_CIDR(String cidr) throws UnparseableIpv4RangeException
	{
		String[] tokens=cidr.split("/");
		if(tokens.length<2)				{	throw new UnparseableIpv4RangeException("Not '/' found in cidr.");		}
		if(tokens.length>2)				{	throw new UnparseableIpv4RangeException("More than one '/' in cidr.");	}
		try{this.first_ip=new Ipv4(tokens[0]);}	
		catch(UnparseableIpv4Exception e)	{	throw new UnparseableIpv4RangeException("Couldnt parse begin ip: "+tokens[0]);				}
		try
		{
			this.mask_length=Integer.parseInt(tokens[1]);
		}
		catch(NumberFormatException e)
		{
			throw new UnparseableIpv4RangeException("Mask after '/' is not a number.");
		}
		if(!(0<=this.mask_length && this.mask_length<=32))
		{
			throw new UnparseableIpv4RangeException("Mask after '/' is not valid. Shouyld be in [0,32].");
		}
		this.current_ip=this.first_ip;
		String right_mask_string="0";
		for(int i=0;i<32-this.mask_length;i++)
		{
			right_mask_string+="1";
		}
		int right_mask=0;
		if(!right_mask_string.equals("0"))
		{
			right_mask=Integer.parseInt(right_mask_string,2);
		}
		long last_ip_as_double=(long) (this.first_ip.getIp_as_long() + right_mask);
		try {this.last_ip=new Ipv4(last_ip_as_double);} catch (UnparseableIpv4Exception e) {}
		this.hasNext=true;
		this.number_of_ips=(this.last_ip.getIp_as_long()-this.first_ip.getIp_as_long())+1;
	}
	//---------------------------------------------------------------------------------------------------------
	public String getNextIpAsString() 
	{
		return this.getNextIpAsString().toString();
	}
	//---------------------------------------------------------------------------------------------------------
	public Ipv4 getNextIp() 
	{
		if(!this.hasNext)
		{
			return null;
		}
		Ipv4 result=this.current_ip.get_next_ip();
		if(result.equals(this.last_ip))
		{
			this.hasNext=false;
		}
		return result;
	}
	//---------------------------------------------------------------------------------------------------------
	public ArrayList<Ipv4>	toArrayList()
	{
		ArrayList<Ipv4> result=new ArrayList<Ipv4>();
		Ipv4 aux_current_ip=this.first_ip;
		boolean finished=false;
		while(!finished)
		{
			if(aux_current_ip.equals(this.last_ip))
			{
				result.add(aux_current_ip);
				finished=true;
			}
			else
			{
				result.add(aux_current_ip);
				aux_current_ip=aux_current_ip.get_next_ip();
			}
		}
		return result;
	}
	//---------------------------------------------------------------------------------------------------------
	public boolean ipBelongsToRange(Ipv4 ip) 
	{
		return (this.first_ip.getIp_as_long()<=ip.getIp_as_long()  && ip.getIp_as_long()<=this.last_ip.getIp_as_long());
	}
	//---------------------------------------------------------------------------------------------------------
	public ArrayList<Ipv4Range> decompose_in_ranges_of_size(int size) 
	{
		// TODO Auto-generated method stub
		return null;
	}
	//---------------------------------------------------------------------------------------------------------
	public ArrayList<Ipv4Range> toCIDR() 
	{
		ArrayList<Ipv4Range> result=new ArrayList<Ipv4Range>();
		result.add(this);
		return result;
	}
	//---------------------------------------------------------------------------------------------------------
	public ArrayList<String> toCIDRStrings() 
	{
		ArrayList<String> result=new ArrayList<String>();
		result.add(this.toString());
		return result;
	}
	//---------------------------------------------------------------------------------------------------------
	public String toString() 
	{
		return this.first_ip.toString()+"/"+this.mask_length;	
	}
	//---------------------------------------------------------------------------------------------------------
	public ArrayList<String> to_braa_masscan_string() 
	{
		ArrayList<String> result=new ArrayList<String>();
		result.add(this.first_ip+"-"+this.last_ip);
		return result;
	}
	//---------------------------------------------------------------------------------------------------------
	public void reset() 
	{
		this.current_ip=this.first_ip;
	}
	//---------------------------------------------------------------------------------------------------------
}
