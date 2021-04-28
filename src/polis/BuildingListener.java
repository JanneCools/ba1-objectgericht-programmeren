package polis;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import java.util.ArrayList;
import java.util.Map;

public class BuildingListener {

    private final CityMap model;
    private final Map<PolygonTile, Paint> originalPaint;
    private final CityMapListener cityMapListener;
    private static final int CELL_SIZE = 64;

    // Veld om na te kijken of een gebouw geplaatst mag worden, ik maak er een veld van zodat die
    // makkelijk door meerdere methodes gebruikt kan worden. Het is een array
    // omdat er 4 tegels beschikbaar moeten zijn om een gebouw te mogen plaatsen.
    private final boolean[] availabilityBuilding;

    public BuildingListener(CityMap model, CityMapListener listener, Map<PolygonTile, Paint> originalPaint) {
        this.model = model;
        this.cityMapListener = listener;
        this.originalPaint = originalPaint;
        availabilityBuilding = new boolean[4];
    }

    public void checkBuildingAvailability(String key) {
        for (int i = 0; i < 4; i++) {
            availabilityBuilding[i] = false;
        }
        if (model.getPolygonMap().containsKey(key)) {
            PolygonTile polygonTile = model.getPolygonMap().get(key);
            ArrayList<PolygonTile> polygonNeighbours = polygonTile.getNeighbours(model.getPolygonMap(), true);
            int index = 0;
            for (PolygonTile tile : polygonNeighbours) {
                String tileKey = tile.getKey();
                if (polygonNeighbours.size() != 4) {
                    tile.setFill(Color.rgb(255, 0, 0, 0.5));
                } else {
                    availabilityBuilding[index] = cityMapListener.checkAvailability(tileKey);
                }
                index++;
            }
        }
    }

    public void addBuilding(String key) {
        int index = 0;
        while (index < availabilityBuilding.length && availabilityBuilding[index]) {
            index++;
        }
        if (index == availabilityBuilding.length) {         //gebouw mag geplaatst worden
            int r = Integer.parseInt(key.split("-")[0]);
            int k = Integer.parseInt(key.split("-")[1]);
            BuildingTile newTile = new IndustryTile(CELL_SIZE, r, k, originalPaint);
            if (model.getButtonSelected().equals("residence")) {
                newTile = new ResidenceTile(CELL_SIZE, r, k, originalPaint);
            } else if (model.getButtonSelected().equals("commerce")) {
                newTile = new CommerceTile(CELL_SIZE, r, k, originalPaint);
            }
            model.getChildren().add(newTile);
            String[] newKeys = {r + "-" + k, (r - 1) + "-" + k, r + "-" + (k + 1), (r - 1) + "-" + (k + 1)};
            for (String newKey : newKeys) {
                model.userPolygons.put(newKey, newTile);
            }
            originalPaint.put(newTile, newTile.getFill());
        }
    }
}
