package polis;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import java.util.HashMap;
import java.util.Map;

public class CityMapListener extends Pane implements InvalidationListener {

    private final CityMap model;
    private static final int CELL_SIZE = 64;

    // Ik gebruik extra klassen als soort van "subluisteraars" (maar deze klassen implementeren de interface
    // InvalidationListener niet) die zich bezighouden met 1 soort tegel.
    // Deze klasse "CityMapListener" ontvangt dus alle veranderingen en laat RoadListener en BuildingListener
    // de veranderingen aanbrengen die horen bij een specifiek soort tegel.
    private final RoadListener roadListener;
    private final BuildingListener buildingListener;

    // Deze klasse past het infopaneel aan.
    private final StatisticsEditor statisticsEditor;

    // Veld om bij te houden waar de muis zich momenteel bevind (in het formaat "r-k" zoals bij de maps in het model)
    private String key;

    /*
        Per tegel hou ik zijn originele achtergrond bij zodat, wanneer de cursor
        de tegel verlaat, de tegel weer zijn originele achtergrond krijgt (met de methode 'setOriginalPaint')
        (want wanneer de cursor op de tegel staat, krijgt deze soms een andere achtergrondskleur of omranding).
        Deze achtergrond kan ik dan ook makkelijk gaandeweg aanpassen.
     */
    private final Map<PolygonTile, Paint> originalPaint;

    // Ik gebruik verschillende Maps die telkens andere methodes uitvoeren afhankelijk van de gebeurtenis van de muis
    // en van de knop die is ingedrukt.
    private final Map<String, Runnable> MOUSEMOVED_METHODS = Map.of(
            "bulldozer", () -> setStroke(Color.rgb(255,0,0,0.75), 8.0),
            "selection", () -> setStroke(Color.rgb(255,255,255,0.75), 4.0),
            "road", () -> checkAvailability(key), "industry", this::checkBuildingAvailability,
            "commerce", this::checkBuildingAvailability, "residence", this::checkBuildingAvailability
    );

    // Als de knop van de weg is ingedrukt, moet er niets gedaan worden bij mouseClicked.
    // Om de weg te maken gebruik ik immers mousePressed, mouseDragged en mouseReleased
    private final Map<String, Runnable> MOUSECLICKED_METHODS = Map.of(
            "bulldozer", this::removeTile, "residence", this::addBuilding,
            "industry", this::addBuilding, "commerce", this::addBuilding,
            "selection", this::showStatistics, "road", () -> {}
    );

    public CityMapListener(CityMap model, Label labelTitle, Label labelStatistics) {
        setId("cityMapListener");
        this.model = model;
        model.addListener(this);
        setPrefWidth(CELL_SIZE * 2 * 32);
        setPrefHeight(CELL_SIZE * 32);
        originalPaint = new HashMap<>();
        for (PolygonTile tile : model.getPolygonMap().values()) {
            originalPaint.put(tile, tile.getFill());
        }
        key = "";
        roadListener = new RoadListener(model, this, originalPaint);
        buildingListener = new BuildingListener(model, this, originalPaint);
        statisticsEditor = new StatisticsEditor(labelTitle, labelStatistics, model.userPolygons);
    }

