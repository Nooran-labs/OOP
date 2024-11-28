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
        Disc disc;
        if(gameStatus.isFirstPlayerTurn()){
        disc = new SimpleDisc(gameStatus.getFirstPlayer());}
        else { disc = new SimpleDisc(gameStatus.getSecondPlayer());}
        ans = new Move(disc,vm.get(maxFlips));
        return ans;


    }
}
