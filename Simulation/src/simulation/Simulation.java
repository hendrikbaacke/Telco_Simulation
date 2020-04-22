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
    public Machine mach;
	

        /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	// Create an eventlist
	CEventList l = new CEventList();
	// Two queues for the machine
        Queue q1 = new Queue();
        Queue q2 = new Queue();
	// A source
        Source calls_corporate = new Source(q1,l,"Source 1");
        Source calls_customers = new Source(q2,l,"Source 2");
	// A sink
	Sink si = new Sink("Sink 1");
	// A machine
        Machine CSA_corporate = new Machine(q1,si,l,"CSA_corporate");  //TODO: CSA Corporate should be able to get cust from q1 and q2
        Machine CSA = new Machine(q2,si,l,"CSA");
	// start the eventlist
	l.start(2000); // 2000 is maximum time
    }
    
}
