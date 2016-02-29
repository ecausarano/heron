package eu.heronnet.module.kad.net;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import eu.heronnet.module.kad.model.Node;

/**
 * @author edoardocausarano
 */
public class SelfNodeProvider {

    private final byte[] id;

    public SelfNodeProvider(byte[] id) {
        this.id = id;
    }

    public Node getSelf() throws SocketException {
    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        List<byte[]> nodeAddresses = new ArrayList<>();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            // don't bother with interaces that are down
            if (!networkInterface.isUp()) continue;
            // don' bother with loopback
            if (networkInterface.isLoopback() || networkInterface.isPointToPoint()) continue;

            List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
            // we expect these to be all sane unicast network addresses
            interfaceAddresses.forEach(interfaceAddress -> {
                nodeAddresses.add(interfaceAddress.getAddress().getAddress());
            });
        }
        return new Node(id, nodeAddresses);
    }
}
