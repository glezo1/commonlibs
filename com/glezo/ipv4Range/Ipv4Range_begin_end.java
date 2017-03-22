package com.glezo.ipv4Range;

import java.util.ArrayList;

import com.glezo.ipv4.Ipv4;
import com.glezo.ipv4.UnparseableIpv4Exception;

public class Ipv4Range_begin_end extends Ipv4Range
{
	private String separator;
	//--------------------------------------------------------------------------------------------------------------------------------------
	public Ipv4Range_begin_end(String begin_end,String separator) throws UnparseableIpv4RangeException
	{
		this.separator=separator;
		
		String[] tokens=begin_end.split(separator);
		if(tokens.length<2)				{	throw new UnparseableIpv4RangeException("Too few arguments at "+begin_end+". Should be 2.");	}
		if(tokens.length>2)				{	throw new UnparseableIpv4RangeException("Too many arguments at "+begin_end+". Should be 2.");	}
		try{this.first_ip=new Ipv4(tokens[0]);}	
		catch(UnparseableIpv4Exception e)	{	throw new UnparseableIpv4RangeException("Couldnt parse begin ip: "+tokens[0]);				}
		try{this.last_ip=new Ipv4(tokens[1]);}
		catch(UnparseableIpv4Exception e)	{	throw new UnparseableIpv4RangeException("Couldnt parse end ip: "+tokens[0]);					}
		if(this.first_ip.getIp_as_long()>this.last_ip.getIp_as_long())
		{
			throw new UnparseableIpv4RangeException("Begin musnt be greater than end");
		}
		this.current_ip=new Ipv4(this.first_ip);
		this.hasNext=true;
		this.number_of_ips=(this.last_ip.getIp_as_long()-this.first_ip.getIp_as_long())+1;
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	public String				getNextIpAsString() 
	{
		if(!this.hasNext)
		{
			return null;
		}
		Ipv4 next_ip=this.current_ip.get_next_ip();
		String result=next_ip.toString();
		this.current_ip=next_ip;
		if(this.current_ip.equals(this.last_ip))
		{
			this.hasNext=false;
		}
		return result;
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	public Ipv4					getNextIp() 
	{
		if(!this.hasNext)
		{
			return null;
		}
		Ipv4 next_ip=this.current_ip.get_next_ip();
		this.current_ip=next_ip;
		if(this.current_ip.equals(this.last_ip))
		{
			this.hasNext=false;
		}
		return next_ip;
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	public ArrayList<Ipv4>		toArrayList()
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
	public boolean				ipBelongsToRange(Ipv4 ip) 
	{
		return (this.first_ip.getIp_as_long()<=ip.getIp_as_long()  && ip.getIp_as_long()<=this.last_ip.getIp_as_long());
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	public void reset() 
	{
		this.current_ip=this.first_ip;
		this.hasNext=true;
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	public ArrayList<Ipv4Range>	decompose_in_ranges_of_size(int size) 
	{
		ArrayList<Ipv4Range> result=new ArrayList<Ipv4Range>();
		boolean finished=false;
		long last_ip_as_long=this.getLastIp().getIp_as_long();
		long current_ip_as_long=this.getFirstIp().getIp_as_long();
		while(!finished)
		{
			long a=current_ip_as_long;
			long z=current_ip_as_long+size;
			Ipv4 a_debug=null;
			try{	a_debug=new Ipv4(a);}
			catch(UnparseableIpv4Exception e){}
			if(!this.ipBelongsToRange(a_debug))
			{
				break;
			}
			
			
			if(z > last_ip_as_long)
			{
				finished=true;
				try 
				{
					String current_subrange=new Ipv4((long)a).toString()+" - "+this.getLastIp().toString();
					Ipv4Range nr=new Ipv4Range_begin_end(current_subrange," - ");
					nr.setASNumber(this.asnumber_or_description);
					result.add(nr);
				} 
				catch (UnparseableIpv4Exception e)		{}
				catch (UnparseableIpv4RangeException e)	{}
			}
			else
			{
				try 
				{
					String current_subrange=new Ipv4((long)a).toString()+" - "+new Ipv4((long)z).toString();
					Ipv4Range nr=new Ipv4Range_begin_end(current_subrange," - ");
					nr.setASNumber(this.asnumber_or_description);
					result.add(nr);
				} 
				catch (UnparseableIpv4Exception e)			{}
				catch (UnparseableIpv4RangeException e)		{}
				current_ip_as_long=z+1;
			}
		}
		return result;
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	public ArrayList<Ipv4Range>	toCIDR() 
	{
		ArrayList<Ipv4Range> result=new ArrayList<Ipv4Range>();
		ArrayList<String> decomposed=this.toCIDRStrings();
		for(int i=0;i<decomposed.size();i++)
		{
			String current_pieze=decomposed.get(i);
			Ipv4Range current_range=null;
			try 
			{
				current_range = new Ipv4Range_begin_end(current_pieze,this.separator);
				current_range.setASNumber(this.asnumber_or_description);
			} 
			catch (UnparseableIpv4RangeException e)	{}
			result.add(current_range);
		}
		return result;
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	public ArrayList<String>	toCIDRStrings() 
	{
		ArrayList<String> pairs = new ArrayList<String>();         
		//based on the code of 'Stephen': http://stackoverflow.com/questions/5020317/in-java-given-an-ip-address-range-return-the-minimum-list-of-cidr-blocks-that
		double[] CIDR2MASK = new double[] 
		{ 
				//this represents the decimal number for /31,/30,/29 etc
				//0x00000000, 0x80000000, 0xC0000000, 0xE0000000, 0xF0000000, 0xF8000000, 0xFC000000,             
				0,2147483648.0,3221225472.0,3758096384.0,4026531840.0,4160749568.0,4227858432.0,
				//0xFE000000, 0xFF000000, 0xFF800000, 0xFFC00000, 0xFFE00000,
				4261412864.0  , 4278190080.0, 4286578688.0, 4290772992.0, 4292870144.0,
				//0xFFF00000, 0xFFF80000, 0xFFFC0000, 0xFFFE0000, 0xFFFF0000,             
				4293918720.0  , 4294443008.0, 4294705152.0, 4294836224.0, 4294901760.0,
				//0xFFFF8000, 0xFFFFC000, 0xFFFFE000, 0xFFFFF000, 0xFFFFF800,             
				4294934528.0,   4294950912.0, 4294959104.0, 4294963200.0, 4294965248.0,
				//0xFFFFFC00, 0xFFFFFE00, 0xFFFFFF00, 0xFFFFFF80, 0xFFFFFFC0,             
				4294966272.0,   4294966784.0, 4294967040.0, 4294967168.0, 4294967232.0,
				//0xFFFFFFE0, 0xFFFFFFF0, 0xFFFFFFF8, 0xFFFFFFFC, 0xFFFFFFFE, 0xFFFFFFFF             
				4294967264.0,   4294967280.0, 4294967288.0, 4294967292.0, 4294967294.0, 4294967295.0
		};       
		long start = (long)this.first_ip.getIp_as_long();
		long end = (long)this.last_ip.getIp_as_long();           
		while ( end >= start ) 
		{             
			byte maxsize = 32;             
			while ( maxsize > 0) 
			{                 
				long mask = (long)CIDR2MASK[ maxsize -1 ];                 
				long maskedBase = start & mask;                 
				if ( maskedBase != start ) 
				{                     
					break;                 
				}                 
				maxsize--;             
			}               
			double x = Math.log( end - start + 1) / Math.log( 2 );             
			byte maxdiff = (byte)( 32 - Math.floor( x ) );             
			if ( maxsize < maxdiff) 
			{                 
				maxsize = maxdiff;             
			}             
			Ipv4 ip=null;
			try 
			{
				ip = new Ipv4(start);
			} 
			catch (UnparseableIpv4Exception e)	{}
			pairs.add( ip.toString() + "/" + maxsize);             
			start += Math.pow( 2, (32 - maxsize) );         
		}         
		return pairs;
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	public String				toString() 
	{
		return this.first_ip+" - "+this.last_ip;
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	public ArrayList<String>	to_braa_masscan_string() 
	{
		ArrayList<String> result=new ArrayList<String>();
		result.add(this.first_ip+"-"+this.last_ip);
		return result;
	}
}
