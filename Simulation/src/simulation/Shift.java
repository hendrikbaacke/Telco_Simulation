package simulation;

public class Shift implements CProcess {

    private Queue queue_con;

    private Queue queue_cor;

    private final CEventList eventlist;

    private Sink sink;

    //amount consumer CSA
    private int number_csa1;

    //amount only corporate CSA
    private int number_csa2;

    private int shift_end;


    public Shift(Queue q1, Queue q2,  Sink si, CEventList l, int shift_end, int number_csa1, int number_csa2){
        queue_con = q1;
        queue_cor = q2;
        sink = si;
        eventlist = l;
        this.number_csa1 = number_csa1;
        this.number_csa2 = number_csa2;
        this.shift_end = shift_end;
    }

    @Override
    public void execute(int type, double tme) {
        //set idle CSA counter to zero
        CSA.corpCsaIdleCounter = 0;
        for (int i = 0; i < number_csa1; i++) {
            // A consumer CSA (handle both is always false for them)
            CSA CSA_consumer= new CSA(queue_con, sink, eventlist, "consumer CSA nr " + i, shift_end,0);
        }

        for (int i = 0; i < number_csa2; i++) {
            // A csa
            CSA CSA_corporate = new CSA(queue_con,queue_cor, sink, eventlist, "corporate CSA nr " + i, shift_end,1);
        }

    }

    public String toString(){
        return "shift ending at seconds " + this.shift_end + " hours " + this.shift_end /3600;
    }
}

