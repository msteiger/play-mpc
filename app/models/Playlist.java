
package models;

import helper.DefaultPagingList;
import helper.MpdMonitor;

import java.util.List;

import org.bff.javampd.MPD;
import org.bff.javampd.MPDPlaylist;
import org.bff.javampd.exception.MPDException;
import org.bff.javampd.objects.MPDSong;

import com.avaje.ebean.Page;

/**
 * MPD Playlist
 */
public class Playlist
{
	/**
	 * Returns a page of the current playlist
	 * @param page Page to display
	 * @param pageSize Number of computers per page
	 * @return a page with all relevant entries
	 * @throws MPDException if MPD reports an error
	 */
	public static Page<MPDSong> getSongs(int page, final int pageSize) throws MPDException
	{
		MPD mpd = MpdMonitor.getInstance().getMPD();
		MPDPlaylist playlist = mpd.getMPDPlaylist();

		List<MPDSong> songs = playlist.getSongList();
		
		DefaultPagingList<MPDSong> pagingList = new DefaultPagingList<>(songs, pageSize);

		return pagingList.getPage(page);

	}
}
