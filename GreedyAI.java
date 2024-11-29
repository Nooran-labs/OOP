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
        Move ans;
        int maxFlips=0;
        List<Position> vm=gameStatus.ValidMoves();
        for (int i=0;i<vm.size();i++){
            Position PP=vm.get(i);
            if(gameStatus.countFlips(PP)>maxFlips){
                maxFlips=i;
            }

        }
        Disc disc1;
        if(gameStatus.isFirstPlayerTurn()){
            if(gameStatus.getFirstPlayer().number_of_bombs > 0){
                disc1 = new BombDisc(gameStatus.getFirstPlayer());
            }
            else {
                   disc1 = new SimpleDisc(gameStatus.getFirstPlayer());
            }
        }
        else  if(gameStatus.getSecondPlayer().number_of_bombs > 0){
            disc1 = new BombDisc(gameStatus.getSecondPlayer());
        }
        else { disc1 = new SimpleDisc(gameStatus.getSecondPlayer());}
        ans = new Move(disc1,vm.get(maxFlips));
        return ans;


    }
}
