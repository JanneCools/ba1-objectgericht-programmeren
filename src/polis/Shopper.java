package polis;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class Shopper extends MovingActor {

    private static final List<String> NEEDED_BUILDING = List.of("commerce");

    public Shopper(int r, int k, CityMap model, Simulator simulator, BuildingTile home) {
        super(r, k, r, k, model, simulator, home, "shopper");
    }

    @Override
    public List<String> getNeededBuildings() {
        return NEEDED_BUILDING;
    }

    @Override
    public void destinationNotFound() {
        changeHomeCapacity(home, "factor.shop.not.found");
        if (model.userPolygons.containsKey(home.getKey())) {
            Sleeper sleeper = new Sleeper(homeR, homeK, model, simulator, home);
            simulator.addActor(sleeper);
            home.changeResident(this, sleeper);
        }
        removeActor();
    }

    @Override
    public void enterBuilding(String key) {
        changeHomeCapacity(home, "factor.shop.found");
        BuildingTile shop = (BuildingTile) model.userPolygons.get(key);
        Customer customer = new Customer(r, k, homeR, homeK, model, simulator, home, shop);
        home.changeResident(this, customer);
        simulator.addActor(customer);
        removeActor();
    }
}
