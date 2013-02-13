package eu.heronnet.core;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: ecausarano
 * Date: 2/10/13
 * Time: 5:34 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Heron {

    public void store(Serializable data, UUID uuid);

    public void index(UUID target, String tag, String value);

    public UUID search(String tag, String value);

    public Serializable fetch(UUID uuid);


}
