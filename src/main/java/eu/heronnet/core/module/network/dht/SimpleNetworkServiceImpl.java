package eu.heronnet.core.module.network.dht;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.UUID;

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
public class SimpleNetworkServiceImpl extends Service {

    private final Logger logger = LoggerFactory.getLogger(SimpleNetworkServiceImpl.class);

    private ServerSocket serverSocket = null;

    @Override
    public void run() {
//        kbr = injector.getInstance(KeybasedRouting.class);
//        try {
//            kbr.create();
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//        }
//
//        storage = injector.getInstance(AgeLimitedDHTStorage.class).create();
//
//        dht = injector.getInstance(DHT.class)
//                .setName("schema")
//                .setStorage(storage)
//                .create();

        try {
            final ServerSocketChannel channel = ServerSocketChannel.open();
            channel.bind(new InetSocketAddress(5555));

            final Selector selector = Selector.open();


            final SocketChannel socketChannel = channel.accept();
            socketChannel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_CONNECT | SelectionKey.OP_READ);


            ServerSocketChannel ssChannel1 = ServerSocketChannel.open();
            ssChannel1.configureBlocking(false);
            ssChannel1.socket().bind(new InetSocketAddress(80));

            ServerSocketChannel ssChannel2 = ServerSocketChannel.open();
            ssChannel2.configureBlocking(false);
            ssChannel2.socket().bind(new InetSocketAddress(81));

            ssChannel1.register(selector, SelectionKey.OP_ACCEPT);
            ssChannel2.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();
                Iterator it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey selKey = (SelectionKey) it.next();
                    it.remove();

                    if (selKey.isAcceptable()) {
                        ServerSocketChannel ssChannel = (ServerSocketChannel) selKey.channel();
                        SocketChannel sc = ssChannel.accept();
                        ByteBuffer bb = ByteBuffer.allocate(100);
                        sc.read(bb);


                    }catch(IOException e){
                        logger.error(e.getMessage());
                    }
                }

                @Override
                public void put ( final Serializable data, final UUID uuid){
                    throw new RuntimeException("PUT not implemented");
                }

                @Override
                public List<Serializable> get ( final String UUID){
                    throw new RuntimeException("GET not implemented");
                }
            }
