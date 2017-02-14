package com.glezo.htmlWrapper;

public class HtmlProxy 
{
	private String	address;
	private int		port;
	private String	user;
	private String	password;
	
	public HtmlProxy(String address,int port,String user,String password)
	{
		this.address	=address;
		this.port		=port;
		this.user		=user;
		this.password	=password;
		System.setProperty("http.proxyHost",this.address);
		System.setProperty("https.proxyHost",this.address);
		System.setProperty("http.proxyPort",Integer.toString(this.port));
		System.setProperty("https.proxyPort",Integer.toString(this.port));
		if(this.user!=null)
		{
			System.getProperties().put("http.proxyUser",this.user);
			System.getProperties().put("https.proxyUser",this.user);
		}
		if(this.password!=null)
		{
			System.getProperties().put("http.proxyPassword",this.password);
			System.getProperties().put("https.proxyPassword",this.password);
		}
	}
}
