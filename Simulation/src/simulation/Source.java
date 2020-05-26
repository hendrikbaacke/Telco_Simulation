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
	 * Interarrival times are exponentially distributed with mean 33
	 *
	 * @param q The receiver of the calls
	 * @param l The eventlist that is requested to construct events
	 * @param n Name of object
	 */
	public Source(CallAcceptor q, CEventList l, String n) {
		list = l;
		queue = q;
		name = n;
		meanArrTime = 33;
		// put first event in list for initialization
		list.add(this, 0, drawRandomExponential(meanArrTime)); //target,type,time
	}

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
		if (type == 0) {
			meanArrTime = getAverageArrivalRateCons(l.getTime());
		}
		if (type == 1) {
			meanArrTime = getAverageArrivalRateCorp(l.getTime());
		}
		// put first event in list for initialization
		list.add(this, type, drawRandomExponential(meanArrTime) + l.getTime()); //target,type,time
	}

	/**
	 * Constructor, creates objects
	 * Interarrival times are exponentially distributed with specified mean
	 *
	 * @param q The receiver of the calls
	 * @param l The eventlist that is requested to construct events
	 * @param n Name of object
	 * @param m Mean arrival time
	 */
	public Source(CallAcceptor q, CEventList l, String n, double m) {
		list = l;
		queue = q;
		name = n;
		meanArrTime = m;
		// put first event in list for initialization
		list.add(this, 0, drawRandomExponential(meanArrTime)); //target,type,time
	}

	/**
	 * Constructor, creates objects
	 * Interarrival times are prespecified
	 *
	 * @param q  The receiver of the calls
	 * @param l  The eventlist that is requested to construct events
	 * @param n  Name of object
	 * @param ia interarrival times
	 */
	public Source(CallAcceptor q, CEventList l, String n, double[] ia) {
		list = l;
		queue = q;
		name = n;
		meanArrTime = -1;
		interarrivalTimes = ia;
		interArrCnt = 0;
		// put first event in list for initialization
		list.add(this, 0, interarrivalTimes[0]); //target,type,time
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
		if (meanArrTime > 0) {
			if (type == 0) {
				double duration = drawRandomExponential(getMaxArrivalRateCons(), tme);
				//double time = drawRandomExponential(getMaxArrivalRateCons(), tme);

				// Create a new event in the eventlist
				list.add(this, type, tme + duration); //target,type,time
				System.out.println("Duration till next call in hours " + duration / 3600 + " in secs " + duration);

			}

			if (type == 1) {
				double duration = drawRandomExponential(getAverageArrivalRateCorp(tme));

				// Create a new event in the eventlist
				list.add(this, type, tme + duration); //target,type,time
				//System.out.println("Duration till next call in hours " + duration / 3600 + " in secs " + duration);
			}


		} else {
			interArrCnt++;
			if (interarrivalTimes.length > interArrCnt) {
				if (type == 0) {
					double duration = drawRandomExponential(getMaxArrivalRateCons(), tme);
					list.add(this, 0, tme + interarrivalTimes[interArrCnt]); //target,type,time
					//double time = drawRandomExponential(getMaxArrivalRateCons(), tme);
					//list.add(this, type, time);
				}

				if (type == 1) {
					double duration = drawRandomExponential(getAverageArrivalRateCorp(tme));
					list.add(this, 0, tme + interarrivalTimes[interArrCnt]); //target,type,time

				}

			} else {
				list.stop();
			}
		}
	}


	public static double getMaxArrivalRateCons() {

		//min 60/0.2 max: 60/3.8
		double maxArrivalRate = 3.8;


		return maxArrivalRate;
	}

	public static double getAverageArrivalRateCorp(double tme) {
		double avgtme = 0;

		//time in hours
		double tme_h = tme / 3600;

		//handle clock when one day is over
		if (tme_h > 24) {
			tme_h = tme_h - 24;
		}


		//corporate calls
		if (8 < tme_h && tme_h < 18) {
			avgtme = 60;
		}
		if (18 < tme_h || tme_h < 8) {
			avgtme = 60 / 0.2;
		}

		return avgtme;
	}


	public static double drawRandomExponential(double mean) {
		// draw a [0,1] uniform distributed number
		double u = Math.random();
		// Convert it into a exponentially distributed random variate with mean "mean"
		double res = -mean * Math.log(u);
		return res;
	}

	public static double drawRandomExponential(double maxLambda, double tme) {
		double Max_mean = 60 /maxLambda;

		//time in hours
		double tme_h = tme / 3600;

		//handle clock when one day is over
		if (tme_h > 24) {
			tme_h = tme_h - 24;

		}
		//double lambda_t = (1.8 * Math.sin((2 * Math.PI / 24) * (tme_h + 15)) + 2);   //<-tme_h (?)
		double lambda_t = 1.8 * Math.sin((2 * Math.PI / 24) * (tme_h + 9)) + 2;
		// 60 divided by rate per minute to get avg arrival time in seconds
		//avgtme = 60 / (1.8 * Math.sin((2*Math.PI/24)*(tme_h+15))+2);

		// draw a [0,1] uniform distributed number
		double u1 = Math.random();
		double u2 = Math.random();

		// Convert it into a exponentially distributed random variate with mean "mean"
		double res = tme_h - (1/maxLambda)*Math.log(u1);
		//double res = -Max_mean * Math.log(u1);

		if (u2 <= (lambda_t)/maxLambda) {
			return res; // in seconds
		}
		else {
			return drawRandomExponential(getMaxArrivalRateCons(), tme);
		}

	}


	public static double getAverageArrivalRateCons(double tme){
		//time in hours
		double tme_h = tme / 3600;

		//handle clock when one day is over
		if (tme_h > 24) {
			tme_h = tme_h - 24;

		}

		// 60 divided by rate per minute to get avg arrival time in seconds
		double avgtme = 60 / (1.8 * Math.sin((2*Math.PI/24)*(tme_h+15))+2);


		return avgtme;
	}

	public String toString(){
		return "source " + type + " ma " + meanArrTime;
	}
}