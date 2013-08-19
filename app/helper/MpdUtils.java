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

import java.net.UnknownHostException;

import org.bff.javampd.MPD;
import org.bff.javampd.exception.MPDConnectionException;

import play.Configuration;
import play.Play;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class MpdUtils
{
	public static MPD createInstance() throws UnknownHostException, MPDConnectionException
	{
		MPD mpd = null;
		Configuration config = Play.application().configuration();
		String hostname = config.getString("mpd.hostname");
		int port = config.getInt("mpd.port");

		String password = null;
		
		mpd = new MPD(hostname, port, password, 0);
		
		return mpd;
	}
}
