package oauth2.pixelpin.api;

import java.io.Serializable;

/**
 * Created by Adrian on 08/08/2015.
 */
public class PixelPinUserProfile implements Serializable {
    private static final long serialVersionUID = 1L;
    private final long id;
    /*private final String login;*/
    private String firstName;
    private String email;

    public PixelPinUserProfile(long id) {
        this.id = id;
    }


    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
