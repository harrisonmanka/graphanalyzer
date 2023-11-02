import java.io.FileNotFoundException;

public class Driver {

    public static void main(String[] args){

        if(args.length < 1){
            System.out.print("Usage: java Driver.java fileName");
            System.exit(1);
        }
        try{
            GraphAnalyzer<?> graph = new GraphAnalyzer<>(args[0]);
            graph.go();
        }
        catch(Error e){
            System.out.println(e.getMessage());
        }
    }
}
