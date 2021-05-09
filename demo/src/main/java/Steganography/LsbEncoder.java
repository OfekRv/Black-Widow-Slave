package Steganography;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.LinkedList;
import java.util.Queue;

import static utils.ImageUtil.getPixelCoordinates;

public class LsbEncoder implements Encoder<Bitmap, String> {
    private static final int NEW_RGB_FIRST_COLOR = 0;
    private static final int LAST_RGB_COLOR_IDX = 2;
    private static String END_OF_MESSAGE = "\0";
    private static byte MASK = 1;

    @Override
    public Bitmap encode(Bitmap source, String message) {
        int pixelIndex = 0;
        int colorIndex = 0;
        int rgb = 0;
        for (Byte messageByte : messageToLsbQueue(message + END_OF_MESSAGE)) {
            Point coord = getPixelCoordinates(pixelIndex, source);
            if (colorIndex == NEW_RGB_FIRST_COLOR) {
                rgb = source.getPixel(coord.x, coord.y);
            }
            if (colorIndex <= LAST_RGB_COLOR_IDX) {
                rgb = encodeLsbInRgbColor(rgb, colorIndex, messageByte);
                colorIndex++;
            }
            if (colorIndex > 2) {
                source.setPixel(coord.x, coord.y, rgb);
                pixelIndex++;
                colorIndex = 0;
            }
        }

        return source;
    }

    private Queue<Byte> messageToLsbQueue(String message) {
        Queue<Byte> lsbQueue = new LinkedList<>();
        message += END_OF_MESSAGE;
        byte[] messageBytes = message.getBytes();
        for (Byte currentByte : messageBytes) {
            for (int shift = 0; shift < 8; shift++) {
                lsbQueue.add(getLsbAsByte((byte) (currentByte >> shift)));
            }
        }
        return lsbQueue;
    }

    private int encodeLsbInRgbColor(int rgb, int colorIndex, Byte messageByte) {
        int colorOffset = (colorIndex * 8);
        int color = ((((rgb >> colorOffset) & 0xFE)) | messageByte) << colorOffset;
        rgb = (rgb & (~(0xFF << colorOffset))) | color;
        return rgb;
    }

    private byte getLsbAsByte(byte original) {
        return (byte) (original & MASK);
    }
}
