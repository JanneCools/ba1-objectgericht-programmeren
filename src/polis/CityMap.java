package polis;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CityMap extends Pane implements Observable {

    private static final int CELL_SIZE = 64;

    // Deze map bevat de originele tegels van de kaart, met de onverwijderbare weg
    private final Map<String, PolygonTile> polygonMap;

    // Deze map is public zodat "CityMapListener" deze ook kan bewerken en houdt alle door de gebuiker
    // gemaakte tegels (weg of gebouw) bij
    public Map<String, PolygonTile> userPolygons;

    private final ArrayList<InvalidationListener> listeners;
    private String buttonSelected;      // houdt bij welke knop geselecteerd is (of geen enkele)


    public CityMap() {
        listeners = new ArrayList<>();
        polygonMap = new HashMap<>();
        userPolygons = new HashMap<>();
        setPrefWidth(CELL_SIZE * 2 * 32);
        setPrefHeight(CELL_SIZE * 32);
        buttonSelected = "null"; // in het begin is geen enkele knop geselecteerd
        // Maak alle tegels
        for (int r = 0; r < 32; r++) {
            for (int k = 0; k < 32; k++) {
                String key = r + "-" + k;
                PolygonTile tile = new PolygonTile(CELL_SIZE, 1, r, k);
                if (k == 15 && r <= 15) {
                    tile = new RoadTile(CELL_SIZE, r, k);
                }
                polygonMap.put(key, tile);
                getChildren().add(tile);
                tile.changeBackground(this, null, true);
            }
        }
    }

    @Override
    public void addListener(InvalidationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        listeners.remove(listener);
    }

    public String getButtonSelected() {
        return buttonSelected;
    }
    public Map<String, PolygonTile> getPolygonMap() {
        return polygonMap;
    }

    public void checkButtonAction(String button) {
        if (! buttonSelected.equals(button)) {
            buttonSelected = button;
            fireInvalidationEvent();
        }
    }

    private void fireInvalidationEvent() {
        for (InvalidationListener listener: listeners) {
            listener.invalidated(this);
        }
    }
}
