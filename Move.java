public class Move {

    private Disc d;
    private Position p; // Position on the board for the move

    public  Move(Disc disc, Position position) {
        this.d = disc;
        this.p = position;
    }


    public Position position() {
        return p;
    }

    public Disc disc() {return d;}
















}
