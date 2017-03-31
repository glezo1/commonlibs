package com.glezo.alphabet;

public class Alphabet 
{
	private char[]	letters;
	private	char	description;
	private	int		number_of_letters;
	
	//---------------------------------------------------------------------
	public Alphabet(String letters,char description)
	{
		this.number_of_letters	=letters.length();
		this.description		=description;
		this.letters			=new char[letters.length()];
		for(int i=0;i<this.number_of_letters;i++)
		{
			this.letters[i]=letters.charAt(i);
		}
	}
	//---------------------------------------------------------------------
	public Alphabet(Alphabet anotherAlphabet)
	{
		this.description		=anotherAlphabet.description;
		this.number_of_letters	=anotherAlphabet.description;
		this.letters			=new char[anotherAlphabet.letters.length];
		for(int i=0;i<anotherAlphabet.number_of_letters;i++)
		{
			this.letters[i]=anotherAlphabet.letters[i];
		}
	}
	//GETTERS-------------------------------------------------------------------
	public char		getFirstChar()					{	return this.letters[0];														}
	public char		getLastChar()					{	return this.letters[this.number_of_letters-1];								}
	public int		getNumberOfLetters()			{	return this.number_of_letters;												}
	public char		getDescription()				{	return this.description;													}
	public char		getNextChar(int oldPosition)	{	oldPosition++;return this.letters[oldPosition % this.number_of_letters];	}			
	//OTHERS--------------------------------------------------------------------
	public boolean	belongsTo(char entryChar)
	{
		for(int i=0;i<this.number_of_letters;i++)
		{
			if(this.letters[i]==entryChar)
			{
				return true;
			}
		}
		return false;
	}
	//--------------------------------------------------------------------------
	public String	toString()
	{
		String result="";
		result+="Number of letters: "+Integer.toString(this.number_of_letters);
		result+="Letters          : ";
		for(int i=0;i<this.number_of_letters;i++)
		{
			result+=this.letters[i];
			if(i!=this.number_of_letters)
			{
				result+=",";
			}
		}
		return result;
	}
	//--------------------------------------------------------------------------
}
