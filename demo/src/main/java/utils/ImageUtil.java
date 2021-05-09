package utils;

import android.graphics.Bitmap;
import android.graphics.Point;

public class ImageUtil {
    public static Point getPixelCoordinates(int pixelIndex, Bitmap img) {
        return new Point(pixelIndex % img.getWidth(), pixelIndex / img.getWidth());
    }
}
