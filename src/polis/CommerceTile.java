package polis;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

public class CommerceTile extends BuildingTile {

    private ArrayList<Actor> traders;
    private ArrayList<Actor> goods;
    private ArrayList<Actor> customers;
    private double jobCapacity;
    private double goodsCapacity;
    private final double badTrade;
    private final double goodTrade;
    private final double customersPerTrader;
    private final double goodsPerCustomer;

    // Dit veld wordt aangepast door de methodes "checkGoodsCapacity", "checkJobCapacity" en "checkCustomerCapacity".
    // Hierdoor kan ik ervoor zorgen dat die methodes niets moeten teruggeven, waardoor ik deze zonder problemen
    // in de map CHECK_CAPACITY kan plaatsen. Dit veld wordt daarna gebruikt in de methode "hasEnoughCapacity".
    private boolean checkCapacity;

    // Dit veld zorgt ervoor dat ik makkelijk vanuit verschillende methodes de acteur kan gebruiken
    // die het gebouw is binnengegaan.
    private Actor arrivingActor;

    private final Map<String, Runnable> CHECK_CAPACITY = Map.of(
            "goods", this::checkGoodsCapacity,
            "jobseeker", this::checkJobCapacity,
            "shopper", this::checkCustomerCapacity
    );

    private final Map<String, Runnable> ADD_ACTOR = Map.of(
            "goods", this::addGoods, "trader", this::addTrader,
            "customer", this::addCustomer
    );


    public CommerceTile(int cellsize, int r, int k, Map<PolygonTile, Paint> originalPaint) {
        super(cellsize, r, k, originalPaint);
        String imageName = "polis/tiles/commerce-0.png";
        Image image = new Image(imageName);
        setFill(new ImagePattern(image));
        double tempGood = 1.0;
        double tempBad = 1.0;
        double tempCustomersPerTrader = 1.0;
        double tempGoodsPerCustomer = 1.0;
        Properties properties = new Properties();
        try (InputStream in = getClass().getResourceAsStream("engine.properties")) {
            properties.load(in);
            tempCustomersPerTrader = Double.parseDouble(properties.getProperty("customers.per.trader"));
            tempGoodsPerCustomer = Double.parseDouble(properties.getProperty("goods.per.customer"));
            tempGood = Double.parseDouble(properties.getProperty("factor.good.trade"));
            tempBad = Double.parseDouble(properties.getProperty("factor.bad.trade"));
        } catch (IOException ex) {
            //Doe niets
        }
        traders = new ArrayList<>();
        goods = new ArrayList<>();
        customers = new ArrayList<>();
        goodTrade = tempGood;
        badTrade = tempBad;
        customersPerTrader = tempCustomersPerTrader;
        goodsPerCustomer = tempGoodsPerCustomer;
        jobCapacity = capacity / customersPerTrader;
        goodsCapacity = capacity * goodsPerCustomer;
    }

    @Override
    public String getBackground() {
        return "commerce";
    }
    public String getAdjective() {
        return "commercial";
    }

    @Override
    public int getNumberOfCustomers() {
        return customers.size();
    }
    public int getNumberOfGoods() {
        return goods.size();
    }
    public int getNumberOfJobs() {
        return traders.size();
    }

    @Override
    public double getCustomerCapacity() {
        return capacity;
    }
    public double getJobCapacity() {
        return jobCapacity;
    }
    public double getGoodsCapacity() {
        return goodsCapacity;
    }

    public void sellGoods() {
        goods.remove(0);
    }

    @Override
    public boolean hasEnoughCapacity(String actor) {
        boolean enough;
        if (actor.equals("shopper") && (traders.size() < customers.size() + 1 || goods.size() < customers.size() + 1)) {
            enough = false;
            changeCapacity(badTrade);
        } else {
            CHECK_CAPACITY.get(actor).run();
            enough = checkCapacity;
        }
        return enough;
    }


    private void checkGoodsCapacity() {
        checkCapacity = goods.size() + 1 <= goodsCapacity;
    }
    private void checkJobCapacity() {
        checkCapacity = traders.size() + 1 <= jobCapacity;
    }
    private void checkCustomerCapacity() {
        checkCapacity = customers.size() + 1 <= capacity;
    }

    @Override
    public void addActor(Actor actor) {
        arrivingActor = actor;
        ADD_ACTOR.get(actor.getActorType()).run();
        if (level == 0) {
            changeImage(1);
        } else if (level < 3 && capacity >= capacityForHigherLevel.get(level)) {
            changeImage(1);
        }
        if (customers.size() + 1 > capacity) {
            changeCapacity(goodTrade);
        }
    }

    private void addGoods() {
        goods.add(arrivingActor);
    }
    private void addTrader() {
        traders.add(arrivingActor);
    }
    private void addCustomer() {
        customers.add(arrivingActor);
    }


    @Override
    public void removeActor(Actor actor) {
        goods.remove(actor);
        traders.remove(actor);
        customers.remove(actor);
        // Aangezien de acteur maar in 1 van deze lijsten zal zitten en er geen error ontstaat als je
        // een onbestaand object wilt verwijderen, verwijder ik de acteur gewoon uit alle lijsten.
    }

    @Override
    public void changeCapacity(double factor) {
        super.changeCapacity(factor);
        jobCapacity = capacity / customersPerTrader;
        goodsCapacity = capacity * goodsPerCustomer;
    }
}
