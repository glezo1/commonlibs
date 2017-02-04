package com.glezo.ipv4;

import java.util.StringTokenizer;

public class Ipv4 implements Comparable<Ipv4>
{
	private long	ip_long;
	private String	ip_string;
	private int		ip_octet1;
	private int		ip_octet2;
	private int		ip_octet3;
	private int		ip_octet4;
	

	//----------------------------------------------------------------
	public Ipv4(String ip_string) throws UnparseableIpv4Exception
	{
		this.ip_string=ip_string.trim();
		this.ip_long=0;
		StringTokenizer stringTokenizer=new StringTokenizer(this.ip_string,".");
		String tokens[]=new String[4];
		for(int i=0;i<4;i++)
		{
			if(!stringTokenizer.hasMoreTokens())
			{
				throw new UnparseableIpv4Exception("Less than 4 bytes!!");
			}
			tokens[i]=stringTokenizer.nextToken();
			Integer tmp=0;
			try
			{
				tmp=Integer.parseInt(tokens[i]);
				if(  !(0<=tmp && tmp<=255)  )
				{
					throw new UnparseableIpv4Exception("Invalid value at byte "+i+":"+tokens[i]);				
				}
				double tmp2= Math.pow(256,(4-i-1));
				this.ip_long+=tmp*tmp2;
				if(i==0)		{	this.ip_octet1=(int)tmp;}
				else if(i==1)	{	this.ip_octet2=(int)tmp;}
				else if(i==2)	{	this.ip_octet3=(int)tmp;}
				else if(i==3)	{	this.ip_octet4=(int)tmp;}
			}
			catch(NumberFormatException e)
			{
				throw new UnparseableIpv4Exception("Octet "+i+"th is not a number: "+tokens[i]);
			}
		}
		if(stringTokenizer.hasMoreTokens())
		{
			throw new UnparseableIpv4Exception("More than 4 bytes!!");
		}
	}
	//----------------------------------------------------------------
	public Ipv4(Ipv4 another)
	{
		this.ip_long=another.ip_long;
		this.ip_string=new String(another.ip_string);
		this.ip_octet1=another.ip_octet1;
		this.ip_octet2=another.ip_octet2;
		this.ip_octet3=another.ip_octet3;
		this.ip_octet4=another.ip_octet4;
	}
	//----------------------------------------------------------------
	public Ipv4(long ipAsDouble) throws UnparseableIpv4Exception
	{
		if(ipAsDouble<0 || 4294967295L<ipAsDouble)
		{
			throw new UnparseableIpv4Exception("Overflow! Invalid long value "+ipAsDouble+": must be in [0,4294967295]!");
		}
		int[] bytes=new int[4];
		int foo=(int)ipAsDouble;
		bytes[0]=(foo >> 0  ) & 0xFF;
		bytes[1]=(foo >> 8  ) & 0xFF;
		bytes[2]=(foo >> 16 ) & 0xFF;
		bytes[3]=(foo >> 24 ) & 0xFF;
		String ipAsString=Integer.toString(bytes[3])+"."+Integer.toString(bytes[2])+"."+Integer.toString(bytes[1])+"."+Integer.toString(bytes[0]);

		this.ip_string=ipAsString;
		this.ip_long=ipAsDouble;
		this.ip_octet1=bytes[3];
		this.ip_octet2=bytes[2];
		this.ip_octet3=bytes[1];
		this.ip_octet4=bytes[0];
	}
	//----------------------------------------------------------------
	//GETTERS---------------------------------------------------------
	//----------------------------------------------------------------
	public long 			getIp_as_long()
	{
		return this.ip_long;
	}
	//----------------------------------------------------------------
	public String 			toString()
	{
		String result=new String(this.ip_string);
		return result;
	}
	//----------------------------------------------------------------
	public Ipv4				get_next_ip()
	{
		try 
		{
			return new Ipv4((long)this.getIp_as_long()+1);
		} 
		catch (UnparseableIpException e) 
		{
			return null;
		}
	}
	//----------------------------------------------------------------
	public boolean			equals(Ipv4 another)
	{
		return this.ip_long==another.ip_long;
	}
	//----------------------------------------------------------------
	public int				getOctet(int i) //1,2,3,4. 1 is the most significative
	{
		if(i==1)		{	return this.ip_octet1;	}
		else if(i==2)	{	return this.ip_octet2;	}
		else if(i==3)	{	return this.ip_octet3;	}
		else if(i==4)	{	return this.ip_octet4;	}
		else			{	return -1;				}
	}
	//----------------------------------------------------------------
	public int				compareTo(Ipv4 o) 
	{
		Double a=new Double(this.getIp_as_long());
		Double b=new Double(o.getIp_as_long());
		return a.compareTo(b);
	}
	//--------------------------------------------------------------------------	
	public static boolean	is_valid_ip(String ip)
	{
		try 
		{
			new Ipv4(ip);
		} 
		catch (UnparseableIpException e) 
		{
			return false;
		}
		return true;
	}
	//--------------------------------------------------------------------------	
}

