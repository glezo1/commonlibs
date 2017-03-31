package com.glezo.language;

import java.util.ArrayList;

public interface Language 
{
	abstract public long				getNumberOfWords();
	abstract public long				getNumberOfBytes();
	abstract public String				getNextDicionaryWord();
	abstract public void				saveDictionaryToFile(String output_file_path);
	abstract public ArrayList<String>	getWholeDictionary();
}
