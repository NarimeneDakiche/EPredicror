CliqueMod software

This page provides the software developed for the paper Detecting Communities in Networks by Merging Cliques.

The program used for the experiments in the paper is available here:

Software
To run it:

   java -cp CM.jar CM <networkFile> -m <method> -c <nComm>
where <networkFile> is the file containing the network, in "list of edges" format, <method> is the clique-finding algorithm to use ("BK" or "KJ"), and <nComm> is the number of communities required.

The program prints the modularity of the solution and outputs the solution to a file named "ClustersOutput.txt".

Papers, Software and Datasets can be found here: http://gregory.org/research/networks/