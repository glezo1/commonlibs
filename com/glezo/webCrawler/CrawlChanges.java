package com.glezo.webCrawler;

import java.util.ArrayList;

import com.glezo.parsedUrl.ParsedUrl;

public class CrawlChanges 
{
	private ArrayList<CrawledUrl>	not_yet_visited_to_visited;
	private ArrayList<ParsedUrl>	wont_be_visited;
	private ArrayList<ParsedUrl>	not_yet_visited;

	public CrawlChanges()
	{
		this.not_yet_visited_to_visited=new ArrayList<>();
		this.wont_be_visited=new ArrayList<>();
		this.not_yet_visited=new ArrayList<>();
	}
	public CrawlChanges(ArrayList<CrawledUrl> not_yet_visited_to_visited,ArrayList<ParsedUrl> wont_be_visited,ArrayList<ParsedUrl> not_yet_visited)
	{
		this.not_yet_visited_to_visited=not_yet_visited_to_visited;
		this.wont_be_visited=wont_be_visited;
		this.not_yet_visited=not_yet_visited;
	}
	public ArrayList<CrawledUrl>	get_not_yet_visited_to_visited()					{	return this.not_yet_visited_to_visited;			}
	public ArrayList<ParsedUrl>		get_wont_be_visited()								{	return this.wont_be_visited;					}
	public ArrayList<ParsedUrl>		get_not_yet_visited()								{	return this.not_yet_visited;					}
	
	public void 					add_not_yet_visited_to_visited(CrawledUrl u)		{	this.not_yet_visited_to_visited.add(u);			}
	public void 					add_wont_be_visited(ParsedUrl u)					{	this.wont_be_visited.add(u);					}
	public void 					add_not_yet_visited(ParsedUrl u)					{	this.not_yet_visited.add(u);					}
}
