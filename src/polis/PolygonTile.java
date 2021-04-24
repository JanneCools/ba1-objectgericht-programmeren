package polis;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;

import java.util.*;


public class PolygonTile extends Polygon {

    protected final int r;
    protected final int k;

    public PolygonTile(int cellSize, int size, int r, int k) {
        super(0, 0,
                cellSize * size, 0.5 * cellSize * size,
                0, cellSize * size,
                -cellSize * size, 0.5 * cellSize * size
        );
        this.r = r;
        this.k = k;
        int x = cellSize * (size - r + k);
        int y = cellSize * (-size + r + k) / 2;
        setTranslateX(x + 64 * 31);
        setTranslateY(y);
        setFill(Color.rgb(204, 249, 170));
        setViewOrder(-r - k - size);
    }

    //Deze methode wordt enkel gebruikt door BuildingTile (en zijn subklassen).
    public boolean hasEnoughCapacity(String actor) {
        return true;
    }

    public String getKey() {
        return r + "-" + k;
    }

    public String getBackground() {
        return "green";
    }
    // Geeft weer wat de achtergrond van een tegel is.
    // Elke subklasse geeft een andere achtergrond terug.

    public void remove(CityMap model, Map<PolygonTile, Paint> originalPaint) {
        model.userPolygons.remove(r+"-"+k);
        model.getChildren().remove(this);
        originalPaint.remove(this);
    }

    // Deze methode wordt enkel overschreven door RoadTile, voor alle andere polygons doet deze methode niets
    public void changeBackground(CityMap model, Map<PolygonTile, Paint> originalPaint, boolean firstTime) {}

    public void checkNeighbourRoads(CityMap cityMap, Map<PolygonTile, Paint> originalPaint) {
        ArrayList<PolygonTile> neighbours = getNeighbours(cityMap.getPolygonMap(), false);
        neighbours.addAll(getNeighbours(cityMap.userPolygons, false));
        for (PolygonTile tile: neighbours) {
            tile.changeBackground(cityMap, originalPaint, false);
        }
    }

    /*
        In de volgende methode worden de 3 buren van de tegel gevraagd.
        Als voor de parameter 'polygon' true meegegeven wordt, dan worden de rechter-,
        rechterschuinboven- en rechterschuinonderbuur gevraagd (waardoor de 4 tegels een ruit/polygon vormen).
        Anders worden alle schuine buren gevraagd (waardoor de 4 tegels een kruis vormen).
     */
    public ArrayList<PolygonTile> getNeighbours(Map<String, PolygonTile> map, boolean polygon) {
        ArrayList<PolygonTile> neighbours = new ArrayList<>();
        neighbours.add(this);
        neighbours.add(map.get(r + "-" + (k+1)));
        neighbours.add(map.get((r-1) + "-" + k));
        if (polygon) {
            neighbours.add(map.get((r-1) + "-" + (k+1)));
        } else {
            neighbours.add(map.get((r) + "-" + (k-1)));
            neighbours.add(map.get((r+1) + "-" + (k)));
        }
        neighbours.removeIf(Objects::isNull);
        return neighbours;
    }

    // Deze methodes worden gebruikt door de klasse StatisticsPanel voor het infopaneel
    // en worden overschreven door de subklassen (soms door meerdere subklassen, soms door 1).
    // Hierdoor moet ik in de methodes van de klasse StatisticsPanel niet nakijken of een gebouw van het juiste
    // type is ("commerce", "industry" of "residence").
    public double getResidenceCapacity() {
        return 0.0;
    }
    public double getJobCapacity() {
        return 0.0;
    }
    public double getGoodsCapacity() { return 0.0;}
    public double getCustomerCapacity() { return 0.0;}
    public int getNumberOfResidents() {
        return 0;
    }
    public int getNumberOfJobs() {
        return 0;
    }
    public int getNumberOfGoods() {
        return 0;
    }
    public int getNumberOfCustomers() {
        return 0;
    }
}
