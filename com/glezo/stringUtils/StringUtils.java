package com.glezo.stringUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
	//removes blanks, non-breaking space, \t, \r and \n
	public static String		true_trim(String input)
	{
		if(input==null)				{	return null;	}
		else if(input.equals(""))	{	return input;	}
		
		int first_significative_char_index=input.length()-1;
		int last_significative_char_index=input.length()-1;
		
		for(int i=0;i<input.length();i++)
		{
			char c=input.charAt(i);
			int k=c;
			if(!(k==32 || k==160 || k==9 || k==13 || k==10))
			{
				first_significative_char_index=i;
				break;
			}
		}
		for(int i=input.length()-1;i>=0;i--)
		{
			char c=input.charAt(i);
			int k=c;
			if(!(k==32 || k==160 || k==9 || k==13 || k==10))
			{
				last_significative_char_index=i+1;
				break;
			}
		}
		return input.substring(first_significative_char_index,last_significative_char_index);
	}
	//-----------------------------------------------------------------------------------------------	
	public static String				md5(String input) throws NoSuchAlgorithmException 
	{
		String result = input;
		if(input != null) 
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(input.getBytes());
			BigInteger hash = new BigInteger(1, md.digest());
			result = hash.toString(16);
			while(result.length() < 32) 
			{
				result = "0" + result;
			}
		}
		return result;
	}	
	//-----------------------------------------------------------------------------------------------------------------------
	public static String				sha1(String input) throws NoSuchAlgorithmException 
	{
		String result = input;
		if(input != null) 
		{
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(input.getBytes());
			BigInteger hash = new BigInteger(1, md.digest());
			result = hash.toString(16);
			while(result.length() < 40) 
			{
				result = "0" + result;
			}
		}
		return result;
	}	
	//-----------------------------------------------------------------------------------------------------------------------
}
