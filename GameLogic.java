import java.util.ArrayList;
import java.util.List;


public class GameLogic implements PlayableLogic{
public  int NoValidMoves = 0;
private Player FirstPlayer = new HumanPlayer(isFirstPlayerTurn());
private Player SecondPlayer = new HumanPlayer(!(isFirstPlayerTurn()));
private Player CurrentPlayer;
private int WinnerDiscs;
private int LoserDiscs;
private int CurrentPlayerNum;
private List<ArrayList<Position>> DiscsFlipedH;
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
    public int NVMN(){
        return this.NoValidMoves;
    }
    public GameLogic() {

        board = new Disc[BoardSize][BoardSize];
        InitializeBoard();
        MoveH = new ArrayList<>();
        DiscsFlipedH = new ArrayList<>();
    }
    public boolean locate_disc(Position a, Disc disc){
        boolean ans =false ;
        if(a==null && disc==null) {
            return true;
        }
        if(ValidMoves().size() == 0){
            NoValidMoves++;
            ChangeTurn();
            return ans;
        }

           if(has(ValidMoves(),a)) {
               NoValidMoves = 0;
               Move PMove = null;
               if (disc instanceof BombDisc) {
                   if (disc.getOwner().getNumber_of_bombs() == 0) {
                       System.out.println("You have no more BOMBS💣!!");
                       return ans;
                   }
                   board[a.row()][a.col()] = new BombDisc(CurrentPlayer);
                    PMove = new Move(disc,a);
                   disc.getOwner().reduce_bomb();
                   System.out.println("Player" + CurrentPlayerNum + " placed a Bomb 💣 Disc at : " + (a.row()+1) + "," + (a.col()+1));
               }
               if (disc instanceof UnflippableDisc) {
                   if (disc.getOwner().getNumber_of_unflippedable() == 0) {
                       System.out.println("You have no more Unflippables⭕!!");
                       return ans;
                   }
                   board[a.row()][a.col()] = new UnflippableDisc(CurrentPlayer);
                   disc.getOwner().reduce_unflippedable();
                    PMove = new Move(disc,a);
                   System.out.println("Player" + CurrentPlayerNum + " placed an Unflippable ⭕ Disc at : " + (a.row()+1) + "," + (a.col()+1));
               }
               if (disc instanceof SimpleDisc) {
                   board[a.row()][a.col()] = new SimpleDisc(CurrentPlayer);
                   PMove = new Move(disc,a);
                   System.out.println("Player" + CurrentPlayerNum + " placed a Simple ⬤ Disc at : " + (a.row()+1) + "," + (a.col()+1));
               }
               ans = true;
               MoveH.add(PMove);
                ArrayList<Position> FlipH = new ArrayList<>();
                DiscsFlipedH.add(FlipH);
               for (int i = 0; i < 8; i++) {
                   int[] dir = directions[i];
                   int test = Testflip(a, dir);
                   if (test > 0) {   //if there are flips in direction "dir"
                       Position Nextpos = new Position(a.row() + dir[0], a.col() + dir[1]);
                       while (OnTheBoard(Nextpos.row(), Nextpos.col()) && board[Nextpos.row()][Nextpos.col()].getOwner() != CurrentPlayer) {   //while the next Position is on the board
                           Disc NPDisc = board[Nextpos.row()][Nextpos.col()]; //Next position Disc

                           if (!(NPDisc instanceof UnflippableDisc)) {
                               if (!(isNearB(a,Nextpos,disc))) {           // if the next Position isn't a bomb in the radius of
                                                                           // the explosion or the disc that isn't being placed is a Bomb Disc
                                   board[Nextpos.row()][Nextpos.col()].setOwner(CurrentPlayer);
                                   System.out.println("Player"+CurrentPlayerNum+" fliped the "+board[Nextpos.row()][Nextpos.col()].getType()+" Disc at location"+(Nextpos.row()+1)+","+(Nextpos.col()+1));
                                   DiscsFlipedH.getLast().add(Nextpos);
                               }
                           }
                           Nextpos = new Position(Nextpos.row() + dir[0], Nextpos.col() + dir[1]);
                       }
                   }
               }
               if (disc instanceof BombDisc) {
                   LocateBomb(a, disc);        //Triggers the Bomb and flips the surrounding enemy discs
               }
               if(isGameFinished()){
                   FinishRoundAndRestart();
               }
               else {
                   ChangeTurn();
               }
           }

        return ans;
        }
    private void LocateBomb (Position pos, Disc disc) {

        int r = pos.row();
        int c = pos.col();
        for(int i = 0; i<directions.length;i++){ // test every location around the bomb and try to flip it
            int[] dir= directions[i];
            Position p = new Position(r+dir[0],c+dir[1]);
            if(OnTheBoard(p.row(),p.col())) { // if the Position is on the board
                Disc PDisc = getDiscAtPosition(p); // the disc in position p
                if ( PDisc != null && disc.getOwner() != PDisc.getOwner()) {// if the Position has
                                                                            // a disc owned by the other player try to flip it
                    if (!(PDisc instanceof UnflippableDisc)) {
                        PDisc.setOwner(CurrentPlayer);
                        DiscsFlipedH.getLast().add(p);
                        if (PDisc instanceof BombDisc) {
                            LocateBomb(p, PDisc);
                        }
                    }
                }
            }
        }
    }
    public boolean isNearB (Position P1 ,Position P2 , Disc disc1) {    // returns true if disc1 is a Bomb Disc and P2 is in P1 radius
            if(disc1 instanceof  BombDisc) {
                if (board[P1.row()][P1.col()] instanceof BombDisc) {
                    return true;
                }
        }
        return false;
    }
    public void FinishRoundAndRestart(){
          Player Winner = WiningPlayer();
          Winner.addWin();
          int otherPlayerNum;
          if (CurrentPlayerNum == 1) {
              otherPlayerNum = 2;
          } else {
              otherPlayerNum = 1;
          }
          System.out.println("Player" + CurrentPlayerNum + " has WON the round with " + WinnerDiscs + " discs!! Player" + otherPlayerNum + " had " + LoserDiscs + " discs.");
          reset();
    }
    public void IncNoValidM(){
      NoValidMoves++;
    }
    public boolean has (List<Position> positions,Position p1){
        for(int i = 0; i<positions.size();i++){
            if(positions.get(i).row()==p1.row() && positions.get(i).col() == p1.col()){return true;}
        }
        return false;
    }
    public Player WiningPlayer() {
        int Player1 = 0;
        int Player2 = 0;
        Player winner;
        for (int i = 0; i < BoardSize; i++) {
            for (int j = 0; j < BoardSize; j++) {
                if(board[i][j] != null) {
                    if (board[i][j].getOwner() == FirstPlayer) {
                        Player1++;
                    }
                    if (board[i][j].getOwner() == SecondPlayer) {
                        Player2++;
                    }
                }

            }
        }
        if(Player1 > Player2){
            winner = FirstPlayer ;
            WinnerDiscs = Player1;
            LoserDiscs = Player2;
        }  else {
            winner = SecondPlayer;
            WinnerDiscs = Player2;
            LoserDiscs = Player1;
        }
        return winner;
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
            if (board[Nextpos.row()][Nextpos.col()]==null || !(OnTheBoard(clone.row() + dir[0], clone.col() + dir[1])) ) {
                return 0;
            } else {
                if(board[Nextpos.row()][Nextpos.col()].getOwner()==CurrentPlayer ){return NumOfFlips;}
                if(!(getDiscAtPosition(Nextpos) instanceof UnflippableDisc)){
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
            if(BoardIsFull() || CPOAD() || NoValidMoves == 2){
                return true;
            }
            return false;
    }

    private boolean CPOAD (){         // Current Player Owns All Discs
            for (int i = 0; i < BoardSize; i++){
                 for (int j = 0; j < BoardSize; j++) {
                      if(board[i][j] != null && board[i][j].getOwner() != CurrentPlayer){
                          return false;
                      }
                 }
            }
            return true;
    }
    private boolean BoardIsFull(){
       for (int i = 0; i < BoardSize; i++) {
           for (int j = 0; j < BoardSize; j++) {
                if(board[i][j] == null) {
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
        System.out.println("Undoing last move...");
        if (MoveH.size() == 0){
            System.out.println("No previous move available to undo!");
            return;
        }
        for (int i = 0; i < DiscsFlipedH.getLast().size(); i++){  // Unflip the previous flipped discs
             Position p = new Position(DiscsFlipedH.getLast().get(i).row(),DiscsFlipedH.getLast().get(i).col());
            System.out.println("Flipping back "+board[p.row()][p.col()].getType()+" at "+(p.row()+1)+","+(p.col()+1));
             board[p.row()][p.col()].setOwner(CurrentPlayer);   // Because we haven't changed turns the Current player's
                                                                // Discs got flipped the last turn, so we flip them back
        }
        DiscsFlipedH.removeLast();
        board[MoveH.getLast().position().row()][MoveH.getLast().position().col()] = null;    // Remove the disc that was located
        System.out.println("Removing"+MoveH.getLast().disc().getType()+" at "+(MoveH.getLast().position().row()+1)+","+(MoveH.getLast().position().col()+1));
        MoveH.removeLast();                                                                                          
        ChangeTurn();;
        
    }
}