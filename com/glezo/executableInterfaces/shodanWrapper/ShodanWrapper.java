package com.glezo.executableInterfaces.shodanWrapper;

import java.io.IOException;
import java.util.ArrayList;

import com.glezo.ipv4.Ipv4;
import com.glezo.ipv4.UnparseableIpv4Exception;
import com.glezo.systemCommandExecutor.*;


public class ShodanWrapper 
{
	private final static String	shodan_wrapper_path="/root/shodan_wrapper.py";
	
	public static ArrayList<Ipv4>	query_shodan(String api_key,String search,String country,String org,String port)
	{
		ArrayList<String> command=new ArrayList<String>();
		command.add("python");
		command.add(shodan_wrapper_path);
		if(country!=null)
		{
			command.add("-country");
			command.add(country);
		}
		if(org!=null)
		{
			command.add("-org");
			command.add("\""+org+"\"");
		}
		if(port!=null)
		{
			command.add("-port");
			command.add(port);
		}
		command.add("-apikey");
		command.add(api_key);
		if(search!=null)
		{
			command.add("-search");
			command.add(search);
		}
		
		SystemCommandExecutor sce=new SystemCommandExecutor(command);
		try 
		{
			sce.executeCommand();
		} 
		catch (IOException e)			{}
		catch (InterruptedException e)	{}
		ArrayList<Ipv4> result=new ArrayList<Ipv4>();
		String stdout=sce.getStandardOutputFromCommand().toString();
		String lines[]=stdout.split("\n");
		for(int i=0;i<lines.length;i++)
		{
			String current_line=lines[i];
			if(!current_line.startsWith("#"))
			{
				try 
				{
					result.add(new Ipv4(current_line));
				} 
				catch (UnparseableIpv4Exception e)	{}
			}
		}
		return result;
	}
}
