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
        //a shift consists of:
        //[start hour,finish hour,amount of agents type 1, amount of agents type 2]
        int[][] shifts = {{6,14,10,0},{14,22,15,0},{22,30,10,0}};

        //flag defining if CSA agents of type 2 are allowed to handle calls of type 1
        boolean handle_both = false;

        for (int j = 0; j < shifts.length; j++) {
            //shift start in seconds
            double start = shifts[j][0] * 60 * 60;
            double finish = shifts[j][1] * 60 * 60;
            int n_agents = shifts[j][2];
            int n_agents_corp = shifts[j][3];

            // Create an eventlist
            CEventList l = new CEventList(start);
            // Two queues for the csa
            Queue q1 = new Queue();

            //type 0 means customer calls and type 1 means corporate calls
            Source calls_customer = new Source(q1, l, "Source 1", 0);
            Source calls_corporate = new Source(q1, l, "Source 2", 1);

            // A sink
            Sink si = new Sink("Sink 1");

            for (int i = 0; i < n_agents; i++) {
                // A csa
                CSA CSA_corporate = new CSA(q1, si, l, "corporate CSA nr " + i, 2,handle_both);
                CSA CSA_customer = new CSA(q1, si, l, "customer CSA nr " + i, 1);
            }

            // start the eventlist
            l.start(finish);

            //save the data
            si.toFile(" "+j+".csv");
        }
    }
}
