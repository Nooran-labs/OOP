public class BombDisc implements Disc{
    private Player player;

    public BombDisc(Player p)
    {

        this.player=p;
    }

    public Player getOwner()
    {
        return this.player;
    }

    public void setOwner(Player player)
    {
        this.player=player;

    }


    public String getType()
    {
        return "💣";
    }







}
