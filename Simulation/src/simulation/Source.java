package simulation;

/**
 *	A source of calls
 *	This class implements CProcess so that it can execute events.
 *	By continuously creating new events, the source keeps busy.
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public class Source implements CProcess {
	/**
	 * Eventlist that will be requested to construct events
	 */
	private CEventList list;
	/**
	 * Queue that buffers calls for the csa
	 */
	private CallAcceptor queue;
	/**
	 * Name of the source
	 */
	private String name;
	/**
	 * Mean interarrival time
	 */
	private double meanArrTime;
	/**
	 * Interarrival times (in case pre-specified)
	 */
	private double[] interarrivalTimes;
	/**
	 * Interarrival time iterator
	 */
	private int interArrCnt;
	/**
	 * type of the source
	 */
	private int type;

	/*
	 * counter for calls
	 */
	private int counter;

	/**
	 * Constructor, creates objects
	 * Interarrival times are exponentially distributed with mean dependent on
	 * the type of the source
	 *
	 * @param q  The receiver of the calls
	 * @param l  The eventlist that is requested to construct events
	 * @param n  Name of object
	 * @param tp type of the object
	 */
	public Source(CallAcceptor q, CEventList l, String n, int tp) {
		list = l;
		queue = q;
		name = n;
		type = tp;
		// put first event in list for initialization
		list.add(this, type, drawRandomExponential(l.getTime())); //target,type,time
	}


	@Override
	public void execute(int type, double tme) {
		// show arrival
		//System.out.println("Arrival of type " + type + " at time in hours " + tme / 3600 + " in secs " + tme);
		// give arrived call to queue
		Call p = new Call(type,this.counter);
		//call id
		counter++;
		p.stamp(tme, "Creation", name);
		queue.giveCall(p);
		// generate duration
		if (type == 0) {
			double arr_tme = drawRandomExponential(tme);

			// Create a new event in the eventlist
			list.add(this, type,arr_tme); //target,type,time
			System.out.println("time for next cons call in hours " + arr_tme / 3600 + " in secs " + arr_tme);

		}
		else if (type == 1) {
			double duration = drawRandomExponential(getAverageArrivalRateCorp(tme));
			//System.out.println("Duration till next corp call in hours " + duration / 3600 + " in secs " + duration);
			// Create a new event in the eventlist
			list.add(this, type, tme + duration); //target,type,time
			//System.out.println("Duration till next call in hours " + duration / 3600 + " in secs " + duration);
		}
	}

	public static double getAverageArrivalRateCorp(double tme) {
		double avgtme = 0;

		//time in hours for a day
		double tme_h = tme / 3600 % 24;

		//corporate calls
		if (8 < tme_h && tme_h < 18) {
			avgtme = 60;
		}
		if (18 < tme_h || tme_h < 8) {
			avgtme = 60 / 0.2;
		}

		return avgtme;
	}

	public static double drawRandomExponential(double tme) {
		double max_lambda = 3.8; //maximum rate in a day

		//time in hours for a day
		double tme_h = tme / 3600 % 24;

		//function
		double lambda_t = 1.8 * Math.sin((2 * Math.PI / 24) * (tme_h + 15)) + 2;

		// draw a [0,1] uniform distributed number
		double u1 = Math.random();
		double u2 = Math.random();

		double arr_tme = tme / 60 - (1/max_lambda)*Math.log(u1); //next arrival time

		if (u2 <= (lambda_t)/max_lambda) {
			System.out.println(arr_tme * 60 + " " + lambda_t);
			return arr_tme * 60; // in seconds
		}
		else {
			return drawRandomExponential(tme);
		}

	}

	public String toString(){
		return "source " + type + " ma " + meanArrTime;
	}
}