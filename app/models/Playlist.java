package models;

import helper.DefaultPagingList;
import helper.EmptyPage;
import helper.MpdUtils;

import java.net.UnknownHostException;
import java.util.List;

import org.bff.javampd.MPD;
import org.bff.javampd.MPDPlaylist;
import org.bff.javampd.exception.MPDException;
import org.bff.javampd.objects.MPDSong;

import play.Logger;

import com.avaje.ebean.Page;

/**
 * MPD Playlist
 */
public class Playlist 
{  
    /**
     * Return a page of computer
     *
     * @param page Page to display
     * @param pageSize Number of computers per page
     * @param sortBy Computer property used for sorting
     * @param order Sort order (either or asc or desc)
     * @param filter Filter applied on the name column
     */
    public static Page<MPDSong> getSongs(int page, final int pageSize, String sortBy, String order, String filter) 
    {
    	MPD mpd;
		try
		{
			mpd = MpdUtils.createInstance();

			MPDPlaylist playlist = mpd.getMPDPlaylist();
			
			List<MPDSong> songs = playlist.getSongList();
			
			DefaultPagingList<MPDSong> pagingList = new DefaultPagingList<>(songs, pageSize);
			
			// TODO: include order, sorting and filter
			
			return pagingList.getPage(page);
		}
		catch (UnknownHostException | MPDException e)
		{			
			Logger.warn("Error", e);
			
			return new EmptyPage<MPDSong>();
		}
    }
}

