/*----------------------------------------------------------------------------*
 * This file is part of JBlake2.                                              *
 * Copyright (C) Osman Koçak <kocakosm@gmail.com>                             *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify it    *
 * under the terms of the GNU Lesser General Public License as published by   *
 * the Free Software Foundation, either version 3 of the License, or (at your *
 * option) any later version.                                                 *
 * This program is distributed in the hope that it will be useful, but        *
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY *
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public     *
 * License for more details.                                                  *
 * You should have received a copy of the GNU Lesser General Public License   *
 * along with this program. If not, see <http://www.gnu.org/licenses/>.       *
 *----------------------------------------------------------------------------*/

package org.kocakosm.jblake2;

import org.kocakosm.pitaya.charset.Charsets;
import org.kocakosm.pitaya.util.BaseEncoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

/**
 * Utility class to read BLAKE2 test vectors.
 *
 * @author Osman Koçak
 */
final class TestVectors
{
	private static final Gson GSON;
	static {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(byte[].class, new Base16Deserializer());
		GSON = gson.create();
	}

	static List<TestVector> read(URL url)
	{
		try (InputStream in = url.openStream()) {
			Reader reader = new InputStreamReader(in, Charsets.UTF_8);
			Type type = new TypeToken<List<TestVector>>(){}.getType();
			return Collections.unmodifiableList(GSON.fromJson(reader, type));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static final class Base16Deserializer implements JsonDeserializer<byte[]>
	{
		@Override
		public byte[] deserialize(JsonElement json, Type type, JsonDeserializationContext ctx)
		{
			return BaseEncoding.BASE_16.decode(json.getAsString());
		}
	}

	private TestVectors()
	{
		// See "Effective Java" (Item 4)
		throw new AssertionError("Not meant to be instantiated");
	}
}