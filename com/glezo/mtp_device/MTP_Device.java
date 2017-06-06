package com.glezo.mtp_device;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.glezo.abstractFileSystem.AbstractFileSystem;
import com.glezo.abstractFileSystem.MTP_Entry;

import jmtp.DeviceAlreadyOpenedException;
import jmtp.PortableDevice;
import jmtp.PortableDeviceFolderObject;
import jmtp.PortableDeviceObject;
import jmtp.PortableDeviceStorageObject;

public class MTP_Device 
{
	private PortableDevice		device;
	private	AbstractFileSystem	abstract_file_system;
	
	public MTP_Device(PortableDevice device)
	{
		this.device					=	device;
		this.abstract_file_system	=	new AbstractFileSystem(new MTP_Entry("/",null,"/",null,null,null,null,true,null));
	}
	//---------------------------------------------------------------------------------------------
	public AbstractFileSystem	get_abstract_file_system()	{	return this.abstract_file_system;	}
	public PortableDevice		get_device()				{	return this.device;					}
	//---------------------------------------------------------------------------------------------
	public void scan()
	{
		try
		{
			this.device.open();
		}
		catch(DeviceAlreadyOpenedException e)
		{
		}
		// Iterate over root deviceObjects
		for (PortableDeviceObject object : this.device.getRootObjects()) 
		{
			// If the object is a storage object
			if (object instanceof PortableDeviceStorageObject) 
			{
				PortableDeviceStorageObject storage = (PortableDeviceStorageObject) object;
				//let's create two arrayLists, so we can add files first and then folders
				ArrayList<MTP_Entry> root_file_entries		=new ArrayList<MTP_Entry>();
				ArrayList<MTP_Entry> root_folder_entries	=new ArrayList<MTP_Entry>();
				for(PortableDeviceObject o2 : storage.getChildObjects()) 
				{
					String		id			=o2.getID();
					String		name		=o2.getOriginalFileName();
					Date		atime		=o2.getDateAuthored();
					Date		ctime		=o2.getDateCreated();
					Date		mtime		=o2.getDateModified();
					BigInteger	size		=o2.getSize();
					boolean		is_folder	=o2 instanceof PortableDeviceFolderObject;
					MTP_Entry e=new MTP_Entry(name,id,"/"+name,atime,ctime,mtime,size,is_folder,o2);
					//System.out.println(e);
					if(!is_folder)	{	root_file_entries.add(e);	}
					else			{	root_folder_entries.add(e);	}
				}
				Collections.sort(root_file_entries,new Comparator<MTP_Entry>(){
					@Override
					public int compare(MTP_Entry a,MTP_Entry b)
					{
						return a.get_name().compareTo(b.get_name());
					}
				});
				Collections.sort(root_folder_entries,new Comparator<MTP_Entry>(){
					@Override
					public int compare(MTP_Entry a,MTP_Entry b)
					{
						return a.get_name().compareTo(b.get_name());
					}
				});
				for(int j=0;j<root_file_entries.size();j++)
				{
					this.abstract_file_system.add_entry(root_file_entries.get(j));
				}
				for(int j=0;j<root_folder_entries.size();j++)
				{
					MTP_Entry current_directory=root_folder_entries.get(j);
					this.abstract_file_system.add_entry(current_directory);
					PortableDeviceFolderObject pdfo =(PortableDeviceFolderObject)current_directory.get_portable_device_object();
					String parent_path="/"+root_folder_entries.get(j).get_name();
					this.scan_folder(pdfo,parent_path);
				}
			}
		}
	}
	//---------------------------------------------------------------------------------------------
	private void scan_folder(PortableDeviceFolderObject folder,String parent_path)
	{
		ArrayList<MTP_Entry> root_file_entries		=new ArrayList<MTP_Entry>();
		ArrayList<MTP_Entry> root_folder_entries	=new ArrayList<MTP_Entry>();
		PortableDeviceObject[] folder_entries=folder.getChildObjects();
		for(int i=0;i<folder_entries.length;i++)
		{
			PortableDeviceObject current_entry=folder_entries[i];
			String		id			=current_entry.getID();
			String		name		=current_entry.getOriginalFileName();
			Date		atime		=current_entry.getDateAuthored();
			Date		ctime		=current_entry.getDateCreated();
			Date		mtime		=current_entry.getDateModified();
			BigInteger	size		=current_entry.getSize();
			boolean		is_folder	=current_entry instanceof PortableDeviceFolderObject;
			MTP_Entry e=new MTP_Entry(name,id,parent_path+"/"+name,atime,ctime,mtime,size,is_folder,current_entry);
			if(!is_folder)	{	root_file_entries.add(e);	}
			else			{	root_folder_entries.add(e);	}
		}
		Collections.sort(root_file_entries,new Comparator<MTP_Entry>(){
			@Override
			public int compare(MTP_Entry a,MTP_Entry b)
			{
				return a.get_name().compareTo(b.get_name());
			}
		});
		Collections.sort(root_folder_entries,new Comparator<MTP_Entry>(){
			@Override
			public int compare(MTP_Entry a,MTP_Entry b)
			{
				return a.get_name().compareTo(b.get_name());
			}
		});
		for(int j=0;j<root_file_entries.size();j++)
		{
			this.abstract_file_system.add_entry(root_file_entries.get(j));
		}
		for(int j=0;j<root_folder_entries.size();j++)
		{
			MTP_Entry current_directory=root_folder_entries.get(j);
			this.abstract_file_system.add_entry(current_directory);
			PortableDeviceFolderObject pdfo =(PortableDeviceFolderObject)current_directory.get_portable_device_object();
			String parent_path_aux=parent_path+"/"+root_folder_entries.get(j).get_name();
			this.scan_folder(pdfo,parent_path_aux);
		}
	}
	//---------------------------------------------------------------------------------------------
	public String	toString()
	{
		try
		{
			this.device.open();
		}
		catch(DeviceAlreadyOpenedException e)
		{
		}
		String result=	"Friendly_Name="	+this.device.getFriendlyName()+"\n";
		result+=		"Type="				+this.device.getType()+"\n";
		result+=		"Protocol="			+this.device.getProtocol()+"\n";
		result+=		"Description="		+this.device.getDescription()+"\n";
		result+=		"Firmware_Version="	+this.device.getFirmwareVersion()+"\n";
		result+=		"Manufacturer="		+this.device.getManufacturer()+"\n";
		result+=		"Model="			+this.device.getModel()+"\n";
		result+=		"Serial_Number="+	this.device.getSerialNumber()+"\n";
		return result;
	}
	//---------------------------------------------------------------------------------------------
	
}
