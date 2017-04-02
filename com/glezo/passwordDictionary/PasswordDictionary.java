package com.glezo.passwordDictionary;

import java.util.ArrayList;

public class PasswordDictionary 
{
	private String				description;
	private ArrayList<String>	words;
	private String				notes;
	
	public PasswordDictionary(String description,ArrayList<String> words,String notes)
	{
		this.description	=description;
		this.words			=words;
		this.notes			=notes;
	}
	public String				getDescription()	{	return this.description;	}
	public ArrayList<String>	getWords()			{	return this.words;			}
	public String				getNotes()			{	return this.notes;			}
}
