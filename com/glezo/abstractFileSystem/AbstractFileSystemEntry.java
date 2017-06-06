package com.glezo.abstractFileSystem;

import java.util.Date;

public abstract class AbstractFileSystemEntry 
{
	protected Date		atime;
	protected Date		ctime;
	protected Date		mtime;
	protected String	name;
	protected String	path;
	protected boolean	is_directory;
	
	public AbstractFileSystemEntry(Date atime,Date ctime,Date mtime,String name,String path,boolean is_dir)
	{
		this.atime			=atime;
		this.ctime			=ctime;
		this.mtime			=mtime;
		this.name			=name;
		this.path			=path;
		this.is_directory	=is_dir;
	}
	public Date		get_atime()		{	return this.atime;			}
	public Date		get_ctime()		{	return this.ctime;			}
	public Date		get_mtime()		{	return this.mtime;			}
	public String	get_name()		{	return this.name;			}
	public String	get_path()		{	return this.path;			}
	public boolean	is_directory()	{	return this.is_directory;	}
}
