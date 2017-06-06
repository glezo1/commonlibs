package com.glezo.abstractFileSystem;

import java.util.ArrayList;

import com.glezo.generalizedList.GeneralizedList;
import com.glezo.generalizedList.GeneralizedListNode;

/*
	so what on earth is this?
	in quite some occasions I have needed an abstract representation of different file-system(ish)
	e.g.: mtp devices, ptp devices, exploring a remote ext4 after getting a shell, etc. 

*/

public class AbstractFileSystem 
{
	private GeneralizedList<AbstractFileSystemEntry>	root_node;
	private int											number_of_files;
	private int											number_of_directories;
	
	public AbstractFileSystem(AbstractFileSystemEntry root)
	{
		this.root_node	=	new GeneralizedList<>();
		GeneralizedListNode<AbstractFileSystemEntry>	root_node_node=new GeneralizedListNode<AbstractFileSystemEntry>(true,root,null,null);
		this.root_node.set_root_node(root_node_node);
		this.number_of_directories=0;
		this.number_of_files=0;
	}
	//-----------------------------------------------------------------------------------------------
	public int	get_number_of_files()		{	return this.number_of_files;		}
	public int	get_number_of_directories()	{	return this.number_of_directories;	}
	//-----------------------------------------------------------------------------------------------
	public void add_entry(AbstractFileSystemEntry e)
	{
		if(e.is_directory())	{	this.number_of_directories++;	}
		else					{	this.number_of_files++;			}
		
		String parts[]=e.get_path().split("/");
		GeneralizedListNode<AbstractFileSystemEntry> pointer=this.root_node.get_root_node();
		for(int i=1;i<parts.length;i++)
		{
			String current_folder=parts[i];
			if(i+1==parts.length)
			{
				//need to add
				boolean is_dir=e.is_directory();
				GeneralizedListNode<AbstractFileSystemEntry> new_node=new GeneralizedListNode<AbstractFileSystemEntry>(is_dir,e,null,pointer);
				if(pointer.get_son()==null)
				{
					pointer.set_son(new_node);
				}
				else
				{
					pointer=pointer.get_son();
					while(pointer.get_next_brother()!=null)
					{
						pointer=pointer.get_next_brother();
					}
					pointer.set_next_brother(new_node);
				}
			}
			else
			{
				//follow the path
				pointer=pointer.get_son();
				boolean finished=false;
				while(!finished)
				{
					if(pointer.get_data().get_name().equals(current_folder))
					{
						finished=true;
					}
					else
					{
						pointer=pointer.get_next_brother();
					}
				}
			}
		}
	}
	//-----------------------------------------------------------------------------------------------
	//debug, mostly
	public String toString()
	{
		String result="";
		ArrayList<AbstractFileSystemEntry> in_order=this.root_node.in_order();
		for(int i=0;i<in_order.size();i++)
		{
			result+="\n";
			result+=in_order.get(i).toString();
		}
		//remove first, dummy \n
		result=result.substring(1);
		return result;
	}
	//-----------------------------------------------------------------------------------------------
	 
}
