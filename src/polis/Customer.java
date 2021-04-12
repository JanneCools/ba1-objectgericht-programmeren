package polis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Customer extends StaticActor {

    private final BuildingTile shop;

    public Customer(int r, int k, int homeR, int homeK, CityMap model, Simulator simulator,
                  BuildingTile home, BuildingTile shop) {
        super(r, k, homeR, homeK, model, simulator, home, "customer");
        this.shop = shop;
        shop.addActor(this);
    }

    @Override
    public void changeRole() {
        shop.sellGoods();
        shop.removeActor(this);
        if (model.userPolygons.containsKey(home.getKey())) {
            Sleeper sleeper = new Sleeper(homeR, homeK, model, simulator, home);
            home.changeResident(this, sleeper);
            simulator.addActor(sleeper);
        }
        removeActor();
    }
}
