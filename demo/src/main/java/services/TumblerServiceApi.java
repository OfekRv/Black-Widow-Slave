package services;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;

import java.io.File;
import java.io.IOException;

import exceptions.BlackWidowListenerException;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TumblerServiceApi implements TumblerService {
    private final static String EMPTY_STATUS = "";

    @Override
    public void postMedia(File media) throws BlackWidowListenerException {
        PhotoPost post = null;

        JumblrClient client = generateClient();

        try {
            post = client.newPost(client.user().getBlogs().get(0).getName(), PhotoPost.class);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new BlackWidowListenerException("Could not create post from blog", e);
        }

        post.setCaption(EMPTY_STATUS);
        post.setPhoto(new Photo(media));

        try {
            post.save();
        } catch (IOException e) {
            throw new BlackWidowListenerException("Could not upload image to the post", e);
        }
    }

    private JumblrClient generateClient() {
        JumblrClient client = new JumblrClient(
                "INSERT_HERE",
                "INSERT_HERE"
        );
        client.setToken(
                "INSERT_HERE",
                "INSERT_HERE"
        );

        return client;
    }
}
