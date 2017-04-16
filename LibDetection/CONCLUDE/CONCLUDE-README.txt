CONCLUDE 0.1
************

USER GUIDE
**********

To launch the community detection algorithm type:

java -jar CONCLUDE.jar input-filename output-filename delimiter compute-weights-only

input-filename: the name of the file containing the edgelist representing the network

output-filename: the output filename

delimiter: default "\t" (tab-separated values); other options: "," (csv) or " " (space-separated values)

compute-weights-only: with this option the algorithm will compute just the weights of the edges, without clustering the network.

The dataset is simply structured as follows: each line represents an (undirected) edge connecting two users which are connected by a friendship relationship in the Facebook graph.

-----------------
By Emilio FERRARA
14 Nov. 2011

USER GUIDE
**********

To launch the community detection algorithm type:

java -jar CONCLUDE.jar input-filename output-filename delimiter(optional) compute-weights-only(optional)

input-filename: the name of the file containing the edgelist representing the network

output-filename: the output filename

delimiter: this parameter is optional, default "\t" (tab-separated values); example of other options: "," (csv) or " " (space-separated values)

compute-weights-only: this parameter is optional; default: 0; setting to 1, the algorithm will compute just the weights of the edges, without clustering the network.

Example line commands:

	
java -jar CONCLUDE.jar facebook-links.txt partition-facebook-links.txt


This call will invoke CONCLUDE passing the input network file called facebook-links, prepared as an edgelist of tab-separated values.


	
java -Xmx4G -jar CONCLUDE.jar facebook-links.txt partition-facebook-links.txt


This call will invoke CONCLUDE asking the Java Virtual Machine to allocate 4G of memory to this process. This parameter is usually required because CONCLUDE is an in-memory execution algorithm which manages possibly very large matrices.

	
java -Xmx4G -jar CONCLUDE.jar facebook-links.txt partition-facebook-links.txt " "


This invokation is required for passing a "space-separated" edgelist.


	
java -Xmx4G -jar CONCLUDE.jar facebook-links.txt reweighted-facebook-links.txt "\t" 1


These options allow to execute only the first step of the algorithm in order to obtain a weighted network, without the clustering steps. Note that if you specify the compute the weights only, you have to pass the delimiter.