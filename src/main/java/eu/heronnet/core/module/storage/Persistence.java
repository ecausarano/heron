/*
 * Copyright (C) 2014 edoardocausarano
 *
 * heron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * heron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with heron. If not, see http://www.gnu.org/licenses
 */

package eu.heronnet.core.module.storage;

import com.google.common.util.concurrent.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Persistence extends Service {

    List<Map<String, byte[]>> getAll() throws IOException;

    Map<String, byte[]> getById(byte[] id) throws IOException;

    void put(Map<String, byte[]> item) throws IOException;

    void delete(byte[] id) throws IOException;

    Map<String, String> getMetadataForID(byte[] id);

    List<Map<String, String>> getAllMetadata() throws IOException;

    long count();
}
