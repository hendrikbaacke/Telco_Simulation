package simulation;

import java.awt.*;
import java.util.ArrayList;

/**
 *	Event processing mechanism
 *	Events are created here and it is ensured that they are processed in the proper order
 *	The simulation clock is located here.
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public class CEventList implements CProcess
{
	/** The time in the simulation */
	private double currentTime;
	/** List of events that have to be executed */
	private final ArrayList<CEvent> events;
	/** Stop flag */
	private boolean stopFlag;

	/**
	*	Standard constructor
	*	Create an CEventList object
	*/
	public CEventList()
	{
		currentTime = 0;
		stopFlag = false;
		events = new ArrayList<>();
	}

	/**
	 *	Standard constructor
	 *	Create an CEventList object
	 */
	public CEventList(double start_time)
	{
		currentTime = start_time;
		stopFlag = false;
		events = new ArrayList<>();
	}

	/**
	*	Method for the construction of a new event.
	*	@param target The object that will process the event
	*	@param type A type indicator of the event for objects that can process multiple s of events.
	*	@param tme The time at which the event will be executed
	*/
	public void add(CProcess target, int type, double tme)
	{
		boolean success=false;
		// First create a new event using the parameters
		CEvent evnt;
                evnt = new CEvent(target,type,tme);
		// Now it is examened where the event has to be inserted in the list
		for(int i=0;i<events.size();i++)
		{
			// The events are sorted chronologically
			if(events.get(i).getExecutionTime()>evnt.getExecutionTime())
			{
				// If an event is found in the list that has to be executed after the current event
				success=true;
				// Then the new event is inserted before that element
				events.add(i,evnt);
				break;
			}
		}
		if(!success)
		{
			// Else the new event is appended to the list
			events.add(evnt);
		}
	}
	
	/**
	*	Method for starting the eventlist.
	*	It will run until there are no longer events in the list
	*/
	public void start()
	{
		// stop criterion
		while((events.size()>0)&&(!stopFlag))
		{
			// Make the similation time equal to the execution time of the first event in the list that has to be processed
			currentTime=events.get(0).getExecutionTime();
			// Let the element be processed
			events.get(0).execute();
			// Remove the event from the list
			events.remove(0);
		}
	}

	/**
	*	Method for starting the eventlist.
	*	It will run until there are no longer events in the list or that a maximum time has elapsed
	*	@param mx De maximum time of the simulation
	*/
	public void start(double mx)
	{
		//appends an event at finish time 'mx' to the list that will stop the simulation
		add(this,-1,mx);
		// stop criterion
		while((events.size()>0)&&(!stopFlag))
		{
			// Make the similation time equal to the execution time of the first event in the list that has to be processed
			currentTime=events.get(0).getExecutionTime();
			// Let the element be processed
			events.get(0).execute();
			// Remove the event from the list
			events.remove(0);
		}
	}

	public void stop()
	{
		stopFlag = true;
	}

	/**
	*	Method for accessing the simulation time.
	*	The variable with the time is private to ensure that no other object can alter it.
	*	This method makes it possible to read the time.
	*	@return The current time in the simulation
	*/
	public double getTime()
	{
		return currentTime;
	}
	
	/**
	*	Method to have this object process an event
	*	@param type	The type of the event that has to be executed
	*	@param tme	The current time
	*/
	@Override
	public void execute(int type, double tme)
	{
		if(type==-1)
			stop();
	}

	public String toString() {return "event list time " + currentTime;}
}