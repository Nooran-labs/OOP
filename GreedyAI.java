import java.util.List;

public class GreedyAI extends AIPlayer{
    public GreedyAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }


    public boolean isHuman() {
        return false;
    }



    public Move makeMove(PlayableLogic gameStatus)
    {
        Disc disc2 = null;
        Position p = null;
        Move ans ;
        int currentPlayerNum;
        if(gameStatus.isFirstPlayerTurn()){
            currentPlayerNum = 1;
        }
        else {
            currentPlayerNum = 2;
        }
        if(gameStatus.ValidMoves().size() == 0) {
            System.out.println("Player" + currentPlayerNum + " has no valid moves");
            if (gameStatus instanceof GameLogic) {
                ((GameLogic) gameStatus).IncNoValidM();
                ((GameLogic) gameStatus).ChangeTurn();
            }
            return new Move(disc2,p);

        }
        int maxFlips=0;
        List<Position> vm=gameStatus.ValidMoves();
        for (int i=0;i<vm.size();i++){
            Position PP=vm.get(i);
            if(gameStatus.countFlips(PP)>maxFlips){
                maxFlips=i;
            }
            if(gameStatus.countFlips(PP) == maxFlips){
                if(PP.col()>gameStatus.ValidMoves().get(maxFlips).col()) { // Choose the most right move
                    maxFlips = i;
                }
                if(PP.col() == gameStatus.ValidMoves().get(maxFlips).col()) { // Choose the most right move down if they are at the same column
                    if(PP.row() == gameStatus.ValidMoves().get(maxFlips).row()) {
                        maxFlips = i;
                    }
                }

            }

        }
        Disc disc1 = null;
        if(gameStatus.isFirstPlayerTurn()){

                   disc1 = new SimpleDisc(gameStatus.getFirstPlayer());

        }
        else   { disc1 = new SimpleDisc(gameStatus.getSecondPlayer());}
        ans = new Move(disc1,vm.get(maxFlips));
        return ans;


    }
}
