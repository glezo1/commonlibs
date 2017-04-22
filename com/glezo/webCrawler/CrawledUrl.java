package com.glezo.webCrawler;

import java.util.Date;

import com.glezo.htmlWrapper.HtmlWrapperReturn;
import com.glezo.parsedUrl.ParsedUrl;

public class CrawledUrl 
{
	private ParsedUrl			parsedUrl;
	private HtmlWrapperReturn	content;
	private Date				crawled_timestamp;
	
	public CrawledUrl(ParsedUrl parsedUrl,HtmlWrapperReturn content,Date date)
	{
		this.parsedUrl			=parsedUrl;
		this.content			=content;
		this.crawled_timestamp	=date;
	}
	public ParsedUrl			getParsedUrl()	{	return this.parsedUrl;				}
	public HtmlWrapperReturn	getContent()	{	return this.content;				}
	public Date					getTimestamp()	{	return this.crawled_timestamp;		}
	public int					hashCode()		{	return this.parsedUrl.hashCode();	}
	public boolean				equals(Object o)
	{
		if(o==null)
		{
			return false;
		}
		else
		{
			if(this.getClass()!=o.getClass())
			{
				return false;
			}
			CrawledUrl oo=(CrawledUrl)o;
			return this.parsedUrl.equals(oo.getParsedUrl());
		}
	}
	
}
