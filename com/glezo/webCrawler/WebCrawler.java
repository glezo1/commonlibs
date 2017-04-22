package com.glezo.webCrawler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.glezo.htmlParser.GhettoHTMLParser;
import com.glezo.htmlParser.HtmlToken;
import com.glezo.htmlWrapper.HtmlProxy;
import com.glezo.htmlWrapper.HtmlWrapperReturn;
import com.glezo.parsedUrl.ParsedUrl;
import com.glezo.queueSet.QueueSet;
import com.glezo.tuples.N3Tuple;


public class WebCrawler 
{
	//Feel free to get greedy on threads, for they shall be blocked in IO:  
	//Sequential:	1 thread,	360 links in 00:15:49.845
	//Parallel:		8 threads,	360 links in 00:10:14.256	<--	8 threads seems nice for a small page 
	//Parallel:		16 threads,	360 links in 00:08:53.636
	//Anyway, the bottleneck lies upon the GhettoHTMLParser.
	
	private String										base_url;
	private ParsedUrl									base_url_parsed;
	private String										user_agent;
	private boolean										crawl_different_domains;
	private boolean 									crawl_different_subdomains;
	private int											num_threads;
	private int											num_retries;
	private ExecutorService								executor_service;
	CompletionService<CrawledUrl>						completion_service;
	
	private HtmlProxy									proxy;
	private InetSocketAddress							tor;
	
	private QueueSet<ParsedUrl>							already_visited;
	private QueueSet<ParsedUrl>							not_yet_visited;
	private QueueSet<ParsedUrl>							wont_be_visited;
	private QueueSet<N3Tuple<ParsedUrl,Integer,String>>	couldnt_be_visited;			//if failed after this.num_retries times 
	private ArrayList<ParsedUrl>						already_visited_template;	//maximum spanning tree. yep, of a tree. Pretty weird from a theoretical point of view, but that's what we need.
	private QueueSet<String>							directories_found;			//maximum spanning tree, keeping an entry for each and every segment of every tree branch. Pretty weird from a theoretical point of view, but that's what we need.
	
	//TODO! would be nice to parse js to retrieve form post data, $.ajax etc.
	
