import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

public class GraphAnalyzer<E> {

    private List<LinkedList<Vertex<E>>> adjList;
    private boolean[][] adjMatrix;
    private boolean cycle;
    private String file;

    public GraphAnalyzer(String file){
        this.file = file;
        this.adjList = null;
        this.adjMatrix = null;
        this.cycle = false;
    }

    public void buildList(){
        try{

        }
        catch(Error e){
            System.out.println(e.getMessage());
        }
    }

    public void promptMenu(){
        String result = "";
        result += "1) Depth First Search Path Discovery\n" +
                "2) Depth First Search Path Discovery + Cycle Detection\n" +
                "3) Cycle Detection\n" + "4) Breadth First Search\n" +
                "5) Transitive Closure\n" + "6) All Tests\n" +
                "7) Display Results\n" + "8) Initialize New Graph\n" +
                "0) Quit\n" + "-------------------------------------------------------";
        System.out.println(result);
    }

    public void go(){
        System.out.println("Welcome to GraphAnalyzermeister 2023 >>");
        buildList();
        //prompt();
    }
}
