package com.glezo.queueSet;

import java.util.ArrayList;

public class QueueSet<T>
{
	private ArrayList<T>	queue;
	
	//-------------------------------------------------------------------------------------------------------
	public QueueSet()						{	this.queue=new ArrayList<T>();								}
	//GETTERS------------------------------------------------------------------------------------------------
	public boolean contains(Object o)		{	return this.queue.contains(o);								}
	public boolean isEmpty()				{	return this.queue.isEmpty();								}
	public void add(T t)					{	if(!this.queue.contains(t)){this.queue.add(t);}				}
	public String toString()				{	return this.queue.toString();								}
	public ArrayList<T>	get_whole_queue()	{	return this.queue;											}	
	public T peek()							
	{	
		if(this.isEmpty())
		{
			return null;
		}
		return this.queue.get(0);	
	}
	public T poll()							
	{	
		if(this.isEmpty())
		{
			return null;
		}
		T t=this.queue.get(0);	
		this.queue.remove(0);	
		return t;	
	}
}
