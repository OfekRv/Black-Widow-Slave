package services;

import exceptions.BlackWidowServiceException;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.time.LocalDateTime;

public class TwitterServiceApi implements TwitterService {
    private final static String EMPTY_STATUS = "";

    private Twitter client;

    public TwitterServiceApi() {
        ConfigurationBuilder twitterConfigBuilder = new ConfigurationBuilder();
        twitterConfigBuilder.setOAuthConsumerKey("INSERT HERE");
        twitterConfigBuilder.setOAuthConsumerSecret("INSERT HERE");
        twitterConfigBuilder.setOAuthAccessToken("INSERT HERE");
        twitterConfigBuilder.setOAuthAccessTokenSecret("INSERT HERE");
        client = new TwitterFactory(twitterConfigBuilder.build()).getInstance();
    }

    @Override
    public void postMedia(File media) throws BlackWidowServiceException {
        StatusUpdate status = new StatusUpdate(EMPTY_STATUS);
        status.setMedia(media);

        try {
            client.updateStatus(status);
        } catch (TwitterException e) {
            throw new BlackWidowServiceException("Could not upload image to status");
        }
    }
}
