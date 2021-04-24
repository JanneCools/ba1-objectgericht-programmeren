package polis;

import javafx.scene.control.Label;
import java.util.Map;


public class StatisticsEditor {

    private final Label labelTitle;
    private final Label labelStatistics;
    private final Map<String, PolygonTile> userpolygons;

    // Alle benodige statistieken
    private double residentCapacity;
    private double jobCapacity;
    private double goodsCapacity;
    private double customerCapacity;
    private int residents;
    private int jobs;
    private int goods;
    private int customers;

    // De PolygonTile waarop geklikt is, houd ik bij in een veld zodat ik deze makkelijk kan raadplegen
    private PolygonTile tile;

    //Deze map zorgt ervoor dat de juiste methode opgeroepen wordt.
    //De sleutel is telkens de achtergrond van een PolygonTile
    private final Map<String, Runnable> METHODS_FOR_STATS = Map.of(
            "green", this::showAllStats, "road", this::showAllStats,
            "commerce", this::showCommerceStats, "industry", this::showIndustryStats,
            "residence", this::showResidenceStats
    );

    public StatisticsEditor(Label labelTitle, Label labelStatistics, Map<String, PolygonTile> userpolygons) {
        this.labelTitle = labelTitle;
        this.labelStatistics = labelStatistics;
        this.userpolygons = userpolygons;
    }

    public void showStats(PolygonTile polygonTile) {
        tile = polygonTile;
        METHODS_FOR_STATS.get(polygonTile.getBackground()).run();
    }

    private void showResidenceStats() {
        residents = tile.getNumberOfResidents();
        residentCapacity = Math.round(tile.getResidenceCapacity() * 10) / 10.0;
        labelTitle.setText("RESIDENTIEEL");
        labelStatistics.setText("Bewoners: " + residents + " / " + residentCapacity);
    }

    private void showIndustryStats() {
        jobs = tile.getNumberOfJobs();
        jobCapacity = Math.round(tile.getJobCapacity() * 10) / 10.0;
        labelTitle.setText("INDUSTRIEEL");
        labelStatistics.setText("Jobs: " + jobs + " / " + jobCapacity);
    }

    private void showCommerceStats() {
        jobs = tile.getNumberOfJobs();
        jobCapacity = Math.round(tile.getJobCapacity() * 10) / 10.0;
        goods = tile.getNumberOfGoods();
        goodsCapacity = Math.round(tile.getGoodsCapacity() * 10) / 10.0;
        customers = tile.getNumberOfCustomers();
        customerCapacity = Math.round(tile.getCustomerCapacity() * 10) / 10.0;
        labelTitle.setText("COMMERCIEEL");
        labelStatistics.setText("Jobs: " + jobs + " / " + jobCapacity + "\n" +
                "Goederen: " + goods + " / " + goodsCapacity + "\n" +
                "Klanten: " + customers + " / " + customerCapacity);
    }

    private void showAllStats() {
        residents = 0;
        residentCapacity = 0.0;
        jobs = 0;
        jobCapacity = 0.0;
        goods = 0;
        goodsCapacity = 0.0;
        customers = 0;
        customerCapacity = 0.0;
        for (PolygonTile polygonTile: userpolygons.values()) {
            residents += polygonTile.getNumberOfResidents();
            residentCapacity += polygonTile.getResidenceCapacity();
            jobs += polygonTile.getNumberOfJobs();
            jobCapacity += polygonTile.getJobCapacity();
            goods += polygonTile.getNumberOfGoods();
            goodsCapacity += polygonTile.getGoodsCapacity();
            customers += polygonTile.getNumberOfCustomers();
            customerCapacity += polygonTile.getCustomerCapacity();
        }
        divideAllNumbers();
        labelTitle.setText("STATISTIEKEN");
        labelStatistics.setText("Bewoners: " + residents + " / " + (Math.round(residentCapacity * 10) / 10.0) + "\n" +
                "Jobs: " + jobs + " / " + (Math.round(jobCapacity * 10) / 10.0) + "\n" +
                "Goederen: " + goods + " / " + (Math.round(goodsCapacity * 10) / 10.0) + "\n" +
                "Klanten: " + customers + " / " + (Math.round(customerCapacity * 10) / 10.0));
    }

    // Elk gebouw neemt 4 tegels in op de kaart, hierdoor houd ik 4 coördinaten bij per gebouw en
    // komt elk gebouw dus 4 keer voor in de map userPolygons.
    // Daarom moet ik bij de methode "showAllStats" alle statistieken nog delen door 4.
    private void divideAllNumbers() {
        residents /= 4;
        jobs /= 4;
        customers /= 4;
        goods /= 4;
        residentCapacity /= 4.0;
        jobCapacity /= 4.0;
        goodsCapacity /= 4.0;
        customerCapacity /= 4.0;
    }

}
