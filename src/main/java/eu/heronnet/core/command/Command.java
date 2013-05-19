package eu.heronnet.core.command;

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

/**
 * UI modules translate user input into dispatchable implementation of this interface
 */
public interface Command {

    /**
     * Every command is uniquely identified by this key.
     *
     * @return String
     */
    public String getKey();

    /**
     * This is the "callback" body that an executor calls to process the user request
     */
    void execute();

    /**
     * The arguments - if any - that parametrize the user command request
     *
     * @param varargs String
     */
    @Deprecated
    void setArgs(String... varargs);

    /**
     *
     * The arguments - use this!
     */
    // void setArgs(Map<String, String> args);
    /**
     *
     * A list with the arguments that this command understands
     *
     * @return List
     */
    // List<String> getArgumentKeys();

    /**
     *
     * Useful attributes for the command (eg, required, optional, the default value, etc...)
     *
     * @param argument
     * @return
     */
    // List<String> getArgumentMetadata(String argument);
}
