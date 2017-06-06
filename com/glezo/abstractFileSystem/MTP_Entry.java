package com.glezo.abstractFileSystem;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import jmtp.PortableDeviceObject;

public class MTP_Entry extends AbstractFileSystemEntry 
{
	private PortableDeviceObject	portable_device_object;
	private BigInteger				size;
	private String					id;
	
	public MTP_Entry(String name,String id,String path,Date atime,Date ctime,Date mtime,BigInteger size,boolean is_directory,PortableDeviceObject portable_device_object)
	{
		super(atime,ctime,mtime,name,path,is_directory);
		this.size					=	size;
		this.id						=	id;
		this.portable_device_object	=	portable_device_object;
	}
	public BigInteger			get_size()						{	return this.size;					}
	public PortableDeviceObject	get_portable_device_object()	{	return this.portable_device_object;	}
	public String				get_id()						{	return this.id;						}
	public String toString()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
		String result="";
		if(this.is_directory)	{	result+="F\t";					}
		else					{	result+="f\t";					}
		if(this.id!=null)		{	result+=this.id;				}
		result+="\t"+this.path+"\t";
		if(this.ctime!=null)	{	result+=sdf.format(this.ctime);	}	
		result+="\t";
		if(this.mtime!=null)	{	result+=sdf.format(this.mtime);	}
		result+="\t";
		if(this.size!=null)		{	result+=this.size.toString();	}
		return result;
	}

}
