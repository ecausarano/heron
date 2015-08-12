package eu.heronnet.module.kad.net.codec;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;

import org.testng.Assert;
import org.testng.annotations.Test;

import eu.heronnet.model.Bundle;
import eu.heronnet.model.IdentifierNode;
import eu.heronnet.model.Statement;
import eu.heronnet.model.builder.StringNodeBuilder;
import eu.heronnet.module.kad.model.Node;
import eu.heronnet.module.kad.model.rpc.message.FindValueResponse;
import eu.heronnet.module.kad.model.rpc.message.KadMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author edoardocausarano
 */
public class KadMessageCodecTest {
    @Test
    public void testBasic() throws Exception {
        KadMessage msg = new KadMessage();
        msg.setOrigin(new Node(new byte[] {0}, Collections.singletonList(new InetSocketAddress(0))));
        check(msg);
    }

    @Test
    public void testFindValueResponse() throws Exception {
        FindValueResponse findValueResponse = new FindValueResponse();
        ArrayList<Bundle> bundles = new ArrayList<>();
        Bundle bundle = new Bundle(new IdentifierNode(new byte[]{0}));
        bundle.add(new Statement(
                StringNodeBuilder.withString("predicate"),
                StringNodeBuilder.withString("object")));
        bundles.add(bundle);

        findValueResponse.setBundles(bundles);
        findValueResponse.setOrigin(new Node(new byte[]{0}, Collections.singletonList(new InetSocketAddress(0))));
        check(findValueResponse);
    }

    private void check(KadMessage message) throws Exception {
        KadMessageCodec kadMessageCodec = new KadMessageCodec();
        ArrayList<Object> objects = new ArrayList<>();
        ByteBuf buffer = Unpooled.buffer();

        kadMessageCodec.encode(null, message, buffer);
        kadMessageCodec.decode(null, buffer, objects);

        Assert.assertEquals(1, objects.size());
    }
}
