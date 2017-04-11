package graphclasses1;

/**
 * The {@code Edge} class represents a timestamped edge in an
 * {@link EdgeWeightedGraph}. Each edge consists of two Stringegers (naming the two
 * vertices) and a real-value timestamp. The data type provides methods for
 * accessing the two endpoStrings of the edge and the timestamp. The natural order
 * for this data type is by ascending order of timestamp.
 * <p>
 * For additional documentation, see
 * <a href="http://algs4.cs.princeton.edu/43mst">Section 4.3</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public class Edge implements Comparable<Edge> {

    private String v;
    private String w;
    private long timestamp;

    /**
     * Initializes an edge between vertices {@code v} and {@code w} of the given
     * {@code timestamp}.
     *
     * @param v one vertex
     * @param w the other vertex
     * @param timestamp the timestamp of this edge
     * @throws IllegalArgumentException if either {@code v} or {@code w} is a
     * negative Stringeger
     * @throws IllegalArgumentException if {@code timestamp} is {@code NaN}
     */
    public Edge(String v, String w, long timestamp) { //CustomEdge
        
        //if (Long.isNaN(timestamp)) throw new IllegalArgumentException("Weight is NaN");
        this.v = v;
        this.w = w;
        this.timestamp = timestamp;
    }

    public Edge(String v, String w) { //CustomEdge
        
        //if (Long.isNaN(timestamp)) throw new IllegalArgumentException("Weight is NaN");
        this.v = v;
        this.w = w;
        this.timestamp = -1;
    }

    /**
     * Returns the timestamp of this edge.
     *
     * @return the timestamp of this edge
     */
    public long timestamp() {
        return getTimestamp();
    }

    /**
     * Returns either endpoString of this edge.
     *
     * @return either endpoString of this edge
     */
    public String either() {
        return getV();
    }

    /**
     * Returns the endpoString of this edge that is different from the given
     * vertex.
     *
     * @param vertex one endpoString of this edge
     * @return the other endpoString of this edge
     * @throws IllegalArgumentException if the vertex is not one of the
     * endpoStrings of this edge
     */
    public String other(String vertex) {
        if (vertex == getV()) {
            return getW();
        } else if (vertex == getW()) {
            return getV();
        } else {
            throw new IllegalArgumentException("Illegal endpoString");
        }
    }

    /**
     * Compares two edges by timestamp. Note that {@code compareTo()} is not
     * consistent with {@code equals()}, which uses the reference equality
     * implementation inherited from {@code Object}.
     *
     * @param that the other edge
     * @return a negative Stringeger, zero, or positive Stringeger depending on
     * whether the timestamp of this is less than, equal to, or greater than the
     * argument edge
     */
    @Override
    public int compareTo(Edge that) {
        return Long.compare(this.getTimestamp(), that.getTimestamp());
    }

    /**
     * Returns a string representation of this edge.
     *
     * @return a string representation of this edge
     */
    public String toString() {
        return String.format("%d-%d %.5f", getV(), getW(), getTimestamp());
    }

    /**
     * Unit tests the {@code Edge} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        Edge e = new Edge("12", "34", 567);
        System.out.println(e);
    }

    /**
     * @return the v
     */
    public String getV() {
        return v;
    }

    /**
     * @param v the v to set
     */
    public void setV(String v) {
        this.v = v;
    }

    /**
     * @return the w
     */
    public String getW() {
        return w;
    }

    /**
     * @param w the w to set
     */
    public void setW(String w) {
        this.w = w;
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
