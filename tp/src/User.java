import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

public class User {
    private Graph graph_;
    private Vector<Request> listRequests_; // contient tous les requetes des clients
    private Vector<Node> listNodesCharge_=new Vector<Node>();
    private Node position_; // current position du user
    public static final String FILEGRAPH = "./arrondissements.txt";
    public static final String FILEREQUEST = "./requetes.txt";
    private int battery = 100;

    public Graph getGraphe() {
        return graph_;
    }

    public Vector<Request> getListRequests(){
        return listRequests_;
    }

    void creerGraphe(String filepath) throws FileNotFoundException { // Mettre le path du fichier en parametre
        boolean partie2 = false; // utiliser pour savoir dans quelle partie du fichier a scanner
        Vector<Node> listNodes = new Vector(); // vecteur contenant les vertexs
        Vector<Edge> listEdges = new Vector(); // vecteur contenant les Edges

        File file = new File(filepath);
        Scanner scan = new Scanner(file);
        String data = "";

        data = scan.nextLine();
        while (!data.isEmpty() && partie2 == false) { // Scan la 1ere partie pour les vertex et les bornes
            String NodeIdStr = "";
            boolean nodeBorne = false;

            int nodeId = 0;
            for (int i = 0; i < data.length(); i++) { // separe les donnees et les transformes en int et bool
                if (data.charAt(i) == ',') {
                    String str2 = "" + data.charAt(i + 1);
                    int nodeBorneInt = Integer.parseInt(str2);
                    if (nodeBorneInt == 1) {
                        nodeBorne = true;
                    }
                    break;
                }
                NodeIdStr += data.charAt(i); // creer un string du id de la vertex
            }
            nodeId = Integer.parseInt(NodeIdStr); // converti le string en int
            listNodes.add(new Node(nodeId, nodeBorne));
            data = scan.nextLine();
        }

        partie2 = true; // partie1 terminee
        int idDepart = 0;
        int cost = 0;
        int idDest = 0;
        while (scan.hasNextLine() && partie2) {
            data = scan.nextLine(); // itere le scanner
            if (!data.isEmpty()) { // skip les lignes vides
                String str = "";
                int compteurVirgule = 0;
                for (int i = 0; i < data.length(); i++) {
                    if (data.charAt(i) == ',') {
                        // le str est converti en integer;
                        if (compteurVirgule == 0) {
                            idDepart = Integer.parseInt(str);
                            str = ""; // reset le string
                        }
                        if (compteurVirgule == 1) {
                            idDest = Integer.parseInt(str);
                            str = ""; // reset le string
                            // forme le string pour le cost
                            for (int j = i + 1; j < data.length(); j++) {
                                str += data.charAt(j);
                            }
                            cost = Integer.parseInt(str);
                            break;
                        }
                        compteurVirgule++;
                        i++; // skip la virgule
                    }
                    str += data.charAt(i); // remplie le string
                }
            }

            Node depart = null;
            Node destination = null;
            // remplie un vecteur avec tous les edges du graph
            for (Node it : listNodes) { // ajoute les nodes liee a un edge
                int id = it.getNodeId();
                if (id == idDest) {
                    destination = it;
                }
                if (id == idDepart) {
                    depart = it;
                }
            }

            Edge newEdge = new Edge(depart, destination, cost);
            listEdges.add(newEdge);

            // remplie le vecteur d edges connue par une Node
            for (Node it : listNodes) {
                if (it.getNodeId() == idDepart) {
                    it.addEdge(newEdge);
                }
                if (it.getNodeId() == idDest) {
                    it.addEdge(new Edge(destination, depart, cost));
                }
            }
        }
        graph_ = new Graph(listNodes,listEdges); // creer le graphe
    }
    public void readRequest(String filepath) throws FileNotFoundException { //lis le fichier Request.txt

        listRequests_ = new Vector<>();
        File file = new File(filepath);
        Scanner scan = new Scanner(file);
        String data = "";
        data = scan.nextLine();
        int idClient = 0;
        Node locationClient = null;
        Node destinationClient = null;
        int time = 0;
        if(scan.hasNextLine()){ // trouve la node qui est le depart
            int nodeid = Integer.parseInt(data);
            position_= graph_.findNode(nodeid);
        }

        while (scan.hasNextLine()) {
            data = scan.nextLine(); // itere le scanner
            String str = "";
            int compteurVirgule = 0;
            for (int i = 0; i < data.length(); i++) {
                if (data.charAt(i) == ',') {
                    // le str est converti en integer;
                    if (compteurVirgule == 0) {
                        idClient = Integer.parseInt(str);
                        str = ""; // reset le string
                    }
                    if (compteurVirgule == 1) {
                        int temp = Integer.parseInt(str);
                        locationClient = graph_.findNode(temp);
                        str = ""; // reset le string
                    }
                    if (compteurVirgule == 2) {
                        int temp = Integer.parseInt(str);
                        destinationClient = graph_.findNode(temp);
                        str = ""; // reset le string

                        // forme le string pour le temps voulu
                        for (int j = i + 1; j < data.length(); j++) {
                            str += data.charAt(j);
                        }
                        time = Integer.parseInt(str);
                        break;
                    }
                    compteurVirgule++;
                    i++; // skip la virgule
                }
                str += data.charAt(i); // remplie le string
            }
            listRequests_.add(new Request(idClient,time,locationClient,destinationClient));
        }
    }

