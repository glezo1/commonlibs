package com.glezo.ipv4Range;

public class Ipv4Range_Factory 
{
	public static Ipv4Range getRange(String range_string) throws UnparseableIpv4RangeException
	{
		String accum_message="";
		try 
		{
			Ipv4Range_begin_end a=new Ipv4Range_begin_end(range_string," - ");
			return a;
		} 
		catch (UnparseableIpv4RangeException e) 
		{
			accum_message+="Begin-End  :"+e.getMessage();
		}
		try 
		{
			Ipv4Range_CIDR a=new Ipv4Range_CIDR(range_string);
			return a;
		} 
		catch (UnparseableIpv4RangeException e) 
		{
			accum_message+="CIDR       :"+e.getMessage();
		}
		try 
		{
			Ipv4Range_custom_list a=new Ipv4Range_custom_list(range_string);
			return a;
		} 
		catch (UnparseableIpv4RangeException e) 
		{
			accum_message+="Custom List:"+e.getMessage();
		}
		throw new UnparseableIpv4RangeException(accum_message);
	}
}
