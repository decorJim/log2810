import javafx.util.Pair;
import java.util.Vector;

import java.util.ArrayList;

public class Node {
    private boolean borne_;     // si il ya une borne a ce noeud
    private int nodeId_;    // quel est le nombre inscrit sur le noeud
    private Vector<Edge> listEdges_;     // tableau qui contient les Edges voisins
    Node(){
        borne_ = false;
        nodeId_ = 0;
        listEdges_ = new Vector<Edge>();
    }
    Node(int nodeId,boolean borne) {
        borne_ = borne;
        nodeId_ = nodeId;
        listEdges_ = new Vector<Edge>();
    }

    public void addEdge(Edge e){
        listEdges_.add(e);
    }
    public boolean isBorne() {
        return borne_;
    }
    public int getNodeId() {
        return nodeId_;
    }
    public Vector<Edge> getListEdges(){ return listEdges_; }
    public void setNodeId(int id) {
        nodeId_=id;
    }

}
