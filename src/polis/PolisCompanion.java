package polis;

import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import prog2.util.Viewport;
import java.util.Map;

public class PolisCompanion {

    public StackPane stackPane;
    private CityMap cityMap;
    public Button buttonSimulator;
    private Simulator simulator;

    private final Map<KeyCode, Runnable> KEYEVENTS = Map.of(
            KeyCode.R, this::residenceSelected, KeyCode.I, this::industrySelected,
            KeyCode.B, this::bulldozerSelected, KeyCode.C, this::commerceSelected,
            KeyCode.S, this::roadSelected, KeyCode.E, this::selectionSelected
    );


    public void initialize() {
        stackPane.getStylesheets().add("polis/polis.css");
        // stadskaart aanmaken en in de viewport plaasten
        cityMap = new CityMap();
        Viewport viewport = new Viewport(cityMap, 0.5);
        stackPane.getChildren().add(viewport);
        viewport.toBack();
        // eerste listener van de stadskaart aanmaken
        CityMapListener cityMapListener = new CityMapListener(cityMap);
        cityMap.getChildren().add(cityMapListener);
        cityMapListener.toFront();
        cityMapListener.setViewOrder(-100);
        viewport.setFocusTraversable(true);
        viewport.requestFocus();
        stackPane.setOnKeyPressed(keyEvent -> {
            if (KEYEVENTS.containsKey(keyEvent.getCode())) {
                KEYEVENTS.get(keyEvent.getCode()).run();
            }
        });
        // Simulator aanmaken
        simulator = new Simulator(cityMap, buttonSimulator);
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

    public void simulatorSelected() {
        simulator.changeImage();
    }
}