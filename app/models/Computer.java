package models;

import helper.DefaultPagingList;
import helper.MpdUtils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.bff.javampd.MPD;
import org.bff.javampd.MPDPlaylist;
import org.bff.javampd.exception.MPDException;
import org.bff.javampd.objects.MPDSong;

import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import com.avaje.ebean.Page;

/**
 * Computer entity managed by Ebean
 */
@Entity 
public class Computer extends Model 
{
	private static final long serialVersionUID = 1288011138574047294L;

	@Id
    public Long id;
    
    @Constraints.Required
    public String name;
    
    @Formats.DateTime(pattern="yyyy-MM-dd")
    public Date introduced;
    
    @Formats.DateTime(pattern="yyyy-MM-dd")
    public Date discontinued;
    
    @ManyToOne
    public Company company;
    
    /**
     * Generic query helper for entity Computer with id Long
     */
    public static Finder<Long,Computer> find = new Finder<Long,Computer>(Long.class, Computer.class); 
    
    /**
     * Return a page of computer
     *
     * @param page Page to display
     * @param pageSize Number of computers per page
     * @param sortBy Computer property used for sorting
     * @param order Sort order (either or asc or desc)
     * @param filter Filter applied on the name column
     */
    public static Page<Computer> page(int page, final int pageSize, String sortBy, String order, String filter) 
    {
    	MPD mpd;
		try
		{
			mpd = MpdUtils.createInstance();

			MPDPlaylist playlist = mpd.getMPDPlaylist();
			
			List<MPDSong> songs = playlist.getSongList();
			
			List<Computer> compList = new ArrayList<>();
			
			long id = 0;
			for (MPDSong song : songs)
			{
				Computer c = new Computer();
				
				c.id = id++;
				c.name = song.getTitle();
				
				compList.add(c);
			}
			
			DefaultPagingList<Computer> pagingList = new DefaultPagingList<>(compList, pageSize);
			
			return pagingList.getPage(page);
		}
		catch (UnknownHostException | MPDException e)
		{			
			Logger.warn("Error", e);
			
			return null;	// TODO: fix
		}

//        return 
//            find.where()
//                .ilike("name", "%" + filter + "%")
//                .orderBy(sortBy + " " + order)
//                .fetch("company")
//                .findPagingList(pageSize)
//                .getPage(page);
    
    }
    
}

