package polis;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static java.lang.Integer.max;
import static java.lang.Integer.min;


public class CityMapListener extends Pane implements InvalidationListener {

    private final CityMap model;
    private static final int CELL_SIZE = 64;

    // Veld om bij te houden waar de muis zich momenteel bevind (in het formaat "r-k" zoals bij de lijsten in het model)
    private String key;

    // Veld om alle tegels bij te houden die door de gebruiker worden aangeduid om er een weg van de maken
    // In de lijst worden de coördinaten bijgehouden in de vorm "r-k"
    private final ArrayList<String> selectedRoadKeys;

    // Veld om na te kijken of een gebouw geplaatst mag worden, ik maak er een veld van zodat die
    // makkelijk door meerdere methodes gebruikt kunnen worden. Het is een array
    // omdat er 4 tegels beschikbaar moeten zijn om een gebouw te mogen plaatsen.
    private final boolean[] availabilityBuilding;

    /*
        Per tegel hou ik zijn originele achtergrond bij zodat, wanneer de cursor
        de tegel verlaat, de tegel weer zijn originele achtergrond krijgt (met de methode 'setOriginalPaint')
        (want wanneer de cursor op de tegel staat, krijgt deze een andere achtergrondskleur of omranding).
        Deze achtergrond kan ik dan ook makkelijk gaandeweg aanpassen.
     */
    private final Map<PolygonTile, Paint> originalPaint;

    // Ik gebruik verschillende Maps die telkens andere methodes uitvoeren afhankelijk van de gebeurtenis van de muis
    // en van de knop die is ingedrukt.
    private final Map<String, Runnable> MOUSEMOVED_METHODS = Map.of(
            "bulldozer", () -> setStroke(Color.RED), "selection", () -> setStroke(Color.WHITE),
            "road", () -> checkAvailability(key), "industry", this::checkBuildingAvailability,
            "commerce", this::checkBuildingAvailability, "residence", this::checkBuildingAvailability
    );

    // Als de knop van de weg is ingedrukt, moet er niets gedaan worden bij mouseClicked,
    // om de weg te maken gebruik ik immers mousePressed, mouseDragged en mouseReleased
    private final Map<String, Runnable> MOUSECLICKED_METHODS = Map.of(
            "bulldozer", this::removeTile, "residence", this::addBuilding,
            "industry", this::addBuilding, "commerce", this::addBuilding,
            "selection", this::changeBuildingImage
    );

    public CityMapListener(CityMap model) {
        setId("cityMapListener");
        this.model = model;
        model.addListener(this);
        setPrefWidth(CELL_SIZE * 2 * 32);
        setPrefHeight(CELL_SIZE * 32);
        originalPaint = new HashMap<>();
        for (PolygonTile tile : model.getPolygonMap().values()) {
            Paint paint = tile.getFill();
            originalPaint.put(tile, paint);
        }
        key = "";
        availabilityBuilding = new boolean[4];
        selectedRoadKeys = new ArrayList<>();
    }

    @Override
    public void invalidated(Observable observable) {
        String button = model.getButtonSelected();
        setOnMouseMoved(mouseEvent -> {
            model.getPolygonMap().values().forEach(tile -> tile.setFill(originalPaint.get(tile)));
            model.userPolygons.values().forEach(tile -> tile.setFill(originalPaint.get(tile)));
            key = getKey(mouseEvent);
            MOUSEMOVED_METHODS.get(button).run();
        });
        setOnMouseClicked(mouseEvent -> {
            key = getKey(mouseEvent);
            if (MOUSECLICKED_METHODS.containsKey(button)) {
                MOUSECLICKED_METHODS.get(button).run();
            }
        });
        //Bij mousePressed, -dragged en -released moet er enkel iets gedaan worden als de knop van de weg is ingedrukt
        setOnMousePressed(mouseEvent -> {
            key = getKey(mouseEvent);
            if (button.equals("road")) {
                checkAvailability(key);
                selectedRoadKeys.add(key);
            }
        });
        setOnMouseDragged(mouseEvent -> {
            key = getKey(mouseEvent);
            if (button.equals("road")) {
                createLRoad();
            }
        });
        setOnMouseReleased(mouseEvent -> {
            if (button.equals("road")) {
                for (String key : selectedRoadKeys) {
                    if (checkAvailability(key)) {
                        int r = Integer.parseInt(key.split("-")[0]);
                        int k = Integer.parseInt(key.split("-")[1]);
                        RoadTile roadTile = new RoadTile(CELL_SIZE, r, k);
                        model.userPolygons.put(r + "-" + k, roadTile);
                        model.getChildren().add(roadTile);
                        roadTile.changeBackground(model, originalPaint, true);
                    }
                }
                selectedRoadKeys.clear();
                setOriginalPaint();
            }
        });
        setOnMouseExited(ev -> setOriginalPaint());
    }

