package services;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.exceptions.JumblrException;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;

import java.io.File;
import java.io.IOException;

import exceptions.BlackWidowPublisherException;
import exceptions.BlackWidowServiceException;

public class TumblerServiceApi implements TumblerService {
    private final static String EMPTY_STATUS = "";

    @Override
    public void postMedia(File media) throws BlackWidowServiceException {
        PhotoPost post = null;

        JumblrClient client = generateClient();

        try {
            post = client.newPost(client.user().getBlogs().get(0).getName(), PhotoPost.class);
        } catch (IllegalAccessException | JumblrException | InstantiationException e) {
            throw new BlackWidowServiceException("Could not create post from blog", e);
        }

        post.setCaption(EMPTY_STATUS);
        post.setPhoto(new Photo(media));

        try {
            post.save();
        } catch (IOException e) {
            throw new BlackWidowServiceException("Could not upload image to the post", e);
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
