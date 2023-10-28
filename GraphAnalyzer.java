import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class GraphAnalyzer<E> {

    private ArrayList<LinkedList<Vertex<E>>> adjList;
    private int[][] adjMatrix;
    private boolean cycle;
    private String file;
    private int count;
    private ArrayList<Vertex<E>> bfsResults;
    private ArrayList<Vertex<E>> dfsResults;

    public GraphAnalyzer(String file){
        this.file = file;
        this.adjList = new ArrayList<LinkedList<Vertex<E>>>();
        this.adjMatrix = null;
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
        printVertices();
        sortAdjList();
        buildMatrix();
        System.out.println();
        //printMatrix();
    }

    public void sortAdjList(){

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
        adjMatrix = new int[count][count];
        for(int i = 0; i < count; i++){
            for(int j = 0; j < adjList.get(i).size(); j++){
                if(i == j){
                    adjMatrix[i][j] = 0;
                }
                else if(adjList.get(i).contains(adjList.get(i).get(j))){
                    adjMatrix[i][j] = 1;
                }
                else{
                    adjMatrix[i][j] = 0;
                }
            }
        }
    }

    public void printMatrix(){
        for(int i = 0; i < adjMatrix.length; i++){
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
