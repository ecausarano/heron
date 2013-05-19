package eu.heronnet.core.module.network.dht;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.google.common.util.concurrent.AbstractExecutionThreadService;

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
public abstract class Service extends AbstractExecutionThreadService {

    protected abstract void run();

    public abstract void put(Serializable data, UUID uuid);

    public abstract List<Serializable> get(String UUID);
}
