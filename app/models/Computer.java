package models;

import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import com.avaje.ebean.*;
import com.google.common.collect.ImmutableList;

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
    	System.out.println("PAGE");
    
    	return new Page<Computer>()
		{
			@Override
			public Page<Computer> prev()
			{
				return null;
			}
			
			@Override
			public Page<Computer> next()
			{
				return null;
			}
			
			@Override
			public boolean hasPrev()
			{
				return false;
			}
			
			@Override
			public boolean hasNext()
			{
				return false;
			}
			
			@Override
			public int getTotalRowCount()
			{
				return pageSize;
			}
			
			@Override
			public int getTotalPageCount()
			{
				return 1;
			}
			
			@Override
			public int getPageIndex()
			{
				return 0;
			}
			
			@Override
			public List<Computer> getList()
			{
				Computer c = new Computer();
				c.id = 12l;
				c.name = "Computer";
				
				return ImmutableList.of(c);
			}
			
			@Override
			public String getDisplayXtoYofZ(String to, String of)
			{
				return "0 to 10 of 10";
			}
		};
//    	return ImmutableList.of()
//        return 
//            find.where()
//                .ilike("name", "%" + filter + "%")
//                .orderBy(sortBy + " " + order)
//                .fetch("company")
//                .findPagingList(pageSize)
//                .getPage(page);
    
    }
    
}

