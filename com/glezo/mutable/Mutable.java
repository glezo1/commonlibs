package com.glezo.mutable;

public class Mutable<T> 
{
	private T	t;
	
	public Mutable(T t)		{	this.t=t;		}
	public T	get()		{	return this.t;	}
	public void	set(T t)	{	this.t=t;		}
}
