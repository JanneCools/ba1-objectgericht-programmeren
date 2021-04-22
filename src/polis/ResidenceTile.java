package polis;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ResidenceTile extends BuildingTile {

    private int residentNumber = 0;

    private Map<Integer, Actor> residents;

    public ResidenceTile(int cellsize, int r, int k, Map<PolygonTile, Paint> originalPaint) {
        super(cellsize, r, k, originalPaint);
        residents = new HashMap<>();
        String imageName = "polis/tiles/residence-0.png";
        Image image = new Image(imageName);
        setFill(new ImagePattern(image));
    }

    @Override
    public String getAdjective() {
        return "residential";
    }
    public String getBackground() {
        return "residence";
    }

    @Override
    public int getNumberOfResidents() {
        return residents.size();
    }
    public double getResidenceCapacity() {
        return capacity;
    }

    @Override
    public boolean hasEnoughCapacity(String actor) {
        return residents.size() + 1 <= capacity;
    }

    public void addActor(Actor actor) {
        residentNumber ++;
        residents.put(residentNumber, actor);
        if (level == 0) {
            changeImage(1);
        }
    }

    @Override
    public void removeActor(Actor actor) {
        int key = findKey(actor);
        residents.remove(key);
    }

    // Wanneer een acteur van rol verandert, wordt deze verwijderd uit de simulatie en wordt een nieuwe
    // acteur toegevoegd. Om errors te vermijden, moet in de lijst "residents" de oude acteur veranderd
    // worden in de nieuwe acteur.
    @Override
    public void changeResident(Actor oldActor, Actor newActor) {
        int key = findKey(oldActor);
        residents.replace(key, newActor);
    }

    @Override
    public void changeCapacity(double factor) {
        super.changeCapacity(factor);
        if (capacity < residents.size()) {
            int index = ((int) capacity) + 1;
            while (index <= residents.size()) {
                Actor actor = residents.get(index);
                actor.removeActor();
                residents.remove(index);
            }
            residentNumber = (int) capacity;
        }
        if (level > 1 && capacity <= capacityForLowerLevel.get(level)) {
            changeImage(-1);
        }
        if (level < 3 && capacity >= capacityForHigherLevel.get(level)) {
            changeImage(1);
        }
    }

    private int findKey(Actor actor) {
        int actorKey = 0;
        Iterator<Integer> iterator = residents.keySet().iterator();
        boolean keyFound = false;    // De lus zal stoppen wanneer de sleutel horend bij de acteur gevonden is
        while (iterator.hasNext() && ! keyFound) {
            int key = iterator.next();
            Actor actor1 = residents.get(key);
            if (actor1.equals(actor)) {
                actorKey = key;
                keyFound = true;
            }
        }
        return actorKey;
    }
}
