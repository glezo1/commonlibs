package com.glezo.parsedUrl;

import java.util.ArrayList;

public class ParsedUrl 
{
	private String				raw_content;
	private String				whole_path;
	private boolean				is_relative;
	private boolean				is_anchor_relative;
	private ParsedUrl			relative_refers_to_parent;
	
	private String				protocol;
	private String				subdomain;
	private ArrayList<String>	list_of_subdomains;
	private String				domain;
	private String				tld;
	private Integer				port;
	private boolean				port_by_default;
	private String				server_side_path;
	// 																/users/pictures/find.php?serie=01&cap=02
	private String				server_side_target;					// /users/pictures/find.php
	private ArrayList<String>	server_side_param_names;			// serie,cap
	private ArrayList<String>	server_side_param_values;			// 01,02
	private ArrayList<String>	server_side_param_decoded_values;	// 01,02
	
	//-------------------------------------------------------------------------------------------------------------
	public ParsedUrl(String url,ParsedUrl parent_url)
	{
		this.raw_content						=url;
		this.is_relative						=false;
		this.is_anchor_relative					=false;
		this.relative_refers_to_parent			=parent_url;
		this.protocol							=null;
		this.subdomain							=null;
		this.domain								=null;
		this.tld								=null;
		this.port								=null;
		this.port_by_default					=false;
		this.server_side_path					=null;
		this.list_of_subdomains					=new ArrayList<String>();
		this.server_side_param_names			=new ArrayList<String>();
		this.server_side_param_values			=new ArrayList<String>();
		this.server_side_param_decoded_values	=new ArrayList<String>();

		//aux is the non-consumed part of the url
		String aux=url;

		//<protocol>://<domain>/<server_side_path>
		
		//protocol
		if(this.raw_content.contains("://"))
		{
			String tokens[]=aux.split("://");
			this.protocol=tokens[0];
			aux=aux.substring(this.protocol.length()+3,aux.length());
			this.is_relative=false;
		}
		
		//carefull now: might be like 
		//	assets/sth/sth... 
		//	or
		//	/assets/sth/sth... 
		//	or
		//	seriesdanko.com
		//	or
		//	seriesdanko.com/sth/sth...
		//so we first need to realize wether if its a relative url or not.
		if(this.protocol!=null)
		{
			//there must be a <subdomain.domain> part
			String subdomain_domain_part=null;
			int index_of_first_slash=aux.indexOf("/");
			if(index_of_first_slash!=-1)
			{
				subdomain_domain_part	=aux.substring(0,index_of_first_slash);
				String server_side_path			=aux.substring(index_of_first_slash,aux.length());
				aux=server_side_path;
				this.server_side_path=aux;
			}
			else
			{
				subdomain_domain_part	=aux;
				aux="";
				this.server_side_path=aux;
			}
			String tokens[]=subdomain_domain_part.split("\\.");
			ArrayList<String> list=new ArrayList<String>();
			for(int i=0;i<tokens.length;i++)
			{
				if(!tokens[i].equals(""))	{list.add(tokens[i]);}
			}
			if(list.size()>0)
			{
				this.tld=list.get(list.size()-1);
				list.remove(list.size()-1);
			}
			if(list.size()>0)
			{
				this.domain=list.get(list.size()-1);
				list.remove(list.size()-1);
			}
			String acum="";
			for(int i=0;i<list.size();i++)
			{
				this.list_of_subdomains.add(list.get(i));
				acum+=list.get(i)+".";
			}
			if(!acum.equals(""))	
			{	
				acum=acum.substring(0,acum.length()-1);	
				this.subdomain=acum;
			}
		}
		else
		{
			if(aux.startsWith("#"))
			{
				this.is_relative		=true;
				this.is_anchor_relative	=true;
			}
			int index_of_first_slash=aux.indexOf("/");
			int num_dots_before_first_slash=0;
			for(int i=0;i<index_of_first_slash;i++)
			{
				if(aux.charAt(i)=='.'){num_dots_before_first_slash++;}
			}
			if(num_dots_before_first_slash==0)
			{
				this.is_relative		=true;
				this.server_side_path	=aux;
				this.protocol			=this.relative_refers_to_parent.protocol;
				this.list_of_subdomains	=this.relative_refers_to_parent.list_of_subdomains;
				this.domain				=this.relative_refers_to_parent.domain;
				this.tld				=this.relative_refers_to_parent.tld;
			}
			else
			{
				this.is_relative=false;
				String domain_part=aux.substring(0,index_of_first_slash);
				String server_part=aux.substring(index_of_first_slash);
				this.server_side_path=server_part;
				String tokens[]=domain_part.split("\\.");
				ArrayList<String> list=new ArrayList<String>();
				for(int i=0;i<tokens.length;i++)
				{
					if(!tokens[i].equals(""))	{list.add(tokens[i]);}
				}
				if(list.size()>0)
				{
					this.tld=list.get(list.size()-1);
					list.remove(list.size()-1);
				}
				if(list.size()>0)
				{
					this.domain=list.get(list.size()-1);
					list.remove(list.size()-1);
				}
				String acum="";
				for(int i=0;i<list.size();i++)
				{
					this.list_of_subdomains.add(list.get(i));
					acum+=list.get(i)+".";
				}
				if(!acum.equals(""))	
				{	
					acum=acum.substring(0,acum.length()-1);	
					this.subdomain=acum;
				}
			}			
		}
		
		if(this.tld.contains(":"))
		{
			this.port_by_default=false;
			String tokens[]=this.tld.split(":");
			this.tld=tokens[0];
			this.port=Integer.parseInt(tokens[1]);
		}
		else
		{
			this.port_by_default=true;
			this.port=80;
		}
		
		//<server_side_path>
		int index_start_of_params=this.server_side_path.indexOf("?");
		if(index_start_of_params!=-1)
		{
			this.server_side_target=this.server_side_path.substring(0,index_start_of_params);
			String current_param="";
			String current_value="";
			int state=0;	//0: reading param-name. 1: reading param-value
			for(int i=index_start_of_params+1;i<this.server_side_path.length();i++)
			{
				char current_char=this.server_side_path.charAt(i);
				if(current_char=='&')
				{
					this.server_side_param_names.add(current_param);
					this.server_side_param_values.add(current_value);
					this.server_side_param_decoded_values.add(ParsedUrl.decode_string(current_value));
					current_param="";
					current_value="";
					state=0;
				}
				else if(current_char=='=')
				{
					state=1;
				}
				else if(state==0)
				{
					current_param+=current_char;
				}
				else if(state==1)
				{
					current_value+=current_char;
				}
			}
			if(!current_param.equals(""))
			{
				this.server_side_param_names.add(current_param);
				this.server_side_param_values.add(current_value);
				this.server_side_param_decoded_values.add(ParsedUrl.decode_string(current_value));
			}
		}
		else
		{
			this.server_side_target=this.server_side_path;
		}
		this.whole_path=this.calculate_whole_path();
	}
	//-------------------------------------------------------------------------------------------------------------
	public String				get_raw_content()						{	return this.raw_content;						}
	public String				get_whole_path()						{	return this.whole_path;							}
	public String				get_protocol()							{	return this.protocol;							}
	public String				get_subdomain()							{	return this.subdomain;							}
	public String				get_domain()							{	return this.domain;								}
	public String				get_tld()								{	return this.tld;								}
	public Integer				get_port()								{	return this.port;								}
	public String				get_server_side_path()					{	return this.server_side_path;					}
	public ArrayList<String>	get_list_of_subdomains()				{	return this.list_of_subdomains;					}
	public boolean				is_relative()							{	return this.is_relative;						}
	public boolean				is_anchor_relative()					{	return this.is_anchor_relative;					}
	public ParsedUrl			get_refer_parent()						{	return this.relative_refers_to_parent;			}
	public String				get_server_side_target()				{	return this.server_side_target;					}
	public ArrayList<String>	get_server_side_param_names()			{	return this.server_side_param_names;			}
	public ArrayList<String>	get_server_side_param_values()			{	return this.server_side_param_values;			}
	public ArrayList<String>	get_server_side_param_decoded_values()	{	return this.server_side_param_decoded_values;	}
	public String				toString()								{	return this.get_whole_path();					}
	//-------------------------------------------------------------------------------------------------------------
	private String				calculate_whole_path()
	{
		if(!this.is_relative)
		{
			return this.raw_content;
		}
		else
		{
			if(this.raw_content.startsWith("/"))
			{
				String result="";
				if(this.relative_refers_to_parent!=null){result+=this.relative_refers_to_parent.protocol+"://";}
				for(int i=0;i<this.relative_refers_to_parent.list_of_subdomains.size();i++)
				{
					result+=this.relative_refers_to_parent.list_of_subdomains.get(i)+".";
				}
				result+=this.relative_refers_to_parent.domain+"."+this.relative_refers_to_parent.tld;
				result+=this.raw_content;
				return result;
			}
			else
			{
				//TODO! not quite sure bout this... relative routes?
				String result="";
				if(this.relative_refers_to_parent!=null){result+=this.relative_refers_to_parent.protocol+"://";}
				for(int i=0;i<this.relative_refers_to_parent.list_of_subdomains.size();i++)
				{
					result+=this.relative_refers_to_parent.list_of_subdomains.get(i)+".";
				}
				result+=this.relative_refers_to_parent.domain+"."+this.relative_refers_to_parent.tld;
				result+="/"+this.raw_content;
				return result;
			}
		}
	}
	//-------------------------------------------------------------------------------------------------------------
	public String				get_url_but_server_side()
	{
		String result="";
		if(this.protocol!=null)				{	result+=this.protocol+"://";		}
		if(this.subdomain!=null)			{	result+=this.subdomain+".";			}
		if(this.domain!=null)				{	result+=this.domain+".";			}
		if(this.tld!=null)					{	result+=this.tld;					}
		if(this.port_by_default==false)		{	result+=":"+this.port;				}
		
		if(result.equals(""))
		{
			return null;
		}
		else
		{
			return result;
		}
	}
	//-------------------------------------------------------------------------------------------------------------
	public boolean				equals(Object o)
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
			ParsedUrl oo=(ParsedUrl)o;
			String a=this.get_whole_path();
			String b=oo.get_whole_path();
			if(a.endsWith("/")){a=a.substring(0,a.length()-1);	}
			if(b.endsWith("/")){b=b.substring(0,b.length()-1);	}
			return a.equals(b);
		}
	}
	//-------------------------------------------------------------------------------------------------------------
	public boolean				equals_but_param_values(ParsedUrl another)
	{
		if(!this.protocol.equals(another.protocol))										{	return false;	}
		if(this.subdomain==null && another.subdomain!=null)								{	return false;	}
		else if(this.subdomain!=null && another.subdomain==null)						{	return false;	}
		else if((this.subdomain!=null && another.subdomain!=null)	
				&&
				(!this.subdomain.equals(another.subdomain))
				)																		{	return false;	}
		if(!this.domain.equals(another.domain))											{	return false;	}
		if(!this.tld.equals(another.tld))												{	return false;	}
		if(this.server_side_target==null && another.server_side_target!=null)			{	return false;	}
		else if(this.server_side_target!=null && another.server_side_target==null)		{	return false;	}
		else if((this.server_side_target!=null && another.server_side_target!=null)
				&&
				!this.server_side_target.equals(another.server_side_target)
				)																		{	return false;	}
		if(this.server_side_param_names.size()!=another.server_side_param_names.size())	{	return false;	}
		for(int i=0;i<this.server_side_param_names.size();i++)
		{
			String this_current_param_name=this.server_side_param_names.get(i);
			if(!another.server_side_param_names.contains(this_current_param_name))
			{
				return false;
			}
		}
		return true;
	}
	//-------------------------------------------------------------------------------------------------------------
    public int					hashCode() 
	{
		String a=this.whole_path;
		if(a.endsWith("/")){a=a.substring(0,a.length()-1);	}
        return a.hashCode();
    }
	//-------------------------------------------------------------------------------------------------------------
    private static String		decode_string(String input)
    {
    	String result=input;
    	boolean finished=false;
    	while(!finished)
    	{
    		ArrayList<Integer> replacement_position=new ArrayList<Integer>();
    		for(int i=0;i<result.length();i++)
    		{
    			if(result.charAt(i)=='%')
    			{
    				replacement_position.add(i);
    			}
    		}
    		if(replacement_position.size()==0)
    		{
    			finished=true;
    		}
    		else
    		{
    			for(int i=0;i<replacement_position.size();i++)
    			{
    				int current_replacement_position=replacement_position.get(i);
    				String replaced=null;
   					replaced=result.substring(current_replacement_position+1,current_replacement_position+3);
    				if(!replaced.equals("25")) //%25==%. will be replaced before return-ing
    				{
    					int replaced_int=Integer.parseInt(replaced, 16);
    					String replacement=""+(char)replaced_int;
    					//we can't do result=result.replace("%"+replaced,replacement);
    					//if there are repeated patters, such as ho%XYatio%XYadios, the second pattern will be as well and will fuck as in the ass. 	
    					result=result.replaceFirst("%"+replaced,replacement);
    					//we have changed %XY by a char, so we need to update the pointers to the %
    					for(int j=i+1;j<replacement_position.size();j++)
    					{
    						replacement_position.set(j,replacement_position.get(j)-2);
    					}
    				}
    			}
    		}
    	}
    	result=result.replace("%25","%");
    	return result;
    }
    //-------------------------------------------------------------------------------------------------------------
}
