package simulation;

import java.util.ArrayList;

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
	/** CSA type (consumer/corporate) */
	private int type;
	/** if CSA is allowed to handle all kind of calls */
	private boolean handle_both;

	private double std;

	private double truncation;

	private double shift_end;

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
	 *        Service times are exponentially distributed with mean 30
	 *	@param q	Queue from which the csa has to take calls
	 *	@param s	Where to send the completed calls
	 *	@param e	Eventlist that will manage events
	 *	@param n	The name of the csa
	 *  @param tp	The type of the csa (consumer/corporate)
	 */
	public CSA(Queue q, CallAcceptor s, CEventList e, String n, int tp)
	{
		status='i';
		queue=q;
		sink=s;
		eventlist=e;
		name=n;
		type = tp;
		if(tp == 0){
		    std = 35;
            meanProcTime=72;
            truncation = 25;

        }
        else{
            std = 72;
            meanProcTime=216;
            truncation = 45;
        }
		handle_both = false;
		queue.askCall(this);
	}

	/**
	 *	Constructor
	 *        Service times are exponentially distributed with mean 30
	 *	@param q	Queue from which the csa has to take calls
	 *	@param s	Where to send the completed calls
	 *	@param e	Eventlist that will manage events
	 *	@param n	The name of the csa
	 *  @param tp	The type of the csa (consumer/corporate)
	 *  @param hb flag if agent is allowed to handle all kinds of calls
	 */
	public CSA(Queue q, CallAcceptor s, CEventList e, String n, int tp, boolean hb, double shift_end)
	{
		status='i';
		queue=q;
		sink=s;
		eventlist=e;
		name=n;
		this.shift_end = shift_end;
        if(tp == 0){
            std = 35;
            meanProcTime=72;
            truncation = 25;

        }
        else{
            std = 72;
            meanProcTime=216;
            truncation = 45;
        }
		type = tp;
		handle_both = hb;
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
		System.out.println("Call finished at time in hours" + tme / 3600+" in sec "+tme);
		// Remove call from system
		call.stamp(tme,"Call finished",name);
		sink.giveCall(call);
		call = null;
		// set csa status to idle
		status = 'i';

		//
		if (tme < shift_end){
			// Ask the queue for calls
			queue.askCall(this);
		}
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
			if (this.handle_both || this.type == p.getType()) {
				System.out.println(this.name + " receives a call of type " + type);
				// accept the call
				call = p;
				// mark starting time
				call.stamp(eventlist.getTime(), "Call started", name);
				// start calls
				startCall();
				// Flag that the call has arrived
				return true;
			}
			else {
				return false;
			}
		}
		// Flag that the call has been rejected
		else {
			return false;
		}
	}

	public String getName(){ return name;}

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
			double duration = drawTruncatedNormal();
			System.out.println("call will take in hours "+duration/3600 + " in secs "+duration);
			// Create a new event in the eventlist
			double tme = eventlist.getTime();
			eventlist.add(this,type,tme+duration); //target,type,time
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


	public double BoxMuller1(double u1,double u2){

		double x1;
		//transform the by the Box-Muller method generated standard normal r.v. to a normally distributed
		// r.v. with the desired mean and std:
		x1 = getMeanProcTime() + getStd()*(Math.sqrt(-2* Math.log(u1)) * (Math.cos((2*Math.PI)*u2)));

		//check whether the generated value is below the truncation threshold
	    if (x1< getTruncation()){

	    	//if yes, then return 'dummy' value -1
	    	x1 = -1;
	    	return x1;
		}
		//otherwise propagate the truncated normally distributed r.v.
	    else {
	    	return x1;
	    }

    }


	public double BoxMuller2(double u1,double u2){
		double x2;
		//transform the by the Box-Muller method generated standard normal r.v. to a normally distributed
		// r.v. with the desired mean and std:
		x2 = getMeanProcTime() + getStd()*(Math.sqrt(-2* Math.log(u1)) * (Math.sin((2*Math.PI)*u2)));

		//check whether the generated value is below the truncation threshold
		if (x2 < getTruncation())
		{

			//if yes, then return 'dummy' value -1
			x2 = -1;
			return x2;
		}
		//otherwise propagate the truncated normally distributed r.v.
		else {
			return x2;
		}


	}


    public double drawTruncatedNormal()
	{
	/*Sampling from the truncated normal distribution is conducted according to following algorithm which imposes the truncation interval by rejection::
		1. Generate 2 Uniform(0,1) random variates U1, U2
		2. Use U1 and U2 to generate pairs Z1,Z2 of independent, standard, normally distributed (zero expectation, 1 variance) random numbers with to Box-Muller transform method
		3. Transform the 2 newly created r.v.s to normal distribution with desired parameters according to (X_i = this.mean + this.std*Z_i)
		4. until a<=(X1,X2)<=b (within truncation range)


		The algorithm is efficient in the simulation context, as the truncation interval represents a large part of the normal probability mass for the given param and truncation ranges:
		For corporate service times the truncated interval >45 represents 99.12% of normal probability mass.
		For consumer service times the truncated interval >25 represents 91.03% of normal probability mass.

		This means that it is very unlikely that the algorithm needs to loop more than once in order to find a valid sample with the desired underlying truncated normal distribution.
		Hence, the algorithm is more efficient than a 'pure' Acceptance-Rejection method and exploits the fact that we can make use of the Box-Muller transform by scaling the output
		according to desired parameters and reject if the generated values are outside of the truncation range.
	*/

		double U1 = Math.random();
		double U2 = Math.random();

		//get random variables for inverse transform method with imposed truncation interval by rejection
		double rv1 = BoxMuller1(U1,U2);
		double rv2 = BoxMuller2(U1,U2);

		//return rv2 if rv1 falls within the truncated range
		if (rv1 < 0 && rv2 > 0)
		{

			return rv2;
		}
		//return rv1 if rv2 falls within the truncated range

		if(rv1 > 0 && rv2 < 0)
		{

			return rv1;
		}


		//accounting for the unlikely case
		if (rv1 < 0 && rv2 < 0)
		{
			//go to step 1 of algo
			drawTruncatedNormal();

		}

		//otherwise both values are valid, so randomly decide which to return
		if(Math.random()<=0.5)
		{
			return rv1;
		}
		else {
			return rv2;
		}

    }


    public String toString()
	{
		return "csa status" + status + " name " + name;
	}


	public double getMeanProcTime(){
	    return this.meanProcTime;
    }

    public double getStd(){
		return this.std;
	}

	public double getTruncation(){
		return this.truncation;
	}
}