	//--------------------------------------------------------------------------------------------------------------------------
	public WebCrawler(String json_as_string)
	{
		try	
		{	
			JSONObject whole_json = new JSONObject(json_as_string);
			this.base_url					=(String)whole_json.get("base_url");
			this.base_url_parsed			=new ParsedUrl(this.base_url,null);
			this.num_threads				=(Integer)whole_json.get("num_threads");
			this.num_retries				=(Integer)whole_json.get("num_retries");
			this.executor_service			=Executors.newFixedThreadPool(this.num_threads);
			this.completion_service			=new ExecutorCompletionService<CrawledUrl>(this.executor_service);
			this.crawl_different_domains	=(Boolean)whole_json.get("crawl_different_domains");
			this.crawl_different_subdomains	=(Boolean)whole_json.get("crawl_different_subdomains");
			this.user_agent					=(String)whole_json.get("user_agent");
			this.already_visited			=new QueueSet<ParsedUrl>();
			this.not_yet_visited			=new QueueSet<ParsedUrl>();
			this.wont_be_visited			=new QueueSet<ParsedUrl>();
			this.couldnt_be_visited			=new QueueSet<N3Tuple<ParsedUrl,Integer,String>>();
			JSONArray av					=whole_json.getJSONArray("already_visited");
			JSONArray wv					=whole_json.getJSONArray("wont_be_visited");
			JSONArray nv					=whole_json.getJSONArray("not_yet_visited");
			for(int i=0;i<av.length();i++)	{	this.already_visited.add(new ParsedUrl((String)av.get(i),null));	}
			for(int i=0;i<nv.length();i++)	{	this.not_yet_visited.add(new ParsedUrl((String)nv.get(i),null));	}
			for(int i=0;i<wv.length();i++)	{	this.wont_be_visited.add(new ParsedUrl((String)wv.get(i),null));	}
			this.directories_found			=new QueueSet<String>();
			this.already_visited_template	=new ArrayList<ParsedUrl>();
			//non mandatory
			try
			{
				JSONArray cbv=whole_json.getJSONArray("couldnt_be_visited");
				for(int i=0;i<cbv.length();i++)	
				{	
					JSONObject cbv_i=(JSONObject) cbv.get(i);
					String p	=cbv_i.getString("parsed_url");
					int n		=cbv_i.getInt("num_retries");
					String r	=cbv_i.getString("reason");
					this.couldnt_be_visited.add(new N3Tuple<ParsedUrl,Integer,String>(new ParsedUrl(p,null),n,r));	
				}
			}
			catch(JSONException e){}
			//non mandatory
			try
			{
				JSONArray avt=whole_json.getJSONArray("already_visited_template");
				for(int i=0;i<avt.length();i++)	{	this.already_visited_template.add(new ParsedUrl((String)avt.get(i),null));	}
			}
			catch(JSONException e){}
			//non mandatory
			try
			{
				JSONArray df=whole_json.getJSONArray("directories_found");
				for(int i=0;i<df.length();i++)	{	this.directories_found.add((String)df.get(i));	}
			}
			catch(JSONException e){}
			this.proxy						=null;
			this.tor						=null;
			//non mandatory
			try{this.proxy=(HtmlProxy)whole_json.get("proxy");		}catch(JSONException e){}//TODO! not actually implemented
			//non mandatory
			try{this.tor=(InetSocketAddress)whole_json.get("proxy");}catch(JSONException e){}//TODO! not actually implemented
		}
		catch(org.json.JSONException e)
		{
			e.printStackTrace();
		}
	}
	//--------------------------------------------------------------------------------------------------------------------------
	public String										getBaseUrl()					{	return this.base_url;									}
	public ParsedUrl									getParsedBaseUrl()				{	return this.base_url_parsed;							}
	public String										getUserAgent()					{	return this.user_agent;									}
	public boolean										getCrawlDifferentDomains()		{	return this.crawl_different_domains;					}
	public boolean										getCrawlDifferentSubdomains()	{	return this.crawl_different_subdomains;					}
	public int											getNumThreads()					{	return this.num_threads;								}
	public int											getNumRetries()					{	return this.num_retries;								}
	public HtmlProxy									getProxy()						{	return this.proxy;										}
	public InetSocketAddress							getTor()						{	return this.tor;										}
	public QueueSet<ParsedUrl>							getAlreadyVisited()				{	return this.already_visited;							}
	public ArrayList<ParsedUrl>							getAlreadyVisitedTemplate()		{	return this.already_visited_template;					}
	public QueueSet<ParsedUrl>							getNotYetVisisted()				{	return this.not_yet_visited;							}
	public QueueSet<ParsedUrl>							getWontBeVisited()				{	return this.wont_be_visited;							}
	public QueueSet<N3Tuple<ParsedUrl,Integer,String>>	getCouldntBeVisited()			{	return this.couldnt_be_visited;							}
	public int											getNumberLinksPending()			{	return this.not_yet_visited.get_whole_queue().size();	}
	//---------------------------------------------------------------------------------------------------------------------------
	public float					getElapsedPercentage()
	{
		int visited=this.already_visited.size();
		int unvisited=this.wont_be_visited.size()+this.not_yet_visited.size();
		int wontvisited=this.wont_be_visited.size();
		int a=visited+wontvisited;
		int b=visited+wontvisited+unvisited;
		float result=a/(float)b;
		result=result*100;
		return result;
	}
	//-------------------------------------------------------------------------------------------------------------
	public String					toString()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DecimalFormat percentage_formatter = new DecimalFormat("000.000");
		String elapsed_percentage=percentage_formatter.format(this.getElapsedPercentage())+"%";
		String foo_a="already:"+String.format("%,d",this.already_visited.size());
		String foo_b="wont:"+String.format("%,d",this.wont_be_visited.size());
		String foo_c="pending:"+String.format("%,d",this.not_yet_visited.size());
		return sdf.format(new Date())+" "+elapsed_percentage+" "+foo_a+" "+foo_b+" "+foo_c;
	}
	//-------------------------------------------------------------------------------------------------------------
	public String					to_JSON_string()
	{
		String r="";
		r+="{\n";
		r+="	base_url:\""+this.base_url+"\"\n";
		r+="	,user_agent:\""+this.user_agent+"\"\n";
		r+="	,crawl_different_domains:"+this.crawl_different_domains+"\n";
		r+="	,crawl_different_subdomains:"+this.crawl_different_subdomains+"\n";
		r+="	,num_threads:"+this.num_threads+"\n";
		r+="	,num_retries:"+this.num_retries+"\n";
		r+="	,already_visited:[\n";
		for(int i=0;i<this.already_visited.size();i++)
		{
			r+="		";
			if(i!=0){r+=",";}
			r+="\""+this.already_visited.get_whole_queue().get(i).get_whole_path()+"\"\n";
		}
		r+="	]\n";
		r+="	,not_yet_visited:[\n";
		for(int i=0;i<this.not_yet_visited.size();i++)
		{
			r+="		";
			if(i!=0){r+=",";}
			r+="\""+this.not_yet_visited.get_whole_queue().get(i).get_whole_path()+"\"\n";
		}
		r+="	]\n";
		r+="	,wont_be_visited:[\n";
		for(int i=0;i<this.wont_be_visited.size();i++)
		{
			r+="		";
			if(i!=0){r+=",";}
			r+="\""+this.wont_be_visited.get_whole_queue().get(i).get_whole_path()+"\"\n";
		}
		r+="	]\n";
		r+="	,couldnt_be_visited:[\n";
		for(int i=0;i<this.couldnt_be_visited.size();i++)
		{
			r+="		";
			if(i!=0){r+=",";}
			r+="{\n";
			r+="			parsed_url:\""+this.couldnt_be_visited.get_whole_queue().get(i).getA().get_whole_path()+"\"\n";
			r+="			,num_retries:"+this.couldnt_be_visited.get_whole_queue().get(i).getB()+"\n";
			r+="			,reason:\""+this.couldnt_be_visited.get_whole_queue().get(i).getC()+"\"\n";
			r+="		}\n";
		}
		r+="	]\n";
		r+="	,already_visited_template:[\n";
		for(int i=0;i<this.already_visited_template.size();i++)
		{
			r+="		";
			if(i!=0){r+=",";}
			r+="\""+this.already_visited_template.get(i).get_whole_path()+"\"\n";
		}
		r+="	]\n";
		r+="	,directories_found:[\n";
		for(int i=0;i<this.directories_found.size();i++)
		{
			r+="		";
			if(i!=0){r+=",";}
			r+="\""+this.directories_found.get_whole_queue().get(i)+"\"\n";
		}
		r+="	]\n";
		r+="}\n";
		return r;
	}
	//-------------------------------------------------------------------------------------------------------------
	public void						parallel_compute()
	{
		Crawl_Profile profile=new Crawl_Profile(this.user_agent,"GET",30,30,true,this.num_retries,this.proxy,this.tor);
		while(!this.not_yet_visited.isEmpty())
		{
			int not_yet_visited=this.not_yet_visited.size();
			for(int i=0;i<not_yet_visited;i++)
			{
				ParsedUrl current_visit=this.not_yet_visited.poll();
				Crawling_Task current_task=new Crawling_Task(current_visit,profile);
				this.completion_service.submit(current_task);
			}
			for(int i=0;i<not_yet_visited;i++)
			{
				Future<CrawledUrl> future = null;
				try 
				{
					future = this.completion_service.take();
					CrawledUrl current_entry=future.get();
					if(current_entry.getContent().get_content_type()==null || current_entry.getContent().get_exception()!=null)
					{
						this.couldnt_be_visited.add(new N3Tuple<ParsedUrl,Integer,String>(current_entry.getParsedUrl(),current_entry.getContent().get_num_tries_taken(),current_entry.getContent().get_exception().toString()));
					}
					else
					{
						this.treat_visited_link(current_entry.getParsedUrl());
						if(current_entry.getContent().get_content_type().contains("text/html"))
						{
							GhettoHTMLParser parser=new GhettoHTMLParser(current_entry.getContent().get_raw_return());
							ArrayList<HtmlToken> links_found=parser.jquery_selector2("[href]",null);
							ArrayList<HtmlToken> iframes	=parser.jquery_selector2("iframe[src]",null);
							links_found.addAll(iframes);
							for(int j=0;j<links_found.size();j++)
							{
								HtmlToken current_token=links_found.get(j);
								String link_aux=null;
								int index_a=current_token.get_properties_names().indexOf("href");
								int index_b=current_token.get_properties_names().indexOf("src");
								if(index_a!=-1)		{	link_aux=current_token.get_properties_values().get(index_a);	}
								else if(index_b!=-1){	link_aux=current_token.get_properties_values().get(index_b);	}
								try
								{
									ParsedUrl url_aux=new ParsedUrl(link_aux,null);
									this.treat_potentially_unvisited_link(url_aux);
								}
								catch(Exception e){}	//malformed url. nevermind.
							}
						}
					}
				} 
				catch (InterruptedException e) 
				{
				} 
				catch (ExecutionException e) 
				{
				}
			}
		}
	}
	//-------------------------------------------------------------------------------------------------------------
	public void						sequential_compute()
	{
		Crawl_Profile profile=new Crawl_Profile(this.user_agent,"GET",30,30,true,this.num_retries,this.proxy,this.tor);
		while(!this.not_yet_visited.isEmpty())
		{
			ParsedUrl current_url=this.not_yet_visited.poll();
			try 
			{
				CrawledUrl crawled_url=new Crawling_Task(current_url, profile).call();
				HtmlWrapperReturn result=crawled_url.getContent();
				if(result.get_content_type()==null || result.get_exception()!=null)
				{
					this.couldnt_be_visited.add(new N3Tuple<ParsedUrl,Integer,String>(current_url,result.get_num_tries_taken(),result.get_exception().toString()));
				}
				else
				{
					this.treat_visited_link(current_url);
					if(result.get_content_type().contains("text/html"))
					{
						GhettoHTMLParser parser=new GhettoHTMLParser(result.get_raw_return());
						ArrayList<HtmlToken> links_found=parser.jquery_selector2("[href]",null);
						ArrayList<HtmlToken> iframes	=parser.jquery_selector2("iframe[src]",null);
						links_found.addAll(iframes);
						for(int j=0;j<links_found.size();j++)
						{
							HtmlToken current_token=links_found.get(j);
							String link_aux=null;
							int index_a=current_token.get_properties_names().indexOf("href");
							int index_b=current_token.get_properties_names().indexOf("src");
							if(index_a!=-1)		{	link_aux=current_token.get_properties_values().get(index_a);	}
							else if(index_b!=-1){	link_aux=current_token.get_properties_values().get(index_b);	}
							try
							{
								ParsedUrl url_aux=new ParsedUrl(link_aux,null);
								this.treat_potentially_unvisited_link(url_aux);
							}
							catch(Exception e){}	//malformed url. nevermind.
						}
					}
				}
			} 
			catch (MalformedURLException e) {}
			catch (ProtocolException e)		{}
			catch (IOException e)			{}
			catch (ClassCastException e)	{}	//mailto:whatever in HtmlWrapper constructor
			catch (Exception e)				{}	//since we use a callable
		}
	}
	//-------------------------------------------------------------------------------------------------------------
	private void					treat_visited_link(ParsedUrl link)
	{
		this.already_visited.add(link);
		
		//this.directories_found
		String tokens[]=link.get_server_side_path().split("/");
		String acum="";
		if(link.get_protocol()!=null)	{acum+=link.get_protocol()+"://";	}
		if(link.get_subdomain()!=null)	{acum+=link.get_subdomain()+".";	}
		if(link.get_domain()!=null)		{acum+=link.get_domain();			}
		if(link.get_tld()!=null)		{acum+="."+link.get_tld();			}
		for(int j=0;j<tokens.length-1;j++) //length-1 to avoid the target itself
		{
			String current_token=tokens[j];
			if(!current_token.equals(""))
			{
				acum+="/"+current_token;
				this.directories_found.add(acum);
				//there could be no direct link to that retrieved folder, which might be browser-able:
				ParsedUrl potentially_unvisited_folder=new ParsedUrl(acum,null);
				this.not_yet_visited.add(potentially_unvisited_folder);
			}
		}
		//already_visited_template
		boolean matches_but_params=false;
		for(int i=0;i<this.already_visited.size();i++)
		{
			ParsedUrl current_already_visited=this.already_visited.get_whole_queue().get(i);
			if(!current_already_visited.equals(link) && current_already_visited.equals_but_param_values(link)) //if exactly equals, no point
			{
				new ParsedUrl(link.get_whole_path(),null);//debug
				matches_but_params=true;
				//now, if it matches: do we take the brand-new visited, or the previously visited? the "greatest" (in terms of parameters)
				//beware! what if... a.php?a=10&b=11	vs	a.php?z=34&w=33		?
				//TODO! upper statement is true, but not then only one. Hic sunt dracones. 
				int brand_new_visit_param_number=link.get_server_side_param_names().size();
				int previous_visit_param_number	=current_already_visited.get_server_side_param_names().size();
				if(brand_new_visit_param_number > previous_visit_param_number)
				{
					this.already_visited.get_whole_queue().remove(i);
					this.already_visited.get_whole_queue().add(link);
				}
			}
		}
		if(!matches_but_params)
		{
			this.already_visited_template.add(link);
		}
	}
	//-------------------------------------------------------------------------------------------------------------
	private CrawlChanges			treat_potentially_unvisited_link(ParsedUrl link)
	{
		CrawlChanges changes=new CrawlChanges();
		if(this.already_visited.contains(link) || this.wont_be_visited.contains(link))
		{
			return changes;
		}
		if(link.is_relative())
		{
			this.not_yet_visited.add(link);
			changes.add_not_yet_visited(link);
			return changes;
		}
		String this_domain			=this.getParsedBaseUrl().get_domain();
		String this_subdomain		=this.getParsedBaseUrl().get_subdomain();
		String other_domain			=link.get_domain();
		String other_subdomain		=link.get_subdomain();
		if(this_subdomain==null)	{	this_subdomain="";	}
		if(other_subdomain==null)	{	other_subdomain="";	}
		boolean domain_equals		=this_domain.equals(other_domain);
		boolean subdomain_equals	=this_subdomain.equals(other_subdomain);
		if(this.crawl_different_domains)
		{
			this.not_yet_visited.add(link);
			changes.add_not_yet_visited(link);
		}
		else if(domain_equals)
		{
			if(this.crawl_different_subdomains)
			{
				this.not_yet_visited.add(link);
				changes.add_not_yet_visited(link);
			}
			else if(subdomain_equals)
			{
				this.not_yet_visited.add(link);
				changes.add_not_yet_visited(link);
			}
			else
			{
				this.wont_be_visited.add(link);
				changes.add_wont_be_visited(link);
			}
		}
		else
		{
			this.wont_be_visited.add(link);
			changes.add_wont_be_visited(link);
		}
		return changes;
	}
	//--------------------------------------------------------------------------------------------------------------
}
