import java.util.Vector;

public class Path {
    private int time_; // la longueur totale du chemin
    private Vector<Node> listNodes_; // tous les noeuds acceder en ordre

    Path() {}
    Path(Node start){
        time_ = 0;
        listNodes_ = new Vector<Node>();
        listNodes_.addElement(start);
    }
    Path(Vector<Node> listNode){
        listNodes_ = new Vector<>(); // deep copy
        listNodes_.addAll(listNode);
    }
    public void setTime(int time) {
        time_=time;
    }

    public int getTime() {
        return time_;
    }

    public Vector<Node> getListNodes(){ return listNodes_;}


    public void addNode(Node obj){
        listNodes_.addElement(obj);
    }

    public void addTime(int time){
        time_+= time;
    }

    public void displayPath(){
        String str = ""+listNodes_.firstElement().getNodeId();
        for(Node it: listNodes_){
            if(listNodes_.firstElement().getNodeId() != it.getNodeId())
            str += "->"+ it.getNodeId();
        }
        System.out.println(str);
    }
}
