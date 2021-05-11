package services;

import java.io.File;

import exceptions.BlackWidowListenerException;

public interface TumblerService extends MediaService{
    void postMedia(File media) throws BlackWidowListenerException;
}
