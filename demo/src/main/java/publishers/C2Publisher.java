package publishers;

import exceptions.BlackWidowPublisherException;

public interface C2Publisher {
    void publish(String message) throws BlackWidowPublisherException;
}