    // De (rij- en kolom-)coördinaten berekenen o.b.v de coördinaten van de muis.
    // Deze rij- en kolom coördinaten vormen de sleutel van de map "polygonMap" en de map "userPolygons"
    // in de klasse CityMap.
    private String getKey(MouseEvent mouseEvent) {
        int x = (int) mouseEvent.getX();
        int y = (int) mouseEvent.getY();
        int r = (int) (2 * y - x + getWidth() / 2) / (2 * CELL_SIZE);
        int k = (int) (x + 2 * y - getWidth() / 2) / (2 * CELL_SIZE);
        return r + "-" + k;
    }

    private void setOriginalPaint() {
        for (PolygonTile tile : originalPaint.keySet()) {
            tile.setFill(originalPaint.get(tile));
            tile.setStroke(null);
        }
    }

    // Methode die bulldozer en selection gebruiken om de omranding van een tegel te kleuren in wit/rood
    private void setStroke(Color color) {
        model.getPolygonMap().values().forEach(tile -> tile.setStroke(null));
        model.userPolygons.values().forEach(tile -> tile.setStroke(null));
        if (model.userPolygons.containsKey(key)) {
            PolygonTile tile = model.userPolygons.get(key);
            tile.setStroke(color);
        } else if (model.getPolygonMap().containsKey(key)) {
            PolygonTile tile = model.getPolygonMap().get(key);
            tile.setStroke(color);
        }
    }

    private void removeTile() {
        if (model.userPolygons.containsKey(key)) {
            PolygonTile tile = model.userPolygons.get(key);
            tile.remove(model, originalPaint);
        }
    }

    // Kijken of de weg of het gebouw geplaatst kan worden, zo niet dan wordt de achtergond rood gekleurd
    // en geeft de methode false terug, anders wordt de achtergrond blauw en wordt true teruggegeven
    private boolean checkAvailability(String key) {
        boolean available = false;
        if (model.userPolygons.containsKey(key)) {
            PolygonTile tile = model.userPolygons.get(key);
            if (tile.getBackground().equals("building")) {
                tile = model.getPolygonMap().get(key);
            }
            tile.setFill(Color.RED);
        } else if (model.getPolygonMap().containsKey(key)) {
            PolygonTile tile = model.getPolygonMap().get(key);
            if (tile.getBackground().equals("road")) {
                tile.setFill(Color.RED);
            } else {
                tile.setFill(Color.CORNFLOWERBLUE);
                available = true;
            }
        }
        return available;
    }

    private void createLRoad() {
        int r = Integer.parseInt(key.split("-")[0]);
        int k = Integer.parseInt(key.split("-")[1]);
        String firstKey = selectedRoadKeys.get(0);
        int firstR = Integer.parseInt(firstKey.split("-")[0]);
        int firstK = Integer.parseInt(firstKey.split("-")[1]);
        setOriginalPaint();
        selectedRoadKeys.clear();
        selectedRoadKeys.add(firstKey);
        int minR = min(r, firstR);
        int maxR = max(r, firstR);
        int minK = min(k, firstK);
        int maxK = max(k, firstK);
        for (int i = minR; i <= maxR; i++) {
            String newKey = i + "-" + firstK;
            checkAvailability(newKey);
            selectedRoadKeys.add(newKey);
        }
        for (int i = minK; i < maxK; i++) {
            String newKey = r + "-" + i;
            checkAvailability(newKey);
            selectedRoadKeys.add(newKey);
        }
    }
    
    private void checkBuildingAvailability() {
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
                    availabilityBuilding[index] = checkAvailability(tileKey);
                }
                index++;
            }
        }
    }

    private void addBuilding() {
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

    private void changeBuildingImage() {
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