    /////////////////////////////////////////

    public Vector<Path> findNearestCharge(Node position) {
        for(int i=0;i< graph_.getListNodes().size();i++) {
            if(graph_.getListNodes().get(i).isBorne()) {
                listNodesCharge_.add(graph_.getListNodes().get(i));
            }
        }
        Path distanceMinCharge=new Path();
        distanceMinCharge.setTime(10000);

        Vector<Path> currentToChargeVec=new Vector<Path>();
        for(int i=0;i<listNodesCharge_.size();i++) {
            currentToChargeVec=plusCourtChemin(position.getNodeId(),listNodesCharge_.get(i).getNodeId());
            Path currentToCharge=currentToChargeVec.get(0);
            if(currentToCharge.getTime()<distanceMinCharge.getTime()) {
                distanceMinCharge=currentToCharge;
            }
        }
        Vector<Path> returnVector=new Vector<Path>();
        returnVector.add(distanceMinCharge);
        return returnVector;
    }


    public int doRequest(int currentRequestIndex,Path fromCurrentToStart,Path distanceMin) {
        int time;
        Vector<Path> v=new Vector<Path>();
        System.out.println("la demande du client "+currentRequestIndex+" est fait");
        v=graph_.shortestPath(distanceMin.getListNodes().get(0).getNodeId(),distanceMin.getListNodes().lastElement().getNodeId());     // faire la demande
        Path answer=v.get(0);
        System.out.println("la distance est de " +answer.getTime());
        time=listRequests_.get(currentRequestIndex).getTemps()-(fromCurrentToStart.getTime()+distanceMin.getTime());       // calculer le temps surplus
        return time;
    }


    static int currentRequest=0;
    static int batteryPercentage_=100;
    static int extraTime=0;
    static int actualTime=0;
    static int requestCounter_=0;  // le compteur de demande

