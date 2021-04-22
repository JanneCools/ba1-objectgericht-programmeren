package polis;

import javafx.scene.paint.Paint;
import java.util.ArrayList;
import java.util.Map;

public class RoadListener {

    private final CityMap model;
    private Map<PolygonTile, Paint> originalPaint;
    private final CityMapListener cityMapListener;
    private static final int CELL_SIZE = 64;

    // Veld om alle tegels bij te houden die door de gebruiker worden aangeduid om er een weg van de maken
    // In de lijst worden de coördinaten bijgehouden in de vorm "r-k"
    private final ArrayList<String> selectedRoadKeys;

    public RoadListener(CityMap model, CityMapListener cityMapListener, Map<PolygonTile, Paint> originalPaint) {
        this.model = model;
        this.originalPaint = originalPaint;
        this.cityMapListener = cityMapListener;
        selectedRoadKeys = new ArrayList<>();
    }

    public void addFirstRoadKey(String key) {
        cityMapListener.checkAvailability(key);
        selectedRoadKeys.add(key);
    }

    public void createLRoad(String key) {
        int r = Integer.parseInt(key.split("-")[0]);
        int k = Integer.parseInt(key.split("-")[1]);
        String firstKey = selectedRoadKeys.get(0);
        int firstR = Integer.parseInt(firstKey.split("-")[0]);
        int firstK = Integer.parseInt(firstKey.split("-")[1]);
        cityMapListener.setOriginalPaint();
        selectedRoadKeys.clear();
        //Elke keer dat de muis een andere tegel aanduidt (door erover te slepen),
        // moet de lijst van de coördinaten van de weg die gemaakt moet worden, terug leeggemaakt worden.
        // Anders worden alle vorige coördinaten ook nog bijgehouden.
        // Enkel de coördinaten van de eerst aangeduide tegel moet behouden worden.
        selectedRoadKeys.add(firstKey);
        int minR = Math.min(r, firstR);
        int maxR = Math.max(r, firstR);
        int minK = Math.min(k, firstK);
        int maxK = Math.max(k, firstK);
        for (int i = minR; i <= maxR; i++) {
            String newKey = i + "-" + firstK;
            cityMapListener.checkAvailability(newKey);
            selectedRoadKeys.add(newKey);
        }
        for (int i = minK; i < maxK; i++) {
            String newKey = r + "-" + i;
            cityMapListener.checkAvailability(newKey);
            selectedRoadKeys.add(newKey);
        }
    }

    public void placeRoad() {
        for (String key : selectedRoadKeys) {
            if (cityMapListener.checkAvailability(key)) {
                int r = Integer.parseInt(key.split("-")[0]);
                int k = Integer.parseInt(key.split("-")[1]);
                RoadTile roadTile = new RoadTile(CELL_SIZE, r, k);
                model.userPolygons.put(r + "-" + k, roadTile);
                model.getChildren().add(roadTile);
                roadTile.changeBackground(model, originalPaint, true);
            }
        }
        selectedRoadKeys.clear();
    }
}
