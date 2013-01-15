heron
=====

A distributed platform for the dissemination of culture


Design goal of the tool is to provide a simple platform for the trusted publication, search and manipulation of artifacts over a P2P network in order to facilitate and encourage peer-reviewed dissemination of Culture and Knowledge.

In broad terms the tool consists of:

1. A DHT implementatin (tentatively Kademlia) to provide the underlying platform for the distribution of the artifacts and of their indexed metadata attributes.
2. An interface and mechanism to publish and manipulate the metadata contained in the DHT.
3. A mechanism to issue distributed search queries on the distributed metadata index.  
4. A mechanism (tentatively GPG) to cryptographically apply a "seal" to metadata attributes and transactions affecting them (such as PUBLISH, RETRACT, ENDORSE, UNENDORSE) published to the DHT.
5. A mechanism - similar in principle to the PGP WOT or Liquid Feedback - to build, maintain and publish a social graph of the cryptographic keys associated to the P2P network users.
6. A mechanism to build and publish metadata maps, overlays and stories.

Key to the platform design and goal is the trust assigned by each participant to their immediate and transitive peers. This trust is embedded into the metadata published on the DHT by means of the aforementioned cryptographic seals.

