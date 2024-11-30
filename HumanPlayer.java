public class HumanPlayer extends Player {


    public HumanPlayer(boolean isPlayerOne) {
        super(isPlayerOne);
    }


    public boolean isHuman(){
        return true;
    }
    public boolean isPlayerOne()
    {
        return isPlayerOne;
    }

    public int getWins()
    {
        return wins;
    }
    public void addWin()
    {
        this.wins++;
    }









}
