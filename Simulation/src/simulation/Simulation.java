/**
 *	Example program for using eventlists
 *	@author Joel Karel
 *	@version %I%, %G%
 */

package simulation;

public class Simulation {

    public CEventList list;
    public Queue queue;
    public Source source;
    public Sink sink;
    public CSA mach;
	

        /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //TODO data structure or pipeline for roster input to simulation output file
        //TODO adjusting the code to allow to either let CSA agents help out or not

        //shift start in seconds
        double shift_start = 6 * 60 * 60;
        double shift_end = 14 * 60 * 60;

        // Create an eventlist
        CEventList l = new CEventList(shift_start);
        // Two queues for the csa
        Queue q1 = new Queue();
        //Queue q2 = new Queue();
        // An example source of type 1 (customer calls) from shift  starting at time 6am

        //type 0 means customer calls and type 1 means corporate calls
        Source calls_customer = new Source(q1,l,"Source 1",0);
        Source calls_corporate = new Source(q1,l,"Source 2",1);
        //Source calls_customers = new Source(q2,l,"Source 2");
        // A sink
        Sink si = new Sink("Sink 1");
        // A csa
        CSA CSA_corporate = new CSA(q1,si,l,"CSA_corporate");  //TODO: CSA Corporate should be able to get cust from q1 and q2


        // start the eventlist
        l.start(shift_end); // 2000 is maximum time
    }
}
