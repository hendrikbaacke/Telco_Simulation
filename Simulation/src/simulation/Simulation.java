/**
 *	Example program for using eventlists
 *	@author Joel Karel
 *	@version %I%, %G%
 */

package simulation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        boolean handle_both = true;

        //roster: agents for each shift
        //Shift 1: (6am - 2pm)
        //Shift 2: (2pm - 10pm)
        //Shift 3: (10pm - 6am)
        //first agent{0,x}: consumer CSA
        //second agent{x,1} corporate CSA
        int[][] roster = {{2,4},{2,5},{2,4}};

        // n is the number of runs
        int n = 10;

        //amount of days of simulation runs
        int days = 10;
        for (int i = 0; i < n; i++) {
            int start_time = 6 * 60 * 60;
            int sim_duration = days * 24 *60 * 60;
            int shift_duration = 8 * 60 * 60;

            // Create an eventlist
            CEventList l = new CEventList(start_time); // start time is at 6 o clock first day

            // Two queues for the csa- one for consumers, one for the corporate
            Queue q_con = new Queue();
            Queue q_cor = new Queue();

            //type 0 means consumer calls and type 1 means corporate calls
            Source calls_consumer = new Source(q_con, l, "Source 1", 0);
            Source calls_corporate = new Source(q_cor, l, "Source 2", 1);

            // A sink
            Sink si = new Sink("Sink 1");

            //
            int amount_shifts = sim_duration / shift_duration + 1;
            int shift_end = start_time;
            for(int j = 0; j < amount_shifts; j++){
                shift_end += shift_duration;
                int shift_type = j % 3;
                l.add(new Shift(q_con, q_cor, si, l, handle_both,
                        shift_end, roster[shift_type][0], roster[shift_type][1]),0,shift_end - shift_duration);
            }




            // start the eventlist
            l.start(start_time + sim_duration);

            //save the data
            si.toMatrixFile("informationCalls" +  i + ".csv");
            si.toWaitTimeFileConsumer("waitingTimesConsumer" + i + ".csv");
            si.toWaitTimeFileCorporate("waitingTimesCorporate" + i + ".csv");

            int cost = (roster[0][0]  + roster[1][0] + roster[2][0])* 8 * 35 + (roster[0][1] + roster[1][1] + roster[2][1]) * 8 * 60;
            System.out.println("________________________"+"Cost per day: "+cost+"________________________");

        }
    }
}
