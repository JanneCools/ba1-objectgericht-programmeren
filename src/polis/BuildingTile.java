package polis;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;

import java.util.Map;

public class BuildingTile extends PolygonTile {

    private String imageName;

    public BuildingTile(int cellsize, int r, int k, String imageName) {
        super(cellsize, 2, r, k);
        this.imageName = imageName;
        Image image = new Image(imageName);
        setFill(new ImagePattern(image));
    }

    public String getImageName() {
        return imageName;
    }

    public void setImage(String imageName, Map<PolygonTile, Paint> originalPaint) {
        this.imageName = imageName;
        Image image = new Image(this.imageName);
        setFill(new ImagePattern(image));
        originalPaint.replace(this, getFill());
    }

    public String getBackground() {
        return "building";
    }

    public void remove(CityMap model, Map<PolygonTile, Paint> originalPaint) {
        model.userPolygons.remove((r-1)+"-"+k);
        model.userPolygons.remove(r+"-"+(k+1));
        model.userPolygons.remove((r-1)+"-"+(k+1));
        super.remove(model, originalPaint);
    }
}
