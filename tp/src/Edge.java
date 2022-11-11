public class Edge {
    private Node destination_;
    private int distance_;
    private Node start_;

    Edge(Node start, Node destination, int distance){
        start_ = start;
        destination_= destination;
        distance_ = distance;
    }
    public int getDistance(){
        return distance_;
    }
    public Node getStart(){
        return start_;
    }
    public Node getDestination(){
        return destination_;
    }
}
