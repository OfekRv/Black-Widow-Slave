package services;

import java.io.File;

import exceptions.BlackWidowServiceException;

public interface TwitterService extends MediaService {
    void postMedia(File media) throws BlackWidowServiceException;
}
