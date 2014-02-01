
package controllers;

import static org.bff.javampd.MPDPlayer.PlayerStatus.STATUS_PLAYING;
import helper.EmptyPage;
import helper.MpdMonitor;
import helper.UrlParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.Database;
import models.Playlist;

import org.bff.javampd.MPD;
import org.bff.javampd.MPDAdmin;
import org.bff.javampd.MPDFile;
import org.bff.javampd.MPDOutput;
import org.bff.javampd.MPDPlayer;
import org.bff.javampd.MPDPlayer.PlayerStatus;
import org.bff.javampd.MPDPlaylist;
import org.bff.javampd.events.PlayerBasicChangeEvent;
import org.bff.javampd.events.PlayerBasicChangeListener;
import org.bff.javampd.events.PlaylistBasicChangeEvent;
import org.bff.javampd.events.PlaylistBasicChangeListener;
import org.bff.javampd.events.TrackPositionChangeEvent;
import org.bff.javampd.events.TrackPositionChangeListener;
import org.bff.javampd.events.VolumeChangeEvent;
import org.bff.javampd.events.VolumeChangeListener;
import org.bff.javampd.exception.MPDAdminException;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.exception.MPDException;
import org.bff.javampd.exception.MPDPlayerException;
import org.bff.javampd.monitor.MPDStandAloneMonitor;
import org.bff.javampd.objects.MPDSavedPlaylist;
import org.bff.javampd.objects.MPDSong;

import play.Logger;
import play.Routes;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.database;
import views.html.info;
import views.html.main;
import views.html.playlist;
import views.html.playlists;

import com.avaje.ebean.Page;

