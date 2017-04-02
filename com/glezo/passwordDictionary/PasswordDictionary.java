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
	public void					saveDictionaryToFile(String output_file_path)
	{
		FileWriter fw;
		try 
		{
			fw = new FileWriter(output_file_path);
			for(int i=0; i<this.words.size(); i++)
			{
				String current_word=this.words.get(i);
				fw.write(current_word+"\n");
			}
			fw.close();
		} 
		catch (IOException e) 
		{
		}
	}
}
