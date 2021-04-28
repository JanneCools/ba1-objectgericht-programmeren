package polis;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class Actor extends Circle {

    protected final CityMap model;
    protected int r;
    protected int k;
    protected static final int CELL_SIZE = 64;
    protected final Simulator simulator;
    protected int age;
    protected final BuildingTile home;
    protected final int homeR;
    protected final int homeK;

    public Actor(int r, int k, int homeR, int homeK, CityMap model, Simulator simulator,
                 BuildingTile home, String actor) {
        super(0, CELL_SIZE/2.0, CELL_SIZE/6.0);
        Properties properties = new Properties();
        // De parameter "actor" wordt gebruikt om te bepalen van welke acteur de leeftijd gezocht moet worden
        // in het bestand "engine.properties".
        try (InputStream in = getClass().getResourceAsStream("engine.properties")) {
            properties.load(in);
            age = Integer.parseInt(properties.getProperty(actor + ".age"));
        } catch (IOException ex) {
            // Doe niets
        }
        this.r = r;
        this.k = k;
        int x = CELL_SIZE * (1 - r + k);
        int y = CELL_SIZE * (-1 + r + k) / 2;
        setTranslateX(x + 64 * 31);
        setTranslateY(y);
        setViewOrder(-50);
        setFill(Color.TRANSPARENT);
        this.model = model;
        this.simulator = simulator;
        this.home = home;
        this.homeR = homeR;
        this.homeK = homeK;
    }

    public abstract void act();
    public abstract String getActorType();
    public void removeActor() {
        simulator.removeActor(this);
    }
}