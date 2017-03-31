package com.glezo.language;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Language_prefix_middle_suffix implements Language
{
	private Alphabet	alphabet;
	private int			current_string_state[];
	private int			word_length;
	private String		prefix;
	private String		suffix;

	private String		current_middle_part;
	//--------------------------------------------------------------------------------------
	public Language_prefix_middle_suffix(String letters,String prefix,String suffix,int length) throws IllegalArgumentException
	{
		this.alphabet				=new Alphabet(letters,' ');
		this.prefix					=prefix;
		this.suffix					=suffix;
		this.word_length			=length;
		if(this.prefix.length()+this.suffix.length() > this.word_length)
		{
			throw new IllegalArgumentException("Prefix length + suffix length must <= word length");
		}
		this.current_string_state	=new int[this.word_length-this.prefix.length()-this.suffix.length()];
		for(int i=0;i<this.current_string_state.length;i++)
		{
			this.current_string_state[i]=0;
		}
		this.current_middle_part=this.getMiddleBaseCase();
	}
	//------------------------------------------------------------------------------
	public Alphabet	getAlphabet()				
	{	
		return this.alphabet;	
	}
	//------------------------------------------------------------------------------
	public long		getNumberOfWords()
	{
		int variable_part_length=this.word_length - this.prefix.length() - this.suffix.length();
		return (long)Math.pow(this.alphabet.getNumberOfLetters(),variable_part_length);
	}
	//------------------------------------------------------------------------------
	public long		getNumberOfBytes()
	{
		return (this.word_length+1) * this.getNumberOfWords();
	}	
	//------------------------------------------------------------------------------
	public void		setPrefix(String newPrefix)	{	this.prefix=newPrefix;	}
	public void		setSuffix(String newSuffix)	{	this.suffix=newSuffix;	}
	public void		setAlphabet(Alphabet a)		{	this.alphabet=a;		}
	public void		setLength(int newLength)	
	{
		this.word_length=newLength;
		for(int i=0;i<this.word_length;i++)
		{
			this.current_string_state[i]=0;
		}
	}
	//------------------------------------------------------------------------------
	public boolean	belongsToAlphabet(char entryChar)
	{
		return this.alphabet.belongsTo(entryChar);
	}
	//--------------------------------------------------------------------------
	public String	getBaseCase()
	{
		String result=this.prefix;
		int middleLength=this.word_length - this.prefix.length() - this.suffix.length();
		char firstChar=this.alphabet.getFirstChar();
		for(int i=0;i<middleLength;i++)
		{
			result+=firstChar;
		}
		result+=this.suffix;
		return result;
		
	}
	//--------------------------------------------------------------------------
	public String	getMiddleBaseCase()
	{
		String result="";
		for(int i=0;i<this.word_length-this.prefix.length()-this.suffix.length();i++)
		{
			result+=this.alphabet.getFirstChar();
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
		String current_word=this.getMiddleBaseCase();
		do
		{
			result.add(new String(this.prefix+current_word+this.suffix));
			//System.out.println(result.get(result.size()-1));
			//System.out.println(new String(this.prefix+current_word+this.suffix));
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
			String current_word=this.getMiddleBaseCase();
			do
			{
				current_word=new String(this.prefix+current_word+this.suffix);
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
		String result=this.prefix+this.current_middle_part+this.suffix;
		this.current_middle_part=this.iterate(this.current_middle_part);
		return result;
	}
	//--------------------------------------------------------------------------
}
