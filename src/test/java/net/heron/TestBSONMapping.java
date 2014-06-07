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

package net.heron;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import de.undercouch.bson4jackson.BsonFactory;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TestBSONMapping {


    @Test
    public void testBSON() throws IOException {
        Wrapper wrapper = new Wrapper();
        wrapper.put("key", "value");

        final ObjectMapper mapper = new ObjectMapper(new BsonFactory());
        final ByteOutputStream outputStream = new ByteOutputStream();
        mapper.writeValue(outputStream, wrapper);
        final byte[] outputStreamBytes = outputStream.getBytes();

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStreamBytes);
        final Wrapper readValue = mapper.readValue(inputStream, Wrapper.class);
    }

}
