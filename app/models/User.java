package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class User extends Model {
	private static final long serialVersionUID = 1L;

	@Id
	public String email;
	public String firstName;
	public String lastName;
	public String password;

	public static Finder<String, User> find = new Finder<String, User>(String.class, User.class);

	public User(String email, String firstName, String lastName, String password) {
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
	}

	public static User authenticate(String email, String password) {
		return find.where().eq("email", email).eq("password", password).findUnique();
	}

    public static int count() {
        return find.findRowCount();
    }
}
