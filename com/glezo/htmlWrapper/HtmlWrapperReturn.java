package com.glezo.htmlWrapper;


import java.util.ArrayList;


public class HtmlWrapperReturn 
{
	private int						return_code;	
	private String					redirects_to;
	private String					content_type;
	private String					raw_return;
	private ArrayList<String>		lines_return;
	private Exception				exception;
	
	
	public HtmlWrapperReturn(int retcode,String redirects_to,String content_type,String raw_return,Exception exception)
	{
		this.return_code	=retcode;
		this.redirects_to	=redirects_to;
		this.content_type	=content_type;
		this.raw_return		=raw_return;
		if(this.raw_return==null)
		{
			this.lines_return=null;
		}
		else
		{
			//lines
			String lines[]=this.raw_return.split("\n");
			this.lines_return=new ArrayList<String>();
			for(int i=0;i<lines.length;i++)
			{
				this.lines_return.add(lines[i]);
			}
		}
		this.exception		=exception;
	}
	public HtmlWrapperReturn(String raw_return,ArrayList<String> lines_return,ArrayList<String> tokens_return,Exception exception)
	{
		this.raw_return		=raw_return;
		this.lines_return	=lines_return;
		this.exception		=exception;
	}
	public int					get_return_code()			{	return this.return_code;			}
	public String				get_redirect_to()			{	return this.redirects_to;			}
	public String				get_content_type()			{	return this.content_type;			}
	public String				get_raw_return()			{	return this.raw_return;				}
	public ArrayList<String>	get_lines_return()			{	return this.lines_return;			}
	public Exception			get_exception()				{	return this.exception;				}
}
