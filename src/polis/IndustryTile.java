package polis;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import java.util.ArrayList;
import java.util.Map;

public class IndustryTile extends BuildingTile {

    private ArrayList<Actor> workers;

    public IndustryTile(int cellsize, int r, int k, Map<PolygonTile, Paint> originalPaint) {
        super(cellsize, r, k, originalPaint);
        String imageName = "polis/tiles/industry-0.png";
        Image image = new Image(imageName);
        setFill(new ImagePattern(image));
        workers = new ArrayList<>();
    }

    @Override
    public String getBackground() {
        return "industry";
    }
    public String getAdjective() {
        return "industrial";
    }

    @Override
    public int getNumberOfJobs() {
        return workers.size();
    }
    public double getJobCapacity() {
        return capacity;
    }

    @Override
    public boolean hasEnoughCapacity(String actor) {
        return workers.size() + 1 <= capacity;
    }

    public void addActor(Actor actor) {
        workers.add(actor);
        if (level == 0) {
            changeImage(1);
        }
    }

    @Override
    public void removeActor(Actor actor) {
        workers.remove(actor);
    }

    @Override
    public void changeCapacity(double factor) {
        super.changeCapacity(factor);
        if (level > 1 && capacity <= capacityForLowerLevel.get(level)) {
            changeImage(-1);
        }
        if (level != 0 && level < 3 && capacity >= capacityForHigherLevel.get(level)) {
            changeImage(1);
        }
    }
}
