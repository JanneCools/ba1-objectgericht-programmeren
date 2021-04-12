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

    private ArrayList<StaticActor> traders;
    private ArrayList<MovingActor> goods;
    private ArrayList<StaticActor> customers;
    private double jobCapacity;
    private double goodsCapacity;
    private final double badTrade;
    private final double goodTrade;

    public CommerceTile(int cellsize, int r, int k, Map<PolygonTile, Paint> originalPaint) {
        super(cellsize, r, k, originalPaint);
        String imageName = "polis/tiles/commerce-0.png";
        Image image = new Image(imageName);
        setFill(new ImagePattern(image));
        double tempGood = 1.0;
        double tempBad = 1.0;
        Properties properties = new Properties();
        try (InputStream in = getClass().getResourceAsStream("engine.properties")) {
            properties.load(in);
            double customersPerTrader = Double.parseDouble(properties.getProperty("customers.per.trader"));
            double goodsPerCustomer = Double.parseDouble(properties.getProperty("goods.per.customer"));
            jobCapacity = capacity / customersPerTrader;
            goodsCapacity = capacity * goodsPerCustomer;
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
    }

    @Override
    public String getBackground() {
        return "commerce";
    }

    public String getAdjective() {
        return "commercial";
    }

    @Override
    public boolean hasEnoughCapacity(MovingActor actor) {
        boolean enough;
        if (actor instanceof Goods) {
            enough =  goods.size() + 1 <= goodsCapacity;
        } else if (actor instanceof JobSeeker) {
            enough = traders.size() + 1 <= jobCapacity;
        } else {
            if (traders.size() < customers.size() + 1 || goods.size() < customers.size() + 1) {
                enough = false;
                changeCapacity(badTrade);
            } else {
                enough = customers.size() + 1 <= capacity;
            }
        }
        return enough;
    }



    @Override
    public void addActor(Actor actor) {
        if (actor instanceof Goods) {
            goods.add((MovingActor) actor);
        } else if (actor instanceof Trader) {
            traders.add((StaticActor) actor);
        } else {
            customers.add((StaticActor) actor);
        }
        if (level == 0) {
            changeImage(1);
        } else if (level < 3 && capacity >= capacityForHigherLevel.get(level)) {
            changeImage(1);
        }
        if (customers.size() + 1 > capacity) {
            changeCapacity(goodTrade);
        }
    }

    @Override
    public void removeActor(Actor actor) {
        goods.remove(actor);
        traders.remove(actor);
        customers.remove(actor);
        // Aangezien de acteur maar in 1 van deze lijsten zal zitten en er geen error ontstaat als je
        // een onbestaand object wilt verwijderen, verwijder ik de acteur gewoon uit alle lijsten.
    }

    public void sellGoods() {
        goods.remove(0);
    }
}
