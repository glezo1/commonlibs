package com.glezo.timeFormatter;

public class TimeFormatter 
{
	public static String	format(long milliseconds)
	{
		long days	=milliseconds/1000/60/60/24;
		milliseconds-=days*1000*60*60*24;
		long hours	=milliseconds/1000/60/60;
		milliseconds-=hours*1000*60*60;
		long minutes=milliseconds/1000/60;
		milliseconds-=minutes*1000*60;
		long seconds=milliseconds/1000;
		milliseconds-=seconds*1000;
		String r="";
		if(days!=0)			{	r+=days+" days, "+String.format("%02d",hours)+":"+String.format("%02d",minutes)+":"+String.format("%02d",seconds)+"."+String.format("%03d",milliseconds);	}
		else 				{	r+=String.format("%02d",hours)+":"+String.format("%02d",minutes)+":"+String.format("%02d",seconds)+"."+String.format("%03d",milliseconds);					}
		return r;
	}
}
