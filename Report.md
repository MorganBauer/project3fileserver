# Introduction #

In this project, we implemented a Distributed file system with Redundancy and Parallelism.

# Features #

  * xml serialization of data using JAXB: This was done to allows us to connect with a client written in any language.

  * The client does not interact with any one particular server for all its operations. Each of the supported commands may be executed at any one of the servers in the Distributed File System randomly chosen  and the changes made by the client are replicated through out the system.

  * File replication: The servers periodically send each other a pulse for staying alive and an update about its current directory status . The other servers read these messages and update their directory by requesting all the newly written files and or deleting the files that are deleted at other servers.

  * Time stamp based file Updates: The update message from a server carries the Last modified time stamp of each of the files that it has enabling the receiving peer servers whether to apply an update(file put/delete) or not.

  * SSL: All communication is secured using SSL and the encryption method to be used can be specified in the client command line.

  * Logging: A Separate Log server is maintained, that accepts UDP log Messages from all the servers. It publishes an HTML file that shows the current load at all servers and a few of the recent updates in the system.There is also a text log of all messages that is maintained.

  * Priority and Locking: The incoming service requests are processed at each server with a priority mentioned in the client command line and server to server updates are given maximum priority. The read operations into a server directory are re-entrant while the write operations are serialized.

  * Server Failure: When a server fails int the middle of an operation, the client tries to complete that operation with another server.

# Commands Supported #

## Basic ##
  * file get - Download of a file from the system.
  * file put - Upload of a file to any server that is replicated through out the system
  * terminate - Shutdown of the system.
  * hello
  * directory list
  * bye - client exit.
## Extra ##
  * my dir list - client directory list.
  * delete file - Delete a file from the system.
  * encryptify "Algorithm" - change communication to the given algorithm.