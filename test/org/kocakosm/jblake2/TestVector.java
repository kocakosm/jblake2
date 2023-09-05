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

import static org.kocakosm.pitaya.util.XArrays.copyOf;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a BLAKE2 test vector.
 *
 * @author Osman Koçak
 */
final class TestVector
{
	@SerializedName("key")
	private final byte[] key;

	@SerializedName("in")
	private final byte[] input;

	@SerializedName("out")
	private final byte[] output;

	TestVector(byte[] key, byte[] input, byte[] output)
	{
		this.key = copyOf(key);
		this.input = copyOf(input);
		this.output = copyOf(output);
	}

	byte[] getKey()
	{
		return copyOf(key);
	}

	byte[] getInput()
	{
		return copyOf(input);
	}

	byte[] getOutput()
	{
		return copyOf(output);
	}
}