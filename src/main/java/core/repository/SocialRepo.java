package core.repository;

import core.entity.SocialProvider;

/**
 * Created by Adrian on 06/07/2015.
 */
public interface SocialRepo {
    public SocialProvider findSocialProviderByName(String name);
}
