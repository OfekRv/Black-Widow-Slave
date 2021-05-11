package services;

import java.io.File;

public interface TwitterService extends MediaService {
    void postMedia(File media);
}
