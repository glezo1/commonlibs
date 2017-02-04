package com.glezo.stringUtils;

public class StringUtils 
{
	/*
		Just a bunch of common-use, one-liner static methods, implemented here to enhance readibility 
	*/
	//-----------------------------------------------------------------------------------------------
	public static boolean		is_decimal_integer(String input)
	{
		if(input==null)
		{
			return false;
		}
		for(int i=0;i<input.length();i++)
		{
			char current_char=input.charAt(i);
			if('0'<=current_char && current_char<='9')
			{
				
			}
			else
			{
				return false;
			}
		}
		return true;
	}
	//-----------------------------------------------------------------------------------------------
	public static String		max_prefix_conforming_an_integer_for(String input)
	{
		for(int i=input.length()-1;i>=0;i--)
		{
			String current_string=input.substring(0,i);
			if(StringUtils.is_decimal_integer(current_string))
			{
				return current_string;
			}
		}
		return null;
	}
	//-----------------------------------------------------------------------------------------------
}
