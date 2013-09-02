
package controllers;

import static org.bff.javampd.MPDPlayer.PlayerStatus.STATUS_PLAYING;
import helper.EmptyPage;
import helper.MpdMonitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import models.Database;
import models.Playlist;

import org.bff.javampd.MPD;
import org.bff.javampd.MPDPlayer;
import org.bff.javampd.MPDPlayer.PlayerStatus;
import org.bff.javampd.MPDPlaylist;
import org.bff.javampd.events.VolumeChangeEvent;
import org.bff.javampd.events.VolumeChangeListener;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.exception.MPDException;
import org.bff.javampd.exception.MPDPlayerException;
import org.bff.javampd.monitor.MPDStandAloneMonitor;
import org.bff.javampd.objects.MPDSong;

import play.Logger;
import play.Routes;
import play.api.templates.Html;
import play.libs.Comet;
import play.libs.F.Callback0;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import views.html.database;
import views.html.main;
import views.html.playlist;
import views.html.info;

import com.avaje.ebean.Page;

/**
 * Manage a database of computers
 */
@Security.Authenticated(Secured.class)
public class Application extends Controller
{
	/**
	 * This result directly redirect to application home.
	 */
	public static Result GO_HOME = redirect(routes.Application.playlist(0));

	public static final List<Comet> sockets = new ArrayList<Comet>();
	
