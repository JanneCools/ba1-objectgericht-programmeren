package polis;

import java.util.List;

public class Immigrant extends MovingActor {

    private static final List<String> NEEDED_BUILDING = List.of("residence");

    public Immigrant(CityMap model, Simulator simulator) {
        super(-1, 15, 0, 0, model, simulator, null, "immigrant");
        // Een immigrant heeft nog geen huis dus voor de coördinaten homeR en homeK geeft ik gewoon (0, 0) mee
        // en voor home geef ik null mee
    }

    @Override
    public List<String> getNeededBuildings() {
        return NEEDED_BUILDING;
    }
    public String getActorType() {
        return "immigrant";
    }

    @Override
    public void destinationNotFound() {
        simulator.changeTempo(true);
        removeActor();
    }

    @Override
    public void enterBuilding(String keyBuilding) {
        BuildingTile building = (BuildingTile) model.userPolygons.get(keyBuilding);
        Sleeper sleeper = new Sleeper(r, k, model, simulator, building);
        building.addActor(sleeper);
        simulator.addActor(sleeper);
        removeActor();
    }
}
