package simulation;

public class Shift implements CProcess {

    private Queue queue;

    private final CEventList eventlist;

    private Sink sink;

    private int number_csa1;

    private int number_csa2;

    private int shift_end;

    private boolean handle_both;

    public Shift(Queue q, Sink si, CEventList l, boolean handle_both ,int number_csa1, int number_csa2, int shift_end ){
        queue = q;
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
            CSA CSA_consumer= new CSA(queue, sink, eventlist, "consumer CSA nr " + i, 0,false,shift_end);
        }

        for (int i = 0; i < number_csa2; i++) {
            // A csa
            CSA CSA_corporate = new CSA(queue, sink, eventlist, "corporate CSA nr " + i, 1,handle_both,shift_end);
        }

        int day = 24 * 60 * 60;

        //create new shift for the next day
        eventlist.add(new Shift(queue,sink,
                eventlist, handle_both,number_csa1,
                number_csa2, shift_end  + day),
                0,tme + day);
    }
}
