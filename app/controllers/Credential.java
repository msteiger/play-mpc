
package controllers;

import models.User;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.login;

/**
 * Deals with user authorization
 * @author Martin Steiger
 */
public class Credential extends Controller
{
	/**
	 * Login credentials
	 */
	public static class Login
	{
		public String email;
		public String password;

		/**
		 * @return null if successful or an error string
		 */
		public String validate()
		{
			if (User.authenticate(email, password) == null)
			{
				return "Invalid user or password";
			}

			return null;
		}
	}

	/**
	 * Performs GET /login
	 * @return an action result
	 */
	public static Result login()
	{
		return ok(login.render(Form.form(Login.class)));
	}

	/**
	 * Performs POST /login
	 * @return an action result
	 */
	public static Result authenticate()
	{
		Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
		if (loginForm.hasErrors())
		{
			return unauthorized(login.render(loginForm));
		}
		else
		{
			session().clear();
			session("email", loginForm.get().email);
			return redirect(routes.Application.index());
		}
	}

	/**
	 * Performs GET /logout
	 * @return an action result
	 */
	public static Result logout()
	{
		session().clear();
		flash("success", "You've been logged out");
		return redirect(routes.Credential.login());
	}

}
