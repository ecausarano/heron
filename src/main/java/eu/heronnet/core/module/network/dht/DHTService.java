package eu.heronnet.core.module.network.dht;

import com.google.common.util.concurrent.AbstractIdleService;
import eu.heronnet.core.model.BinaryItem;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.UUID;

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
public abstract class DHTService extends AbstractIdleService {


    public abstract UUID persist(BinaryItem data);

    public abstract BinaryItem findByID(UUID id);

    public abstract void deleteByID(UUID id);

    public abstract void ping();
}
