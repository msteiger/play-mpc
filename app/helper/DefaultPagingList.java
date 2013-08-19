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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import play.Logger;

import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class DefaultPagingList<T> implements PagingList<T>
{
	private List<T> list;
	private int pageSize;

	public DefaultPagingList(List<T> list, int pageSize)
	{
		this.list = list;
		this.pageSize = pageSize;
	}
	
	@Override
	public List<T> getAsList()
	{
		return Collections.unmodifiableList(list);
	}

	@Override
	public Future<Integer> getFutureRowCount()
	{
		return new ConstantFuture<Integer>(list.size());
	}

	@Override
	public Page<T> getPage(int index)
	{
		return new DefaultPage<T>(this, index);
	}

	@Override
	public int getPageSize()
	{
		return pageSize;
	}

	@Override
	public int getTotalPageCount()
	{
		int rowCount = getTotalRowCount();
		if (rowCount == 0)
			return 0; else
			return ((rowCount-1) / getPageSize()) + 1;
	}

	@Override
	public int getTotalRowCount()
	{
		try
		{
			return getFutureRowCount().get();
		}
		catch (InterruptedException | ExecutionException e)
		{
			Logger.warn("Error getting row count", e);
			return 0;
		}
	}

	@Override
	public void refresh()
	{
		// ignore
	}

	@Override
	public PagingList<T> setFetchAhead(boolean fetchAhead)
	{
		return this;
	}

}
