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

package eu.heronnet.core.application.bus;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.inject.Provider;

import java.util.concurrent.Executors;

public class EventBusProvider implements Provider<EventBus> {

    private final String MAIN_BUS = "MAIN_BUS";
    private int N_THREADS = 5;

    EventBus eventBus = new AsyncEventBus(MAIN_BUS, Executors.newFixedThreadPool(N_THREADS));

    @Override
    public EventBus get() {
        return eventBus;
    }
}
