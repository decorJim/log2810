
public class Request {
   private int idClient_;     // l'order de la demande
   private int time_;     // le time demander
   private Node start_;   // place ou le client est situe
   private Node destination_;  // ou le client veut aller


   Request(int client, int time, Node start, Node destination) {
       idClient_ = client;
       time_ = time;
       start_ = start;
       destination_ = destination;
   }

   public int getIdClient() {
       return idClient_;
   }

   public int getTemps() {
       return time_;
   }

   public Node getDepart() {
       return start_;
   }

   public Node getDestination() {
       return destination_;
   }

}
