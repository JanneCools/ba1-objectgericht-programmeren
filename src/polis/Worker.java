package polis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Worker extends StaticActor {

    private final BuildingTile work;
    private final int stepsPerGoods;

    public Worker(int r, int k, int homeR, int homeK, CityMap model, Simulator simulator,
                  BuildingTile home, BuildingTile work) {
        super(r, k, homeR, homeK, model, simulator, home, "worker");
        int temp = 0;
        Properties properties = new Properties();
        try (InputStream in = getClass().getResourceAsStream("engine.properties")) {
            properties.load(in);
            temp = Integer.parseInt(properties.getProperty("steps.per.goods"));
        } catch (IOException ex) {
            //Doe niets
        }
        this.work = work;
        work.addActor(this);
        stepsPerGoods = temp;
    }

    @Override
    public void act() {
        super.act();
        if (age % stepsPerGoods == 0) {
            Goods goods = new Goods(r, k, model, simulator, work);
            simulator.addActor(goods);
        }
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
