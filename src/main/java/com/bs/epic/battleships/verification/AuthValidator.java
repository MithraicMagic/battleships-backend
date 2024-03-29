package com.bs.epic.battleships.verification;

import com.bs.epic.battleships.events.ErrorEvent;
import com.bs.epic.battleships.rest.requestbodies.Login;
import com.bs.epic.battleships.rest.requestbodies.Register;
import com.bs.epic.battleships.util.result.Error;
import com.bs.epic.battleships.util.result.Success;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

@Component
public class AuthValidator {
    public Result isValidLogin(Login login) {
        if (!isValidEmail(login.email)) {
            return Result.error(422, "Invalid email");
        }

        return isValidPassword(login.password);
    }

    public Result isValidRegister(Register register) {
        if (!isValidEmail(register.email)) {
            return Result.error(422, "Invalid email");
        }

        var usernameResult = isValidUsername(register.username);
        if (!usernameResult.success) return usernameResult;

        var passwordResult = isValidPassword(register.password);
        if (!passwordResult.success) return passwordResult;

        return Result.success();
    }

    public com.bs.epic.battleships.util.result.Result verifyUsername(String name) {
        var result = isValidUsername(name);

        return new com.bs.epic.battleships.util.result.Result(result.success,
                result.success ? null : new ErrorEvent("inputUsername", result.message));
    }

    public Result isValidUsername(String username) {
        if (username == null) return Result.error(422, "Missing username");
        if (username.length() < 4) return Result.error(422, "Username is too short");
        if (username.length() > 20) return Result.error(422, "Username is too long");
        if (username.isBlank()) return Result.error(422, "Username needs to contain actual characters");

        return Result.success();
    }

    private boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    private Result isValidPassword(String password) {
        if (password == null) return Result.error(422, "Missing password");
        if (password.length() < 8) return Result.error(422, "Password is too short");
        if (password.length() > 64) return Result.error(422, "Password is too long");
        if (password.isBlank()) return Result.error(422, "Password needs to contain actual characters");

        return Result.success();
    }
}
