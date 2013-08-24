
package controllers;

import static org.bff.javampd.MPDPlayer.PlayerStatus.STATUS_PLAYING;
import static play.data.Form.form;
import helper.MpdMonitor;
import helper.MpdUtils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import models.Computer;
import models.Playlist;

import org.bff.javampd.MPD;
import org.bff.javampd.MPDPlayer;
import org.bff.javampd.MPDPlayer.PlayerStatus;
import org.bff.javampd.events.VolumeChangeEvent;
import org.bff.javampd.events.VolumeChangeListener;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.exception.MPDException;
import org.bff.javampd.exception.MPDPlayerException;
import org.bff.javampd.monitor.MPDStandAloneMonitor;
import org.bff.javampd.objects.MPDSong;

import play.Configuration;
import play.Logger;
import play.Play;
import play.Routes;
import play.data.Form;
import play.libs.Comet;
import play.libs.F.Callback0;
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

	public static final List<Comet> sockets = new ArrayList<Comet>();
	
	static
	{
		MPDStandAloneMonitor monitor = MpdMonitor.getInstance().getMonitor();
		monitor.addVolumeChangeListener(new VolumeChangeListener()
		{
			@Override
			public void volumeChanged(VolumeChangeEvent event)
			{
				Logger.info("Volume changed :" + event.getVolume() + " - " + event.getMsg());
				
				for (Comet comet : sockets)
				{
					comet.sendMessage(String.valueOf(event.getVolume()));
				}
			}
		});
	}

	/**
	 * Handle default path requests, redirect to computers list
	 * @return an action result
	 */
	public static Result index()
	{
		return GO_HOME;
	}
	
	/**
	 * Handles calls from the IFRAME and returns 
	 * @return a Comet connection Result
	 */
	public static Result liveUpdate()
	{
		final Comet comet = new Comet("parent.volumeChanged")
		{
			@Override
			public void onConnected()
			{
				sockets.add(this);
				Logger.info("New browser connected (" + sockets.size() + " browsers currently connected)");
				
				final Comet myComet = this;
				
				Callback0 callback = new Callback0()
				{
					@Override
					public void invoke() throws Throwable
					{
						sockets.remove(myComet);
						Logger.info("Browser disconnected (" + sockets.size() + " browsers currently connected)");
					}
				};
				
				this.onDisconnected(callback);

			}
		};
		
		return ok(comet);
	}
	
	public static Result javascriptRoutes() 
	{
	    response().setContentType("text/javascript");
	    return ok(Routes.javascriptRouter("jsRoutes",
	            controllers.routes.javascript.Application.volume(),
	            controllers.routes.javascript.Application.selectSong()
	        )
	    );
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

			player.playNext();
			
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
			
			player.playPrev();
			
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
	 * Performs POST /volume
	 * @return an action result
	 */
	public static Result volume(int volume)
	{
		Logger.info("VOLUME " + volume);
		
		MPD mpd = MpdMonitor.getInstance().getMPD();
		try
		{
			mpd.getMPDPlayer().setVolume(volume);
		}
		catch (MPDPlayerException | MPDConnectionException e)
		{
			flash("error", "Changing volume failed! " + e.getMessage());
		}
		
		return GO_HOME;
	}

	/**
	 * Performs POST /selectsong/:pos
	 * @return an action result
	 */
	public static Result selectSong(int pos)
	{
		Logger.info("Play Song " + pos);
		
		MPD mpd = MpdMonitor.getInstance().getMPD();
		try
		{
			MPDSong song = mpd.getMPDPlaylist().getSongList().get(pos);
			mpd.getMPDPlayer().playId(song);
		}
		catch (MPDException e)
		{
			flash("error", "Changing volume failed! " + e.getMessage());
		}
		
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
