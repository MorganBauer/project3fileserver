serverStartup
[serverStartup](http://www.cise.ufl.edu/~mhb/serverOperations.png)
![http://www.cise.ufl.edu/~mhb/serverOperations.png](http://www.cise.ufl.edu/~mhb/serverOperations.png)

# Details #

This assumes that the client can read the file at any time and only needs one server. (or something like that) Also, if multiple servers start up at the exact same time, there are also problems.

Assumptions
  * 'shared' table of servers among all servers. this is updated with some kind of atomic update/commit/transaction.
  * if the first server ever goes down, the next highest guy needs to do the updating of the config.ini
  * a server has to connect on the client port to get updates (heh, we can use this same function to give updates to the client.)

Point of Contention
  * once connected, who notifies everyone else? Masters? (puts load on masters, but they have the authority (to update the server table), so they will be contacted anyway.)

There are three main parts

A server starts up, does one of two things, then spawns the service thread.

## readConfig ##

server reads the config file, and tries to connect to one of the first 3 servers (if there are that many there. (the first three, cause they are the lowest timestamps and thus the 'masters') if it cannot, it ought to try to connect to the rest, and determine if there are any alive.

from here we branch, and call either firstServer, or subsequentServer

## firstServer ##
```
// invariant: we could not connect to anyone thus our table is empty
new ServerTable.
insert Self into ServerTable
```
The first server to start up will read the config.ini file, and remember the chunksize to use. (really, it doesn't need to, the client ought to be giving the server the chunksize it reports.) It wipes the file, and puts the chunksize (as the first line (not necessary)), followed by the servers own information.

## subsequentServer ##
connected to someone, give them our information and then start serving files (?or tell everyone else?)


# other thing #

we need a message for getting and putting server information, whether as a whole, or piecemeal.