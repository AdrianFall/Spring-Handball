package oauth2.pixelpin.api.impl.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Adrian on 08/08/2015.
 */
abstract class PixelPinUserProfileMixin extends PixelPinObjectMixin {

    @JsonProperty("name")
    String name;

    @JsonProperty("location")
    String location;

    @JsonProperty("firstName")
    String firstName;

    @JsonProperty("id")
    long id;

    @JsonProperty("email")
    String email;

    PixelPinUserProfileMixin(
            @JsonProperty("id") long id) {}

}
