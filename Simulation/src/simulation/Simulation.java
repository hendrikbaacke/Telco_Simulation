/**
 *	Example program for using eventlists
 *	@author Joel Karel
 *	@version %I%, %G%
 */

package simulation;
import java.io.IOException;

public class Simulation {

        /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
/*To do the comparison of system configuration 1 and 2 in Matlab, let CSA.minIdle==0 to have system config. 1
/or let CSA.minIdle>0 to have system config. 2
/Let each system run with the desired amount of replications n and length (days) at least 15
This way one can generate two sets of output data for each system config.
*/

        //roster: agents for each shift 6-14 14-22 22-6 and
        //1. -> consumer CSA
        //2. -> flexible corporate CSA
        int[][] roster = {{0,5},{2,5},{0,5}};

        //number of CSA corporate to kept idle to handle incoming corporate calls
        CSA.minIdle = 0;
        String strategy_name = "Strategy1";  //Strategy1 flexible
        if(CSA.minIdle>0) {

            strategy_name = "Strategy2";      //Strategy2 mixed
        }

        // n is the number of runs
        int n = 5;
        //number of days a single simulation is run, let days>=15 as 5 days are truncated to obtain steady-state
        int days = 15;
        if (days<15){
            System.out.println("Warning: Let days >=15 to produce viable results for the output analysis.");

        }

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

            //init the shifts
            int amount_shifts = sim_duration / shift_duration + 1;
            int shift_end = start_time;
            for(int j = 0; j < amount_shifts; j++){
                shift_end += shift_duration;
                int shift_type = j % 3;
                l.add(new Shift(q_con, q_cor, si, l, shift_end, roster[shift_type][0], roster[shift_type][1]),0,shift_end - shift_duration);
            }

            // start the eventlist
            l.start(start_time + sim_duration);

            //save the data
            si.toMatrixFile(strategy_name+"informationCalls" +  i + ".csv");
            si.toWaitTimeFileConsumer(strategy_name+"waitingTimesConsumer" + i + ".csv");
            si.toWaitTimeFileCorporate(strategy_name+"waitingTimesCorporate" + i + ".csv");
        }
        int cost = (roster[0][0]  + roster[1][0] + roster[2][0])* 8 * 35 + (roster[0][1] + roster[1][1] + roster[2][1]) * 8 * 60;
        System.out.println("______________________Cost of the roster per day: "+cost+"_________________________");

    }
}
