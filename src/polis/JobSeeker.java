package polis;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class JobSeeker extends MovingActor {

    private static final List<String> NEEDED_BUILDINGS = List.of("commerce", "industry");

    public JobSeeker(int r, int k, CityMap model, Simulator simulator, BuildingTile home) {
        super(r, k, r, k, model, simulator, home, "jobseeker");
        //De meegekregen (r,k)-coördinaat is ook de coördinaat van het huis van de werkzoekende
    }

    @Override
    public List<String> getNeededBuildings() {
        return NEEDED_BUILDINGS;
    }

    @Override
    public void destinationNotFound() {
        changeHomeCapacity(home, "factor.job.not.found");
        // Nakijken of het gebouw nog bestaat
        if (model.userPolygons.containsKey(home.getKey())) {
            Sleeper sleeper = new Sleeper(homeR, homeK, model, simulator, home);
            simulator.addActor(sleeper);
            home.changeResident(this, sleeper);
        }
        removeActor();
    }

    @Override
    public void enterBuilding(String buildingKey) {
        BuildingTile work = (BuildingTile) model.userPolygons.get(buildingKey);
        if (work.getBackground().equals("commerce")) {
            Trader trader = new Trader(r, k, homeR, homeK, model, simulator, home, work);
            simulator.addActor(trader);
            home.changeResident(this, trader);
        } else {
            Worker worker = new Worker(r, k, homeR, homeK, model, simulator, home, work);
            simulator.addActor(worker);
            home.changeResident(this, worker);
        }
        changeHomeCapacity(home, "factor.job.found");
        removeActor();
    }
}
