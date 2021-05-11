package services;

import java.io.File;

import exceptions.BlackWidowListenerException;

public interface MediaService {
    void postMedia(File media) throws BlackWidowListenerException;
}
