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

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

import play.Configuration;
import play.Play;

/**
 * Digests string data in the form<br/>
 * result = Base64(SHA(str + application.secret))
 * @author Martin Steiger
 */
public class Digester
{
	/**
	 * @param str the input string
	 * @return the digested string
	 * @throws NoSuchAlgorithmException encryption algorithm not installed 
	 */
	public static String digest(String str) throws NoSuchAlgorithmException
	{
		Configuration config = Play.application().configuration();

		String salt = config.getString("application.secret");
		
        String saltedStr = str + salt;
		
		MessageDigest md = MessageDigest.getInstance("SHA");
		md.update(saltedStr.getBytes());
        
        byte byteData[] = md.digest();
        
        byte[] base64 = Base64.encodeBase64(byteData);
        
        String result = new String(base64, Charset.defaultCharset());

        return result;
	}
}
