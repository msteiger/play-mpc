/*
 * Copyright (C) 2012-2013 Martin Steiger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package helper;

import java.util.List;

import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;

/**
 * The "default" implementation of Page<T>. 
 * It redirects all calls to {@link PagingList}
 * @author Martin Steiger
 */
public class DefaultPage<T> implements Page<T>
{
	private PagingList<T> pagingList;
	private int index;
	
	DefaultPage(PagingList<T> pagingList, int index)
	{
		this.pagingList = pagingList;
		this.index = index;
	}

	@Override
	public Page<T> prev()
	{
		return pagingList.getPage(index - 1);
	}

	@Override
	public Page<T> next()
	{
		return pagingList.getPage(index + 1);
	}

	@Override
	public boolean hasPrev()
	{
		return index > 0;
	}

	@Override
	public boolean hasNext()
	{
		return index < pagingList.getTotalPageCount() - 1;
	}

	@Override
	public int getTotalRowCount()
	{
		return pagingList.getTotalRowCount();
	}

	@Override
	public int getTotalPageCount()
	{
		return pagingList.getTotalPageCount();
	}

	@Override
	public int getPageIndex()
	{
		return index;
	}

	@Override
	public String getDisplayXtoYofZ(String to, String of)
	{
		int first = index * pagingList.getPageSize() + 1;
		int last = Math.min(getTotalRowCount(), (index + 1) * pagingList.getPageSize());

		int total = getTotalRowCount();
		
		return first+to+last+of+total;
	}

	@Override
	public List<T> getList()
	{
		int from = index * pagingList.getPageSize();
		int to = Math.min(getTotalRowCount(), (index + 1) * pagingList.getPageSize());
		
		return pagingList.getAsList().subList(from, to);
	}
}

