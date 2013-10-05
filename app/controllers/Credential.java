
package controllers;

import helper.Digester;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import play.Configuration;
import play.Play;
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
		 * This method seems to be called in authenticate()
		 * when bindFromRequest() is invoked. The return value
		 * seems to be some kind of error message
		 * @return null if successful or an error string
		 */
		public String validate()
		{
			try
			{
				String digest = Digester.digest(password);

				Configuration config = Play.application().configuration();

				String username = config.getString("login.user");
				String userpass = config.getString("login.pass");
				
				if (Objects.equals(username, email) && 
					Objects.equals(userpass, digest))
					return null;
				
				return "Invalid user or password";
			}
			catch (NoSuchAlgorithmException e)
			{
				return "No valid encryption algorithm implemented";
			}
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
