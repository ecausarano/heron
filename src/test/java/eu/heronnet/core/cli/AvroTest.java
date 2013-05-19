package eu.heronnet.core.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.util.Utf8;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This file is part of heron Copyright (C) 2013-2013 edoardocausarano
 * <p/>
 * heron is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * <p/>
 * heron is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with Foobar.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
public class AvroTest {

    Logger logger = LoggerFactory.getLogger(AvroTest.class);

    @Test
    public void testAvro() throws IOException, URISyntaxException {
        URL path = getClass().getClassLoader().getResource("user.avsc");
        assert path != null;

        FileInputStream stream = new FileInputStream(new File(path.toURI()));
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                /* Instead of using default, pass in a decoder. */
            String schemaDescription = Charset.defaultCharset().decode(bb).toString();
            logger.debug(schemaDescription);

            Schema s = Schema.parse(schemaDescription);
            FileOutputStream outputStream = new FileOutputStream(File.createTempFile("test-", ".hro"));
            Encoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
            GenericDatumWriter writer = new GenericDatumWriter(s);

            // Populate data
            GenericRecord r = new GenericData.Record(s);
            r.put("name", new Utf8("Doctor Who"));
            r.put("num_likes", 1);
            r.put("num_groups", 423);
            r.put("num_photos", 0);

            // Encode
            writer.write(r, encoder);
            encoder.flush();
        } finally {
            stream.close();
        }
    }
}
