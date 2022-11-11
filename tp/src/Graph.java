import javax.print.attribute.standard.Destination;
import java.io.File;
import java.util.*;
import java.io.FileNotFoundException;




public class Graph {
    private Vector<Node> listNodes_;  // contient tous les noeuds dans le graphe
    private Vector<Edge> listEdges_;  // contient tous les aretes dans le graphe

    Graph(Vector<Node> nodes, Vector<Edge> edges) {
        listEdges_ = edges; // shallow copy
        listNodes_ = nodes;
    }

    public Vector<Node> getListNodes() {
        return listNodes_;
    }

    public Vector<Edge> getListEdges() {
        return listEdges_;
    }

    public Node findNode(int nodeid) {
        for (Node it : listNodes_) {
            if (it.getNodeId() == nodeid) {
                return it;
            }
        }
        return null; // si aucune node est trouver
    }

    public Vector<Path> shortestPath(int idDepart, int destId) { //Algorithm de Dijkstra
        Node depart = findNode(idDepart);
        Node destination = findNode(destId);

        Vector<Path> listPath = new Vector<>(); // Liste pour les chemins

        for (Edge it : depart.getListEdges()) {
            Path temp = new Path(depart);
            temp.addNode(it.getDestination());
            temp.addTime(it.getDistance());
            listPath.addElement(temp);
            if (destination.getNodeId() == it.getDestination().getNodeId()) {
                Vector<Path> vec = new Vector<>();
                vec.add(temp);
                return vec;
            }
        }
        int tempsMin = Integer.MAX_VALUE; // le plus petit temps obtenu par un chemin arriver a la destination


        while (true) {
            // trier le vector : les chemins avec un temps plus petit se mettent au debut du vector
            Vector<Path> tmp = new Vector<>(); // vecteur temporaire

            for (int i = 0; i <= listPath.size(); i++) {
                Path minPath = listPath.elementAt(0); // Assume que le 1er elem du vecteur est le chemin le moins couteux(pas necessairement le cas)
                for (int j = 0; j < listPath.size(); j++) {
                    if (minPath.getTime() > listPath.elementAt(j).getTime()) { // change le min si le chemin est moins couteux
                        minPath = listPath.elementAt(j);
                    }
                }
                boolean meilleurchemin = true;
                for (int j = 0; j < tmp.size(); j++) {
                    // verifie si un meilleur chemin existe deja pour le meme noeud
                    if (minPath.getListNodes().lastElement().getNodeId() == tmp.elementAt(j).getListNodes().lastElement().getNodeId()) {
                        meilleurchemin = minPath.getTime() <= tmp.elementAt(j).getTime(); // si le path est egale alors il ne sera pas enlever du vecteur(il ne peut pas etre plus petit)
                    }
                }
                if (meilleurchemin) {
                    tmp.addElement(minPath);
                }
                listPath.remove(minPath);
                i = 0; // evite d etre out of range dans le vector
            }

            listPath = tmp; // listPath devient le nouveau vecteur trier
            tmp = null;

            // verifier si un ou plusieurs path on ete trouve
            boolean ArrivedDest = true;
            boolean timeRespected = true;
            for (Path it : listPath) {
                // condition pour arreter le while(true)
                Node id = it.getListNodes().lastElement();
                if (id.getNodeId() != destination.getNodeId()) {
                    ArrivedDest = false;
                }
                if (it.getTime() != tempsMin) {
                    timeRespected = false;
                }
            }
            if (ArrivedDest && timeRespected) {
                return listPath;
            }

            // Agrandir les chemins en creant des nouveaux path
            Vector<Path> tmp2 = new Vector<>();
            int listPathSize = listPath.size();
            boolean ArriverplusTot = false;
            for (Path it : listPath) {
                //boolean Bloquer = true; // si le path ne peu plus bouger et qu il n est pas a la destination alors le retirer
                for (Edge it2 : it.getListNodes().lastElement().getListEdges()) {
                    boolean peutAvancer = true;
                    boolean Arriver = false;
                    ArriverplusTot = false;
                    if (it2.getStart().getNodeId() == destination.getNodeId()) { // si le path est deja arriver depuis un moment
                        ArriverplusTot = true;
                    }
                    for (Node it3 : it.getListNodes()) {// nodes d un  path
                        // peut Avancer sur cette edge si il n est jamais passer par celle ci ou si il n est pas a la destination
                        if (it3.getNodeId() == it2.getDestination().getNodeId() || it2.getStart().getNodeId() == destination.getNodeId()) {
                            peutAvancer = false;
                        }
                        // si se path est deja arriver a la destination on veut pas le supprimer ni l agrandir
                        if (it2.getDestination().getNodeId() == destination.getNodeId()) {
                            Arriver = true;
                        }
                    }
                    // Ajoute le path dans le vecteur
                    if (Arriver) {
                        int totalTemps = it.getTime() + it2.getDistance();
                        if (totalTemps <= tempsMin) {
                            // ajout du temps du nouveau chemin et de la nouvelle node
                            Path newPath = new Path(it.getListNodes());
                            newPath.getListNodes().addElement(it2.getDestination());
                            newPath.addTime(totalTemps);
                            tmp2.addElement(newPath);
                            tempsMin = totalTemps;
                        }
                    }

                    // creer des paths plus long a partir des anciens path
                    if (peutAvancer && !Arriver) {
                        int totalTemps = it.getTime() + it2.getDistance();
                        // modification du path si il est toujours sous le temps min
                        if (totalTemps < tempsMin) {
                            // ajout du temps du nouveau chemin et de la nouvelle node
                            Path newPath = new Path(it.getListNodes());
                            newPath.getListNodes().addElement(it2.getDestination());
                            newPath.addTime(totalTemps);
                            tmp2.addElement(newPath);
                        }
                    }
                }
                // remettre son path dans le vecteur temporaire sans modifier son path
                if (ArriverplusTot) {
                    if (it.getTime() <= tempsMin) ;
                    tmp2.addElement(it);
                }
            }
            listPath = tmp2;
            tmp2 = null;
        }
    }
}