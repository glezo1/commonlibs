package com.glezo.executableInterfaces.rsf;

import java.util.ArrayList;

public class RSF_Retrieved_Info 
{
	private String				user;
	private String				password;
	private boolean				got_cmd;
	private String				auth_bypass_suffix;
	private ArrayList<String>	cmd_command_output;
	
	public RSF_Retrieved_Info(String user,String password,String auth_bypass_suffix,boolean got_cmd,ArrayList<String> cmd_command_output)
	{
		this.user				=user;
		this.password			=password;
		this.got_cmd			=got_cmd;
		this.auth_bypass_suffix	=auth_bypass_suffix;
		this.cmd_command_output	=cmd_command_output;
	}
	public String				getUser()				{	return this.user;				}
	public String				getPassword()			{	return this.password;			}
	public boolean				getCmd()				{	return this.got_cmd;			}
	public String				getAuthBypassSuffix()	{	return this.auth_bypass_suffix;	}
	public ArrayList<String>	getCommandOutput()		{	return this.cmd_command_output;	}
}
