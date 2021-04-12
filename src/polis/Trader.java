package polis;

public class Trader extends StaticActor {

    private final BuildingTile work;

    public Trader(int r, int k, int homeR, int homeK, CityMap model, Simulator simulator,
                  BuildingTile home, BuildingTile work) {
        super(r, k, homeR, homeK, model, simulator, home, "trader");
        this.work = work;
        work.addActor(this);
    }

    @Override
    public void changeRole() {
        Shopper shopper = new Shopper(homeR, homeK, model, simulator, home);
        home.changeResident(this, shopper);
        work.removeActor(this);
        simulator.removeActor(this);
        simulator.addActor(shopper);
    }

}
