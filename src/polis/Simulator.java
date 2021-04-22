package polis;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import java.util.Random;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class Simulator {

    public static final Random RG = new Random();

    private final CityMap model;
    private final Button buttonSimulator;
    private boolean simulating;             //Hierdoor weet ik of de simulatie bezig is (voor methode "changeImage")

    private final int initialRate;
    private final int slowestRate;
    private final double factorRecovery;
    private final double factorSlowDown;
    private int tempo;
    private int counter;

    private static final Timeline timeline = new Timeline();
    private ArrayList<Actor> actors;


    public Simulator(CityMap model, Button simulator) {
        int tempInRate = 1;
        int tempSlowestRate = 1;
        double tempRecovery = 1.0;
        double tempSlowDown = 1.0;
        Properties properties = new Properties();
        try (InputStream in = getClass().getResourceAsStream("engine.properties")) {
            properties.load(in);
            tempInRate = Integer.parseInt(properties.getProperty("region.initial.rate"));
            tempSlowestRate = Integer.parseInt(properties.getProperty("region.slowest.rate"));
            tempRecovery = Double.parseDouble(properties.getProperty("region.factor.recovery"));
            tempSlowDown = Double.parseDouble(properties.getProperty("region.factor.slow.down"));
        } catch (IOException ex) {
            //Doe niets
        }
        this.model = model;
        buttonSimulator = simulator;
        Image image = new Image("polis/buttons/play.png");
        buttonSimulator.setGraphic(new ImageView(image));
        actors = new ArrayList<>();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(250),
                        (parameter) -> simulate())
        );
        initialRate = tempInRate;
        slowestRate = tempSlowestRate;
        factorRecovery = tempRecovery;
        factorSlowDown = tempSlowDown;
        tempo = initialRate;
        counter = RG.nextInt(tempo);
        simulating = false;
    }

    public void changeTempo(boolean slowDown) {
        if (slowDown && tempo * factorSlowDown <= slowestRate) {
            tempo *= factorSlowDown;
        } else if (!slowDown && tempo * factorRecovery >= initialRate) {
            tempo *= factorRecovery;
        }
    }

    public void simulate() {
        changeTempo(false);
        counter--;
        if (counter < 0) {
            Immigrant immigrant = new Immigrant(model, this);
            addActor(immigrant);
            counter = RG.nextInt(tempo);
        }
        int index = 0;
        while (index < actors.size()) {
            actors.get(index).act();
            index++;
        }
    }

    public void changeImage() {
        Image image = new Image("polis/buttons/pause.png");
        if (simulating) {
            simulating = false;
            image = new Image("polis/buttons/play.png");
            timeline.stop();
        } else {
            simulating = true;
            timeline.play();
        }
        buttonSimulator.setGraphic(new ImageView(image));
    }

    public void addActor(Actor actor) {
        actors.add(actor);
        model.getChildren().add(actor);
    }

    public void removeActor(Actor actor) {
        actors.remove(actor);
        model.getChildren().remove(actor);
    }
}