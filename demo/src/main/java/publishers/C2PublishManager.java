package publishers;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import exceptions.BlackWidowPublisherException;
import services.TumblerService;
import services.TumblerServiceApi;
import services.TwitterService;
import services.TwitterServiceApi;

public class C2PublishManager implements C2Publisher {
    private List<C2Publisher> publishers;

    public C2PublishManager(String baseDirectory) {
        this.publishers = new LinkedList();
        publishers.add(new C2MediaPublisher<TwitterService>(baseDirectory, new TwitterServiceApi()));
        publishers.add(new C2MediaPublisher<TumblerService>(baseDirectory, new TumblerServiceApi()));
    }

    public void publish(String message) throws BlackWidowPublisherException {
        boolean published = false;
        for (C2Publisher publisher : publishers) {
            try {
                publisher.publish(message);
                published = true;
                break;
            } catch (BlackWidowPublisherException e) {
            }
        }

        if (!published) {
            throw new BlackWidowPublisherException("Any of the publishers could not publish");
        }
    }
}
