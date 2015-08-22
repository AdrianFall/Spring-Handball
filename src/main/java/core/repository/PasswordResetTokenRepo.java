package core.repository;

import core.entity.PasswordResetToken;

/**
 * Created by Adrian on 29/06/2015.
 */
public interface PasswordResetTokenRepo {
    public PasswordResetToken createPasswordResetToken(PasswordResetToken token);
    public PasswordResetToken findPasswordResetToken(String token);
}
