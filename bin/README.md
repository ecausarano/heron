heron
=====

a peer 2 peer, distributed, storage and indexing facility


Heron provides a simple platform for the trusted publication, search and retrieval of data over a P2P network in order to facilitate and encourage peer-reviewed sharing of information.

Epics:

1. A distributed hash table (DHT) to store data, metadata and indexes over a peer 2 peer network.
2. A UI to extract metadata, annotate and publish files to the DHT.
3. A UI to issue distributed search queries on the distributed metadata index (filtered by peer signature) and download the results.  
4. Cryptographic routines (GPG) to create digital identities, trust relations and manage the social graph of the platform Web of Trust (WOT.)
5. A UI to cryptographically "sign" uploads (such as files, metadata and transactions affecting them) to the DHT. 
6. UI elements and a metadata item to represent the peer review process (PUBLISH, RETRACT, ENDORSE, UNENDORSE)
7. A UI to draft and publish new metadata collections and format definitions, maps, overlays, comments, stories... 
