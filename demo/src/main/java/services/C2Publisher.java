package services;

import java.io.IOException;

public interface C2Publisher {
    void publishMessage(String message) throws IOException;
}
