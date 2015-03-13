![http://www.cise.ufl.edu/~mhb/p3setup.png](http://www.cise.ufl.edu/~mhb/p3setup.png)

# Introduction #

These are the goals we are trying to achieve.

# Details #

  1. Leader Election (Bully Algorithm, Top N leaders, N is odd (for next point))
    * Priority is Timestamp
  1. DME based on leader voting (N being odd makes it sure there is a majority
    * yes or no voting, no choices
  1. Distributed files
    1. distributed file table
      * filename
      * read/write locks on a per file basis (with priority)
      * vesion (timestamp or date)
      * striping information
        * stripe size
        * stripe number
    1. full duplication (we seem to be doing this)
      1. When updating, do a move/rename to a random filename as backup before writing.
    1. striping
  1. Encrypted/Authorized Communication
  1. Easy Server startup - It’s a Feature!
    * Need server information table at each server (okay if incomplete? lock for writing, need DME)
      * server name
      * location
      * port
    * I have an idea/algorithm about how to do this
  1. Monitor Server with html page update.
  1. Dynamic ChunkSize for client (Why not? not as if it would be hard)


Client needs to maintain version, or someone does, otherwise we just do a global replace and increment the file version (What happens when two clients read the same file, one updates, and then a different one updates?)