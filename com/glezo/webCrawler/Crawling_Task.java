package com.glezo.webCrawler;

import java.util.Date;
import java.util.concurrent.Callable;

import com.glezo.htmlWrapper.HtmlWrapper;
import com.glezo.htmlWrapper.HtmlWrapperReturn;
import com.glezo.parsedUrl.ParsedUrl;

public class Crawling_Task implements Callable<CrawledUrl> 
{
	private ParsedUrl		url_parsed;
	private Crawl_Profile	profile;
	
	public Crawling_Task(ParsedUrl url_parsed,Crawl_Profile profile)
	{
		this.url_parsed=url_parsed;
		this.profile=profile;
	}
	public CrawledUrl call() throws Exception 
	{
		HtmlWrapper html_wrapper=new HtmlWrapper(this.url_parsed.get_whole_path()
												,this.profile.getUserAgent()
												,this.profile.getMethod()
												,this.profile.getConnectTimeout()
												,this.profile.getReadTimeout()
												,this.profile.getFollowRedirect()
												,this.profile.getProxy()
												,this.profile.getTor());
		HtmlWrapperReturn result=html_wrapper.get_html(this.profile.getNumRetries());
		CrawledUrl current_entry=new CrawledUrl(this.url_parsed,result,new Date());
		return current_entry;
	}

}