    @Override
    public void invalidated(Observable observable) {
        setOriginalPaint();
        String button = model.getButtonSelected();
        setOnMouseMoved(mouseEvent -> {
            model.getPolygonMap().values().forEach(tile -> tile.setFill(originalPaint.get(tile)));
            model.userPolygons.values().forEach(tile -> tile.setFill(originalPaint.get(tile)));
            key = getKey(mouseEvent);
            MOUSEMOVED_METHODS.get(button).run();
        });
        setOnMouseClicked(mouseEvent -> {
            key = getKey(mouseEvent);
            MOUSECLICKED_METHODS.get(button).run();
        });
        // Bij mousePressed en -released moet er enkel iets gedaan worden als de knop van de weg is ingedrukt.
        // Bij mouseDragged wordt er iets gedaan als de knop van de weg of van de bulldozer wordt ingedrukt.
        setOnMousePressed(mouseEvent -> {
            key = getKey(mouseEvent);
            if (button.equals("road")) {
                roadListener.addFirstRoadKey(key);
            }
        });
        setOnMouseDragged(mouseEvent -> {
            key = getKey(mouseEvent);
            if (button.equals("road")) {
                roadListener.createLRoad(key);
            } else if (button.equals("bulldozer")) {
                setOriginalPaint();
                removeTile();
                if (model.getPolygonMap().containsKey(key)) {
                    PolygonTile tile = model.getPolygonMap().get(key);
                    tile.setStroke(Color.rgb(255, 0, 0, 0.5));
                    tile.setStrokeWidth(8.0);
                }
            }
        });
        setOnMouseReleased(mouseEvent -> {
            if (button.equals("road")) {
                roadListener.placeRoad();
                setOriginalPaint();
            }
        });
        setOnMouseExited(ev -> setOriginalPaint());
    }

    // Hier bereken ik de (rij- en kolom-)coördinaten o.b.v de coördinaten van de muis.
    // Deze rij- en kolom coördinaten vormen de sleutel van de map "polygonMap" en de map "userPolygons"
    // in de klasse CityMap.
    private String getKey(MouseEvent mouseEvent) {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        int r = (int) (2 * y - x + getWidth() / 2) / (2 * CELL_SIZE);
        int k = (int) (x + 2 * y - getWidth() / 2) / (2 * CELL_SIZE);
        return r + "-" + k;
    }

    public void setOriginalPaint() {
        for (PolygonTile tile : originalPaint.keySet()) {
            tile.setFill(originalPaint.get(tile));
            tile.setStroke(null);
        }
    }

    // Methode die bulldozer en selection gebruiken om de omranding van een tegel te kleuren in wit/rood
    private void setStroke(Color color, double strokeWidth) {
        model.getPolygonMap().values().forEach(tile -> tile.setStroke(null));
        model.userPolygons.values().forEach(tile -> tile.setStroke(null));
        if (model.userPolygons.containsKey(key)) {
            PolygonTile tile = model.userPolygons.get(key);
            tile.setStrokeWidth(strokeWidth);
            tile.setStroke(color);
        } else if (model.getPolygonMap().containsKey(key)) {
            PolygonTile tile = model.getPolygonMap().get(key);
            tile.setStrokeWidth(strokeWidth);
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
    public boolean checkAvailability(String key) {
        boolean available = false;
        if (model.userPolygons.containsKey(key)) {
            PolygonTile tile = model.userPolygons.get(key);
            if (! tile.getBackground().equals("road")) {
                tile = model.getPolygonMap().get(key);
            }
            tile.setFill(Color.rgb(255, 0, 0, 0.5));
        } else if (model.getPolygonMap().containsKey(key)) {
            PolygonTile tile = model.getPolygonMap().get(key);
            if (tile.getBackground().equals("road")) {
                tile.setFill(Color.rgb(255, 0, 0, 0.5));
            } else {
                tile.setFill(Color.rgb(0, 127, 255, 0.5));
                available = true;
            }
        }
        return available;
    }

    // In de hashmaps "MOUSEMOVED_METHODS" en "MOUSECLICKED_METHODS" kon ik de methodes van buildingListener nog niet
    // aanspreken omdat buildingListener eerst nog geïnitialiseerd moest worden. Hierdoor heb ik private methodes
    // gemaakt zodat de methodes van buildingListener alsnog gemakkelijk uitgevoerd kunnen worden.
    private void checkBuildingAvailability() {
        buildingListener.checkBuildingAvailability(key);
    }

    private void addBuilding() {
        buildingListener.addBuilding(key);
    }

    private void showStatistics() {
        if (model.userPolygons.containsKey(key)) {
            statisticsEditor.showStats(model.userPolygons.get(key));
        } else if (model.getPolygonMap().containsKey(key)) {
            statisticsEditor.showStats(model.getPolygonMap().get(key));
        }
    }
}