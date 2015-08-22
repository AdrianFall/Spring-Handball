package core.service;

import core.entity.SocialProvider;

/**
 * Created by Adrian on 06/07/2015.
 */
public interface SocialService {
    public SocialProvider getSocialProviderByName(String name);
}
