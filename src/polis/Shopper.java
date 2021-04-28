package polis;

import java.util.List;

public class Shopper extends MovingActor {

    private static final List<String> NEEDED_BUILDING = List.of("commerce");

    public Shopper(int r, int k, CityMap model, Simulator simulator, BuildingTile home) {
        super(r, k, r, k, model, simulator, home, "shopper");
    }

    @Override
    public List<String> getNeededBuildings() {
        return NEEDED_BUILDING;
    }
    public String getActorType() {
        return "shopper";
    }

    @Override
    public void destinationNotFound() {
        changeHomeCapacity(home, "factor.shop.not.found");
        // Enkel van rol veranderen als de woning van de acteur nog bestaat
        if (model.userPolygons.containsKey(home.getKey())) {
            Sleeper sleeper = new Sleeper(homeR, homeK, model, simulator, home);
            simulator.addActor(sleeper);
            home.changeResident(this, sleeper);
        }
        simulator.removeActor(this);
    }

    @Override
    public void enterBuilding(String key) {
        changeHomeCapacity(home, "factor.shop.found");
        BuildingTile shop = (BuildingTile) model.userPolygons.get(key);
        Customer customer = new Customer(r, k, homeR, homeK, model, simulator, home, shop);
        home.changeResident(this, customer);
        simulator.addActor(customer);
        simulator.removeActor(this);
    }
}