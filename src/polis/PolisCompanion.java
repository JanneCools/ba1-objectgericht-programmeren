package polis;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import prog2.util.Viewport;
import java.util.Map;

public class PolisCompanion {

    public StackPane stackPane;
    private CityMap cityMap;
    private Viewport viewport;
    private CityMapListener cityMapListener;

    private final Map<KeyCode, Runnable> KEYEVENTS = Map.of(
            KeyCode.R, this::residenceSelected, KeyCode.I, this::industrySelected,
            KeyCode.B, this::bulldozerSelected, KeyCode.C, this::commerceSelected,
            KeyCode.S, this::roadSelected, KeyCode.ESCAPE, this::selectionSelected
    );


    public void initialize() {
        stackPane.getStylesheets().add("polis/polis.css");
        // stadskaart aanmaken en in de viewport plaasten
        cityMap = new CityMap();
        viewport = new Viewport(cityMap, 0.5);
        stackPane.getChildren().add(viewport);
        viewport.toBack();
        // listener van de stadskaart aanmaken
        cityMapListener = new CityMapListener(cityMap);
        cityMap.getChildren().add(cityMapListener);
        cityMapListener.toFront();
        cityMapListener.setViewOrder(-100);
        // Ik laat de cityMap alle toetsenbordgebeurtenissen detecteren, aangezien enkel de
        // voorouders stackPane en Viewport deze gebeurtenissen verwerken (en CityMapListener dus niet).
        cityMap.setFocusTraversable(true);
        cityMap.requestFocus();
        stackPane.setOnKeyPressed(keyEvent -> {
            if (KEYEVENTS.containsKey(keyEvent.getCode())) {
                KEYEVENTS.get(keyEvent.getCode()).run();
            }
        });
    }

    public void roadSelected() {
        cityMap.checkButtonAction("road");
    }

    public void bulldozerSelected() {
        cityMap.checkButtonAction("bulldozer");
    }

    public void selectionSelected() {
        cityMap.checkButtonAction("selection");
    }

    public void residenceSelected() {
        cityMap.checkButtonAction("residence");
    }

    public void industrySelected() {
        cityMap.checkButtonAction("industry");
    }

    public void commerceSelected() {
        cityMap.checkButtonAction("commerce");
    }
}