import java.util.List;
import java.util.Random;

public class RandomAI extends AIPlayer{


    public RandomAI(boolean isPlayerOne)
    {
        super(isPlayerOne);
        wins=0;
        reset_bombs_and_unflippedable();
    }

    public boolean isHuman() {
        return false;
    }
    public  Move makeMove(PlayableLogic gameStatus)
    {
        Disc disc2 = null;
        Move ans = null;
        Position p = null;
        Player currentPlayer;
        int currentPlayerNum;
        if(gameStatus.isFirstPlayerTurn()){
            currentPlayer = gameStatus.getFirstPlayer();
            currentPlayerNum = 1;
        }
        else {
            currentPlayer = gameStatus.getSecondPlayer();
            currentPlayerNum = 2;
        }
        if(gameStatus.ValidMoves().size() == 0){
            System.out.println("Player"+currentPlayerNum+" has no valid moves");
            if(gameStatus instanceof GameLogic){
                ((GameLogic) gameStatus).IncNoValidM();
                if (gameStatus.isGameFinished()){
                    ((GameLogic) gameStatus).FinishRoundAndRestart();
                }
                System.out.println(((GameLogic) gameStatus).NVMN());
                ((GameLogic) gameStatus).ChangeTurn();
            }
            return new Move(disc2,p);
        }
            List<Position> vm = gameStatus.ValidMoves();
            int r1 = (int) (Math.random() * 3);
            int r = (int) (Math.random() * (vm.size()));
            ;
            if (gameStatus.ValidMoves().size() != 1) {
                p = vm.get(r);
            } else {
                p = vm.getFirst();
            }

            if (r1 == 0 && currentPlayer.getNumber_of_bombs() > 0) {
                ans = new Move(new BombDisc(currentPlayer), p);
                return ans;
            }
            if (r1 == 1 && currentPlayer.getNumber_of_unflippedable() > 0) {
                ans = new Move(new UnflippableDisc(currentPlayer), p);
                return ans;
            }


            ans = new Move(new SimpleDisc(currentPlayer), p);


            return ans;



}
}