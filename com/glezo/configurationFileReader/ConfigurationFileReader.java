package com.glezo.configurationFileReader;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;


public class ConfigurationFileReader 
{
	private String					configuration_file_path;
	private HashMap<String,String>	variables;
	
	//------------------------------------------------------------------------
	public ConfigurationFileReader(String configuration_file_path) throws FileNotFoundException,ParseException,IOException
	{
		this.configuration_file_path=configuration_file_path;
		this.variables=new HashMap<String,String>();
		File f=new File(this.configuration_file_path);
		BufferedReader br;
		br = new BufferedReader(new FileReader(f));
		String current_line = null;
		int current_line_number=0;
		while ((current_line = br.readLine()) != null) 
		{
			current_line_number++;
			String current_line_trimmed=current_line.trim();
			if(!current_line_trimmed.startsWith("#"))
			{
				int separator_position=current_line_trimmed.indexOf('=');
				if(separator_position==-1)
				{
					br.close();
					throw new ParseException("Unparseable line number "+current_line_number+":\t"+current_line,0);
				}
				String current_var_name		=	current_line_trimmed.substring(0,separator_position).trim();
				String current_var_value	=	current_line_trimmed.substring(separator_position+1,current_line_trimmed.length()).trim();
				if(current_var_value.equals(""))
				{
					br.close();
					throw new ParseException("Unparseable line number "+current_line_number+":\t"+current_line,0);
				}
				this.variables.put(current_var_name, current_var_value);
			}
		}
		br.close();
	}
	//------------------------------------------------------------------------
	public boolean	exists_variable(String variable_name)		{	return this.variables.get(variable_name)!=null;		}
	public String	get_variable_value(String variable_name)	{	return this.variables.get(variable_name);			}
	//------------------------------------------------------------------------
}
