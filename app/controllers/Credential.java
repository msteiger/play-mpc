package controllers;


import models.User;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.login;

public class Credential extends Controller {

    public static class Login {

        public String email;
        public String password;

        public String validate() {
            if (User.authenticate(email, password) == null) {
                return "Invalid user or password";
            }

            return null;
        }
    }


    public static Result login() {
        return ok(
            login.render(Form.form(Login.class))
        );
    }

    public static Result authenticate() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return unauthorized(login.render(loginForm));
        } else {
            session().clear();
            session("email", loginForm.get().email);
            return redirect(
                routes.Application.index()
            );
        }
    }

    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(
                routes.Credential.login()
        );
    }


}
