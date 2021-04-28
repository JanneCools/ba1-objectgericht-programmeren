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
    public String getActorType() {
        return "trader";
    }

    @Override
    public void changeRole() {
        // Enkel van rol veranderen als de woning van de acteur er nog staat
        if (model.userPolygons.containsKey(home.getKey())) {
            Shopper shopper = new Shopper(homeR, homeK, model, simulator, home);
            home.changeResident(this, shopper);
            work.removeActor(this);
            simulator.addActor(shopper);
        }
        simulator.removeActor(this);
    }
}
