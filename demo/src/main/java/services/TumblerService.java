package services;

import java.io.File;

import exceptions.BlackWidowPublisherException;
import exceptions.BlackWidowServiceException;

public interface TumblerService extends MediaService{
    void postMedia(File media) throws BlackWidowPublisherException, BlackWidowServiceException;
}
