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

    //amount flexible corporate CSA
    private int number_csa3;

    private int shift_end;


    public Shift(Queue q1, Queue q2,  Sink si, CEventList l, int shift_end, int number_csa1, int number_csa2, int number_csa3){
        queue_con = q1;
        queue_cor = q2;
        sink = si;
        eventlist = l;
        this.number_csa1 = number_csa1;
        this.number_csa2 = number_csa2;
        this.number_csa3 = number_csa3;
        this.shift_end = shift_end;
    }

    @Override
    public void execute(int type, double tme) {
        for (int i = 0; i < number_csa1; i++) {
            // A consumer CSA (handle both is always false for them)
            CSA CSA_consumer= new CSA(queue_con, sink, eventlist, "consumer CSA nr " + i, shift_end,0);
        }

        for (int i = 0; i < number_csa2; i++) {
            // A csa
            CSA CSA_corporate = new CSA(queue_cor, sink, eventlist, "corporate CSA nr " + i, shift_end,1);
        }

        for (int i = 0; i < number_csa3; i++) {
            // A csa
            CSA CSA_corporate_flex = new CSA(queue_cor, queue_con, sink, eventlist, "corporate CSA nr " + i, shift_end,1);
        }
    }

    public String toString(){
        return "shift ending at seconds " + this.shift_end + " hours " + this.shift_end /3600;
    }
}

