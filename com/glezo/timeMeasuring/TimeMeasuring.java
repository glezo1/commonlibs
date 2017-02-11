package com.glezo.timeMeasuring;

import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

public class TimeMeasuring 
{
	private ArrayList<TimeMeasuringActivity>	activities;
	private ArrayList<TimeMeasuringEvent>		events;
	private long								time_elapsed_time_measuring;	// overhead due to TimeMeasuring operations itself

	public TimeMeasuring()
	{
		this.activities						=new ArrayList<>();
		this.events							=new ArrayList<>();
		this.time_elapsed_time_measuring	=0;
	}
	//---------------------------------------------------------------------
	public void start_running(String activity)
	{
		TimeMeasuringActivity new_activity=new TimeMeasuringActivity(activity,0,true);
		this.activities.add(new_activity);
		TimeMeasuringEvent new_event=new TimeMeasuringEvent(new Date(), activity,"start");
		this.events.add(new_event);
	}
	//---------------------------------------------------------------------
	public void stop_running(String activity)
	{
		TimeMeasuringEvent new_event=new TimeMeasuringEvent(new Date(), activity,"stop");
		this.events.add(new_event);
	}
	//---------------------------------------------------------------------
	public String	report()
	{
		Stack<TimeMeasuringEventExecution>		stack	=new Stack<TimeMeasuringEventExecution>();
		ArrayList<TimeMeasuringEventExecution>	result	=new ArrayList<TimeMeasuringEventExecution>();
		Date count_from_section=null;
		for(int i=0;i<this.events.size();i++)
		{
			TimeMeasuringEvent		current_event				=this.events.get(i);
			String					current_event_activity_name	=current_event.getActivityName();
			Date					current_event_timestamp		=current_event.getEventTimestamp();
			this.activities.indexOf(current_event_activity_name);
			if(current_event.getEventType().equals("start"))
			{
				if(stack.isEmpty())
				{
					stack.push(new TimeMeasuringEventExecution(current_event,0));
					count_from_section=current_event.getEventTimestamp();
				}
				else
				{
					long current_section_start			=count_from_section.getTime();
					long current_section_end			=current_event_timestamp.getTime();
					long current_section_time_elapsed	=current_section_end - current_section_start;
					stack.peek().addTimeElapsed(current_section_time_elapsed);
					stack.push(new TimeMeasuringEventExecution(current_event,0));
					count_from_section=current_event.getEventTimestamp();
				}
			}
			else
			{
				//it is assumed the closing event refers to the top of the stack
				long current_section_start			=count_from_section.getTime();
				long current_section_end			=current_event_timestamp.getTime();
				long current_section_time_elapsed	=current_section_end - current_section_start;
				count_from_section=current_event.getEventTimestamp();
				stack.peek().addTimeElapsed(current_section_time_elapsed);
				result.add(stack.pop());
			}
		}
		
		//alright, so now we have the real-execution time of each and every activity.
		long total_time_elapsed=0;
		int max_activity_name_length=0;
		int max_activity_time_string_length=0;
		for(int i=0;i<result.size();i++)
		{
			total_time_elapsed+=result.get(i).getTimeElapsed();
			if(result.get(i).getEvent().getActivityName().length()>max_activity_name_length)
			{
				max_activity_name_length=result.get(i).getEvent().getActivityName().length();
			}
			if(Long.toString(result.get(i).getTimeElapsed()).length()>max_activity_time_string_length)
			{
				max_activity_time_string_length=Long.toString(result.get(i).getTimeElapsed()).length();
			}
		}
		total_time_elapsed-=this.time_elapsed_time_measuring; //sustract the overhead due to the TimeMeasuring class itself
		
		String result_string="";
		for(int i=0;i<result.size();i++)
		{
			result_string+=result.get(i).getEvent().getActivityName();
			for(int j=result.get(i).getEvent().getActivityName().length();j<max_activity_name_length+1;j++)
			{
				result_string+=" ";
			}
			for(int j=Long.toString(result.get(i).getTimeElapsed()).length();j<max_activity_time_string_length+1;j++)
			{
				result_string+=" ";
			}
			result_string+=result.get(i).getTimeElapsed()+"ms ";
			float current_activity_percentage=((float)result.get(i).getTimeElapsed()/(float)total_time_elapsed)*100;
			String current_activity_perentage_string=String.format("%.3f",current_activity_percentage);
			result_string+=current_activity_perentage_string+"%\n";
		}
		return result_string;
	}
	//---------------------------------------------------------------------
}
