package polis;

public abstract class StaticActor extends Actor {


    public StaticActor(int r, int k, int homeR, int homeK, CityMap model, Simulator simulator,
                       BuildingTile home, String actor) {
        super(r, k, homeR, homeK, model, simulator, home, actor);
    }

    public abstract void changeRole();
    public abstract String getActorType();

    public void act() {
        age --;
        if (age == 0) {
            changeRole();
        }
    }

}
