package core.service.impl;

import core.entity.SocialProvider;
import core.repository.SocialRepo;
import core.service.SocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Adrian on 06/07/2015.
 */
@Service
public class SocialServiceImpl implements SocialService {

    @Autowired
    private SocialRepo socialRepo;

    @Override
    public SocialProvider getSocialProviderByName(String name) {
        return socialRepo.findSocialProviderByName(name);
    }
}
