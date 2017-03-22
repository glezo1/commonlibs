package com.glezo.languageDetector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.tika.language.LanguageIdentifier;

/*
	Just a org.apache.tika server one-line static wrapper
*/

@SuppressWarnings("deprecation")
public class LanguageDetector 
{
	public static class Tuple_String_Integer implements Comparable<Tuple_String_Integer>
	{
		private String	string;
		private Integer	integer;
		public Tuple_String_Integer(String s,Integer i)
		{
			this.string=s;
			this.integer=i;
		}
		public String	getString()							{	return this.string;							}
		public Integer	getInteger()						{	return this.integer;						}
		public int		compareTo(Tuple_String_Integer t)	{	return this.integer.compareTo(t.integer);	}
	}
	//----------------------------------------------------------------
	public static String	detect_language(String content)
	{
		if(content==null)
		{
			return null;
		}
		return new LanguageIdentifier(content).getLanguage();
	}
	//----------------------------------------------------------------
	public static String	detect_language(String ...contents)
	{
		HashMap<String,Integer> match=new HashMap<String,Integer>();
		for(String s : contents)
		{
			String current_content_language=LanguageDetector.detect_language(s);
			if(match.containsKey(current_content_language))
			{
				Integer i=match.get(current_content_language)+1;
				match.remove(current_content_language);
				match.put(current_content_language,i);
			}
			else
			{
				match.put(current_content_language,1);
			}
		}
		Set<Entry<String,Integer>> match_set=match.entrySet();
		ArrayList<Tuple_String_Integer> sorted_matches=new ArrayList<Tuple_String_Integer>();
		for(Entry<String,Integer> e : match_set)
		{
			sorted_matches.add(new Tuple_String_Integer(e.getKey(),e.getValue()));
		}
		Collections.sort(sorted_matches);
		if(sorted_matches.size()==0)	{	return null;													}
		else							{	return sorted_matches.get(sorted_matches.size()-1).getString();	}
	}
	//----------------------------------------------------------------
}
