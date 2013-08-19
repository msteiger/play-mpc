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

import com.avaje.ebean.Page;

/**
 * An empty implementation of Page<T>. 
 * @author Martin Steiger
 */
public final class EmptyPage<T> implements Page<T>
{
	@Override
	public Page<T> prev()
	{
		// maybe null, but in doubt avoid NPE
		return this;
	}

	@Override
	public Page<T> next()
	{
		// maybe null, but in doubt avoid NPE
		return this;
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
		return 0;
	}

	@Override
	public int getTotalPageCount()
	{
		// not sure whether 0 is a valid value, could be 1 also
		return 0;
	}

	@Override
	public int getPageIndex()
	{
		return 0;
	}

	@Override
	public String getDisplayXtoYofZ(String to, String of)
	{	
		return "- " + to + " - " + of + " -";
	}

	@Override
	public List<T> getList()
	{
		return Collections.emptyList();
	}
}

