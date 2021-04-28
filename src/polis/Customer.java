package polis;

public class Customer extends StaticActor {

    private final BuildingTile shop;

    public Customer(int r, int k, int homeR, int homeK, CityMap model, Simulator simulator,
                  BuildingTile home, BuildingTile shop) {
        super(r, k, homeR, homeK, model, simulator, home, "customer");
        this.shop = shop;
        shop.addActor(this);
    }

    @Override
    public String getActorType() {
        return "customer";
    }

    @Override
    public void changeRole() {
        shop.sellGoods();
        shop.removeActor(this);
        //Enkel van rol veranderen als de woning van de acteur nog bestaat.
        if (model.userPolygons.containsKey(home.getKey())) {
            Sleeper sleeper = new Sleeper(homeR, homeK, model, simulator, home);
            home.changeResident(this, sleeper);
            simulator.addActor(sleeper);
        }
        simulator.removeActor(this);
    }
}
