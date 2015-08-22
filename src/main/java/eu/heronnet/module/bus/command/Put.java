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

package eu.heronnet.module.bus.command;

import eu.heronnet.model.Bundle;
import eu.heronnet.model.Statement;
import eu.heronnet.model.builder.BundleBuilder;

import java.nio.file.Path;

/**
 * Command to persist a {@link Bundle} of {@link Statement statements} to storage
 *
 */
public class Put {

    private final BundleBuilder builder;
    private final Path path;

    public Put(BundleBuilder builder, Path path) {
        this.builder = builder;
        this.path = path;
    }

    public Put(BundleBuilder builder) {
        this.builder = builder;
        this.path = null;

    }

    public BundleBuilder getBuilder() {
        return builder;
    }

    public Path getPath() {
        return path;
    }
}
