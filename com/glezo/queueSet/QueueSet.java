package com.glezo.queueSet;

import java.util.ArrayList;
import java.util.HashSet;

public class QueueSet<T>
{
	private ArrayList<T>		queue;
	private HashSet<T>			hash_set;

	
	//-------------------------------------------------------------------------------------------
	public QueueSet()							
	{	
		this.queue		=new ArrayList<T>();
		this.hash_set	=new HashSet<T>();
	}
	//GETTERS------------------------------------------------------------------------------------
	public boolean		contains(T o)		
	{	
		return this.hash_set.contains(o);								
	}
	//-------------------------------------------------------------------------------------------
	public boolean		add(T t)				
	{	
		if(this.hash_set.contains(t))
		{
			return false;
		}
		this.hash_set.add(t);
		this.queue.add(t);
		return true;				
	}
	//-------------------------------------------------------------------------------------------
	public boolean		isEmpty()				{	return this.queue.isEmpty();				}
	public int			size()					{	return this.queue.size();					}
	public String		toString()				{	return this.queue.toString();				}
	public ArrayList<T>	get_whole_queue()		{	return this.queue;							}	
	//-------------------------------------------------------------------------------------------
	public T peek()							
	{	
		if(this.isEmpty())
		{
			return null;
		}
		return this.queue.get(0);
	}
	//--------------------------------------------------------------------------------------------
	public T poll()							
	{	
		if(this.isEmpty())
		{
			return null;
		}
		T t=this.queue.get(0);	
		this.queue.remove(0);
		this.hash_set.remove(t);
		return t;	
	}
	//-------------------------------------------------------------------------------------------
}
