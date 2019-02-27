package LIC.UC04v1.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import LIC.UC04v1.model.User;
import LIC.UC04v1.web.UserDto;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(final PasswordMatches constraintAnnotation) {
        //
    }

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        final UserDto user = (UserDto) obj;
        return user.getPassword().equals(user.getMatchingPassword());
    }

}