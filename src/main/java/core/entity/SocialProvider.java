package core.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Adrian on 06/07/2015.
 */
@Entity
@Table(name = "social_provider")
public class SocialProvider {
    @Id
    private String provider_name;

     public SocialProvider(String provider_name) {
         this.provider_name = provider_name;
     }

    public SocialProvider() {

    }

    public String getProvider_name() { return provider_name; }
    public void setProvider_name(String provider_name) { this.provider_name = provider_name; }
}
