package com.glezo.ipv4Range;

import java.util.ArrayList;
import java.util.Collections;

import com.glezo.ipv4.Ipv4;
import com.glezo.ipv4.UnparseableIpv4Exception;

public class Ipv4Range_custom_list extends Ipv4Range 
{
	private ArrayList<Ipv4>		customRange;
	private int					currentCustomIp;

	//----------------------------------------------------------------------
	public Ipv4Range_custom_list(ArrayList<Ipv4> cr)
	{
		this.customRange=cr;
		Collections.sort(this.customRange);
		this.currentCustomIp			=0;
		this.number_of_ips				=this.customRange.size();
		if(this.number_of_ips==0)
		{
			this.first_ip	=null;
			this.last_ip	=null;
			this.current_ip	=null;
			this.hasNext	=false;
		}
		else
		{
			this.first_ip					=this.customRange.get(0);
			this.last_ip					=this.customRange.get((int)(this.number_of_ips-1));
			this.current_ip					=this.first_ip;
			this.hasNext					=true;
		}
	}
	//----------------------------------------------------------------------
	public Ipv4Range_custom_list(String str) throws UnparseableIpv4RangeException
	{
		this.customRange=new ArrayList<Ipv4>();
		String tokens[]=str.split(",");
		for(int i=0;i<tokens.length;i++)
		{
			try 
			{
				this.customRange.add(new Ipv4(tokens[i]));
			} 
			catch (UnparseableIpv4Exception e) 
			{
				throw new UnparseableIpv4RangeException("Unparseable ip number "+i+"\n"+e.getMessage());
			}
		}
		Collections.sort(this.customRange);
		this.currentCustomIp			=0;
		this.number_of_ips				=this.customRange.size();
		this.first_ip					=this.customRange.get(0);
		this.last_ip					=this.customRange.get((int)(this.number_of_ips-1));
		this.current_ip					=this.first_ip;
		this.hasNext					=true;
	}
	//----------------------------------------------------------------------
	public String getNextIpAsString() 
	{
		return this.getNextIp().toString();
	}
	//----------------------------------------------------------------------
	public Ipv4 getNextIp() 
	{
		Ipv4 result=this.customRange.get(this.currentCustomIp);
		this.currentCustomIp++;
		if(this.currentCustomIp==this.number_of_ips)
		{
			this.hasNext=false;
		}
		return result;
	}
	//----------------------------------------------------------------------
	public boolean ipBelongsToRange(Ipv4 ip) 
	{
		return this.customRange.contains(ip);
	}
	//----------------------------------------------------------------------
	public ArrayList<Ipv4Range> decompose_in_ranges_of_size(int size) 
	{
		//I could try to collapse but.... there's actually no point on doing so, since custom ranges shall be quite small.
		ArrayList<Ipv4Range> result=new ArrayList<Ipv4Range>();
		for(int i=0;i<this.number_of_ips;i++)
		{
			Ipv4 current_ip=this.customRange.get(i);
			ArrayList<Ipv4> current_array=new ArrayList<Ipv4>();
			current_array.add(current_ip);
			Ipv4Range_custom_list current_range=new Ipv4Range_custom_list(current_array);
			result.add(current_range);
		}
		return result;
	}
	//----------------------------------------------------------------------
	public ArrayList<Ipv4Range> toCIDR() 
	{
		//I could try to collapse but.... there's actually no point on doing so, since custom ranges shall be quite small.
		ArrayList<Ipv4Range> result=new ArrayList<Ipv4Range>();
		for(int i=0;i<this.number_of_ips;i++)
		{
			Ipv4 current_ip=this.customRange.get(i);
			ArrayList<Ipv4> current_array=new ArrayList<Ipv4>();
			current_array.add(current_ip);
			Ipv4Range_custom_list current_range=new Ipv4Range_custom_list(current_array);
			result.add(current_range);
		}
		return result;
	}
	//----------------------------------------------------------------------
	public ArrayList<String> 	toCIDRStrings() 
	{
		//I could try to collapse but.... there's actually no point on doing so, since custom ranges shall be quite small.
		ArrayList<String> result=new ArrayList<String>();
		for(int i=0;i<this.number_of_ips;i++)
		{
			Ipv4 current_ip=this.customRange.get(i);
			result.add(current_ip+"/32");
		}
		return result;
	}
	//----------------------------------------------------------------------
	public String 				toString() 
	{
		String result="";
		for(int i=0;i<this.number_of_ips;i++)
		{
			result+=this.customRange.get(i)+",";
		}
		result=result.substring(0,result.length()-1);
		return result;
	}
	//----------------------------------------------------------------------
	public ArrayList<String>	to_braa_masscan_string() 
	{
		//I could try to collapse but.... there's actually no point on doing so, since custom ranges shall be quite small.
		ArrayList<String> result=new ArrayList<String>();
		for(int i=0;i<this.number_of_ips;i++)
		{
			Ipv4 current_ip=this.customRange.get(i);
			result.add(current_ip+"-"+current_ip);
		}
		return result;
	}
	//----------------------------------------------------------------------
	public void reset() 
	{
		if(this.number_of_ips==0)
		{
			this.first_ip		=null;
			this.current_ip		=null;
			this.currentCustomIp=0;
			this.hasNext		=false;
		}
		else
		{
			this.first_ip=this.customRange.get(0);
			this.current_ip=this.first_ip;
			this.currentCustomIp=0;
			this.hasNext=true;
		}
	}
	//----------------------------------------------------------------------
}
