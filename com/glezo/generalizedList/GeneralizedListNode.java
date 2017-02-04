package com.glezo.generalizedList;

//T must equals(), no actually need for compareTo()
public class GeneralizedListNode<T> 
{
	private boolean					is_list;
	private T						data;
	private GeneralizedListNode<T>	next_brother;
	private GeneralizedListNode<T>	father;
	private GeneralizedListNode<T>	son;

	public GeneralizedListNode(boolean is_list,T data,GeneralizedListNode<T> son,GeneralizedListNode<T> father)
	{
		this.next_brother	=null;
		this.son			=null;
		this.is_list		=is_list;
		this.data			=data;
		if(this.is_list)
		{
			this.son=son;
		}
		this.father			=father;
	}

	public boolean	is_list()									{	return this.is_list;		}
	public T		get_data()									{	return data;				}
	public GeneralizedListNode<T>	get_father()				{	return this.father;			}
	public GeneralizedListNode<T>	get_next_brother()			{	return this.next_brother;	}
	public GeneralizedListNode<T>	get_son()					{	return this.son;			}
	public void		set_data(T data)							{	this.data=data;				} 
	public void		set_next_brother(GeneralizedListNode<T> n) 	{	this.next_brother=n;		}
	public void		set_son(GeneralizedListNode<T> n)			{	this.son=n;					}

	//just for debugging purpose
	public String toString()
	{
		String result="";
		//if(this.is_list)	{	result+="[L]\t";	}
		//else				{	result+="[E]\t";	}
		result+=this.data;
		return result;
	}
    
}

