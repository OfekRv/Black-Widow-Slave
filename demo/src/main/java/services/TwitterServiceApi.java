package services;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.time.LocalDateTime;

public class TwitterServiceApi implements TwitterService {
    private final static String EMPTY_STATUS = "";

    @Override
    public void postMedia(File media) {
        ConfigurationBuilder twitterConfigBuilder = new ConfigurationBuilder();
        twitterConfigBuilder.setOAuthConsumerKey("INSERT HERE");
        twitterConfigBuilder.setOAuthConsumerSecret("INSERT HERE");
        twitterConfigBuilder.setOAuthAccessToken("INSERT HERE");
        twitterConfigBuilder.setOAuthAccessTokenSecret("INSERT HERE");
        Twitter twitter = new TwitterFactory(twitterConfigBuilder.build()).getInstance();
        StatusUpdate status = new StatusUpdate(EMPTY_STATUS);
        status.setMedia(media);

        try {
            twitter.updateStatus(status);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }
}
