# Cassandra and Akka

Builds a very simple Twitter search engine and language detector using 
* Cassandra 
* Akka 
* Scalatra
* Dispatch
* Json4s

## Slides as...

* [Google doc](https://docs.google.com/presentation/d/120mMUnirRDnsfvxW3pAafU3LUVOLpiI3bX3uWG_nYMA/pub?start=false&loop=false&delayms=3000#slide=id.p)
* [PDF](slides.pdf)

## How to run it

* [Install Cassandra >= 1.2](http://wiki.apache.org/cassandra/GettingStarted).
* The default localhost:9160 is lazily hardcoded.  Pull requests welcome if
  youre running it elsewhere -- see DataStaxExtension.
* `sbt run`
* REST interface will start on http://localhost:8080/

## Credits

* Presented by [Ross A. Baker](http://github.com/rossabaker), Senior Cloud 
Engineer at [CrowdStrike](http://www.crowdstrike.com/)
* Delivered to [Indy Scala](http://indyscala.org/), August 5, 2013
