package validation;

import core.model.form.RegistrationForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by Adrian on 11/05/2015.
 */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public void initialize(PasswordMatches passwordMatches) {

    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        RegistrationForm regForm = (RegistrationForm) obj;
        return regForm.getPassword().equals(regForm.getConfirmPassword());
    }

}
