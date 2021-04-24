package polis;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public abstract class MovingActor extends Actor {

    public static final Random RG = new Random();

    // Het veld "direction" houdt bij in welke richting de acteur momenteel kijkt. Dit geeft een getal terug van 0 tot en met 3.
    // 0 betekent dat de acteur naar rechtsboven kijkt, 1 betekent rechtsonder, 2 linksonder en 3 linksboven.
    private int direction;

    // Deze array wordt gebruikt in de methode "changeDirection". Op basis van de parameter "value"
    // wordt het veld "direction" met een bepaald getal verhogd of verlaagd.
    private static final int[] CHANGE_DIRECTION = {-1, 1, 0, 2};

    /*
     De sleutels van de volgende mappen horen bij het veld "direction". Afhankelijk van naar waar de acteur
     kijkt, zal een andere array verkregen worden.
     Bij zo'n array zal bij index 0 staan hoeveel je bij de r-coördinaat moet optellen
     om de tegel links van de acteur te bekomen, bij index 1 om de tegel rechts te bekomen, bij index 2 de
     tegel voor de acteur en index 3 achter de acteur (analoog voor de array directionsK).
    */
    private static final Map<Integer, int[]> directionsR = Map.of(
            0, new int[]{0, 0, -1, 1}, 1, new int[]{-1, 1, 0, 0},
            2, new int[]{0, 0, 1, -1}, 3, new int[]{1, -1, 0, 0}
    );
    private static final Map<Integer, int[]> directionsK = Map.of(
            0, new int[]{-1, 1, 0, 0}, 1, new int[]{0, 0, 1, -1},
            2, new int[]{1, -1, 0, 0}, 3, new int[]{0, 0, -1, 1}
    );


    public MovingActor(int r, int k, int homeR, int homeK, CityMap model, Simulator simulator,
                       BuildingTile home, String actor) {
        super(r, k, homeR, homeK, model, simulator, home, actor);
        direction = 2;
    }

    // Elke bewegende acteur wilt een ander soort gebouw bereiken. Met deze methode weten we welk soort gebouw.
    public abstract List<String> getNeededBuildings();

    public abstract void enterBuilding(String key);
    public abstract void destinationNotFound();
    public abstract String getActorType();

    public void act() {
        if (age != 0 && arrivedAtDestination() == null) {
            int index = 0;
            boolean canMove = false;
            ArrayList<Integer> indices = randomNumbers();
            // Nakijken of de acteur vooruit, naar links of naar recht kan bewegen (in willekeurige volgorde).
            while (index < 3 && ! canMove) {
                int destR = r + directionsR.get(direction)[indices.get(index)];
                int destK = k + directionsK.get(direction)[indices.get(index)];
                canMove = move(destR, destK);
                index ++;
            }
            // Achteruit bewegen als er geen andere mogelijkheid is.
            if (! canMove) {
                int destR = r + directionsR.get(direction)[3];
                int destK = k + directionsK.get(direction)[3];
                move(destR, destK);
            }
            changeDirection(indices.get(index-1));
            age --;
        } else if (age == 0) {
            destinationNotFound();
        } else {
            enterBuilding(arrivedAtDestination());
        }
    }

    public String arrivedAtDestination() {
        String keyLeft = (r+directionsR.get(direction)[0]) + "-" + (k+directionsK.get(direction)[0]);
        String keyRight = (r+directionsR.get(direction)[1]) + "-" + (k+directionsK.get(direction)[1]);
        String arrived = null;
        // Enkel als de acteur zich bij het juiste gebouw bevindt die nog voldoende capaciteit heeft,
        // heeft de acteur zijn bestemming bereikt.
        if (model.userPolygons.containsKey(keyLeft)
                && getNeededBuildings().contains(model.userPolygons.get(keyLeft).getBackground())
                && model.userPolygons.get(keyLeft).hasEnoughCapacity(getActorType())) {
            arrived = keyLeft;
        } else if (model.userPolygons.containsKey(keyRight)
                && getNeededBuildings().contains(model.userPolygons.get(keyRight).getBackground())
                && model.userPolygons.get(keyRight).hasEnoughCapacity(getActorType())) {
            arrived = keyRight;
        }
        return arrived;
    }

    // Als de acteur kan bewegen, wordt true teruggegeven en verplaatst de acteur zich,
    // anders wordt false teruggegeven en gebeurt er niets.
    public boolean move(int destR, int destK) {
        boolean canMove = (model.getPolygonMap().containsKey(destR+"-"+destK)
                && model.getPolygonMap().get(destR+"-"+destK).getBackground().equals("road"))
                || (model.userPolygons.containsKey(destR+"-"+destK)
                && model.userPolygons.get(destR+"-"+destK).getBackground().equals("road"));
        if (canMove) {
            r = destR;
            k = destK;
            int x = CELL_SIZE * (1 - r + k);
            int y = CELL_SIZE * (-1 + r + k) / 2;
            setTranslateX(x + 64 * 31);
            setTranslateY(y);
        }
        return canMove;
    }

    // Dit creërt een lijst met de getallen 0, 1 en 2 in een willekeurige volgorde.
    // Hiermee kan ik op een willekeurige manier de acteur naar links, recht en naar voor laten kijken.
    private ArrayList<Integer> randomNumbers() {
        ArrayList<Integer> numbers = new ArrayList<>();
        while (numbers.size() != 3) {
            int number = RG.nextInt(3);
            if (! numbers.contains(number)) {
                numbers.add(number);
            }
        }
        return numbers;
    }

    private void changeDirection(int value) {
        // Afhankelijk van of de acteur al dan niet is afgeslagen, moet het veld "direction" veranderen.
        direction += CHANGE_DIRECTION[value];
        direction = direction % 4;
        // "direction" kan na aanpassing de waarde -1 krijgen. Aangezien -1 % 4 gelijk is aan -1,
        // moet ik het getal nog met 4 optellen.
        if (direction < 0) {
            direction += 4;
        }
    }

    public void changeHomeCapacity(BuildingTile building, String property) {
        Properties properties = new Properties();
        try (InputStream in = getClass().getResourceAsStream(
                "engine.properties")) {
            properties.load(in);
            double factor = Double.parseDouble(properties.getProperty(property));
            building.changeCapacity(factor);
        } catch (IOException ex) {
            //Doe niets
        }
    }

}
