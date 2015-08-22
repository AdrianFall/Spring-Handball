package core.authentication;

import core.entity.Role;
import core.entity.SocialProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.social.security.SocialUser;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Adrian on 02/07/2015.
 */
public class AccountUserDetails extends SocialUser {

    private Long id;

    private Set<Role> roles;

    private Set<SocialProvider> socialProviders;

    public AccountUserDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    /*public AccountUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, boolean enabled) {
        super(username, password, authorities);
    }*/

    public static class Builder {

        private Long id;
        private String username;
        private String password;
        private Set<Role> roles;
        private boolean enabled;
        private Set<SocialProvider> socialProviders;
        private Set<GrantedAuthority> authorities;

        public Builder() {
            this.authorities = new HashSet<>();
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder password(String password) {
            if (password == null) {
                password = "SocialUser";
            }

            this.password = password;
            return this;
        }

        public Builder roles(Set<Role> roles) {
            this.roles = roles;

            // Build user's authorities
            for (Role userRole : roles) {
                this.authorities.add(new SimpleGrantedAuthority(userRole.getRole()));
            }

            return this;
        }

        public Builder socialSignInProviders(Set<SocialProvider> socialProviders) {
            this.socialProviders = socialProviders;
            return this;
        }


        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public AccountUserDetails build() {
            AccountUserDetails user = new AccountUserDetails(username, password, enabled, true, true, true, authorities);

            user.id = id;
            user.roles = roles;
            user.socialProviders = socialProviders;

            return user;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<SocialProvider> getSocialProviders() {
        return socialProviders;
    }

    public void setSocialProviders(Set<SocialProvider> socialProviders) {
        this.socialProviders = socialProviders;
    }

    public static Builder getBuilder() {
        return new Builder();
    }
}
