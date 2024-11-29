import java.util.ArrayList;
import java.util.List;

public class GameLogic implements PlayableLogic{
private Player FirstPlayer;
private Player SecondPlayer;
private Player CurrentPlayer;
private int CurrentPlayerNum;
private ArrayList<Move> MoveH;
private boolean Turn=true;
private final int[][] directions = {
        {-1, 0}, {1, 0}, {0, -1}, {0, 1},
        {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
private final int BoardSize = 8; // as in 8x8
    private final Disc [][] board ;
    private void InitializeBoard(){
        if(FirstPlayer==null || SecondPlayer==null){return;}
        CurrentPlayerNum = 1;
        CurrentPlayer = FirstPlayer;
        board[3][3] = new SimpleDisc(FirstPlayer);
        board[4][3] = new SimpleDisc(SecondPlayer);
        board[4][4] = new SimpleDisc(FirstPlayer);
        board[3][4] = new SimpleDisc(SecondPlayer);
    }
    public GameLogic() {

        board = new Disc[BoardSize][BoardSize];
        InitializeBoard();
        MoveH = new ArrayList<>();

    }
    public boolean locate_disc(Position a, Disc disc){
        boolean ans =false ;
           if(has(ValidMoves(),a)) {
               if (disc instanceof BombDisc) {
                   if (disc.getOwner().getNumber_of_bombs() == 0) {
                       System.out.println("You have no more BOMBS💣!!");
                       return ans;
                   }
                   board[a.row()][a.col()] = new BombDisc(CurrentPlayer);
                   disc.getOwner().reduce_bomb();
                   System.out.println("Player" + CurrentPlayerNum + " placed a Bomb 💣 Disc at : " + a.row() + "," + a.col());
               }
               if (disc instanceof UnflippableDisc) {
                   if (disc.getOwner().getNumber_of_unflippedable() == 0) {
                       System.out.println("You have no more Unflippables⭕!!");
                       return ans;
                   }
                   board[a.row()][a.col()] = new UnflippableDisc(CurrentPlayer);
                   disc.getOwner().reduce_unflippedable();
                   System.out.println("Player" + CurrentPlayerNum + " placed an Unflippable ⭕ Disc at : " + a.row() + "," + a.col());
               }
               if (disc instanceof SimpleDisc) {
                   board[a.row()][a.col()] = new SimpleDisc(CurrentPlayer);
                   System.out.println("Player" + CurrentPlayerNum + " placed a Simple ⬤ Disc at : " + a.row() + "," + a.col());
               }


               ans = true;

               for (int i = 0; i < 8; i++) {
                   int[] dir = directions[i];
                   int test = Testflip(a, dir);
                   if (test > 0) {   //if there are flips in direction "dir"
                       Position Nextpos = new Position(a.row() + dir[0], a.col() + dir[1]);
                       while (OnTheBoard(Nextpos.row(), Nextpos.col()) && board[Nextpos.row()][Nextpos.col()].getOwner() != CurrentPlayer) {   //while the next Position is on the board
                           Disc NPDisc = board[Nextpos.row()][Nextpos.col()]; //Next position Disc

                           if (!(NPDisc instanceof UnflippableDisc)) {
                               board[Nextpos.row()][Nextpos.col()].setOwner(CurrentPlayer);
                           }
                           Nextpos = new Position(Nextpos.row() + dir[0], Nextpos.col() + dir[1]);
                       }
                   }
               }
               if (disc instanceof BombDisc) {
                   LocateBomb(a, disc);        //Triggers the Bomb and flips the surrounding enemy discs
               }
               if(isGameFinished()){
                   CurrentPlayer.addWin();
                   System.out.println("Player"+CurrentPlayerNum+" has WON the round!!");
               }
               ChangeTurn();
           }

        return ans;
        }
    private void LocateBomb (Position pos, Disc disc) {

        int r = pos.row();
        int c = pos.col();
        for(int i = 0; i<directions.length;i++){ // test every location around the bomb and try to flip it
            int[] dir= directions[i];
            Position p = new Position(r+dir[0],c+dir[1]);
            Disc PDisc = getDiscAtPosition(p); // the disc in position p
            if(OnTheBoard(p.row(),p.col()) && PDisc != null && disc.getOwner()!=PDisc.getOwner()){// if the Position is on the board and has
                                                                                                  // a disc owned by the other player try to flip it
                if(!(PDisc instanceof UnflippableDisc)){
                    PDisc.setOwner(CurrentPlayer);
                    if(PDisc instanceof BombDisc){
                        LocateBomb(p,PDisc);
                    }
                }
            }
        }
    }
    public boolean has (List<Position> positions,Position p1){
        for(int i = 0; i<positions.size();i++){
            if(positions.get(i).row()==p1.row() && positions.get(i).col() == p1.col()){return true;}
        }
        return false;
    }
    public void ChangeTurn(){
        Turn = !Turn;
        if(CurrentPlayer == FirstPlayer){
            CurrentPlayer = SecondPlayer;
        }
        else {CurrentPlayer = FirstPlayer;}
        if(CurrentPlayerNum == 1){
            CurrentPlayerNum = 2;
        }
        else {CurrentPlayerNum = 1;}
    }
        private boolean OnTheBoard (int r,int c){
        if(r>=BoardSize || c>=BoardSize || r<0 || c<0){
            return false;
        }
        return true;
        }
    public Disc getDiscAtPosition(Position position){
        int row = position.row();
        int col = position.col();
        return board[row][col];
    }
    public int getBoardSize(){
    return this.BoardSize;
    }
    public List<Position> ValidMoves(){
        List<Position> validMoves = new ArrayList<>();
        for(int i = 0; i < BoardSize; i++){
            for (int j = 0; j < BoardSize ;j++){
                if(board[i][j]==null) {
                    Position test = new Position(i, j);
                    int flips = countFlips(test);
                    if(flips!=0){
                        validMoves.add(test);
                    }
                }
            }
        }
        return validMoves;
    }
    public int countFlips(Position a) {
        int flips = 0;


        for (int i = 0; i < 8; i++) {
           flips+= Testflip(a,directions[i]);
        }
        return flips;
    }
    private int Testflip (Position Pos,int[] dir){
        Position clone = Pos;
        int NumOfFlips = 0;
        while (OnTheBoard(clone.row()+dir[0], clone.col()+dir[1])) {   //while the next Position is on the board
            Position Nextpos = new Position(clone.row() + dir[0], clone.col() + dir[1]);
            clone = Nextpos;
            if (board[Nextpos.row()][Nextpos.col()]==null) {
                return 0;
            } else {
                if(board[Nextpos.row()][Nextpos.col()].getOwner()==CurrentPlayer ){return NumOfFlips;}
                if(!(getDiscAtPosition(Pos) instanceof UnflippableDisc)){
                    NumOfFlips++;
                }
            }
        }
        return NumOfFlips;
    }
    public Player getFirstPlayer(){
        return this.FirstPlayer;
    }
    public Player getSecondPlayer(){
        return this.SecondPlayer;
    }
    public void setPlayers(Player player1, Player player2){
        this.FirstPlayer=player1;
        this.SecondPlayer=player2;
    }
    public boolean isFirstPlayerTurn(){
        return this.Turn;
    }
    public boolean isGameFinished(){
    for (int i = 0; i<BoardSize; i++){
        for(int j = 0; j<BoardSize; j++){
            if(board[i][j] == null){
                return false;
            }
        }
    }
    return true;
    }
    public void reset(){
        for (int i = 0; i<BoardSize; i++){
            for(int j = 0; j<BoardSize; j++){
               board[i][j] = null;
                }
            }
        InitializeBoard();
        this.FirstPlayer.reset_bombs_and_unflippedable();
        this.SecondPlayer.reset_bombs_and_unflippedable();
        this.Turn=true;
        }

    public void undoLastMove(){

    }
}