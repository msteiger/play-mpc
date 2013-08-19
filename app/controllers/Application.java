
package controllers;

import static play.data.Form.form;

import helper.MpdUtils;

import java.net.UnknownHostException;

import models.Computer;
import models.Playlist;

import org.bff.javampd.MPD;
import org.bff.javampd.MPDPlayer;
import org.bff.javampd.MPDPlayer.PlayerStatus;

import static org.bff.javampd.MPDPlayer.PlayerStatus.*;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.exception.MPDException;
import org.bff.javampd.exception.MPDPlayerException;

import play.Configuration;
import play.Logger;
import play.Play;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.createForm;
import views.html.editForm;
import views.html.list;

/**
 * Manage a database of computers
 */
@Security.Authenticated(Secured.class)
public class Application extends Controller
{

	/**
	 * This result directly redirect to application home.
	 */
	public static Result GO_HOME = redirect(routes.Application.list(0, "name", "asc", ""));

	/**
	 * Handle default path requests, redirect to computers list
	 * @return an action result
	 */
	public static Result index()
	{
		return GO_HOME;
	}

	/**
	 * Display the paginated list of computers.
	 * @param page Current page number (starts from 0)
	 * @param sortBy Column to be sorted
	 * @param order Sort order (either asc or desc)
	 * @param filter Filter applied on computer names
	 * @return an action result
	 */
	public static Result list(int page, String sortBy, String order, String filter)
	{
		return ok(list.render(Playlist.getSongs(page, 10, sortBy, order, filter), sortBy, order, filter));
	}

	/**
	 * Display the 'edit form' of a existing Computer.
	 * @param id Id of the computer to edit
	 * @return an action result
	 */
	public static Result edit(Long id)
	{
		Form<Computer> computerForm = form(Computer.class).fill(Computer.find.byId(id));
		return ok(editForm.render(id, computerForm));
	}

	/**
	 * Performs GET /computers/:id <br/>
	 * Handle the 'edit form' submission
	 * @param id Id of the computer to edit
	 * @return an action result
	 */
	public static Result update(Long id)
	{
		Form<Computer> computerForm = form(Computer.class).bindFromRequest();
		if (computerForm.hasErrors())
		{
			return badRequest(editForm.render(id, computerForm));
		}
		computerForm.get().update(id);
		flash("success", "Computer " + computerForm.get().name + " has been updated");
		return GO_HOME;
	}

	/**
	 * Performs GET computers/new
	 * Display the 'new computer form'.
	 * @return an action result
	 */
	public static Result create()
	{
		Form<Computer> computerForm = form(Computer.class);
		return ok(createForm.render(computerForm));
	}

	/**
	 * Performs GET /playSong
	 * @return an action result
	 */
	public static Result playSong()
	{
		try
		{
			MPD mpd = MpdUtils.createInstance();
			MPDPlayer player = mpd.getMPDPlayer();
			PlayerStatus status = player.getStatus();
			
			if (status == STATUS_PLAYING)
			{
				player.pause();
			}
			else
			{
				player.play();
			}
			
			mpd.close();
		}
		catch (UnknownHostException | MPDException e)
		{
			flash("error", "Command failed! " + e.getMessage());
		}
		finally
		{
		}
		
		return GO_HOME;
	}

	/**
	 * Performs GET /nextSong
	 * @return an action result
	 */
	public static Result nextSong()
	{
		try
		{
			MPD mpd = MpdUtils.createInstance();
			MPDPlayer player = mpd.getMPDPlayer();
			PlayerStatus status = player.getStatus();
			
			if (status == STATUS_PLAYING)
			{
				player.pause();
			}
			else
			{
				player.playNext();
			}
			
			mpd.close();
		}
		catch (UnknownHostException | MPDException e)
		{
			flash("error", "Command failed! " + e.getMessage());
		}
		finally
		{
		}
		
		return GO_HOME;
	}

	/**
	 * Performs GET /prevSong
	 * @return an action result
	 */
	public static Result prevSong()
	{
		try
		{
			MPD mpd = MpdUtils.createInstance();
			MPDPlayer player = mpd.getMPDPlayer();
			PlayerStatus status = player.getStatus();
			
			if (status == STATUS_PLAYING)
			{
				player.pause();
			}
			else
			{
				player.playPrev();
			}
			
			mpd.close();
		}
		catch (UnknownHostException | MPDException e)
		{
			flash("error", "Command failed! " + e.getMessage());
		}
		finally
		{
		}
		
		return GO_HOME;
	}
	

	/**
	 * Performs GET /stopSong
	 * @return an action result
	 */
	public static Result stopSong()
	{
		return GO_HOME;
	}


	/**
	 * Performs GET /update
	 * @return an action result
	 */
	public static Result updateDB()
	{
		Configuration config = Play.application().configuration();
		String hostname = config.getString("mpd.hostname");
		int port = config.getInt("mpd.port");

		flash("info", "Connecting to " + hostname + ":" + port + " ...");

		try
		{
			MPD mpd = new MPD(hostname, port);

			Logger.info("Connected to: " + hostname + ":" + port);
			Logger.info("Version:" + mpd.getVersion());
			Logger.info("Uptime:" + mpd.getUptime());

			mpd.getMPDAdmin().updateDatabase();
			
			flash("success", "Updating database!");
		
			mpd.close();
		}
		catch (MPDException | UnknownHostException e)
		{
			Logger.warn("Error Connecting:" + e.getMessage());
			flash("error", "Connection to " + hostname + " failed! " + e.getLocalizedMessage());
		}

		return GO_HOME;
	}

	/**
	 * Handle the 'new computer form' submission
	 * @return an action result
	 */
	public static Result save()
	{
		Form<Computer> computerForm = form(Computer.class).bindFromRequest();
		if (computerForm.hasErrors())
		{
			return badRequest(createForm.render(computerForm));
		}
		computerForm.get().save();
		flash("success", "Computer " + computerForm.get().name + " has been created");
		return GO_HOME;
	}

	/**
	 * Handle computer deletion
	 * @param id the computer id
	 * @return an action result
	 */
	public static Result delete(Long id)
	{
		Computer.find.ref(id).delete();
		flash("success", "Computer has been deleted");
		return GO_HOME;
	}

}
