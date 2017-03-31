//Copyright 2012 Rui Araújo, Luis Fonseca: getHexString(byte[] raw | short[] raw | short raw)

package com.glezo.stringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class StringUtils 
{
	/*
		Just a bunch of common-use, one-liner static methods, implemented here to enhance readibility 
	*/
    private static final byte[] HEX_CHAR_TABLE = 
    {
    	(byte) '0', (byte) '1', (byte) '2', (byte) '3',
    	(byte) '4', (byte) '5', (byte) '6', (byte) '7',
    	(byte) '8', (byte) '9', (byte) 'a', (byte) 'b',
    	(byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f'
    };
	
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
    public static String getHexString(byte[] raw)	throws UnsupportedEncodingException 
    {
    	byte[] hex = new byte[2 * raw.length];
    	int index = 0;

    	for (byte b : raw) 
    	{
    		int v = b & 0xFF;
    		hex[index++] = HEX_CHAR_TABLE[v >>> 4];
    		hex[index++] = HEX_CHAR_TABLE[v & 0xF];
    	}
    	return new String(hex, "ASCII");
    }
	//-----------------------------------------------------------------------------------------------------------------------
    public static String getHexString(short[] raw)	throws UnsupportedEncodingException 
    {	
    	byte[] hex = new byte[2 * raw.length];
    	int index = 0;

    	for (short b : raw) 
    	{
    		int v = b & 0xFF;
    		hex[index++] = HEX_CHAR_TABLE[v >>> 4];
    		hex[index++] = HEX_CHAR_TABLE[v & 0xF];
    	}
    	return new String(hex, "ASCII");
    }
	//-----------------------------------------------------------------------------------------------------------------------
    public static String getHexString(short raw) 
    {
    	byte[] hex = new byte[2];
    	int v = raw & 0xFF;
    	hex[0] = HEX_CHAR_TABLE[v >>> 4];
    	hex[1] = HEX_CHAR_TABLE[v & 0xF];
    	try 
    	{
    		return new String(hex, "ASCII");
    	} 
    	catch (UnsupportedEncodingException e) 
    	{
    	}
    	return "";
    }
	//-----------------------------------------------------------------------------------------------------------------------
	/*
		StringUtils.consecutiveCollapseReplace(String input,ArrayList<String> pattern,ArrayList<String> replacement,String prefix,boolean show_times,String suffix)
		input		:	input string
		pattern		:	arraylist of patterns to be replaced
		replacement	:	arraylist of the replacements, one for each pattern
		show_times	:	if true, <pattern> will be replaced by		<replacement><prefix><#of_consecutive_appareances><suffix>.
						otherwise, <pattern> will be replaced by 	<replacement><prefix><suffix>.
	*/
	public static String consecutiveCollapseReplace(String input,ArrayList<String> pattern,ArrayList<String> replacement,boolean show_times,String prefix,String suffix) throws IllegalArgumentException
	{
		if(pattern==null || replacement==null || pattern.size()!=replacement.size())
		{
			throw new IllegalArgumentException("Pattern and replacement musn't be null, and must have same size");
		}
		String result=input;
		for(int i=0;i<pattern.size();i++)
		{
			String current_pattern=pattern.get(i);
			String current_replacement=replacement.get(i);
			int max_consecutive_appereance=1;
			boolean finished=false;
			while(!finished)
			{
				String pattern_n="";
				for(int k=0;k<max_consecutive_appereance;k++){	pattern_n+=current_pattern;	}
				if(result.contains(pattern_n))
				{
					max_consecutive_appereance++;
				}
				else
				{
					finished=true;
				}
			}
			for(int k=max_consecutive_appereance-1;k>0;k--)
			{
				String pattern_n="";
				for(int l=0;l<k;l++){	pattern_n+=current_pattern;	}
				if(result.contains(pattern_n))
				{
					if(k>1)
					{
						if(show_times==true)
						{
							result=result.replace(pattern_n,current_replacement+prefix+k+suffix);
						}
						else
						{
							result=result.replace(pattern_n,current_replacement+prefix+suffix);
						}
					}
					else
					{
						result=result.replace(current_pattern,current_replacement);
					}
				}
			}
		}
		return result;
	}
	//-----------------------------------------------------------------------------------------------------------------------
}
