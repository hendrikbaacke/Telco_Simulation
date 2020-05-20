package simulation;

public class Shift implements CProcess {

    private Queue queue_con;

    private Queue queue_cor;

    private final CEventList eventlist;

    private Sink sink;

    private int number_csa1;

    private int number_csa2;

    private int shift_end;

    private boolean handle_both;

    public Shift(Queue q1, Queue q2,  Sink si, CEventList l, boolean handle_both , int shift_end, int number_csa1, int number_csa2){
        queue_con = q1;
        queue_cor = q2;
        sink = si;
        eventlist = l;
        this.number_csa1 = number_csa1;
        this.number_csa2 = number_csa2;
        this.shift_end = shift_end;
        this.handle_both = handle_both;
    }

    @Override
    public void execute(int type, double tme) {
        for (int i = 0; i < number_csa1; i++) {
            // A consumer CSA (handle both is always false for them)
            CSA CSA_consumer= new CSA(queue_con, sink, eventlist, "consumer CSA nr " + i, shift_end);
        }

        for (int i = 0; i < number_csa2; i++) {
            // A csa
            CSA CSA_corporate = new CSA(queue_cor, queue_con, sink, eventlist, "corporate CSA nr " + i, shift_end);
        }
    }

    public String toString(){
        return "shift ending at seconds " + this.shift_end + " hours " + this.shift_end /3600;
    }
}

