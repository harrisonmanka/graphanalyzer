/**
 * Driver class that creates a GraphAnazlyer class and calls go().
 *
 * @author Harrison Manka
 */
public class Driver {

    /**
     * Main method to start our program.
     * Catches an error if file is not found.
     *
     * @param args
     */
    public static void main(String[] args){

        if(args.length != 1){
            System.out.print("Usage: java Driver.java fileName");
            System.exit(1);
        }
        try {
            GraphAnalyzer<?> graph = new GraphAnalyzer<>(args[0]);
            graph.go();
        }
        catch(Error e){
            System.out.println("File could not be found. Restart.");
        }
    }
}
