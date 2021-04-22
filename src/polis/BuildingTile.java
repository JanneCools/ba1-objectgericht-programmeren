package polis;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class BuildingTile extends PolygonTile {

    protected int level;        // Niveau/afbeelding van het gebouw (0, 1, 2 of 3)
    protected Map<PolygonTile, Paint> originalPaint;
    protected double capacity;
    protected double minCapacity;
    protected final Map<Integer, Double> capacityForHigherLevel;
    protected final Map<Integer, Double> capacityForLowerLevel;

    public BuildingTile(int cellsize, int r, int k, Map<PolygonTile, Paint> originalPaint) {
        super(cellsize, 2, r, k);
        level = 0;
        this.originalPaint = originalPaint;
        capacityForHigherLevel = new HashMap<>();
        capacityForLowerLevel = new HashMap<>();
        Properties properties = new Properties();
        try (InputStream in = getClass().getResourceAsStream("engine.properties");
             InputStream in2 = getClass().getResourceAsStream("levels.properties")
        ) {
            properties.load(in);
            capacity = Double.parseDouble(properties.getProperty(getAdjective() + ".capacity.initial"));
            minCapacity = Double.parseDouble(properties.getProperty(getAdjective() + ".capacity.minimal"));
            properties.load(in2);
            double temp = Double.parseDouble(properties.getProperty(getAdjective() + ".level1to2"));
            capacityForHigherLevel.put(1, temp);
            temp = Double.parseDouble(properties.getProperty(getAdjective() + ".level2to3"));
            capacityForHigherLevel.put(2, temp);
            temp = Double.parseDouble(properties.getProperty(getAdjective() + ".level3to2"));
            capacityForLowerLevel.put(3, temp);
            temp = Double.parseDouble(properties.getProperty(getAdjective() + ".level2to1"));
            capacityForLowerLevel.put(2, temp);
        } catch (IOException ex) {
            //Doe niets
        }
    }

    // Deze abstracte methode gebruik ik om in de constructor de juiste "properties" te krijgen.
    protected abstract String getAdjective();

    public abstract String getBackground();

    // Enkel CommerceTile maakt gebruik van de parameter "actor".
    public abstract boolean hasEnoughCapacity(String actor);
    public abstract void addActor(Actor actor);
    public abstract void removeActor(Actor actor);

    //Deze methode wordt enkel gebruikt door ResidenceTile (uitleg staat in die klasse).
    public void changeResident(Actor oldActor, Actor newActor) {}

    // De parameter "difference" geeft aan of het niveau van het gebouw moet stijgen of dalen met 1 eenheid.
    public void changeImage(int difference) {
        level = (level + difference) % 4;
        String imageName = "polis/tiles/" + getBackground() + "-" + level + ".png";
        Image image = new Image(imageName);
        setFill(new ImagePattern(image));
        originalPaint.replace(this, getFill());
    }

    public void remove(CityMap model, Map<PolygonTile, Paint> originalPaint) {
        model.userPolygons.remove((r-1)+"-"+k);
        model.userPolygons.remove(r+"-"+(k+1));
        model.userPolygons.remove((r-1)+"-"+(k+1));
        super.remove(model, originalPaint);
    }

    public void changeCapacity(double factor) {
        capacity *= factor;
        if (capacity < minCapacity) {
            capacity = minCapacity;
        }
    }

    // Enkel IndustryTile gebruikt deze methode
    public void sellGoods() {}

}
