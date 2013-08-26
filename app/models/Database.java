
package models;

import helper.DefaultPagingList;
import helper.MpdMonitor;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bff.javampd.MPD;
import org.bff.javampd.MPDDatabase;
import org.bff.javampd.exception.MPDException;
import org.bff.javampd.objects.MPDItem;
import org.bff.javampd.objects.MPDSong;

import com.avaje.ebean.Page;

/**
 * MPD Database page
 */
public class Database
{
	/**
	 * Returns a page of the current playlist
	 * @param page Page to display
	 * @param pageSize Number of computers per page
	 * @param sortBy Computer property used for sorting
	 * @param order Sort order (either or asc or desc)
	 * @param filter Filter applied on the name column
	 * @return the page with all relevant entries
	 * @throws MPDException if MPD reports an error
	 */
	public static Page<MPDSong> getSongs(int page, final int pageSize, final String sortBy, final String order, String filter) throws MPDException
	{
		MPD mpd = MpdMonitor.getInstance().getMPD();

		MPDDatabase database = mpd.getMPDDatabase();
		Collection<MPDSong> hits;

		if (filter == null || filter.isEmpty())
			hits = database.listAllSongs(); else
			hits = database.searchAny(filter);

		// HACK: unfortunately casting is necessary here
		List<MPDSong> songs = (List<MPDSong>) hits;

		DefaultPagingList<MPDSong> pagingList = new DefaultPagingList<>(songs, pageSize);

		Collections.sort(songs, new Comparator<MPDSong>()
		{
			private int cmp(String c1, String c2)
			{
				if (c1 == null)
				{
					if (c2 == null)
						return 0;
					
					return -1;
				}
				
				if (c2 == null)
					return 1;

				return c1.compareToIgnoreCase(c2);
			}
			
			private int cmp(MPDItem c1, MPDItem c2)
			{
				if (c1 == null)
				{
					if (c2 == null)
						return 0;
					
					return -1;
				}
				
				if (c2 == null)
					return 1;
				
				return cmp(c1.getName(), c2.getName());
			}
			
			@Override
			public int compare(MPDSong o1, MPDSong o2)
			{
				int result = 0;
				
				switch (sortBy.toLowerCase())
				{
					case "title":
						result = cmp(o1.getTitle(), o2.getTitle());
						break;
						
					case "artist":
						result = cmp(o1.getArtist(), o2.getArtist());
						break;
						
					case "album":
						result = cmp(o1.getAlbum(), o2.getAlbum());
						break;
						
					case "file":
						result = cmp(o1.getFile(), o2.getFile());
						break;
				}
				
				if ("desc".equals(order))
					result = -result;
				
				return result;
			}
		});

		return pagingList.getPage(page);
	}
}
