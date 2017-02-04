package com.glezo.generalizedList;


import java.util.ArrayList;

/*
	A list, where each node might be a leaf node or a list itselfs.
	It might be used as a tree implementation
 	TODO! I should've implemented pre-order and post-order functionatlity, but I won't untill I actually need it.
*/

//T must equals(), no actually need for compareTo()
public class GeneralizedList<T extends Comparable<T>>
{
	private GeneralizedListNode<T> root;
	
	//TODO! recorridos pre y post
	
	public GeneralizedList()
	{
		this.root=null;
	}
	
	public GeneralizedListNode<T>	get_root_node()							{	return this.root;		}
	public boolean					is_empty()								{	return this.root==null;	}
	public void						set_root_node(GeneralizedListNode<T> n)	{	this.root=n;			}

	//----------------------------------------------------------------------------------------------
	public ArrayList<T>								in_order()
	{
		ArrayList<T> result=new ArrayList<T>();
		this.in_order(this.root,result);
		return result;
	}
	//----------------------------------------------------------------------------------------------
	public ArrayList<T>								in_order(T e)
	{
		ArrayList<T> result=new ArrayList<T>();
		ArrayList<GeneralizedListNode<T>> references_to_e=this.get_node_references_to_element(e);
		for(int i=0;i<references_to_e.size();i++)
		{
			GeneralizedListNode<T> current_reference=references_to_e.get(i);
			this.in_order(current_reference,result);
		}
		return result;
	}
	//----------------------------------------------------------------------------------------------
	private void									in_order(GeneralizedListNode<T> root,ArrayList<T> result)
	{
		if(root==null)
		{
			return;
		}
		result.add(root.get_data());
		if(root.is_list() && root.get_son()!=null)
		{
			this.in_order(root.get_son(),result);
		}
		if(root.get_next_brother()!=null)
		{
			this.in_order(root.get_next_brother(),result);
		}
	}
	//----------------------------------------------------------------------------------------------
	private ArrayList<GeneralizedListNode<T>>		in_order_node()
	{
		ArrayList<GeneralizedListNode<T>> result=new ArrayList<GeneralizedListNode<T>>();
		this.in_order_node(this.root,result);
		return result;
	}
	//-------------------------------------------------------------------------------------------
	private void									in_order_node(GeneralizedListNode<T> root,ArrayList<GeneralizedListNode<T>> result)
	{
		if(root==null)
		{
			return;
		}
		result.add(root);
		if(root.is_list() && root.get_son()!=null)
		{
			this.in_order_node(root.get_son(),result);
		}
		if(root.get_next_brother()!=null)
		{
			this.in_order_node(root.get_next_brother(),result);
		}
	}
	//----------------------------------------------------------------------------------------------
	public ArrayList<GeneralizedListNode<T>>		get_node_references_to_element(T e)
	{
		ArrayList<GeneralizedListNode<T>> all_nodes=this.in_order_node();
		ArrayList<GeneralizedListNode<T>> result=new ArrayList<GeneralizedListNode<T>>();
		for(int i=0;i<all_nodes.size();i++)
		{
			GeneralizedListNode<T> current_node=all_nodes.get(i);
			if(current_node.get_data().equals(e))
			{
				result.add(current_node);
			}
		}
		return result;
	}
	//----------------------------------------------------------------------------------------------
	public ArrayList<T>								get_sons(T t)
	{
		ArrayList<T> result=new ArrayList<T>();
		ArrayList<GeneralizedListNode<T>> in_order=this.in_order_node();
		for(int i=0;i<in_order.size();i++)
		{
			if(in_order.get(i).get_father()!=null && in_order.get(i).get_father().get_data().equals(t))
			{
				result.add(in_order.get(i).get_data());
			}
		}
		return result;
	}
	//----------------------------------------------------------------------------------------------
	public ArrayList<T>								get_descendants(T e)
	{
		ArrayList<T> result=new ArrayList<T>();
		ArrayList<GeneralizedListNode<T>> references_to_e=this.get_node_references_to_element(e);
		for(int i=0;i<references_to_e.size();i++)
		{
			GeneralizedListNode<T> current_reference=references_to_e.get(i);
			result.add(current_reference.get_data());//yes, i know.
			this.in_order(current_reference.get_son(),result);
		}
		return result;
	}
	//----------------------------------------------------------------------------------------------
}
