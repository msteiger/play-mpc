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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.bff.javampd.MPDFile;

/**
 * Parses a given URL and returns 
 * a list of referenced mpd files 
 * @author Martin Steiger
 */
public class UrlParser
{
	/**
	 * @param url the url
	 * @return a list of referenced mpd files
	 * @throws IOException if the url or its contents cannot be read
	 * @throws IllegalArgumentException if the url or its contents are invalid
	 */
	public List<MPDFile> getAll(String url) throws IOException
	{
		List<MPDFile> list = new ArrayList<>();

		int dp = url.lastIndexOf('.');
		
		if (dp == -1)
			throw new IllegalArgumentException("URL does not have a valid file ending");
		
		String ext = url.substring(dp + 1);
		ext = ext.trim().toLowerCase();
		
		switch (ext)
		{
		case ".m3u":
			parsePlaylistM3u(url);
			break;
		
		case ".mp3":
			list.add(createSingle(url));
			break;
			
			// TODO: check other file types or test with "ffmpeg -i <url>"
		}
		
		return list;
	}

	private MPDFile createSingle(String url)
	{
		String name = "";
		int from = url.lastIndexOf('/');
		
		if (from != -1)
			name = url.substring(from);
		
		MPDFile file = new MPDFile();
		file.setDirectory(false);
		file.setPath(url);
		file.setName(name);
		
		return file;
	}

	private void parsePlaylistM3u(String url) throws IOException
	{
		URL website = new URL(url);
		URLConnection conn = website.openConnection();
		
		int size = conn.getContentLength();
		
		if (size > 256 * 1024)
			throw new IllegalArgumentException("File suspiciously big");

		try (InputStream is = conn.getInputStream())
		{
			InputStreamReader read = new InputStreamReader(is, Charset.defaultCharset());
			BufferedReader reader = new BufferedReader(read);
			
			String line;
			while ((line = reader.readLine()) != null)
			{
				int comIdx = line.indexOf('#');
				if (comIdx >= 0)
					line = line.substring(0, comIdx);
				line = line.trim();
				
				if (!line.isEmpty())
					getAll(line);
			}
		}
	}

}
