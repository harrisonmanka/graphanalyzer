import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class GraphAnalyzer<E> {

    private ArrayList<LinkedList<Vertex<E>>> adjList;
    private boolean[][] adjMatrix;
    private boolean cycle;
    private String file;
    private int count;
    private int size;
    private ArrayList<Vertex<E>> bfsResults;
    private ArrayList<Vertex<E>> dfsResults;
    private ArrayList<Vertex<E>> dfsPath;
    private Map<String, Boolean> menuOptions;
    private ArrayList<String> newEdges;

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
        size = adjMatrix.length;
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
            throw new IllegalArgumentException("Source not found within the graph.");
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
            throw new IllegalArgumentException("Destination not found within the graph.");
        }
        return result;
    }

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

    public boolean[][] copyMatrix(){
        boolean[][] newM = new boolean[size][size];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                newM[i][j] = this.adjMatrix[i][j];
            }
        }
        return newM;
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

    public Vertex<E> getVertexByID(String s){
        Vertex<E> toFind = null;
        for(int i = 0; i < adjList.size(); i++){
            if(adjList.get(i).getFirst().getId().equals(s)){
                toFind = adjList.get(i).getFirst();
            }
        }
        return toFind;
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

    public void resetStates(){
        for(int i = 0; i < adjList.size(); i++){
            adjList.get(i).get(0).setState("UNVISITED");
        }
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

    public void printBFS(){
        String output = "";
        System.out.print("[BFS Vertices Ordering: " + bfsResults.get(0).getId().toString() + "] ");
        for(Vertex <E> v : bfsResults){
            output += "Vertex " + v.getId() + ", ";
        }
        output = output.substring(0, output.length()-2);
        System.out.println(output + "\n");
    }

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

    public void printCycleDetection(){
        System.out.print("[Cycle]: ");
        if(cycle){
            System.out.println("Cycle Detected\n");
        }
        else{
            System.out.println("Cycle NOT Detected\n");
        }
    }

    public void printNewEdges(){
        System.out.print("[TC: New Edges] " + newEdges.get(0) + "\n\t\t\t\t");
        for(int i = 1; i < newEdges.size(); i++){
            System.out.print(newEdges.get(i) + "\n\t\t\t\t");
        }
        System.out.println();
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

    public void go(){
        System.out.println("Welcome to GraphAnalyzermeister 2023 >>");
        buildList();
        prompt();
    }
}