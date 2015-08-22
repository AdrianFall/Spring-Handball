package oauth2.pixelpin.api.impl.json;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Adrian on 08/08/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
abstract class PixelPinObjectMixin {

    @JsonAnySetter
    abstract void add(String key, Object value);
}
