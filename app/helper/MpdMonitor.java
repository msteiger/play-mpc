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
import org.bff.javampd.exception.MPDException;
import org.bff.javampd.monitor.MPDStandAloneMonitor;

import play.Configuration;
import play.Logger;
import play.Play;

/**
 * A singleton that represents the connection to MPD
 * @author Martin Steiger
 */
public class MpdMonitor
{
	private static MpdMonitor instance = null;
	private MPDStandAloneMonitor monitor;
	private Thread thread;
	private MPD mpd;
	
	/**
	 * @return returns the current instance or creates it if necessary
	 * @throws MPDConnectionException if connection to MPD fails
	 */
	public static MpdMonitor getInstance() throws MPDConnectionException
	{
		// TODO: implement circuit breaker pattern
		
		if (instance == null)
		{
			try
			{
				instance = new MpdMonitor();
			}
			catch (UnknownHostException e)
			{
				throw new MPDConnectionException(e);
			}
		}
		
		return instance;
	}
	
	private MpdMonitor() throws UnknownHostException, MPDConnectionException
	{
		Configuration config = Play.application().configuration();

		String hostname = config.getString("mpd.hostname");
		int port = config.getInt("mpd.port");
		String password = config.getString("mpd.password");
		int timeout = config.getInt("mpd.timeout", 10) * 1000;
		
		Logger.info("Connecting to MPD");
		
		mpd = new MPD(hostname, port, password, timeout);
		monitor = new MPDStandAloneMonitor(mpd, 1000);
		
		thread = new Thread(monitor);
		thread.start();
	}

	/**
	 * @return the MPD instance
	 */
	public MPD getMPD()
	{
		return mpd;
	}
	
	/**
	 * @return the MPD monitor instance
	 */
	public MPDStandAloneMonitor getMonitor()
	{
		return monitor;
	}

	/**
	 * Stops the monitoring and waits for the query thread to terminate
	 */
	public void stop()
	{
		monitor.stop();
		
		try
		{
			thread.join();
			mpd.close();
		}
		catch (InterruptedException e)
		{
			Logger.warn("Monitor thread has not terminated");
		}
		catch (MPDException e)
		{
			Logger.warn("Error closing connection");
		}
	}
}
