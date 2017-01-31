package com.glezo.version;

import java.util.ArrayList;

import string_utils.String_Utils;

public class Version implements Comparable<Version>
{
	private ArrayList<String>	list_of_version_parts;	//String. we allow 0.1b, for example. compareTo will handle.
	private String				additional_info;
	
	//----------------------------------------------------------------
	public Version(String version_string)
	{
		version_string=version_string.trim();
		this.list_of_version_parts=new ArrayList<String>();
		this.additional_info=null;
		int position_of_first_blank=version_string.indexOf(' ');
		String tokens[];
		if(position_of_first_blank!=-1)
		{
			this.additional_info=version_string.substring(position_of_first_blank).trim();
			tokens=version_string.substring(0,position_of_first_blank).split("\\.");
		}
		else
		{
			tokens=version_string.split("\\.");
		}
		for(int i=0;i<tokens.length;i++)
		{
			this.list_of_version_parts.add(tokens[i]);
		}
	}
	//----------------------------------------------------------------
	public String toString()
	{
		String result="";
		for(int i=0;i<this.list_of_version_parts.size();i++)
		{
			result+=this.list_of_version_parts.get(i)+".";
		}
		result=result.substring(0,result.length()-1);
		if(this.additional_info!=null)
		{
			result+=" "+this.additional_info;
		}
		return result;
	}
	//----------------------------------------------------------------
	public boolean equals(Version v)
	{
		if(this.list_of_version_parts.size()!= v.list_of_version_parts.size())
		{
			return false;
		}
		for(int i=0;i<this.list_of_version_parts.size();i++)
		{
			if(!this.list_of_version_parts.get(i).equals(v.list_of_version_parts.get(i)))
			{
				return false;
			}
		}
		if(this.additional_info==null && v.additional_info!=null)
		{
			return false;
		}
		else if(this.additional_info!=null && v.additional_info==null)
		{
			return false;
		}
		else if(this.additional_info==null && v.additional_info==null)
		{
			return true;
		}
		else
		{
			return this.additional_info.equals(v.additional_info);
		}
	}
	//----------------------------------------------------------------
	public int compareTo(Version another) 
	{
		//-1  <==> this<another
		int this_number_of_parts=this.list_of_version_parts.size();
		int another_number_of_parts=another.list_of_version_parts.size();
		int min_number_of_parts=Math.min(this_number_of_parts,another_number_of_parts);
		for(int i=0;i<min_number_of_parts;i++)
		{
			String current_this_raw_part=this.list_of_version_parts.get(i);
			String current_another_raw_part=another.list_of_version_parts.get(i);
			
			String current_this_integer_string		=String_Utils.max_prefix_conforming_an_integer_for(current_this_raw_part); 
			String current_another_integer_string	=String_Utils.max_prefix_conforming_an_integer_for(current_another_raw_part);
			
			Integer current_this	=Integer.parseInt(current_this_integer_string);
			Integer current_another	=Integer.parseInt(current_another_integer_string);
			int current_compare_result=current_this.compareTo(current_another);
			if(current_compare_result!=0)
			{
				return current_compare_result;
			}
			//compare non-integer part:
			String this_non_int_part	=current_this_raw_part.substring(current_this_integer_string.length());
			String another_non_int_part	=current_another_raw_part.substring(current_another_integer_string.length());
			current_compare_result=this_non_int_part.compareTo(another_non_int_part);
			if(current_compare_result!=0)
			{
				return current_compare_result;
			}
		}
		if(this_number_of_parts!=another_number_of_parts)
		{
			//if we reach, we kinda comparing	0.1a.2b	vs 0.1a		;0.1a==>0.1a.0.0.0.0.0.0 so first should be greater.
			if(min_number_of_parts==this_number_of_parts)
			{
				return -1;
			}
			else
			{
				return 1;
			}
		}
		else
		{
			if(			this.additional_info==null && another.additional_info!=null)	{	return -1;														}
			else if(	this.additional_info!=null && another.additional_info==null)	{	return 1;														}
			else if(	this.additional_info==null && another.additional_info==null)	{	return 0;														}
			else																		{	return this.additional_info.compareTo(another.additional_info);	}
		}
	}
	//----------------------------------------------------------------
}
