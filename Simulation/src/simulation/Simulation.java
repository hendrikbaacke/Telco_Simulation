/**
 *	Example program for using eventlists
 *	@author Joel Karel
 *	@version %I%, %G%
 */

package simulation;
import java.io.IOException;

public class Simulation {

    public CEventList list;
    public Queue queue;
    public Source source;
    public Sink sink;
    public CSA mach;




        /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
                //flag defining if CSA agents of type 1 are allowed to handle calls of type 0
        boolean handle_both = false;

        // Create an eventlist
        CEventList l = new CEventList(0);

        // Two queues for the csa
        Queue q = new Queue();

        //type 0 means consumer calls and type 1 means corporate calls
        Source calls_consumer = new Source(q, l, "Source 1", 0);
        Source calls_corporate = new Source(q, l, "Source 2", 1);

        // A sink
        Sink si = new Sink("Sink 1");

        //a shift consists of:
        //[start hour,finish hour,amount of agents type 0, amount of agents type 1]
        int[][] shifts = {{14,100,50},{22,150,150},{6,100,50}};

        l.add(new Shift(q,si,l,handle_both,shifts[0][0],shifts[0][1],shifts[0][2]), 0, shifts[0][0]);
        l.add(new Shift(q,si,l,handle_both,shifts[1][0],shifts[1][1],shifts[1][2]), 0, shifts[1][0]);
        l.add(new Shift(q,si,l,handle_both,shifts[2][0],shifts[2][1],shifts[2][2]), 0, shifts[2][0]);

        // start the eventlist
        l.start(6*60*60*24);

        //save the data
        si.toFile(" "+".csv");
        si.toMatrixFile("informationCalls" + ".csv");
        si.toWaitTimeFileConsumer("waitingTimesConsumer"+".csv");
        si.toWaitTimeFileCorporate("waitingTimesCorporate"+".csv");
        si.toAmountCustomersInSystemFile("customersInSystem" + ".csv");
    }
}
