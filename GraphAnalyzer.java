import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Class representing a GraphAnalyzer which has an adjacencyList and an adjacencyMatrix.
 *
 * @param <E> - our generic term
 */
public class GraphAnalyzer<E> {

    /**
     * An ArrayList<LinkedList<Vertex<E>>> representing our adjacencyList.
     */
    private ArrayList<LinkedList<Vertex<E>>> adjList;

    /**
     * A boolean[][] representing our adjacencyMatrix.
     */
    private boolean[][] adjMatrix;

    /**
     * A boolean representing the cycle status.
     */
    private boolean cycle;

    /**
     * A string representing our file name.
     */
    private String file;

    /**
     * An integer representing the count.
     */
    private int count;

    /**
     * An integer representing the size.
     */
    private int size;

    /**
     * An ArrayList<Vertex<E>> representing our breadth first search results.
     */
    private ArrayList<Vertex<E>> bfsResults;

    /**
     * An ArrayList<Vertex<E>> representing our depth first search results.
     */
    private ArrayList<Vertex<E>> dfsResults;

    /**
     * An ArrayList<Vertex<E>> representing our depth first search path.
     */
    private ArrayList<Vertex<E>> dfsPath;

    /**
     * A Map<String, Boolean> representing our menu options' state.
     */
    private Map<String, Boolean> menuOptions;

    /**
     * An ArrayList<String> representing our newly added edges.
     */
    private ArrayList<String> newEdges;

    /**
     * Creates a new GraphAnalyzer with the specified file and sets all fields
     * to its respected values.
     *
     * @param file - String representing the file.
     */
    public GraphAnalyzer(String file){
        this.file = file;
        this.adjList = new ArrayList<LinkedList<Vertex<E>>>();
        this.adjMatrix = null;
        this.bfsResults = new ArrayList<>();
        this.dfsResults = new ArrayList<>();
        this.dfsPath = new ArrayList<>();
        this.newEdges = new ArrayList<>();
        this.menuOptions = new HashMap<>();
        this.cycle = false;
        this.count = 0;
        this.size = 0;

    }