import static controllers.App.sendWebsocketMessage;

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

	static
	{
		try
		{
			MPDStandAloneMonitor monitor = MpdMonitor.getInstance().getMonitor();

			Logger.info("Start monitoring ...");
			monitor.addPlayerChangeListener(new PlayerBasicChangeListener()
			{
				@Override
				public void playerBasicChange(PlayerBasicChangeEvent event)
				{
					int id = event.getId();

					try
					{
						MPDPlayer player = MpdMonitor.getInstance().getMPD().getMPDPlayer();
					
						switch (id)
						{
						case PlayerBasicChangeEvent.PLAYER_CONSUME_CHANGE:
							sendWebsocketMessage("consume", player.isConsuming() ? 1 : 0);
							break;

						case PlayerBasicChangeEvent.PLAYER_SINGLE_CHANGE:
							sendWebsocketMessage("single", player.isSingleMode() ? 1 : 0);
							break;

						case PlayerBasicChangeEvent.PLAYER_REPEAT_CHANGE:
							sendWebsocketMessage("repeat", player.isRepeat() ? 1 : 0);
							break;
							
						case PlayerBasicChangeEvent.PLAYER_RANDOM_CHANGE:
							sendWebsocketMessage("shuffle", player.isRandom() ? 1 : 0);
							break;

						case PlayerBasicChangeEvent.PLAYER_PAUSED:
							sendWebsocketMessage("status", "pause");
							break;

						case PlayerBasicChangeEvent.PLAYER_STOPPED:
							sendWebsocketMessage("status", "stop");
							break;

						case PlayerBasicChangeEvent.PLAYER_UNPAUSED:
						case PlayerBasicChangeEvent.PLAYER_STARTED:
							sendWebsocketMessage("status", "play");
							break;
							
						case PlayerBasicChangeEvent.PLAYER_BITRATE_CHANGE:
							// ignore silently - changes from 0 to x and back occur frequently 
							break;
							
						default:
							Logger.info("Ignored player change message " + id);
							break;
						}
					}
					catch (MPDException e)
					{
						Logger.warn("Error on event " + id, e);
					}
				}
			});
			
			monitor.addTrackPositionChangeListener(new TrackPositionChangeListener()
			{
				@Override
				public void trackPositionChanged(TrackPositionChangeEvent event)
				{
					sendWebsocketMessage("songpos", event.getElapsedTime());
				}
			});
			
			monitor.addVolumeChangeListener(new VolumeChangeListener()
			{
				@Override
				public void volumeChanged(VolumeChangeEvent event)
				{
					sendWebsocketMessage("volume", event.getVolume());
				}
			});
			
			monitor.addPlaylistChangeListener(new PlaylistBasicChangeListener()
			{
				@Override
				public void playlistBasicChange(PlaylistBasicChangeEvent event)
				{
					try
					{
						MPDPlayer player = MpdMonitor.getInstance().getMPD().getMPDPlayer();
						switch (event.getId())
						{
						case PlaylistBasicChangeEvent.SONG_ADDED:
						case PlaylistBasicChangeEvent.SONG_DELETED:
							sendWebsocketMessage("reload", event.getId());
							break;

						case PlaylistBasicChangeEvent.PLAYLIST_ENDED:
						case PlaylistBasicChangeEvent.PLAYLIST_CHANGED:
							// just don't care
							break;

						case PlaylistBasicChangeEvent.SONG_CHANGED:
							sendWebsocketMessage("select", player.getCurrentSong().getPosition());
							sendWebsocketMessage("songlength", player.getCurrentSong().getLength());
							break;
						}
					}
					catch (MPDException e)
					{
						Logger.warn("Error on event " + event.getId(), e);
					}
				}
			});
		}
		catch (MPDConnectionException e)
		{
			Logger.warn("Could not connect", e);
		}
	}


	/**
	 * Handle default path requests, redirect to computers list
	 * @return an action result
	 */
	public static Result index()
	{
		return GO_HOME;
	}
	
	public static Result javascriptRoutes() 
	{
	    response().setContentType("text/javascript");
	    return ok(Routes.javascriptRouter("jsRoutes",
	            controllers.routes.javascript.Application.prevSong(),
	            controllers.routes.javascript.Application.playSong(),
	            controllers.routes.javascript.Application.nextSong(),
	            controllers.routes.javascript.Application.stopSong(),

	            controllers.routes.javascript.Application.toggleShuffle(),
	            controllers.routes.javascript.Application.toggleRepeat(),
	            controllers.routes.javascript.Application.toggleSingleMode(),
	            controllers.routes.javascript.Application.toggleConsuming(),
	            
	            controllers.routes.javascript.Application.setVolume(),
	            controllers.routes.javascript.Application.selectSong(),
	            controllers.routes.javascript.Application.setSongPos(),
	            controllers.routes.javascript.Application.addUrl(),
	            controllers.routes.javascript.Application.addDbEntry(),
	            controllers.routes.javascript.Application.remove(),

	            controllers.routes.javascript.Application.playlistContent(),
	            controllers.routes.javascript.Application.playlistDelete(),
	            controllers.routes.javascript.Application.playlistLoad(),
	            controllers.routes.javascript.Application.playlistSave(),

	            controllers.routes.javascript.Application.toggleOutput()
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
			Logger.error("MPD error", e);

			flash("error", "Command failed! " + e.getMessage());
			return ok(playlist.render(null, new EmptyPage<MPDSong>()));
		}
		
	}

	/**
	 * Display all available playlists
	 * @return the rendered html content
	 */
	public static Result playlists()
	{
		try
		{
			MPD mpd = MpdMonitor.getInstance().getMPD();
		    
			List<MPDSavedPlaylist> savedlists = mpd.getMPDDatabase().listSavedPlaylists();

//			List<String> savedlists = mpd.getMPDDatabase().listPlaylists();
			
			return ok(playlists.render(savedlists));
		}
		catch (MPDException e)
		{
			Logger.error("MPD error", e);

			flash("error", "Command failed! " + e.getMessage());
			return ok(playlists.render(Collections.<MPDSavedPlaylist>emptyList()));
		}
	}

	/**
	 * Return all songs of a given playlist
	 * @return the rendered html content
	 */
	public static Result playlistContent(String id)
	{
		try
		{
			MPD mpd = MpdMonitor.getInstance().getMPD();
		        
			List<MPDSong> songs = mpd.getMPDDatabase().listPlaylistSongs(id);
			
			return ok(Json.toJson(songs));
		}
		catch (MPDException e)
		{
			Logger.error("MPD error", e);

			flash("error", "Command failed! " + e.getMessage());
			return ok(playlists.render(Collections.<MPDSavedPlaylist>emptyList()));
		}
	}

	/**
	 * Load a playlist
	 * @return an empty ok
	 */
	public static Result playlistLoad(String id)
	{
		try
		{
			MPD mpd = MpdMonitor.getInstance().getMPD();
			MPDPlaylist playlist = mpd.getMPDPlaylist();
			
			playlist.clearPlaylist();
			playlist.loadPlaylist(id);
			
			return ok("");
		}
		catch (MPDException e)
		{
			Logger.error("MPD error", e);

			flash("error", e.getMessage());
			return internalServerError(e.getMessage());
		}
	}

	/**
	 * Save a playlist
	 * @return an empty ok
	 */
	public static Result playlistSave(String id)
	{
		Logger.info("Saving playlist \"" + id + "\"");
		
		try
		{
			MPD mpd = MpdMonitor.getInstance().getMPD();
			MPDPlaylist playlist = mpd.getMPDPlaylist();
			
			playlist.savePlaylist(id);
			
			return ok("");
		}
		catch (MPDException e)
		{
			Logger.error("MPD error", e);

			flash("error", e.getMessage());
			return internalServerError(e.getMessage());
		}
	}

	/**
	 * Delete a given playlist
	 * @return an empty ok
	 */
	public static Result playlistDelete(String id)
	{
		try
		{
			MPD mpd = MpdMonitor.getInstance().getMPD();
			mpd.getMPDPlaylist().deletePlaylist(id);
			
			return ok("");
		}
		catch (MPDException e)
		{
			Logger.error("MPD error", e);

			flash("error", e.getMessage());
			return internalServerError(e.getMessage());
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
		List<String> playlistfiles = new ArrayList<>();
		
		try
		{
			MPD mpd = MpdMonitor.getInstance().getMPD();
			
			List<MPDSong> playlist = mpd.getMPDPlaylist().getSongList();
			for (MPDSong song : playlist)
			{
				playlistfiles.add(song.getFile());
			}
			
			songs = Database.getSongs(page, 10, sortBy, order, filter);
		}
		catch (MPDException e)
		{
			Logger.error("MPD error", e);

			flash("error", "Command failed! " + e.getMessage());
			songs = new EmptyPage<>();
		}
		
		return ok(database.render(songs, playlistfiles, sortBy, order, filter));
	}

	/**
	 * Performs POST /addUrl
	 * Display the 'Add from URL form'.
	 * @return an action result
	 */
	public static Result addUrl(String url)
	{
		try
		{
			Logger.info("Adding to playlist: " + url);
			
			MPD mpd = MpdMonitor.getInstance().getMPD();

			UrlParser parser = new UrlParser();
			List<MPDFile> files = parser.getAll(url);
			
			for (MPDFile file : files)
			{
				mpd.getMPDPlaylist().addFileOrDirectory(file);
			}
			
			return ok("Added " + files.size() + " files");
		}
		catch (Exception e)
		{
			Logger.error("MPD error", e);
			flash("error", e.getMessage());

			return internalServerError(e.getMessage());
		}
			
	}
	/**
	 * Performs POST /addDbEntry
	 * @return an action result
	 */
	public static Result addDbEntry(String path)
	{
		try
		{
			Logger.info("Adding db entry to playlist: " + path);
			
			MPD mpd = MpdMonitor.getInstance().getMPD();
			MPDSong song = new MPDSong();
			song.setFile(path);
			
			mpd.getMPDPlaylist().addSong(song);

			return ok(path);
		}
		catch (MPDException e)
		{
			Logger.error("MPD error", e);
			flash("error", "Command failed! " + e.getMessage());
			
			return notFound(path);
		}		
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
			Logger.error("MPD error", e);
			flash("error", "Command failed! " + e.getMessage());
		}
		
		return ok("");
	}

	/**
	 * Performs GET /toggleRepeat
	 * @return an action result
	 */
	public static Result toggleRepeat()
	{
		try
		{
			MPD mpd = MpdMonitor.getInstance().getMPD();
			MPDPlayer player = mpd.getMPDPlayer();
			player.setRepeat(!player.isRepeat());
			
			Logger.info("Setting repeat: " + player.isRepeat());
		}
		catch (MPDException e)
		{
			Logger.error("MPD error", e);
			flash("error", "Command failed! " + e.getMessage());
		}
		
		return ok("");
	}

	/**
	 * Performs GET /toggleRandome
	 * @return an action result
	 */
	public static Result toggleShuffle()
	{
		try
		{
			MPD mpd = MpdMonitor.getInstance().getMPD();
			MPDPlayer player = mpd.getMPDPlayer();
			player.setRandom(!player.isRandom());

			Logger.info("Setting shuffle: " + player.isRandom());
		}
		catch (MPDException e)
		{
			Logger.error("MPD error", e);
			flash("error", "Command failed! " + e.getMessage());
		}
		
		return ok("");
	}

	/**
	 * Performs GET /toggleConsuming
	 * @return an action result
	 */
	public static Result toggleConsuming()
	{
		try
		{
			MPD mpd = MpdMonitor.getInstance().getMPD();
			MPDPlayer player = mpd.getMPDPlayer();
			player.setConsuming(!player.isConsuming());

			Logger.info("Setting consuming: " + player.isConsuming());
		}
		catch (MPDException e)
		{
			Logger.error("MPD error", e);
			flash("error", "Command failed! " + e.getMessage());
		}
		
		return ok("");
	}
	
	/**
	 * Performs GET /toggleSingleMode
	 * @return an action result
	 */
	public static Result toggleSingleMode()
	{
		try
		{
			MPD mpd = MpdMonitor.getInstance().getMPD();
			MPDPlayer player = mpd.getMPDPlayer();
			player.setSingleMode(!player.isSingleMode());

			Logger.info("Setting single mode: " + player.isSingleMode());
		}
		catch (MPDException e)
		{
			Logger.error("MPD error", e);
			flash("error", "Command failed! " + e.getMessage());
		}
		
		return ok("");
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
			Logger.error("MPD error", e);
			flash("error", "Command failed! " + e.getMessage());
		}
		
		return ok("");
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
			Logger.error("MPD error", e);
			flash("error", "Command failed! " + e.getMessage());
		}
		
		return ok("");
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
			Logger.error("MPD error", e);
			flash("error", "Command failed! " + e.getMessage());
		}

		return ok("");
	}

	/**
	 * Performs POST /setsongpos
	 * @param pos the new song position in seconds
	 * @return an action result
	 */
	public static Result setSongPos(int pos)
	{
		Logger.info("Set song pos " + pos);
		
		try
		{
			MPD mpd = MpdMonitor.getInstance().getMPD();
			mpd.getMPDPlayer().seek(pos);
		}
		catch (MPDPlayerException | MPDConnectionException e)
		{
			Logger.error("MPD error", e);
			flash("error", "Changing song position failed! " + e.getMessage());
		}
		
		return ok("");
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
		catch (MPDException e)
		{
			Logger.error("MPD error", e);
			flash("error", "Changing volume failed! " + e.getMessage());
		}
		
		return ok("");
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
			Logger.error("MPD error", e);
			flash("error", "Changing song failed! " + e.getMessage());
		}
		
		return ok("");
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
			
			flash("success", "Database updated!");
		}
		catch (MPDException e)
		{
			Logger.error("MPD error", e);
			flash("error", "Updating database failed!" + e.getMessage());
		}

		return browseDb(0, "name", "asc", "");	// defaults - same as in routes files
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
			Logger.error("MPD error", e);
			flash("error", "Removing entry from playlist failed! " + e.getMessage());
		}
		
		return ok("");
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
			Logger.error("MPD error", e);
			flash("error", e.getMessage());
			return ok(main.render(null, null)); 
		}
	}
	
	/**
	 * Toggle MPD output 
	 * @param id the output id
	 * @return ok
	 */
	public static Result toggleOutput(int id, boolean check)
	{
		try
		{
			MPD mpd = MpdMonitor.getInstance().getMPD();
			MPDAdmin admin = mpd.getMPDAdmin();
			List<MPDOutput> outputs = (List<MPDOutput>)admin.getOutputs();
			
			if (id < 0 || id >= outputs.size())
				throw new MPDAdminException("Output ID invalid", new IllegalArgumentException());
			
			MPDOutput output = outputs.get(id);
			
			if (check)
				admin.enableOutput(output); else
				admin.disableOutput(output);
		}
		catch (MPDException e)
		{
			Logger.error("MPD error", e);
			flash("error", e.getMessage());
			return notFound(e.getMessage());
		}

		return ok(""); 
	}
}
