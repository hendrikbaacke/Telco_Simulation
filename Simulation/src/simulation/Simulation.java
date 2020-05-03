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

        //

        // Create an eventlist
        CEventList l = new CEventList();
        // Two queues for the csa
        Queue q1 = new Queue();
        //Queue q2 = new Queue();
        // A source
        Source calls_corporate = new Source(q1,l,"Source 1");
        //Source calls_customers = new Source(q2,l,"Source 2");
        // A sink
        Sink si = new Sink("Sink 1");
        // A csa
        CSA CSA_corporate = new CSA(q1,si,l,"CSA_corporate");  //TODO: CSA Corporate should be able to get cust from q1 and q2



        // start the eventlist
        l.start(2000); // 2000 is maximum time
    }
}
