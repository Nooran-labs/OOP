import java.util.ArrayList;
import java.util.List;

public class GameLogic implements PlayableLogic{
private Player FirstPlayer;
private Player SecondPlayer;
private Player CurrentPlayer;
private ArrayList<Move> MoveH;
private boolean Turn=true;
private final int BoardSize = 8; // as in 8x8
    private final Disc [][] board ;
    public GameLogic() {
        board = new Disc[BoardSize][BoardSize];
        MoveH = new ArrayList<>();
        reset();}
    public boolean locate_disc(Position a, Disc disc){
            int row = position.row();
            int col = position.col();

            // טיפול בדיסק פצצה
            if (disc instanceof BombDisc) {
                // הנחת דיסק הפצצה
                board[row][col] = disc;
                placeBombDisc(row, col, disc);

                // הוספת המהלך להיסטוריה
                moveHistory.add(new Move(position, disc));

                // מעבר לשחקן הבא
                isFirstPlayerTurn = !isFirstPlayerTurn;
                currentPlayer = isFirstPlayerTurn ? firstPlayer : secondPlayer;
                return true;
            }

            // יצירת רשימה לדיסקים שיתווספו אם המהלך חוקי
            List<Position> discsToFlip = new ArrayList<>();

            // בדיקת כל 8 הכיוונים
            int[][] directions = {
                    {-1, -1}, {-1, 0}, {-1, 1},
                    {0, -1},         {0, 1},
                    {1, -1}, {1, 0}, {1, 1}
            };

            for (int[] direction : directions) {
                List<Position> currentFlips = getFlippableDiscs(row, col, direction[0], direction[1], disc);
                discsToFlip.addAll(currentFlips);
            }

            // אם אין דיסקים להפוך, המהלך לא חוקי
            if (discsToFlip.isEmpty()) {
                return false;
            }

            // הנחת הדיסק על הלוח
            board[row][col] = disc;

            // הפיכת הדיסקים בכיוונים החוקיים
            for (Position pos : discsToFlip) {
                if (!(board[pos.row()][pos.col()] instanceof UnflippableDisc)) { // דיסקים בלתי הפיכים לא מתהפכים
                    board[pos.row()][pos.col()].setOwner(disc.getOwner());
                }
            }

            // הוספת המהלך להיסטוריה
            moveHistory.add(new Move(position, disc));

            // מעבר לשחקן הבא
            isFirstPlayerTurn = !isFirstPlayerTurn;
            currentPlayer = isFirstPlayerTurn ? firstPlayer : secondPlayer;

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
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        if (isFirstPlayerTurn()) {
            this.CurrentPlayer = FirstPlayer;
        } else {
            this.CurrentPlayer = SecondPlayer;
        }

        for (int i = 0; i < 8; i++) {
           flips+= Testflip(a,directions[i]);
        }
        return flips;
    }
    private int Testflip (Position a,int[] dir){
        Position clone = a;
        int NumOfFlips = 0;
        while (clone.col() + dir[1] >= 0 && clone.row() + dir[0] >= 0 && clone.col() + dir[1] <= 7 && clone.row() + dir[0] <= 7) {   //while the next Position on the board
            Position Nextpos = new Position(clone.row() + dir[0], clone.col() + dir[1]);
            if (board[Nextpos.row()][Nextpos.col()]==null) {
                return 0;
            } else {
                if(board[Nextpos.row()][Nextpos.col()].getOwner()==CurrentPlayer ){return NumOfFlips;}
                NumOfFlips++;
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
        this.FirstPlayer.reset_bombs_and_unflippedable();
        this.SecondPlayer.reset_bombs_and_unflippedable();
        this.Turn=true;
        }

    public void undoLastMove(){

    }
}