package com.glezo.webCrawler;

import java.net.InetSocketAddress;

import com.glezo.htmlWrapper.HtmlProxy;

public class Crawl_Profile 
{
	private String				user_agent;
	private String				method;
	private int					connect_timeout;
	private int					read_timeout;
	private boolean				follow_redirect;
	private	int					num_retries;
	private HtmlProxy			proxy;
	private InetSocketAddress	tor;

	public Crawl_Profile(String user_agent,String method,int connect_timeout,int read_timeout,boolean follow_redirect,int num_retries,HtmlProxy proxy,InetSocketAddress tor)
	{
		this.user_agent	=user_agent;
		this.method		=method;
		this.connect_timeout	=connect_timeout;
		this.read_timeout		=read_timeout;
		this.follow_redirect	=follow_redirect;
		this.num_retries		=num_retries;
		this.proxy				=proxy;
		this.tor				=tor;
	}
	public String				getUserAgent()		{	return this.user_agent;			}
	public String				getMethod()			{	return this.method;				}
	public int					getConnectTimeout()	{	return this.connect_timeout;	}
	public int					getReadTimeout()	{	return this.read_timeout;		}
	public boolean				getFollowRedirect()	{	return this.follow_redirect;	}
	public int					getNumRetries()		{	return this.num_retries;		}
	public HtmlProxy			getProxy()			{	return this.proxy;				}
	public InetSocketAddress	getTor()			{	return this.tor;				}
}
