package com.glezo.mac;

public class Mac 
{
	private String iso_mac;			//01:23:45:67:89:AB	[capital]
	
	//-----------------------------------------------------------------------------------------
	public Mac(String mac) throws UnparseableMacException
	{
		if(mac.length()==17)
		{
			this.iso_mac="";
			boolean separator_matches=	mac.charAt(2)==mac.charAt(5)
										&& mac.charAt(2)==mac.charAt(8)
										&& mac.charAt(2)==mac.charAt(11)
										&& mac.charAt(2)==mac.charAt(14);
			if(!separator_matches)
			{
				throw new UnparseableMacException("Unparseable mac: "+mac+". Separators don't unify");
			}
			else
			{
				for(int i=0;i<mac.length();i++)
				{
					boolean is_separator=(i==2 || i==5 ||i==8 ||i==11 ||i==14);
					if(!is_separator)
					{
						char c_upper=mac.toUpperCase().charAt(i);
						if(Character.digit(c_upper,16)==-1)
						{
							throw new UnparseableMacException("Unparseable mac: "+mac+". Wrong character at position "+i);
						}
						this.iso_mac+=c_upper;
					}
					else
					{
						this.iso_mac+=":";
					}
				}
			}
		}
		else if(mac.length()==12)
		{
			this.iso_mac="";
			for(int i=0;i<mac.length();i++)
			{
				if(i==2 || i==4 ||i==6 ||i==8 ||i==10)
				{
					this.iso_mac+=":";
				}
				char c_upper=mac.toUpperCase().charAt(i);
				if(Character.digit(c_upper,16)==-1)
				{
					throw new UnparseableMacException("Unparseable mac: "+mac+". Wrong character at position "+i);
				}
				this.iso_mac+=c_upper;
			}
		}
		else
		{
			throw new UnparseableMacException("Unparseable mac: "+mac+". Length must be 12 or 17");
		}
	}
	//-----------------------------------------------------------------------------------------
	public Mac(long l) throws UnparseableMacException
	{
		String mac=Long.toString(l,16).toUpperCase();
		for(int i=mac.length();i<12;i++)
		{
			mac="0"+mac;
		}
		this.iso_mac="";
		for(int i=0;i<mac.length();i++)
		{
			if(i==2 || i==4 ||i==6 ||i==8 ||i==10)
			{
				this.iso_mac+=":";
			}
			char c_upper=mac.toUpperCase().charAt(i);
			if(Character.digit(c_upper,16)==-1)
			{
				throw new UnparseableMacException("Unparseable mac: "+mac+". Wrong character at position "+i);
			}
			this.iso_mac+=c_upper;
		}
	}
	//-----------------------------------------------------------------------------------------
	public String			get_mac(String separator,boolean uppercase)
	{
		String sep="";
		sep+=separator;
		String result=this.iso_mac;
		if(uppercase)	{result=result.toUpperCase();}
		else			{result=result.toLowerCase();}
		result=result.replaceAll(":",sep);
		return result;
	}
	//-----------------------------------------------------------------------------------------
	public String			get_mac_oui(String separator,boolean uppercase)
	{
		String sep="";
		sep+=separator;
		String result=this.iso_mac.substring(0,8);
		if(uppercase)	{result=result.toUpperCase();}
		else			{result=result.toLowerCase();}
		result=result.replaceAll(":",sep);
		return result;
	}
	//-----------------------------------------------------------------------------------------
	public String			toString()
	{
		return this.iso_mac;
	}
	//-----------------------------------------------------------------------------------------
	public static boolean	is_valid_mac(String m)
	{
		try
		{
			new Mac(m);
			return true;
		}
		catch(UnparseableMacException e)
		{ 
			return false;
		}
	}
	//-----------------------------------------------------------------------------------------
	public boolean			equals(Object o)
	{
		if(o==null)
		{
			return false;
		}
		else
		{
			if(this.getClass()!=o.getClass())
			{
				return false;
			}
			Mac oo=(Mac)o;
			return oo.iso_mac.equals(oo.iso_mac);
		}
	}
	//-----------------------------------------------------------------------------------------
	public long				toInteger()
	{
		String aux=this.get_mac("",true);
		return Long.parseLong(aux,16);
	}
	//-----------------------------------------------------------------------------------------

}