    /**
     * Method to build our adjacency list based on given input from the file.
     */
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
        buildMatrix();
        initializeMenuValues();
    }

    /**
     * Helper method to initialize all vertices in the adjacencyList.
     */
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

    /**
     * Method to build our adjacencyMatrix based off of the adjacencyList.
     */
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
        size = adjMatrix.length;
    }

    /**
     * Helper method to confirm a source is found within the graph.
     *
     * @param s - String representing a user given source.
     * @return true if it is found, false is not.
     * @throws IllegalArgumentException - if a source is not valid.
     */
    public boolean findSource(String s) throws IllegalArgumentException{
        boolean result = false;
        for(int i = 0; i < adjList.size(); i++){
            Vertex<E> test = adjList.get(i).getFirst();
            if(test.getId().equals(s) && adjList.get(i).size() > 1){
                result = true;
            }
        }
        if(!result){
            throw new IllegalArgumentException("Source not found within the graph.");
        }
        return result;
    }

    /**
     * Helper method to confirm a destination is found within the graph.
     *
     * @param s - String representing a user given destination.
     * @return true if it is found, false is not.
     * @throws IllegalArgumentException - if a destination is not valid.
     */
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
            throw new IllegalArgumentException("Destination not found within the graph.");
        }
        return result;
    }

    /**
     * Method that implements a Breadth First Search with a given starting vertex string value.
     *
     * @param source - String representing starting vertex.
     */
    public void breadthFirstSearch(String source){
        Queue<Vertex<E>> queue = new ArrayDeque<>();
        Vertex<E> first = getVertexByID(source);
        queue.add(first);
        while(!queue.isEmpty()){
            Vertex<E> vertexToRemove = queue.poll();
            vertexToRemove.setState("VISITED"); //change state
            bfsResults.add(vertexToRemove); //add to results
            ArrayList<Vertex<E>> toAdd = getAllNeighbors(vertexToRemove);
            for(Vertex<E> v : toAdd){ //vertices in process
                v.setState("PROCESSING");
            }
            queue.addAll(toAdd);
        }
    }

    /**
     * Method that implements a Depth First Search with a given starting and ending
     * vertex string value.
     *
     * @param source - String representing a starting vertex.
     * @param destination - String representing a destination vertex.
     */
    public void depthFirstSearch(String source, String destination){
        dfsPath.clear();
        dfsResults.clear();
        resetStates();
        Stack<Vertex<E>> stack = new Stack<>();
        ArrayList<Vertex<E>> neighbors;
        Vertex<E> start = getVertexByID(source);
        stack.push(start);
        start.setState("VISITED");
        dfsResults.add(start);
        dfsPath.add(start);
        boolean path = false;
        while(!stack.isEmpty()){
            Vertex<E> vertex = stack.peek();
            neighbors = getNeighbors(vertex);
            Vertex<E> next = nextNeighbor(neighbors);
            if(next != null){
                stack.push(next);
                next.setState("VISITED");
                dfsResults.add(next);
                if(next.getId().toString().equals(destination)){
                    dfsPath.add(next);
                    path = true;
                    break;
                }
                else if(!path){
                    dfsPath.add(next);
                }
            }
            else{
                stack.pop();
            }
            neighbors.clear(); //reset neighbor list
        }
    }

    /**
     * Method that implements a cycle search and sets the global value cycle
     * to true if one is found, false if not.
     *
     * @param source - String representing a starting vertex.
     */
    public void cycleSearch(String source){
        Stack<Vertex<E>> stack = new Stack<>();
        ArrayList<Vertex<E>> neighbors;
        Vertex<E> start = getVertexByID(source);
        stack.push(start);
        while(!stack.isEmpty()){
            Vertex<E> vertex = stack.peek();
            neighbors = getNeighbors(vertex);
            Vertex<E> next = nextNeighbor(neighbors);
            if(next != null){
                stack.push(next);
                next.setState("VISITED");
            }
            else{
                stack.pop();
            }
            neighbors.clear(); //reset neighbor list
        }
    }

    /**
     * Helper method to obtain the nextNeighbor if it has not been
     * visited yet. Sets cycle to true if the next neighbor
     * has been visited.
     *
     * @param list - ArrayList<Vertex<E>> representing a given list of neighbors.
     * @return the next unvisited neighbor.
     */
    public Vertex<E> nextNeighbor(ArrayList<Vertex<E>> list){
        Vertex<E> toReturn = null;
        int i = 0;
        while(i < list.size()) {
            if(list.get(i).getState().equals("UNVISITED")) {
                toReturn = list.get(i);
                break; //NEXT NEIGHBOR FOUND
            }
            else if(list.get(i).getState().equals("VISITED")){
                cycle = true;
            }
            i++;
        }
        return toReturn;
    }

    /**
     * Helper method to obtain all neighbors no matter the state.
     *
     * @param vertex - Vertex to obtain all of his neighbors.
     * @return An ArrayList of all neighbors.
     */
    public ArrayList<Vertex<E>> getNeighbors(Vertex<E> vertex){
        ArrayList<Vertex<E>> list = new ArrayList<>();
        for(int i = 0; i < adjList.size(); i++){
            if(adjList.get(i).getFirst().getId().equals(vertex.getId())){ //gets correct Vertex to check neighbors
                if(adjList.get(i).size() > 1){
                    for(int j = 1; j < adjList.get(i).size(); j++){
                        E string = adjList.get(i).get(j).getId();
                        Vertex<E> toAdd = getVertexByID((String)string);
                        list.add(toAdd);
                    }
                }
            }
        }
        return list;
    }

    /**
     * Method that implements transitive closure and keeps track of all
     * newly added edges. It also checks to make sure an already existing edge
     * is not contained in the newly added edge list.
     */
    public void transitiveClosure(){
        boolean[][] newMatrix = copyMatrix();
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                for(int k = 0; k < size; k++){
                    if((newMatrix[i][j] && newMatrix[k][i])){
                        newMatrix[k][j] = true;
                        String s = "" + getVertexByID(Integer.toString(k)).getId().toString() + "\t"
                                + getVertexByID(Integer.toString(i)).getId().toString();
                        Vertex<E> v1 = getVertexByID(Integer.toString(k));
                        Vertex<E> v2 = getVertexByID(Integer.toString(i));
                        boolean inList = false;
                        for(int h = 1; h < adjList.get(v1.getIndex()).size(); h++){
                            if(adjList.get(v1.getIndex()).get(h).getId().equals(v2.getId())){
                                inList = true;
                            }
                        }
                        if(!newEdges.contains(s) && !inList){
                            newEdges.add(s);
                        }
                    }
                }
            }
        }
    }

    /**
     * Helper method to copy the original matrix into a new matrix to be used
     * with transitive closure.
     *
     * @return A boolean[][] matrix that is a copy of the original matrix.
     */
    public boolean[][] copyMatrix(){
        boolean[][] newM = new boolean[size][size];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                newM[i][j] = this.adjMatrix[i][j];
            }
        }
        return newM;
    }

    /**
     * Helper method to return a boolean value true if a list contains a
     * specific vertex, false if not.
     *
     * @param list - A given LinkedList of vertices.
     * @param vertex - A given vertex to check is contained in the list or not.
     * @return True if a vertex is contained in the list, false if not.
     */
    public boolean hasVertex(LinkedList<Vertex<E>> list, Vertex<E> vertex){
        boolean check = false;
        for(Vertex<E> e : list){
            if(e.getId().equals(vertex.getId())){
                check = true;
            }
        }
        return check;
    }

    /**
     * Helper method to return all unvisited neighbors and ignoring neighbors that
     * have been either visited or in processing.
     *
     * @param vertex - A vertex to obtain all of its neighbors.
     * @return An ArrayList containing all unvisited neighbors.
     */
    public ArrayList<Vertex<E>> getAllNeighbors(Vertex<E> vertex){
        ArrayList<Vertex<E>> list = new ArrayList<>();
        for(int i = 0; i < adjList.size(); i++){
            if(adjList.get(i).getFirst().getId().equals(vertex.getId())){ //gets correct Vertex to check neighbors
                if(adjList.get(i).size() > 1){
                    for(int j = 1; j < adjList.get(i).size(); j++){
                        E string = adjList.get(i).get(j).getId();
                        Vertex<E> toAdd = getVertexByID((String)string);
                        if(toAdd.getState().equals("UNVISITED") &&
                                !toAdd.getState().equals("PROCESSING")){
                            list.add(toAdd);
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * Helper method to get a Vertex by a given string representing the id.
     *
     * @param s - Given string representing an id.
     * @return The vertex in which the id represents.
     */
    public Vertex<E> getVertexByID(String s){
        Vertex<E> toFind = null;
        for(int i = 0; i < adjList.size(); i++){
            if(adjList.get(i).getFirst().getId().equals(s)){
                toFind = adjList.get(i).getFirst();
            }
        }
        return toFind;
    }

    /**
     * Helper method to find an index of a specific based on a string representing
     * the id.
     *
     * @param s - Given string representing an id.
     * @return An integer representing the index of a vertex.
     */
    public int findIndex(String s){
        int index = -1;
        for(int i = 0; i < adjList.size(); i++){
            if(adjList.get(i).getFirst().getId().equals(s)){
                index = i;
            }
        }
        return index;
    }

    /**
     * Helper method that checks if a specific vertex is contained in the adjList.
     *
     * @param s - String representing the id of a vertex to check.
     * @return True if the vertex is contained in the adjList, false if not.
     */
    public boolean check(String s){
        boolean equal = false;
        for(int i = 0; i < adjList.size(); i++){
            if(adjList.get(i).getFirst().getId().equals(s)){
                equal = true;
            }
        }
        return equal;
    }

    /**
     * Helper method to reset all states of the vertices back to UNVISITED.
     */
    public void resetStates(){
        for(int i = 0; i < adjList.size(); i++){
            adjList.get(i).get(0).setState("UNVISITED");
        }
    }

    /**
     * Helper method to initialize all menu states to false.
     */
    public void initializeMenuValues(){ //set all menu selections to false
        menuOptions.put("1", false);
        menuOptions.put("2", false);
        menuOptions.put("3", false);
        menuOptions.put("4", false);
        menuOptions.put("5", false);
    }

    /**
     * Helper method to initialize all menu states to true.
     */
    public void replaceAllOptions(){ // set all menu selections to true
        menuOptions.replace("1", true);
        menuOptions.replace("2", true);
        menuOptions.replace("3", true);
        menuOptions.replace("4", true);
        menuOptions.replace("5", true);
    }

    /**
     * Helper method to print out our BFS results.
     */
    public void printBFS(){
        String output = "";
        System.out.print("[BFS Vertices Ordering: " + bfsResults.get(0).getId().toString() + "] ");
        for(Vertex <E> v : bfsResults){
            output += "Vertex " + v.getId() + ", ";
        }
        output = output.substring(0, output.length()-2);
        System.out.println(output + "\n");
    }

    /**
     * Helper method to print out our DFS results.
     */
    public void printDFS(){
        String ordering = "";
        String path = "";

        ordering += "[DFS Vertices Ordering: " + dfsResults.get(0).getId().toString() + ", "
                + dfsResults.get(dfsResults.size()-1).getId().toString() + "] ";
        for(Vertex<E> v : dfsResults){
            ordering += "Vertex " + v.getId().toString() + ", ";
        }
        ordering = ordering.substring(0, ordering.length()-2);

        path += "[DFS Vertices Path: " + dfsPath.get(0).getId().toString() + ", "
                + dfsPath.get(dfsPath.size()-1).getId().toString() + "] ";
        for(Vertex<E> v : dfsPath){
            path += "Vertex " + v.getId().toString() + " -> ";
        }
        path = path.substring(0, path.length()-4);

        System.out.println(ordering + "\n");
        System.out.println(path + "\n");

    }

    /**
     * Helper method to print our cycle detection results.
     */
    public void printCycleDetection(){
        System.out.print("[Cycle]: ");
        if(cycle){
            System.out.println("Cycle Detected\n");
        }
        else{
            System.out.println("Cycle NOT Detected\n");
        }
    }

    /**
     * Helper method to print our newly added edges results.
     */
    public void printNewEdges(){
        System.out.print("[TC: New Edges] " + newEdges.get(0) + "\n\t\t\t\t");
        for(int i = 1; i < newEdges.size(); i++){
            System.out.print(newEdges.get(i) + "\n\t\t\t\t");
        }
        System.out.println();
    }

    /**
     * Helper method to prompt the menu options.
     */
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

    /**
     * Method that will prompt a user to put the menu options and call the methods that
     * correspond with user given input.
     */
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
                    System.exit(1);
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
                    if(findSource(src2) && findDestination(dest2) && menuOptions.get("1")){
                        depthFirstSearch(src2, dest2);
                        cycleSearch(src2);
                        menuOptions.replace("2", true);
                    }
                    else{
                        System.out.println("Invalid source and/or destination vertex. Try again.");
                    }
                    break;
                case "3":
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
                        cycleSearch(src4);
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
                                    if(menuOptions.get("2")){ //makes sure both results aren't printed
                                        break;
                                    }
                                    printDFS();
                                    break;
                                case "2":
                                    printDFS();
                                    printCycleDetection();
                                    break;
                                case "3":
                                    printCycleDetection();
                                    break;
                                case "4":
                                    printBFS();
                                    break;
                                case "5":
                                    printNewEdges();
                                    break;
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

    /**
     * Entry point into the program. It builds our list and matrix and prompts the user
     * for the options until they quit.
     */
    public void go(){
        System.out.println("Welcome to GraphAnalyzermeister 2023 >>");
        buildList();
        prompt();
    }
}