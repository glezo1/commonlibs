package com.glezo.timeMeasuring;


import java.util.ArrayList;
import java.util.Date;
/*
	This class is aimed to be used an informal, raggedy, Visitor-ish way to measure times. 
	Just
	TimeMeasuring time_measurer=new TimeMeasuring();
	time_measurer.start_running("task1");
	task1_instance.run();
	time_measurer.stop_running("task1");
	
	there's a couple combine_* methods useful for parallel stuff.
	Since this was designed with just a few `activies` in mind, there's no need for Map, as the overhead accesing
	small ArrayList is pretty much (unless in my use case) insignificant.
	The code itself is pretty straight forward.
*/

public class TimeMeasuring 
{
	private	ArrayList<String>	activities;
	private	ArrayList<Boolean>	is_running;
	private	ArrayList<Long>		time_elapsed;
	private	ArrayList<Date>		last_start_time;
	
	//------------------------------------------------------------------
	public TimeMeasuring()
	{
		this.activities		=new ArrayList<String>();
		this.is_running		=new ArrayList<Boolean>();
		this.time_elapsed	=new ArrayList<Long>();
		this.last_start_time=new ArrayList<Date>();
	}
	//------------------------------------------------------------------
	public TimeMeasuring(ArrayList<String> activities)
	{
		this.activities		=new ArrayList<String>();
		this.is_running		=new ArrayList<Boolean>();
		this.time_elapsed	=new ArrayList<Long>();
		this.last_start_time=new ArrayList<Date>();
		
		for(int i=0;i<activities.size();i++)
		{
			this.activities.add(activities.get(i));
			this.is_running.add(new Boolean(false));
			this.time_elapsed.add(new Long(0));
			this.last_start_time.add(new Date());
		}
	}
	//------------------------------------------------------------------
	public TimeMeasuring(TimeMeasuring another)
	{
		this.activities=new ArrayList<String>(another.activities.size());
		this.is_running=new ArrayList<Boolean>(another.is_running.size());
		this.time_elapsed=new ArrayList<Long>(another.time_elapsed.size());
		this.last_start_time=new ArrayList<Date>(another.last_start_time.size());
		
		for(int i=0;i<another.activities.size();i++)
		{
			this.activities.add(new String(another.activities.get(i)));
		}
		for(int i=0;i<another.is_running.size();i++)
		{
			this.is_running.add(new Boolean(another.is_running.get(i)));
		}
		for(int i=0;i<another.time_elapsed.size();i++)
		{
			this.time_elapsed.add(new Long(another.time_elapsed.get(i)));
		}
		for(int i=0;i<another.last_start_time.size();i++)
		{
			this.last_start_time.add(new Date(another.last_start_time.get(i).getTime()));
		}
	}
	//------------------------------------------------------------------
	public void		start_running(String activity) throws IllegalArgumentException
	{
		if(!this.activities.contains(activity))
		{
			this.activities.add(activity);
			this.is_running.add(new Boolean(false));
			this.time_elapsed.add(new Long(0));
			this.last_start_time.add(new Date());
		}
		
		for(int i=0;i<this.activities.size();i++)
		{
			if(this.is_running.get(i))
			{
				throw new IllegalArgumentException("Activity '"+this.activities.get(i)+"' is also running!");
			}
		}
		int index=this.activities.indexOf(activity);
		this.is_running.set(index,true);
		this.last_start_time.set(index,new Date());
	}
	//------------------------------------------------------------------
	public void		stop_running(String activity) throws IllegalArgumentException
	{
		if(!this.activities.contains(activity))
		{
			throw new IllegalArgumentException("Activity '"+activity+"' doesnt exist, can't stop it");
		}
		int index=this.activities.indexOf(activity);
		if(this.is_running.get(index)==false)
		{
			throw new IllegalArgumentException("Activity '"+activity+"' is not running, can't stop it");
		}
		this.is_running.set(index,false);
		Date beginning=this.last_start_time.get(index);
		Date finish=new Date();
		Long elapsed = (finish.getTime() - beginning.getTime())/1000;
		this.time_elapsed.set(index,this.time_elapsed.get(index)+elapsed);
	}
	//------------------------------------------------------------------
	public Long		get_time_elapsed(String activity)
	{
		if(!this.activities.contains(activity))
		{
			throw new IllegalArgumentException("Activity '"+activity+"' doesnt exist");
		}
		int index=this.activities.indexOf(activity);
		return this.time_elapsed.get(index);
	}
	//------------------------------------------------------------------
	public void		combine_max_time_elapsed(TimeMeasuring another)
	{
		//it's NOT asumed that this and another has exactly the same activities.
		if(another==null)
		{
			throw new IllegalArgumentException("Can't combine null TimeMeasuring!");
		}
		for(int i=0;i<this.activities.size();i++)
		{
			if(this.is_running.get(i))
			{
				throw new IllegalArgumentException("Can't combine when there is an activity still running! [this."+this.activities.get(i)+"]");
			}
		}
		for(int i=0;i<another.activities.size();i++)
		{
			if(another.is_running.get(i))
			{
				throw new IllegalArgumentException("Can't combine when there is an activity still running! [right."+another.activities.get(i)+"]");
			}
		}
		
		
		//this left join another
		for(int i=0;i<this.activities.size();i++)
		{
			Long this_time		=this.time_elapsed.get(i);
			Long another_time	=(long)0;
			int index_in_another=another.activities.indexOf(this.activities.get(i));
			if(index_in_another!=-1)
			{
				another_time=another.time_elapsed.get(index_in_another);
			}
			this.time_elapsed.set(i,Math.max(this_time,another_time));
		}
		//in another, not in this
		for(int i=0;i<another.activities.size();i++)
		{
			int index_in_this=this.activities.indexOf(another.activities.get(i));
			if(index_in_this==-1)
			{
				this.activities.add(another.activities.get(i));
				this.is_running.add(new Boolean(false));
				this.time_elapsed.add(new Long(another.time_elapsed.get(i)));
				this.last_start_time.add(another.last_start_time.get(i));
			}
		}
	}
	//------------------------------------------------------------------
	public void		combine_add(TimeMeasuring another)
	{
		//it's NOT asumed that this and another has exactly the same activities.
		if(another==null)
		{
			throw new IllegalArgumentException("Can't combine null TimeMeasuring!");
		}
		for(int i=0;i<this.activities.size();i++)
		{
			if(this.is_running.get(i))
			{
				throw new IllegalArgumentException("Can't combine when there is an activity still running! [this."+this.activities.get(i)+"]");
			}
		}
		for(int i=0;i<another.activities.size();i++)
		{
			if(another.is_running.get(i))
			{
				throw new IllegalArgumentException("Can't combine when there is an activity still running! [right."+another.activities.get(i)+"]");
			}
		}

		//this left join another
		for(int i=0;i<this.activities.size();i++)
		{
			Long this_time		=this.time_elapsed.get(i);
			Long another_time	=(long)0;
			int index_in_another=another.activities.indexOf(this.activities.get(i));
			if(index_in_another!=-1)
			{
				another_time=another.time_elapsed.get(index_in_another);
			}
			this.time_elapsed.set(i,this_time+another_time);
		}
		//in another, not in this
		for(int i=0;i<another.activities.size();i++)
		{
			int index_in_this=this.activities.indexOf(another.activities.get(i));
			if(index_in_this==-1)
			{
				this.activities.add(another.activities.get(i));
				this.is_running.add(new Boolean(false));
				this.time_elapsed.add(new Long(another.time_elapsed.get(i)));
				this.last_start_time.add(another.last_start_time.get(i));
			}
		}
	}
	//------------------------------------------------------------------
	public void		reset_time_elapsed()
	{
		for(int i=0;i<this.activities.size();i++)
		{
			this.time_elapsed.set(i,(long)0);
		}		
	}
	//------------------------------------------------------------------
	public String	toString()
	{
		String result="";
		int max_activity_length=0;
		for(int i=0;i<this.activities.size();i++)
		{
			int current_activity_length=this.activities.get(i).length();
			if(max_activity_length<current_activity_length)
			{
				max_activity_length=current_activity_length;
			}
		}
		for(int i=0;i<this.activities.size();i++)
		{
			result+=this.activities.get(i);
			for(int j=0;j<max_activity_length-this.activities.get(i).length();j++)
			{
				result+=" ";
			}
			if(this.is_running.get(i))
			{
				result+=" running     ";
			}
			else
			{
				result+=" not_running ";
			}
			result+=this.time_elapsed.get(i);
			result+="\n";
		}
		return result;
	}
	//------------------------------------------------------------------
}
