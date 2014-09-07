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

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryKeyCreator;
import de.undercouch.bson4jackson.BsonFactory;
import de.undercouch.bson4jackson.BsonGenerator;
import de.undercouch.bson4jackson.BsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.fasterxml.jackson.core.JsonToken.END_OBJECT;

public class FilenameIndexKeyCreator implements SecondaryKeyCreator {

    private static final Logger logger = LoggerFactory.getLogger(FilenameIndexKeyCreator.class);

    private final BsonFactory factory = new BsonFactory();

    {
        factory.enable(BsonGenerator.Feature.ENABLE_STREAMING);
    }

    @Override
    public boolean createSecondaryKey(SecondaryDatabase secondary, DatabaseEntry key, DatabaseEntry data, DatabaseEntry result) {
        try {
            final BsonParser parser = factory.createParser(data.getData());
            while (parser.nextToken() != END_OBJECT) {
                String fieldName = parser.getCurrentName();
                if ("filename".equals(fieldName)) {
                    parser.nextToken();
                    result.setData(parser.getBinaryValue());
                }
            }
            parser.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return true;
    }
}
