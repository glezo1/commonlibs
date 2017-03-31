package com.glezo.fileSizeFormat;

public class FileSizeFormat 
{
	private long 			petabytes;
	private long 			terabytes;
	private long 			gigabytes;
	private long 			megabytes;
	private	long 			kilobytes;
	private	long 			bytes;
	private	long			total_bytes;
		
	//-------------------------------------------------------------------------------------------
	public FileSizeFormat(long size_in_bytes)
	{
		this.total_bytes=size_in_bytes;
		long size=this.total_bytes;
		this.petabytes=(int)(size/1024/1024/1024/1024/1024);
		size-=this.petabytes*1024*1024*1024*1024*1024;
		this.terabytes=(int)(size/1024/1024/1024/1024);		
		size-=this.terabytes*1024*1024*1024*1024;
		this.gigabytes=(int)(size/1024/1024/1024);			
		size-=this.gigabytes*1024*1024*1024;
		this.megabytes=(int)(size/1024/1024);				
		size-=this.megabytes*1024*1024;
		this.kilobytes=(int)(size/1024);					
		size-=this.kilobytes*1024;
		this.bytes=(int)size;
	}
	//-------------------------------------------------------------------------------------------
	public FileSizeFormat(String formatted) throws IllegalArgumentException
	{
		String tokens[]=formatted.split("\\.");
		for(int i=tokens.length;i>=0;i--)
		{
			try
			{
				long current_token_int=Long.parseLong(tokens[i]);
				if(i==0)		{	this.bytes		=current_token_int;										}
				else if(i==1)	{	this.kilobytes	=current_token_int;										}
				else if(i==2)	{	this.megabytes	=current_token_int;										}
				else if(i==3)	{	this.gigabytes	=current_token_int;										}
				else if(i==4)	{	this.terabytes	=current_token_int;										}
				else if(i==5)	{	this.petabytes	=current_token_int;										}
				else			{	throw new IllegalArgumentException("Must be between 1 and 5 tokens");	}
			}
			catch(NumberFormatException e)
			{
				throw new IllegalArgumentException("Unparseable integer at token "+i);
			}
		}
		this.total_bytes=bytes+(this.kilobytes*1024)+(this.megabytes*1024*1024)+(this.gigabytes*1024*1024*1024);
	}
	//-------------------------------------------------------------------------------------------
	public long getSizeInBytes()
	{
		return this.total_bytes; 
	}
	//-------------------------------------------------------------------------------------------
	public String getSizeInString()
	{
		return Long.toString(this.total_bytes);
	}
	//-------------------------------------------------------------------------------------------
	public String toString()
	{
		String s="";
		if(this.petabytes!=0)	
		{	
			s=Long.toString(this.petabytes)+"."+Long.toString(this.terabytes)+"."+Long.toString(this.gigabytes)+"."+Long.toString(this.megabytes)+"."+Long.toString(this.kilobytes)+"."+Long.toString(this.bytes);
			s+=" P.T.G.M.K.B";
		}
		else if(this.terabytes!=0)	
		{	
			s=Long.toString(this.terabytes)+"."+Long.toString(this.gigabytes)+"."+Long.toString(this.megabytes)+"."+Long.toString(this.kilobytes)+"."+Long.toString(this.bytes);	
			s+=" T.G.M.K.B";
		}
		else if(this.gigabytes!=0)	
		{	
			s=Long.toString(this.gigabytes)+"."+Long.toString(this.megabytes)+"."+Long.toString(this.kilobytes)+"."+Long.toString(this.bytes);	
			s+=" G.M.K.B";
		}
		else if(this.megabytes!=0)	
		{	
			s=Long.toString(this.megabytes)+"."+Long.toString(this.kilobytes)+"."+Long.toString(this.bytes);	
			s+=" M.K.B";
		}
		else if(this.kilobytes!=0)	
		{	
			s=Long.toString(this.kilobytes)+"."+Long.toString(this.bytes);	
			s+=" K.B";
		}
		else
		{
			s=Long.toString(this.bytes);	
			s+=" B";
		}
		return s;
	}
	//-------------------------------------------------------------------------------------------
}
