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

package eu.heronnet.kad.model;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RadixTreeImpl implements RadixTree {

    private static final Logger logger = LoggerFactory.getLogger(RadixTreeImpl.class);

    InternalNode root = new InternalNode();

    @Inject
    @Self
    Node self;


    private static Node ZERO_NODE = new Node();
    private static final int BUCKET_SIZE = 32;

    static {
        ZERO_NODE.setId(new byte[20]);
    }

    public void setSelf(Node self) {
        ZERO_NODE.setId(self.getId());
    }

    @Override
    public void insert(Node node) {
        root.insert(node);
    }

    @Override
    public List<Node> find(byte[] key) {
        return root.find(key);
    }

    @Override
    public void delete(byte[] key) {
        root.delete(key);
    }

    private static class InternalNode {
        Node midpoint = ZERO_NODE;
        List<Node> bucket;
        InternalNode left;
        InternalNode right;

        public Node getMidpoint() {
            return midpoint;
        }

        public void setMidpoint(Node midpoint) {
            this.midpoint = midpoint;
        }

        void insert(Node node) {
            if (bucket == null && left == null && right == null) {
                logger.debug("pristine node, created new list");
                bucket = new ArrayList<>();
            }
            // means we have not yet split the bucket
            if (midpoint == ZERO_NODE) {
                // means bucket is not full yet
                if (bucket.size() < BUCKET_SIZE) {
                    bucket.add(node);
                    logger.debug("added node to bucket {}, current size {}", bucket, bucket.size());
                } else {
                    logger.debug("split bucket {}", bucket);
                    // time to split the bucket
                    Collections.sort(bucket);

                    left = new InternalNode();
                    left.bucket = new ArrayList<>(bucket.subList(0, BUCKET_SIZE / 2));

                    midpoint = bucket.get(BUCKET_SIZE / 2);

                    right = new InternalNode();
                    right.bucket = new ArrayList<>(bucket.subList(BUCKET_SIZE / 2, BUCKET_SIZE));

                    bucket = null;
                    // and reattempt insertion
                    this.insert(node);
                }
            } else {
                // means there are a left and a right (possibly empty) nodes
                if (node.compareTo(midpoint) == -1) {
                    left.insert(node);
                } else {
                    right.insert(node);
                }
            }
        }

        List<Node> find(byte[] key) {
            final Node dummy = new Node();
            dummy.setId(key);

            // it's a leaf node
            if (midpoint == ZERO_NODE) {
                if (bucket != null) {
                    for (Node node : bucket) {
                        if (dummy.compareTo(node) == 0) {
                            logger.debug("found node for key: {}", new String(key));
                            return Collections.singletonList(node);
                        }
                    }
                    logger.debug("couldn't find desired key {}, returning bucket", new String(key));
                    return ImmutableList.copyOf(bucket);
                } else {
                    return Collections.emptyList();
                }
            } else {
                // not a leaf node, search appropriate subtree
                if (dummy.compareTo(midpoint) == 1) {
                    return right.find(key);
                } else {
                    return left.find(key);
                }
            }
        }

        void delete(byte[] key) {
            if (bucket == null) {
                return;
            }
            final Node dummy = new Node();
            dummy.setId(key);
            if (midpoint == ZERO_NODE) {
                for (Node node : bucket) {
                    if (dummy.compareTo(node) == 0) {
                        logger.debug("deleted node: {}", new String(key));
                        bucket.remove(node);
                    }
                }
            } else {
                if (dummy.compareTo(midpoint) == 1) {
                    right.delete(key);
                } else {
                    left.delete(key);
                }
            }
        }
    }
}
