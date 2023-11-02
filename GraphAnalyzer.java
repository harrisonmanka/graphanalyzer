import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class GraphAnalyzer<E> {

    private ArrayList<LinkedList<Vertex<E>>> adjList;
    private boolean[][] adjMatrix;
    private boolean cycle;
    private String file;
    private int count;
    private ArrayList<Vertex<E>> bfsResults;
    private ArrayList<Vertex<E>> dfsResults;
    private Map<String, Boolean> menuOptions;

    public GraphAnalyzer(String file){
        this.file = file;
        this.adjList = new ArrayList<LinkedList<Vertex<E>>>();
        this.adjMatrix = null;
        this.menuOptions = new HashMap<>();
        this.cycle = false;
        this.count = 0;

    }

    public void buildList(){
        try{
            File file = new File(this.file);
            Scanner scanner = new Scanner(file);
            while(scanner.hasNext()){
                String source = scanner.next();
                String dest = scanner.next();
                if(!check(source)){ //checking if source is in the arraylist
                    adjList.add(new LinkedList<Vertex<E>>());
                    Vertex<E> newVertex = new Vertex<>((E)source, count, "UNVISITED");
                    Vertex<E> newVertex2 = new Vertex<>((E)dest, "UNVISITED");
                    adjList.get(count).add(newVertex);
                    adjList.get(count).add(newVertex2);
                    count++;
                    if(!check(dest)){
                        adjList.add(new LinkedList<Vertex<E>>());
                        Vertex<E> newVertex22 = new Vertex<>((E)dest, "UNVISITED");
                        adjList.get(count).add(newVertex22);
                        count++;
                    }
                }
                else{
                    Vertex<E> newVertex = new Vertex<>((E)dest, "UNVISITED");
                    int listIndex = findIndex(source);
                    adjList.get(listIndex).add(newVertex);
                }
            }
        }
        catch(Error | FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        adjustIndexInList();
//        printVertices();
        buildMatrix();
//        System.out.println();
//        printMatrix();
//        boolean result = findSource("3");
//        System.out.println(result);
        initializeMenuValues();
    }

    public void adjustIndexInList(){
        for(int i = 0; i < adjList.size(); i++){
            for(int j = 0; j < adjList.get(i).size(); j++){
                if(adjList.get(i).get(j).getIndex() == -1){
                    int index = findIndex(adjList.get(i).get(j).getId().toString());
                    adjList.get(i).get(j).setIndex(index);
                }
            }
        }
    }

    public void buildMatrix(){
        adjMatrix = new boolean[count][count];
        for(int i = 0; i < count; i++){
            for(int j = 1; j < adjList.get(i).size(); j++){
                Vertex<E> src = adjList.get(i).get(j);
                int checkColumn = adjList.get(i).get(j).getIndex();
                if(i == checkColumn){
                    adjMatrix[i][j] = false;
                }
                else if(hasVertex(adjList.get(i), src)){
                    adjMatrix[i][src.getIndex()] = true;
                }
                else{
                    adjMatrix[i][j] = false;
                }
            }
        }
    }

    public boolean findSource(String s) throws IllegalArgumentException{
        boolean result = false;
        for(int i = 0; i < adjList.size(); i++){
            Vertex<E> test = adjList.get(i).getFirst();
            if(test.getId().equals(s) && adjList.get(i).size() > 1){
                result = true;
            }
        }
        if(!result){
            throw new IllegalArgumentException();
        }
        return result;
    }

    public boolean findDestination(String s) throws IllegalArgumentException{
        boolean result = false;
        for(int i = 0; i < adjList.size(); i++){
            for(int j = 1; j < adjList.get(i).size(); j++){
                Vertex<E> test = adjList.get(i).get(j);
                if(test.getId().equals(s)){
                    result = true;
                }
            }
        }
        if(!result){
            throw new IllegalArgumentException();
        }
        return result;
    }

    public ArrayList<Vertex<E>> breadthFirstSearch(String source){
        return null;
    }

    public ArrayList<Vertex<E>> depthFirstSearch(String source, String destination){
        return null;
    }

    public boolean getCycleStatus(){
        return cycle;
    }

    public void transitiveClosure(){

    }

    public boolean hasVertex(LinkedList<Vertex<E>> list, Vertex<E> vertex){
        boolean check = false;
        for(Vertex<E> e : list){
            if(e.getId().equals(vertex.getId())){
                check = true;
            }
        }
        return check;
    }

    public void printMatrix(){
        for(int i = 0; i < adjList.size(); i++){
            if(i == 0){
                System.out.print("\t");
            }
            System.out.print(adjList.get(i).getFirst().getId().toString() + "     ");
        }
        System.out.println();
        for(int i = 0; i < adjMatrix.length; i++){
            System.out.print(adjList.get(i).getFirst().getId().toString() + " ");
            for(int j = 0; j < adjMatrix.length; j++){
                System.out.print(adjMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void printVertices(){
        for(int i = 0; i < adjList.size(); i++){
            for(int j = 0; j < adjList.get(i).size(); j++){
                if(j == 0){
                    System.out.print(adjList.get(i).get(j).getId() + ": ");
                }
                else{
                    System.out.print(adjList.get(i).get(j).getId() + "(" + adjList.get(i).get(j).getIndex() + ") ");
                }
            }
            System.out.println();
        }
    }

    public int findIndex(String s){
        int index = -1;
        for(int i = 0; i < adjList.size(); i++){
            if(adjList.get(i).getFirst().getId().equals(s)){
                index = i;
            }
        }
        return index;
    }

    public boolean check(String s){
        boolean equal = false;
        for(int i = 0; i < adjList.size(); i++){
            if(adjList.get(i).getFirst().getId().equals(s)){
                equal = true;
            }
        }
        return equal;
    }

    public void initializeMenuValues(){ //set all menu selections to false
        menuOptions.put("1", false);
        menuOptions.put("2", false);
        menuOptions.put("3", false);
        menuOptions.put("4", false);
        menuOptions.put("5", false);
    }

    public void replaceAllOptions(){ // set all menu selections to true
        menuOptions.replace("1", true);
        menuOptions.replace("2", true);
        menuOptions.replace("3", true);
        menuOptions.replace("4", true);
        menuOptions.replace("5", true);
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

    public void prompt(){
        promptMenu();
        Scanner scanner = new Scanner(System.in);
        boolean inProgress = true;
        System.out.print("Enter menu choice >>> ");
        String choice = scanner.next();
        System.out.println();
        while(inProgress){
            switch(choice){
                case "0":
                    inProgress = false;
                    System.out.println("Goodbye!!!");
                    break;
                case "1":
                    System.out.print("Please enter a valid source vertex >>> ");
                    String src = scanner.next();
                    System.out.println();
                    System.out.print("Please enter a valid destination vertex >>> ");
                    String dest = scanner.next();
                    System.out.println();
                    if(findSource(src) && findDestination(dest)){
                        depthFirstSearch(src, dest);
                        menuOptions.replace("1", true);
                    }
                    else{
                        System.out.println("Invalid source and/or destination vertex. Try again.");
                    }
                    break;
                case "2":
                    System.out.print("Please enter a valid source vertex >>> ");
                    String src2 = scanner.next();
                    System.out.println();
                    System.out.print("Please enter a valid destination vertex >>> ");
                    String dest2 = scanner.next();
                    System.out.println();
                    if(findSource(src2) && findDestination(dest2)){
                        depthFirstSearch(src2, dest2);
                        menuOptions.replace("2", true);
                    }
                    else{
                        System.out.println("Invalid source and/or destination vertex. Try again.");
                    }
                    break;
                case "3":
                    //checkForCycles();
                    menuOptions.replace("3", true);
                    break;
                case "4":
                    System.out.print("Please enter a valid source vertex >>> ");
                    String src3 = scanner.next();
                    System.out.println();
                    if(findSource(src3)){
                        breadthFirstSearch(src3);
                        menuOptions.replace("4", true);
                    }
                    else{
                        System.out.println("Invalid source vertex. Try again.");
                    }
                    break;
                case "5":
                    transitiveClosure();
                    menuOptions.replace("5", true);
                    break;
                case "6":
                    System.out.print("Please enter a valid source vertex >>> ");
                    String src4 = scanner.next();
                    System.out.println();
                    System.out.print("Please enter a valid destination vertex >>> ");
                    String dest4 = scanner.next();
                    System.out.println();
                    if(findSource(src4) && findDestination(dest4)){
                        depthFirstSearch(src4, dest4);
                        breadthFirstSearch(src4);
                        transitiveClosure();
                        replaceAllOptions();
                    }
                    break;
                case "7":
                    for(int i = 1; i <= menuOptions.size(); i++){
                        String index = Integer.toString(i);
                        if(menuOptions.get(index)){ //checking which options the user selection
                            switch(index){
                                case "1":
                                    //printDFS();
                                case "2":
                                    //printDFSWithCycles();
                                case "3":
                                    // print cycle detection
                                case "4":
                                    //printBFS();
                                case "5":
                                    //printNewEdges();
                            }
                        }
                    }
                    break;
                case "8":
                    System.out.print("Please enter a new filename >>> ");
                    String file = scanner.next();
                    GraphAnalyzer<E> graph = new GraphAnalyzer<E>(file);
                    graph.buildList();
                    break;
            }
            prompt();
        }

    }

    public void go(){
        System.out.println("Welcome to GraphAnalyzermeister 2023 >>");
        buildList();
        prompt();
    }
}
