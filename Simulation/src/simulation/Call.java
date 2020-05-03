package simulation;

import java.util.ArrayList;
/**
 *	Call that is send trough the system
 *	@author Joel Karel
 *	@version %I%, %G%
 */
class Call
{
	/** Stamps for the calls */
	private ArrayList<Double> times;
	private ArrayList<String> events;
	private ArrayList<String> stations;
	private int type;

	/** 
	*	Constructor for the call
	*	Mark the time at which it is created
	*/
	public Call()
	{
		times = new ArrayList<>();
		events = new ArrayList<>();
		stations = new ArrayList<>();
	}

	/**
	 *	Constructor for the call
	 *	Mark the time at which it is created
	 * @param tp the type of call (consumer/corporate)
	 */
	public Call(int tp)
	{
		times = new ArrayList<>();
		events = new ArrayList<>();
		stations = new ArrayList<>();
		type = tp;
	}
	
	public void stamp(double time,String event,String station)
	{
		times.add(time);
		events.add(event);
		stations.add(station);
	}
	
	public ArrayList<Double> getTimes()
	{
		return times;
	}

	public ArrayList<String> getEvents()
	{
		return events;
	}

	public ArrayList<String> getStations()
	{
		return stations;
	}
	public int getType()
	{
		return type;
	}
	
	public double[] getTimesAsArray()
	{
		times.trimToSize();
		double[] tmp = new double[times.size()];
		for (int i=0; i < times.size(); i++)
		{
			tmp[i] = (times.get(i)).doubleValue();
		}
		return tmp;
	}

	public String[] getEventsAsArray()
	{
		String[] tmp = new String[events.size()];
		tmp = events.toArray(tmp);
		return tmp;
	}

	public String[] getStationsAsArray()
	{
		String[] tmp = new String[stations.size()];
		tmp = stations.toArray(tmp);
		return tmp;
	}
}