package com.glezo.language;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class Language_permutations extends Language
{
	private Alphabet	alphabet;
	private int			current_string_state[];
	private int			word_length;
	private String		current_middle_part;
	//--------------------------------------------------------------------------------------
	public Language_permutations(String letters,int length)
	{
		this.alphabet				=new Alphabet(letters,' ');
		this.word_length			=length;
		this.current_string_state	=new int[this.word_length];
		for(int i=0;i<this.current_string_state.length;i++)
		{
			this.current_string_state[i]=0;
		}
		this.current_middle_part=this.getBaseCase();
	}
	//GETTERS-----------------------------------------------------------------------
	public Alphabet	getAlphabet()				{	return this.alphabet;													}
	public double	getNumberOfWords()			{	return Math.pow(this.alphabet.getNumberOfLetters(),this.word_length);	}
	public double	getNumberOfBytes()			{	return Math.pow(this.word_length+1,this.getNumberOfWords());			}	
	//SETTERS-----------------------------------------------------------------------
	public void		setAlphabet(Alphabet a)		{	this.alphabet=a;																					}
	public void		setLength(int newLength)	{	this.word_length=newLength;	for(int i=0;i<this.word_length;i++){this.current_string_state[i]=0;}	}
	//OTHERS--------------------------------------------------------------------
	public boolean	belongsToAlphabet(char entryChar)
	{
		return this.alphabet.belongsTo(entryChar);
	}
	//--------------------------------------------------------------------------
	public String	getBaseCase()
	{
		String result="";
		char firstChar=this.alphabet.getFirstChar();
		for(int i=0;i<this.word_length;i++)
		{
			result+=firstChar;
		}
		return result;
	}
	//--------------------------------------------------------------------------
	public String	iterate(String current)
	{
		char first=this.alphabet.getFirstChar();
		char last=this.alphabet.getLastChar();
		//current is JUST the variable part:
		int current_length=current.length();
		for(int i=current_length-1;i>=0;i--)
		{
			if(current.charAt(i)!=last)
			{
				String a=current.substring(0,i);
				String c=current.substring(i+1,current_length);
				String result=a+this.alphabet.getNextChar(this.current_string_state[i])+c;
				this.current_string_state[i]++;
				return result;
			}
			else
			{
				current=current.substring(0,i)+first+current.substring(i+1,current.length());
				this.current_string_state[i]=0;
			}
		}
		return null;
	}
	//--------------------------------------------------------------------------
	public ArrayList<String>	getWholeDictionary()
	{
		ArrayList<String> result=new ArrayList<String>();
		double wordNumber=this.getNumberOfWords();
		double wordsWritten=0;
		String current_word=this.getBaseCase();
		do
		{
			result.add(new String(current_word));
			current_word=this.iterate(current_word);
			wordsWritten++;
		}while(wordsWritten<wordNumber);
		return result;
	}
	//--------------------------------------------------------------------------
	public void					saveDictionaryToFile(String output_file_path)
	{
		FileWriter fw;
		try 
		{
			fw = new FileWriter(output_file_path);
			double wordNumber=this.getNumberOfWords();
			double wordsWritten=0;
			String current_word=this.getBaseCase();
			do
			{
				current_word=new String(current_word);
				fw.write(current_word+"\n");
				current_word=this.iterate(current_word);
				wordsWritten++;
			}while(wordsWritten<wordNumber);
			fw.close();
		} 
		catch (IOException e) 
		{
		}
	}
	//--------------------------------------------------------------------------
	public String				getNextDicionaryWord()
	{
		if(this.current_middle_part==null)
		{
			return null;
		}
		String result=this.current_middle_part;
		this.current_middle_part=this.iterate(this.current_middle_part);
		return result;
	}
	//--------------------------------------------------------------------------
}
