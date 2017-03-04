package com.glezo.htmlWrapper;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HtmlWrapper 
{
	private String					url_string;
	private String					user_agent;
	private String					method;
	private Integer					connect_timeout;
	private Integer					read_timeout;
	private Boolean					follow_redirect;
	private HashMap<String,String>	properties;
	private HtmlProxy				proxy;
	private InetSocketAddress		tor;
	
	private URL						url;
	private HttpURLConnection		connection;
	//-------------------------------------------------------------------------------------------------------------------------
	public HtmlWrapper(		String url_string,
							String user_agent,
							String method,
							int connect_timeout,
							int read_timeout,
							boolean follow_redirect,
							HtmlProxy proxy,
							InetSocketAddress tor
						) 
			throws MalformedURLException,ProtocolException,IOException
	{
		this.url_string		=url_string;
		this.user_agent		=user_agent;
		this.method			=method;
		this.connect_timeout=connect_timeout;
		this.read_timeout	=read_timeout;
		this.follow_redirect=follow_redirect;
		this.proxy			=proxy;
		this.tor			=tor;
		
		//Settings to accept ALL certificates
		TrustManager[] trustAllCerts = new TrustManager[]
		{
				new X509TrustManager() 
				{
					public java.security.cert.X509Certificate[] getAcceptedIssuers() 
					{
						return null;
					}
					public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) 
					{
					}
					public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) 
					{
					}
				}
		};
		try 
		{
			SSLContext sc = SSLContext.getInstance("SSL"); 
			sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} 
		catch (GeneralSecurityException e) 
		{
		} 
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
				new javax.net.ssl.HostnameVerifier(){
					public boolean verify(String hostname,javax.net.ssl.SSLSession sslSession) 
					{
						return true;
					}
				});
		
		this.url=new URL(this.url_string);
		if(this.tor!=null)
		{
			this.connection		=(HttpURLConnection)this.url.openConnection(new Proxy(Proxy.Type.SOCKS,this.tor));
		}
		else
		{
			this.connection		=(HttpURLConnection)this.url.openConnection();
		}
		this.connection.setRequestMethod(method);
		this.connection.setRequestProperty("User-Agent",this.user_agent);
		this.connection.setConnectTimeout(1000*this.connect_timeout);
		this.connection.setReadTimeout(1000*this.read_timeout);
		this.connection.setRequestProperty("User-Agent",this.user_agent);
		this.connection.setInstanceFollowRedirects(this.follow_redirect);
		this.properties=new HashMap<String,String>();
	}
	//-------------------------------------------------------------------------------------------------------------------------
	public HtmlProxy				getProxy()	{	return this.proxy;	}
	//-------------------------------------------------------------------------------------------------------------------------
	public void						setRequestProperty(String k,String v)					
	{	
		this.properties.put(k,v);
		this.connection.setRequestProperty(k,v);	
	}
	//-------------------------------------------------------------------------------------------------------------------------
	@SuppressWarnings("deprecation")
	public HtmlWrapperReturn		get_html(int num_retries)
	{
		int			response_code		=0;
		String		redirects_to		=null;
		String		content_type		=null;
		String		result				=null;
		Exception	exception			=null;
		
		try
		{
			if(this.tor==null)
			{
				this.connection.connect();
				response_code=this.connection.getResponseCode();
				redirects_to=this.connection.getHeaderField("Location");
				content_type=this.connection.getContentType();
				/*	I've seen things you people wouldn't believe. 
					Attack ships on fire off the shoulder of Orion.
					nginx 0.5.33 returning null/not null content type randomly for the same url when being crawled.
					All those moments will be lost in time, like tears in rain.
				*/
				if(content_type==null)
				{
					for(int i=0;i<num_retries && content_type==null;i++)
					{
						HttpURLConnection urlConnectionB = (HttpURLConnection)this.url.openConnection();
						urlConnectionB.setRequestProperty("User-Agent",this.user_agent);
						urlConnectionB.setRequestMethod(this.method);
						urlConnectionB.setConnectTimeout(1000*this.connect_timeout);
						urlConnectionB.setReadTimeout(1000*this.read_timeout);
						urlConnectionB.setInstanceFollowRedirects(this.follow_redirect);
						Iterator<Entry<String,String>> it=this.properties.entrySet().iterator();
						while(it.hasNext())
						{
							Entry<String,String> current_entry=it.next();
							urlConnectionB.setRequestProperty(current_entry.getKey(),current_entry.getValue());
						}
						urlConnectionB.connect();
						content_type=urlConnectionB.getContentType();
					}
				}
				else if(content_type.equals("audio/mpeg"))
				{
					
				}
				else
				{
					String normal_output=null;
					String error_output=null;
					try
					{
						DataInputStream dis=null;
						dis = new DataInputStream(this.connection.getInputStream());
						String inputLine = null;
						while ((inputLine = dis.readLine()) != null) 
						{
							if(normal_output==null)	{	normal_output=inputLine+"\n";	}
							else					{	normal_output+=inputLine+"\n";	}
						}
						dis.close();
						this.connection.disconnect();
					}
					catch(IOException e)
					{
					}
					try
					{
						DataInputStream dis=null;
						dis = new DataInputStream(this.connection.getErrorStream());
						String inputLine = null;
						while ((inputLine = dis.readLine()) != null) 
						{
							if(error_output==null)	{	error_output=inputLine+"\n";	}
							else					{	error_output+=inputLine+"\n";	}
						}
						dis.close();
						this.connection.disconnect();
					}
					catch(IOException e)
					{
					}
					catch(NullPointerException e)
					{
					}
					if(normal_output!=null)		{	result=normal_output;	}
					else						{	result=error_output;	}
				}
			}
			else
			{
				InputStream in = this.url.openConnection(new Proxy(Proxy.Type.SOCKS,this.tor)).getInputStream();
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				byte[] stuff = new byte[1024];
				int readBytes = 0;
				while((readBytes = in.read(stuff))>0) 
				{
					bout.write(stuff,0,readBytes);
				}
				byte[] result_byte_array = bout.toByteArray();
				result=new String(result_byte_array);
				//TODO!
				response_code=0;
				redirects_to=null;
				content_type=null;
				exception=null;
			}
		}				
		catch (MalformedURLException e) 
		{
			exception=e;
		}
		catch(ProtocolException e)
		{
			exception=e;
		}
		catch(SocketTimeoutException e)
		{
			exception=e;
		}
		catch(SocketException e)
		{
			exception=e;
		}
		catch(IOException e)
		{
			exception=e;
		}
		return new HtmlWrapperReturn(response_code,redirects_to,content_type,result,exception);
	}
	//-------------------------------------------------------------------------------------------------------------------------

}
