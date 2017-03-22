package com.glezo.executableInterfaces.rsf;

import java.util.concurrent.Callable;

import com.glezo.ipv4.Ipv4;

public class RSF_Scanner_Task implements Callable<RSF_Scanner_Result>
{
	private Ipv4			ip;
	private String			scanner;
	private int				i;
	private int				total;

	public RSF_Scanner_Task(int i,int total_number,Ipv4 ip,String scanner)	
	{
		this.ip			=ip;
		this.scanner	=scanner;
		this.i			=i;
		this.total		=total_number;
	}
	public Ipv4					getIp()				{	return this.ip;			}
	public String				getScanner()		{	return this.scanner;	}
	public int					getI()				{	return this.i;			}
	public int					getTotal()			{	return this.total;		}
	public RSF_Scanner_Result	call() 
	{
		return RSF.scanner(this.i,this.total,this.ip,this.scanner);
	}

}
