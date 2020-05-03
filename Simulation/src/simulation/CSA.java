package simulation;

/**
 *	csa in a factory
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public class CSA implements CProcess, CallAcceptor
{
	/** call that is being handled  */
	private Call call;
	/** Eventlist that will manage events */
	private final CEventList eventlist;
	/** Queue from which the csa has to take calls */
	private Queue queue;
	/** Sink to dump calls */
	private CallAcceptor sink;
	/** Status of the csa (b=busy, i=idle) */
	private char status;
	/** csa name */
	private final String name;
	/** Mean processing time */
	private double meanProcTime;
	/** Processing times (in case pre-specified) */
	private double[] processingTimes;
	/** Processing time iterator */
	private int procCnt;
	

	/**
	*	Constructor
	*        Service times are exponentially distributed with mean 30
	*	@param q	Queue from which the csa has to take calls
	*	@param s	Where to send the completed calls
	*	@param e	Eventlist that will manage events
	*	@param n	The name of the csa
	*/
	public CSA(Queue q, CallAcceptor s, CEventList e, String n)
	{
		status='i';
		queue=q;
		sink=s;
		eventlist=e;
		name=n;
		meanProcTime=30;
		queue.askCall(this);
	}

	/**
	*	Constructor
	*        Service times are exponentially distributed with specified mean
	*	@param q	Queue from which the csa has to take calls
	*	@param s	Where to send the completed calls
	*	@param e	Eventlist that will manage events
	*	@param n	The name of the csa
	*        @param m	Mean processing time
	*/
	public CSA(Queue q, CallAcceptor s, CEventList e, String n, double m)
	{
		status='i';
		queue = q;
		sink=s;
		eventlist=e;
		name=n;
		meanProcTime=m;
		queue.askCall(this);
	}
	
	/**
	*	Constructor
	*        Service times are pre-specified
	*	@param q	Queue from which the csa has to take calls
	*	@param s	Where to send the completed calls
	*	@param e	Eventlist that will manage events
	*	@param n	The name of the csa
	*        @param st	service times
	*/
	public CSA(Queue q, CallAcceptor s, CEventList e, String n, double[] st)
	{
		status='i';
		queue=q;
		sink=s;
		eventlist=e;
		name=n;
		meanProcTime=-1;
		processingTimes=st;
		procCnt=0;
		queue.askCall(this);
	}

	/**
	*	Method to have this object execute an event
	*	@param type	The type of the event that has to be executed
	*	@param tme	The current time
	*/
	public void execute(int type, double tme)
	{
		// show arrival
		System.out.println("Call finished at time = " + tme);
		// Remove call from system
		call.stamp(tme,"Call finished",name);
		sink.giveCall(call);
		call = null;
		// set csa status to idle
		status='i';
		// Ask the queue for calls
		queue.askCall(this);
	}
	
	/**
	*	Let the csa accept a call and let it start handling it
	*	@param p	The call that is offered
	*	@return	true if the call is accepted and started, false in all other cases
	*/
        @Override
	public boolean giveCall(Call p)
	{
		// Only accept something if the csa is idle
		if(status=='i')
		{
			// accept the call
			call =p;
			// mark starting time
			call.stamp(eventlist.getTime(),"Call started",name);
			// start calls
			startCall();
			// Flag that the call has arrived
			return true;
		}
		// Flag that the call has been rejected
		else return false;
	}
	
	/**
	*	Starting routine for the call
	*	Start the handling of the current call with an exponentionally distributed processingtime with average 30
	*	This time is placed in the eventlist
	*/
	private void startCall()
	{
		// generate duration
		if(meanProcTime>0)
		{
			double duration = drawRandomExponential(meanProcTime);
			// Create a new event in the eventlist
			double tme = eventlist.getTime();
			eventlist.add(this,0,tme+duration); //target,type,time
			// set status to busy
			status='b';
		}
		else
		{
			if(processingTimes.length>procCnt)
			{
				eventlist.add(this,0,eventlist.getTime()+processingTimes[procCnt]); //target,type,time
				// set status to busy
				status='b';
				procCnt++;
			}
			else
			{
				eventlist.stop();
			}
		}
	}

	//TODO
	//replace by truncated normal draw
	public static double drawRandomExponential(double mean)
	{
		// draw a [0,1] uniform distributed number
		double u = Math.random();
		// Convert it into a exponentially distributed random variate with mean 33
		double res = -mean*Math.log(u);
		return res;
	}
}