    public void treatRequete() {
        for(int i=currentRequest; i<listRequests_.size(); i++)
        {
            currentRequest=i;   // update variable de currentRequest
            actualTime++;

            Vector<Path> fromCurrentToStartVec=new Vector<Path> ();
            fromCurrentToStartVec=plusCourtChemin(position_.getNodeId(),listRequests_.get(i).getDepart().getNodeId());  // calcul la distance pour arriver au client
            Path fromCurrentToStart=fromCurrentToStartVec.get(0);

            Vector<Path> distanceMinVec=new Vector<Path>();
            distanceMinVec=plusCourtChemin(listRequests_.get(i).getDepart().getNodeId(),listRequests_.get(i).getDestination().getNodeId());  // calcul la distance min
            Path distanceMin=distanceMinVec.get(0);

            int timeGivenByClient=listRequests_.get(i).getTemps();                          // calcul le temps donner par le client

            if(timeGivenByClient< fromCurrentToStart.getTime()+distanceMin.getTime()) {      // si la demande est impossible
                currentRequest++;                                                            // incrementer la requete courante
                treatRequete();                                                            // recursivite aka skip la demande actuelle
            }

            // si c'est possible et battery au dessus 15% en comptant currentPosition------->startingPoint---------->destination
            if((timeGivenByClient>=(fromCurrentToStart.getTime()+distanceMin.getTime())) && (batteryPercentage_-(fromCurrentToStart.getTime()+distanceMin.getTime())>15))
            {
                requestCounter_++;                                                  // incrementer le nombre de demande

                if(requestCounter_<4) {                                             // si c'est plus petit que 4 demandes
                    doRequest(i,fromCurrentToStart,distanceMin);           // faire la demande
                }

                if((requestCounter_==4)) {                                           // si le nombre de demandes est exactement 4
                    requestCounter_=0;                                                // reset le compteur de requetes
                    doRequest(i,fromCurrentToStart,distanceMin);
                }
            }

            if(batteryPercentage_-(fromCurrentToStart.getTime()+distanceMin.getTime())<=15) {        //si il n'y a pas assez de battery apres le trajet

                Vector<Path> chargingPathVec=findNearestCharge(position_); // trouver le chemin le plus proche a un noeud de recharge de position actuelle
                Path chargingPath=chargingPathVec.get(0);
                Vector<Path> fromChargeToClientVec=plusCourtChemin(chargingPath.getListNodes().lastElement().getNodeId(),distanceMin.getListNodes().get(0).getNodeId());       // calculer la distance min du noeud de recharge au depart du client
                Path fromChargeToClient=fromChargeToClientVec.get(0);

                int totalCostNeed=(chargingPath.getTime()+10+fromChargeToClient.getTime()+distanceMin.getTime());     // temps currentPosition-------->NoeudRecharge------->recharge----->start--
                //--------> destination

                if(listRequests_.get(i).getTemps()>totalCostNeed) {     // si
                    // check si il reste du temps si on recharge la voiture
                    requestCounter_++;

                    if(requestCounter_<4) {
                        batteryPercentage_=100;                        // mettre la batterie a jour
                        distanceMin.getListNodes().get(0).setNodeId(chargingPath.getListNodes().lastElement().getNodeId());   // changer le point de depart de la requete

                        extraTime=doRequest(i,fromCurrentToStart,distanceMin)-(chargingPath.getTime()+fromChargeToClient.getTime()+10); // temps surplus=tempsDemande-(tempsPourArriverRecharge+tempsRecharge
                    }
                    if(requestCounter_==4) {
                        requestCounter_=0;
                        batteryPercentage_=100;                        // mettre la batterie a jour
                        distanceMin.getListNodes().get(0).setNodeId(chargingPath.getListNodes().lastElement().getNodeId());   // changer le point de depart de la requete

                        extraTime=doRequest(i,fromCurrentToStart,distanceMin)-(chargingPath.getTime()+fromChargeToClient.getTime()+10);
                    }
                    else {
                        currentRequest++;
                        treatRequete();
                    }
                }
                else {
                    currentRequest++;
                    treatRequete();
                }

            }

        }

    }



    /////////////////////////////////////////////////

    public void displayGraph(){
        String nodeInfo;
        String borneOuPas;
        for(Node itN: graph_.getListNodes()){
            if(itN.isBorne())
                borneOuPas = "borne: oui";
            else
                borneOuPas = "borne: non";

            nodeInfo = "(" + itN.getNodeId() + ", " + borneOuPas + ", (";
            for(Edge itE: itN.getListEdges()){
                nodeInfo += "(" + itE.getDestination().getNodeId() + ", " + itE.getDistance() + "), ";
            }
            nodeInfo = nodeInfo.substring(0, nodeInfo.length()-2);
            nodeInfo += "))";
            System.out.println(nodeInfo);
        }
    }

