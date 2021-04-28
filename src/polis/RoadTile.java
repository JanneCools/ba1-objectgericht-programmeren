package polis;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import java.util.Map;

public class RoadTile extends PolygonTile {

    private static final Image[] ROADIMAGES = {
            new Image("polis/tiles/road-0.png"), new Image("polis/tiles/road-1.png"),
            new Image("polis/tiles/road-2.png"), new Image("polis/tiles/road-3.png"),
            new Image("polis/tiles/road-4.png"), new Image("polis/tiles/road-5.png"),
            new Image("polis/tiles/road-6.png"), new Image("polis/tiles/road-7.png"),
            new Image("polis/tiles/road-8.png"), new Image("polis/tiles/road-9.png"),
            new Image("polis/tiles/road-10.png"), new Image("polis/tiles/road-11.png"),
            new Image("polis/tiles/road-12.png"), new Image("polis/tiles/road-13.png"),
            new Image("polis/tiles/road-14.png"), new Image("polis/tiles/road-15.png")
    };

    private static final int[] CHANGES = new int[] {-1, 0, 0, 1, 1, 0, 0, -1};
    //De array 'changes' gebruik ik bij het leggen van de weg (zie methode 'addRoadTile').

    public RoadTile(int cellsize, int r, int k) {
        super(cellsize, 1, r, k);
    }

    public String getBackground() {
        return "road";
    }

    // Bij het toevoegen of verwijderen van de weg (1 tegel), moeten de naburige wegen hun achtergrond aanpassen
    public void changeBackground(CityMap model, Map<PolygonTile, Paint> originalPaint, boolean firstTime) {
        int index = 0;
        for (int i = 0; i < 8; i += 2) {
            int rKey = r + CHANGES[i];          //Met behulp van de array 'changes' kijk ik naar de buren van de polygon
            int kKey = k + CHANGES[i+1];        //om te zien of deze al dan niet als achtergrond een foto van een weg hebben.
            String key = rKey + "-" + kKey;
            PolygonTile tile = null;
            if (model.userPolygons.containsKey(key) && model.userPolygons.get(key).getBackground().equals("road")
                    && ! model.userPolygons.get(key).equals(this)) {
                tile = model.userPolygons.get(key);
            } else if (model.getPolygonMap().containsKey(key)
                    && model.getPolygonMap().get(key).getBackground().equals("road")) {
                tile = model.getPolygonMap().get(key);
            }
            if (tile != null) {
                index += (int) Math.pow(2, i/2.0);
                if (firstTime) {
                    tile.changeBackground(model, originalPaint, false);
                    //De parameter "firstTime" zorgt ervoor dat er geen oneindige loop ontstaat.
                }
            }
        }
        setFill(new ImagePattern(ROADIMAGES[index]));
        if (originalPaint != null) {
            if (originalPaint.containsKey(this)) {
                originalPaint.replace(this, getFill());
            } else {
                originalPaint.put(this, getFill());
            }
        }
    }

    public void remove(CityMap model, Map<PolygonTile, Paint> originalPaint) {
        super.remove(model, originalPaint);
        PolygonTile mapTile = model.getPolygonMap().get(getKey());
        mapTile.checkNeighbourRoads(model, originalPaint);
    }
}
