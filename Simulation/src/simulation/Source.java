package simulation;

/**
 *	A source of calls
 *	This class implements CProcess so that it can execute events.
 *	By continuously creating new events, the source keeps busy.
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public class Source implements CProcess
{
	/** Eventlist that will be requested to construct events */
	private CEventList list;
	/** Queue that buffers calls for the csa */
	private CallAcceptor queue;
	/** Name of the source */
	private String name;
	/** Mean interarrival time */
	private double meanArrTime;
	/** Interarrival times (in case pre-specified) */
	private double[] interarrivalTimes;
	/** Interarrival time iterator */
	private int interArrCnt;
	/** type of the source*/
	private int type;

	/**
	*	Constructor, creates objects
	*        Interarrival times are exponentially distributed with mean 33
	*	@param q	The receiver of the calls
	*	@param l	The eventlist that is requested to construct events
	*	@param n	Name of object
	*/
	public Source(CallAcceptor q, CEventList l, String n)
	{
		list = l;
		queue = q;
		name = n;
		meanArrTime=33;
		// put first event in list for initialization
		list.add(this,0,drawRandomExponential(meanArrTime)); //target,type,time
	}

	/**
	 *	Constructor, creates objects
	 *        Interarrival times are exponentially distributed with mean dependent on
	 *        the type of the source
	 *	@param q	The receiver of the calls
	 *	@param l	The eventlist that is requested to construct events
	 *	@param n	Name of object
	 *  @param tp   type of the object
	 */
	public Source(CallAcceptor q, CEventList l, String n, int tp)
	{
		list = l;
		queue = q;
		name = n;
		type = tp;
		meanArrTime=getAverageArrivalRate(type, l.getTime());
		// put first event in list for initialization
		list.add(this,type,drawRandomExponential(meanArrTime)+l.getTime()); //target,type,time
	}

	/**
	*	Constructor, creates objects
	*        Interarrival times are exponentially distributed with specified mean
	*	@param q	The receiver of the calls
	*	@param l	The eventlist that is requested to construct events
	*	@param n	Name of object
	*	@param m	Mean arrival time
	*/
	public Source(CallAcceptor q, CEventList l, String n, double m)
	{
		list = l;
		queue = q;
		name = n;
		meanArrTime=m;
		// put first event in list for initialization
		list.add(this,0,drawRandomExponential(meanArrTime)); //target,type,time
	}

	/**
	*	Constructor, creates objects
	*        Interarrival times are prespecified
	*	@param q	The receiver of the calls
	*	@param l	The eventlist that is requested to construct events
	*	@param n	Name of object
	*	@param ia	interarrival times
	*/
	public Source(CallAcceptor q, CEventList l, String n, double[] ia)
	{
		list = l;
		queue = q;
		name = n;
		meanArrTime=-1;
		interarrivalTimes=ia;
		interArrCnt=0;
		// put first event in list for initialization
		list.add(this,0,interarrivalTimes[0]); //target,type,time
	}
	
        @Override
	public void execute(int type, double tme)
	{
		// show arrival
		System.out.println("Arrival of type "+ type +" at time = " + tme);
		// give arrived call to queue
		Call p = new Call(type);
		p.stamp(tme,"Creation",name);
		queue.giveCall(p);

		// generate duration
		if(meanArrTime>0)
		{
			double duration = drawRandomExponential(getAverageArrivalRate(type,tme));
			// Create a new event in the eventlist
			list.add(this,type,tme+duration); //target,type,time
			System.out.println("Duration till next event = " + duration);
		}
		else
		{
			System.out.println("=============================");
			interArrCnt++;
			if(interarrivalTimes.length>interArrCnt)
			{
				double duration = drawRandomExponential(getAverageArrivalRate(type,tme));
				list.add(this,0,tme+interarrivalTimes[interArrCnt]); //target,type,time
			}
			else
			{
				list.stop();
			}
		}
	}

	public static double getAverageArrivalRate(int type,double tme){
		double avgtme = 0;
		//TODO: implement properly
		//customer calls
		if (type == 0){
			avgtme = 5;
		}

		//corporate calls
		if (type == 1) {
			avgtme = 10;
		}

		return avgtme;
	}



	public static double drawRandomExponential(double mean)
	{

		//TODO make mean dependent on time of the day

		// draw a [0,1] uniform distributed number
		double u = Math.random();
		// Convert it into a exponentially distributed random variate with mean "mean"
		double res = -mean*Math.log(u);
		return res;
	}
}