    public void displayMenu(){
        System.out.print("\n");
        System.out.println("(a) Mettre à jour la carte");
        System.out.println("(b) Déterminer le plus court chemin sécuritaire.");
        System.out.println("(c) Traiter les requêtes.");
        System.out.println("(d) Quitter.");
    }

    public boolean verifyId(int id){
        boolean invalidEntree = true;
        if(graph_.findNode(id) == null){
            System.out.println("NOEUD INTROUVABLE");
        }
        else{
            invalidEntree = false;
        }
        return invalidEntree;
    }


    public boolean menu() throws FileNotFoundException {
        displayMenu();
        Scanner input = new Scanner(System.in);
        String instrucString = input.next();
        boolean continuer = true;
        if(instrucString.length() == 1) {
            char instrucChar = instrucString.charAt(0);
            if (instrucChar == 'a') {
                creerGraphe(FILEGRAPH);
                displayGraph();
            }
            else if (instrucChar == 'b') {
                if (graph_ != null) {
                    boolean invalidEntree = true;
                    int depart = 0;
                    int arrivee = 0;
                    while (invalidEntree) {
                        System.out.println("Entrer le point de départ");
                        depart = input.nextInt();
                        invalidEntree = verifyId(depart);
                    }
                    invalidEntree = true;
                    while (invalidEntree) {
                        System.out.println("Entrer le point d'arrivée");
                        arrivee = input.nextInt();
                        invalidEntree = verifyId(arrivee);
                    }

                    //TODO
                     displayShortestPath(depart, arrivee);
                }
                else
                    System.out.println("Veuillez entrer une carte avant");
            }
            else if(instrucChar == 'c'){
                if (graph_ != null) {
                    readRequest(FILEREQUEST);
                    //TODO
                    //afficher chemin
                }
                else
                    System.out.println("Veuillez entrer une carte avant");
            }
            else if(instrucChar == 'd'){
                continuer = false;
            }
            else{
                System.out.println("VEUILLEZ ENTRER UNE ENTREE VALIDE");
            }
        }
        else{
            System.out.println("VEUILLEZ ENTRER UNE ENTREE VALIDE");
        }
        return continuer;
    }
    public void runProgram() throws FileNotFoundException {
        boolean continuer = true;
        while (continuer){
            continuer = menu();
        }
    }
    public Vector<Path> plusCourtChemin(int idDepart, int idDestination){
        Vector<Path> temp = new Vector<>();
        temp = graph_.shortestPath(idDepart,idDestination);
        battery -= temp.firstElement().getTime();
        return graph_.shortestPath(idDepart,idDestination);
    }
    public void displayShortestPath(int start, int destination){
        Vector<Path> paths = new Vector<>();
        paths = graph_.shortestPath(start,destination);
        paths.firstElement().displayPath();
        System.out.println("temps = "+paths.firstElement().getTime());
        System.out.println( "Batterie = "+ (battery -= paths.firstElement().getTime()));
    }
    // pour faire des test...
    public void displayListEdges(){
        for(Edge it: graph_.getListEdges()){
            System.out.println("(" + it.getStart().getNodeId() + " destination id: " + it.getDestination().getNodeId() + " cost : " + it.getDistance());
        }
    }

    public void displayDistanceNeighbourEdges(Node it){
        for(Edge it2: it.getListEdges()){
            System.out.println(" Nodevoisin : " +it2.getDestination().getNodeId()+" duree "+ it2.getDistance());
        }
    }
    public void displayCurrentPos(){
        System.out.println("Current pos: " + position_.getNodeId());
    }
    public void displayListRequest(){
        for(Request it: listRequests_){
            System.out.println("idClient : "+ it.getIdClient()+" location Client : "+ it.getDepart().getNodeId()+ " destination : " + it.getDestination().getNodeId()+ " time limit : " + it.getTemps());
        }
    }
}

