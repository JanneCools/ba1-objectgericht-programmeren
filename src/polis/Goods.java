package polis;

import java.util.List;

public class Goods extends MovingActor {

    private static final List<String> NEEDED_BUILDING = List.of("commerce");
    private final BuildingTile industry;

    public Goods(int r, int k, CityMap model, Simulator simulator, BuildingTile industry) {
        super(r, k, 0, 0, model, simulator, null, "goods");
        // Goederen hebben geen huis, dus houd ik een eigen veld bij met de industrie waar ze gemaakt worden.
        this.industry = industry;
    }

    @Override
    public List<String> getNeededBuildings() {
        return NEEDED_BUILDING;
    }
    public String getActorType() {
        return "goods";
    }

    @Override
    public void destinationNotFound() {
        changeHomeCapacity(industry, "factor.goods.not.delivered");
        removeActor();
    }

    @Override
    public void enterBuilding(String buildingKey) {
        changeHomeCapacity(industry, "factor.goods.delivered");
        BuildingTile shop = (BuildingTile) model.userPolygons.get(buildingKey);
        shop.addActor(this);
        removeActor();
    }
}