	static
	{
//		MPDStandAloneMonitor monitor = MpdMonitor.getInstance().getMonitor();
//		monitor.addVolumeChangeListener(new VolumeChangeListener()
//		{
//			@Override
//			public void volumeChanged(VolumeChangeEvent event)
//			{
//				Logger.info("Volume changed :" + event.getVolume() + " - " + event.getMsg());
//				
//				for (Comet comet : sockets)
//				{
//					comet.sendMessage(String.valueOf(event.getVolume()));
//				}
//			}
//		});
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
	            controllers.routes.javascript.Application.setVolume(),
	            controllers.routes.javascript.Application.selectSong()
	        )
	    );
	}

	/**
	 * Display the paginated list of playlist entries.
	 * @param page Current page number (starts from 0)
	 * @return an action result
	 */
	public static Result playlist(int page)
	{
		try
		{
			MPD mpd = MpdMonitor.getInstance().getMPD();
			MPDPlayer player = mpd.getMPDPlayer();
			Page<MPDSong> songs = Playlist.getSongs(page, 10);

			return ok(playlist.render(player, songs));
		}
		catch (MPDException e)
		{
			flash("error", "Command failed! " + e.getMessage());
			return ok(playlist.render(null, new EmptyPage<MPDSong>()));
		}
		
	}

	/**
	 * Display the paginated list of computers.
	 * @param page Current page number (starts from 0)
	 * @param sortBy Column to be sorted
	 * @param order Sort order (either asc or desc)
	 * @param filter Filter applied on computer names
	 * @return an action result
	 */
	public static Result browseDb(int page, String sortBy, String order, String filter)
	{
		Page<MPDSong> songs = null;
		
		try
		{
			songs = Database.getSongs(page, 10, sortBy, order, filter);
		}
		catch (MPDException e)
		{
			flash("error", "Command failed! " + e.getMessage());
			songs = new EmptyPage<>();
		}
		
		return ok(database.render(songs, sortBy, order, filter));
	}

	/**
	 * Performs POST /addUrl
	 * Display the 'Add from URL form'.
	 * @return an action result
	 */
	public static Result addUrl(String url)
	{
		// TODO: parse ending
		// extract URL from playlist URL if necessary
		
		Logger.info("Adding to playlist: " + url);
		
		return GO_HOME;
	}
	/**
	 * Performs POST /addUrl
	 * Display the 'Add from URL form'.
	 * @return an action result
	 */
	public static Result addDbEntry(String url)
	{
		try
		{
			// TODO: parse ending
			// extract URL from playlist URL if necessary
			
			Logger.info("Adding to playlist: " + url);
			
			MPD mpd = MpdMonitor.getInstance().getMPD();

			// TODO: this is silly - first search song by filename then use the filename of the song
			// However, it prevents adding files that are not in the DB (maybe MPD checks this already?)
			Collection<MPDSong> songs = mpd.getMPDDatabase().searchFileName(url);
	
			if (songs.size() == 1)
			{
				MPDSong song = songs.iterator().next();
				mpd.getMPDPlaylist().addSong(song);
			}
			else
			{
				Logger.warn("Songs expected: 1 - found " + songs.size());
			}
		}
		catch (MPDException e)
		{
			flash("error", "Command failed! " + e.getMessage());
		}
		
		// TODO: either use something like GO_HOME or return "false" and avoid reloading
		return redirect(routes.Application.browseDb(0, "name", "asc", ""));
	}

	/**
	 * Performs GET /playSong
	 * @return an action result
	 */
	public static Result playSong()
	{
		try
		{
			MPD mpd = MpdMonitor.getInstance().getMPD();
			MPDPlayer player = mpd.getMPDPlayer();
			PlayerStatus status = player.getStatus();
			
			if (status == STATUS_PLAYING)
				player.pause(); else
				player.play();
		}
		catch (MPDException e)
		{
			flash("error", "Command failed! " + e.getMessage());
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
			MPD mpd = MpdMonitor.getInstance().getMPD();
			MPDPlayer player = mpd.getMPDPlayer();

			player.playNext();
		}
		catch (MPDException e)
		{
			flash("error", "Command failed! " + e.getMessage());
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
			MPD mpd = MpdMonitor.getInstance().getMPD();
			MPDPlayer player = mpd.getMPDPlayer();
			
			player.playPrev();
		}
		catch (MPDException e)
		{
			flash("error", "Command failed! " + e.getMessage());
		}
		
		return GO_HOME;
	}
	

	/**
	 * Performs GET /stopSong
	 * @return an action result
	 */
	public static Result stopSong()
	{
		try
		{
			MPD mpd = MpdMonitor.getInstance().getMPD();
			MPDPlayer player = mpd.getMPDPlayer();
			
			player.stop();
		}
		catch (MPDException e)
		{
			flash("error", "Command failed! " + e.getMessage());
		}

		return GO_HOME;
	}

	/**
	 * Performs POST /volume
	 * @param volume the new volume level
	 * @return an action result
	 */
	public static Result setVolume(int volume)
	{
		Logger.info("Set volume " + volume);
		
		try
		{
			MPD mpd = MpdMonitor.getInstance().getMPD();
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
		
		try
		{
			MPD mpd = MpdMonitor.getInstance().getMPD();
			MPDSong song = mpd.getMPDPlaylist().getSongList().get(pos);
			mpd.getMPDPlayer().playId(song);
		}
		catch (MPDException e)
		{
			flash("error", "Changing song failed! " + e.getMessage());
		}
		
		return GO_HOME;
	}
	
	/**
	 * Performs GET /update
	 * @return an action result
	 */
	public static Result updateDb()
	{
		try
		{
			MPD mpd = MpdMonitor.getInstance().getMPD();

			mpd.getMPDAdmin().updateDatabase();
			
			flash("success", "Updating database!");
		}
		catch (MPDException e)
		{
			flash("error", "Updating database failed!" + e.getMessage());
		}

		return GO_HOME;
	}

	/**
	 * Remove entry from playlist
	 * @param id the playlist entry pos
	 * @return an action result
	 */
	public static Result remove(int id)
	{
		Logger.info("Removing entry from playlist: " + id);
		
		try
		{
			MPD mpd = MpdMonitor.getInstance().getMPD();
			MPDPlaylist mpdPlaylist = mpd.getMPDPlaylist();
			MPDSong song = mpdPlaylist.getSongList().get(id);

			mpdPlaylist.removeSong(song);
		}
		catch (MPDException e)
		{
			flash("error", "Removing entry from playlist failed! " + e.getMessage());
		}
		
		return GO_HOME;
	}

	/**
	 * Render info page GET /info
	 * @return the info page
	 */
	public static Result info()
	{
		try
		{
			MPD mpd = MpdMonitor.getInstance().getMPD();
			return ok(info.render(mpd));
		}
		catch (MPDException e)
		{
			flash("error", e.getMessage());
			return ok(main.render(null, null)); 
		}
	}
}
