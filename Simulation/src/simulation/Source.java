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
			double arr_tme = drawRandomExponentialNonStat(tme);
			// Create a new event in the eventlist
			list.add(this, type,arr_tme); //target,type,time
			System.out.format("con duration generated: %.2f for arr time %.1f\n",arr_tme-tme,arr_tme/3600 % 24);
			//System.out.format("time now/next con call %.2f %.2f in hours %.2f %.2f\n",tme,arr_tme,tme/3600,arr_tme/3600);
		}
		else if (type == 1) {
			double duration = drawRandomExponential(getAverageArrivalRateCorp(tme));
			System.out.format("corp duration generated: %.2f\n",duration);
			//System.out.format("time now/next corp call %.2f %.2f in hours %.2f %.2f\n",tme,tme+duration,tme/3600,(tme+duration)/3600);
			// Create a new event in the eventlist
			list.add(this, type, tme + duration); //target,type,time
			//System.out.println("Duration till next corp call in hours " + duration / 3600 + " in secs " + duration);
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
		System.out.format("arrival rate %.2f per min ",60/avgtme);
		return avgtme;
	}

	public static double drawRandomExponential(double mean) {
		// draw a [0,1] uniform distributed number
		double u = Math.random();
		// Convert it into a exponentially distributed random variate with mean "mean"
		double res = -mean * Math.log(u);
		return res;
	}

	/*
	1. Set t = ti-1.
	2. Generate U1 and U2 as IID U(0, 1) independent of any previous random variates.
	3. Replace t by t2 (1yl*) ln U1.4. If U2#l(t)yl*, return ti5t.
	 Otherwise, go back to step 2
	 */

	public static double drawRandomExponentialNonStat(double tme) {
		double max_lambda = 3.8; //maximum rate in a day

		// draw a [0,1] uniform distributed number
		double u1 = Math.random();
		double u2 = Math.random();

		double arr_tme = tme / 60 - (1/max_lambda)*Math.log(u1) ; //next arrival time in minutes

		//arrival time in hours for a day
		double tme_h = arr_tme / 60 % 24;

		//function
		double lambda_t = (1.8 * Math.sin((2 * Math.PI / 24) * (tme_h + 15)) + 2 );

		if (u2 <= (lambda_t/max_lambda)) {
			System.out.format("arrival rate %.2f per min ", lambda_t);
			return arr_tme * 60 ; // in seconds
		}
		else {
			//System.out.format("refused duration %.2f\n",arr_tme*60-tme);
			//System.out.format("u2 %.2f ratio %.2f\n",u2,lambda_t/max_lambda);
			return drawRandomExponentialNonStat(arr_tme * 60);
		}
	}

	public String toString(){
		return "source " + type;
	}
}