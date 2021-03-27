package polis;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import java.util.ArrayList;
import java.util.Map;

public class BuildingListener {

    private final CityMap model;
    private Map<PolygonTile, Paint> originalPaint;
    private final CityMapListener cityMapListener;
    private static final int CELL_SIZE = 64;

    // Veld om na te kijken of een gebouw geplaatst mag worden, ik maak er een veld van zodat die
    // makkelijk door meerdere methodes gebruikt kunnen worden. Het is een array
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
                    tile.setFill(Color.RED);
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
        if (index == availabilityBuilding.length) {
            int r = Integer.parseInt(key.split("-")[0]);
            int k = Integer.parseInt(key.split("-")[1]);
            String imageName = "polis/tiles/" + model.getButtonSelected() + "-0.png";
            BuildingTile newTile = new BuildingTile(CELL_SIZE, r, k, imageName);
            model.getChildren().add(newTile);
            String[] newKeys = {r + "-" + k, (r - 1) + "-" + k, r + "-" + (k + 1), (r - 1) + "-" + (k + 1)};
            for (String newKey : newKeys) {
                model.userPolygons.put(newKey, newTile);
            }
            originalPaint.put(newTile, newTile.getFill());
        }
    }

    public void changeBuildingImage(String key) {
        if (model.userPolygons.containsKey(key) && model.userPolygons.get(key).getBackground().equals("building")) {
            BuildingTile tile = (BuildingTile) model.userPolygons.get(key);
            String imageName = tile.getImageName();
            int index = imageName.length() - 5;
            int getal = Integer.parseInt(imageName.substring(index, index + 1));
            getal ++;
            if (getal == 4) {
                getal = 0;
            }
            imageName = imageName.substring(0, index) + getal + imageName.substring(index + 1);
            tile.setImage(imageName, originalPaint);
        }
    }
}
