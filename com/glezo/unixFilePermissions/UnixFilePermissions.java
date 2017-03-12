package com.glezo.unixFilePermissions;

public class UnixFilePermissions 
{
	private String	raw_content;
	private char	file_type;
	private boolean	user_can_read;
	private boolean	user_can_write;
	private boolean	user_can_execute;
	private boolean	group_can_read;
	private boolean	group_can_write;
	private boolean	group_can_execute;
	private boolean	others_can_read;
	private boolean	others_can_write;
	private boolean	others_can_execute;
	
	public UnixFilePermissions(String raw_content) throws UnixFilePermissionsException
	{
		if((raw_content==null) || (raw_content.length()!=10)) 
		{
			throw new UnixFilePermissionsException("Length must be 10: "+raw_content);
		}
		this.raw_content=raw_content;
		this.file_type=this.raw_content.charAt(0);
		if(!(this.file_type=='-' || this.file_type=='d' || this.file_type=='l' || this.file_type=='s' || this.file_type=='b' || this.file_type=='c'))
		{
			throw new UnixFilePermissionsException(this.raw_content+" - Invalid file type "+this.file_type+": should be '-','d' or 'l'");
		}
		for(int i=1;i<this.raw_content.length();i++)
		{
			char c=this.raw_content.charAt(i);
			if(!(c=='r' || c=='w' || c=='x' || c=='-' || c=='s'))
			{
				throw new UnixFilePermissionsException(this.raw_content+" - Invalid char at length "+i+": "+c);
			}
		}
		this.user_can_read		=this.raw_content.charAt(1)=='r';
		this.user_can_write		=this.raw_content.charAt(2)=='w';
		this.user_can_execute	=this.raw_content.charAt(3)=='x';
		this.group_can_read		=this.raw_content.charAt(4)=='r';
		this.group_can_write	=this.raw_content.charAt(5)=='w';
		this.group_can_execute	=this.raw_content.charAt(6)=='x';
		this.others_can_read	=this.raw_content.charAt(7)=='r';
		this.others_can_write	=this.raw_content.charAt(8)=='w';
		this.others_can_execute	=this.raw_content.charAt(9)=='x';
	}
	public char		get_file_type()			{	return this.file_type;			}
	public boolean	user_can_read()			{	return this.user_can_read;		}
	public boolean	user_can_write()		{	return this.user_can_write;		}
	public boolean	user_can_execute()		{	return this.user_can_execute;	}
	public boolean	group_can_read()		{	return this.group_can_read;		}
	public boolean	group_can_write()		{	return this.group_can_write;	}
	public boolean	group_can_execute()		{	return this.group_can_execute;	}
	public boolean	others_can_read()		{	return this.others_can_read;	}
	public boolean	others_can_write()		{	return this.others_can_write;	}
	public boolean	others_can_execute()	{	return this.others_can_execute;	}
	public String	toString()				{	return this.raw_content;		}
	public boolean	equals(Object o)
	{
		if(o==null)								{	return false;	}
		if(!(o instanceof UnixFilePermissions))	{	return false;	}
		UnixFilePermissions oo=(UnixFilePermissions)o;
		return this.raw_content.equals(oo.raw_content);
	}
}
