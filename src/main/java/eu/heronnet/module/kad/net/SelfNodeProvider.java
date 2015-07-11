package eu.heronnet.module.kad.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import eu.heronnet.module.kad.model.Node;

/**
 * @author edoardocausarano
 */
public class SelfNodeProvider {

    @Autowired
    IdGenerator idGenerator;

    public Node getSelf() throws SocketException {
    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        List<InetSocketAddress> nodeAddresses = new ArrayList<>();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            // don't bother with interaces that are down
            if (!networkInterface.isUp()) continue;
            // don' bother with loopback
            if (networkInterface.isLoopback() || networkInterface.isPointToPoint()) continue;

            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            // we expect these to be all sane unicast network addresses
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                nodeAddresses.add(InetSocketAddress.createUnresolved(address.toString(), 6565));
            }
        }
        return new Node(idGenerator.getId(), nodeAddresses);

    }
}
