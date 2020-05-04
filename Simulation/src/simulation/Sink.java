package simulation;

import java.util.ArrayList;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 *	A sink
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public class Sink implements CallAcceptor
{
	/** All calls are kept */
	private ArrayList<Call> calls;
	/** All properties of calls are kept */
	private ArrayList<Integer> numbers;
	private ArrayList<Double> times;
	private ArrayList<String> events;
	private ArrayList<String> stations;
	/** Counter to number calls */
	private int number;
	/** Name of the sink */
	private String name;
	
	/**
	*	Constructor, creates objects
	*/
	public Sink(String n)
	{
		name = n;
		calls = new ArrayList<>();
		numbers = new ArrayList<>();
		times = new ArrayList<>();
		events = new ArrayList<>();
		stations = new ArrayList<>();
		number = 0;
	}
	
        @Override
	public boolean giveCall(Call c)
	{
		number++;
		calls.add(c);
		// store stamps
		ArrayList<Double> t = c.getTimes();
		ArrayList<String> e = c.getEvents();
		ArrayList<String> s = c.getStations();
		for(int i=0;i<t.size();i++)
		{
			numbers.add(number);
			times.add(t.get(i));
			events.add(e.get(i));
			stations.add(s.get(i));
		}
		return true;
	}
	
	public int[] getNumbers()
	{
		numbers.trimToSize();
		int[] tmp = new int[numbers.size()];
		for (int i=0; i < numbers.size(); i++)
		{
			tmp[i] = (numbers.get(i)).intValue();
		}
		return tmp;
	}

	public double[] getTimes()
	{
		times.trimToSize();
		double[] tmp = new double[times.size()];
		for (int i=0; i < times.size(); i++)
		{
			tmp[i] = (times.get(i)).doubleValue();
		}
		return tmp;
	}

	public String[] getEvents()
	{
		String[] tmp = new String[events.size()];
		tmp = events.toArray(tmp);
		return tmp;
	}

	public String[] getStations()
	{
		String[] tmp = new String[stations.size()];
		tmp = stations.toArray(tmp);
		return tmp;
	}

	public void toFile(String filename) throws IOException
	{
		BufferedWriter outputWriter = null;
		outputWriter = new BufferedWriter(new FileWriter(filename));
		for (int i = 0; i < calls.size(); i++) {
			outputWriter.write(numbers.get(i)+" "+events.get(i)
					+" "+times.get(i)+" "+stations.get(i));
			outputWriter.newLine();
		}
		outputWriter.flush();
		outputWriter.close();
	}

	/* First column: 	type of customer (0 = consumer, 1 = corporate)
	   Second column: 	type of agent that helps them (0 = consumer, 1 = corporate)
	   Third column: 	time of call incoming
	   Fourth column: 	time of start call
	   Fifth column: 	time of end call
	 */
	public void toMatrixFile(String filename) throws IOException{
		BufferedWriter outputWriter = new BufferedWriter(new FileWriter(filename));
		for(int i = 0; i < calls.size(); i++){
			int typeAgent = 0;

			if(calls.get(i).getStations().get(1).contains("consumer CSA")){
				typeAgent = 0;
			}
			else{
				typeAgent = 1;
			}
			outputWriter.write(calls.get(i).getType() + " " +
					" " + typeAgent + " " + calls.get(i).getTimes().get(0)
					+ " " + calls.get(i).getTimes().get(1) + " " +
					calls.get(i).getTimes().get(2));

			outputWriter.newLine();
		}
		outputWriter.flush();
		outputWriter.close();
	}

	public void toWaitTimeFileConsumer(String filename) throws IOException{
		BufferedWriter outputWriter = new BufferedWriter(new FileWriter((filename)));
		for (int i = 0; i < calls.size(); i++){
			if (calls.get(i).getStations().get(0).equals("Source 1")) {
				// For every call, extract the creation time from the start time:
				outputWriter.write(Double.toString(calls.get(i).getTimes().get(1) - calls.get(i).getTimes().get(0)));
				outputWriter.newLine();
			}
		}
		outputWriter.flush();
		outputWriter.close();
	}

	public void toWaitTimeFileCorporate(String filename) throws IOException{
		BufferedWriter outputWriter = new BufferedWriter(new FileWriter((filename)));
		for (int i = 0; i < calls.size(); i++){
			if (calls.get(i).getStations().get(0).equals("Source 2")) {
				// For every call, extract the creation time from the start time:
				outputWriter.write(Double.toString(calls.get(i).getTimes().get(1) - calls.get(i).getTimes().get(0)));
				outputWriter.newLine();
			}
		}
		outputWriter.flush();
		outputWriter.close();
	}

	public void toAmountCustomersInSystemFile(String filename) throws IOException{
		// Can be done with every step size. For now, it's checked every minute.
		BufferedWriter outputWriter = new BufferedWriter(new FileWriter((filename)));
		double totalTime = times.get(times.size()-1) - times.get(0);
		int stepSize = 60; //currently one minute
		for (int i = (int)Math.round(times.get(0)); i < times.get(times.size()-1); i += stepSize){
			int currentAmount = 0;
			for (int j = 0; j < calls.size(); j++) {
				if (i > calls.get(j).getTimes().get(0) && i < calls.get(j).getTimes().get(2)){
					currentAmount++;
				}
			}
			outputWriter.write(Integer.toString(currentAmount));
			outputWriter.newLine();
		}
		outputWriter.flush();
		outputWriter.close();
	}
}