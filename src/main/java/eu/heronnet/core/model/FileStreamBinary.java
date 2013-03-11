package eu.heronnet.core.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.UUID;

/**
 * This file is part of heron
 * Copyright (C) 2013-2013 edoardocausarano
 * <p/>
 * heron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * heron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
public class FileStreamBinary implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(FileStreamBinary.class);

    private String path;
    private UUID uuid;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void writeObject(ObjectOutputStream outputStream) throws IOException {
        outputStream.defaultWriteObject();
        outputStream.writeChars(uuid.toString());

        byte[] buffer = new byte[1024 * 32];
        BufferedInputStream bufferedInputStream = new BufferedInputStream(
                new FileInputStream(path));
        while (bufferedInputStream.read(buffer) != -1)
            outputStream.write(buffer);

    }

    public void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {

        if (path == null) {
            throw new IOException("destination path not specified");
        }

        inputStream.defaultReadObject();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                new FileOutputStream(path)
        );

        byte[] buffer = new byte[1024 * 32];
        while (inputStream.read(buffer) != -1)
            bufferedOutputStream.write(buffer);
        logger.debug("Wrote temp file: {}", path);
    }
}
