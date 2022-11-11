import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;



public class main {
    public static void main(String[] args) throws FileNotFoundException {

         User test = new User();
      test.creerGraphe("./arrondissements.txt"); // a changer selon le path de votre fichier
      test.displayGraph();
     Vector<Path> paths = new Vector<>();
      paths = test.getGraphe().shortestPath(16,5);
       for(Path it: paths){
            it.displayPath();
           System.out.println(it.getTime());
      }

        test.displayGraph();
        test.readRequest("./requetes.txt");
        test.treatRequete();

        test.runProgram();



}
}
