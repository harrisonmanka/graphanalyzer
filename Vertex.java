/**
 * Generic class representing a Vertex with fields for id, state, and index.
 *
 * @param <E> - our generic term
 */
public class Vertex<E>{

    /**
     * A generic representing the id of a vertex.
     */
    private E id;

    /**
     * A string representing the state of a vertex.
     */
    private String state;

    /**
     * An integer representing the inedx of a vertex.
     */
    private int index;

    /**
     * Creates a new Vertex with the given id and state.
     * Sets index to -1 if given no index.
     *
     * @param id - Generic representing an id.
     * @param state - String representing the state.
     */
    public Vertex(E id, String state){
        this.id = id;
        this.state = state;
        this.index = -1;
    }

    /**
     * Creates a new Vertex with the given id, index, and state.
     *
     * @param id - Generic representing an id.
     * @param index - Integer representing the index.
     * @param state - String representing the state.
     */
    public Vertex(E id, int index, String state){
        this.id = id;
        this.index = index;
        this.state = state;
    }

    /**
     * Getter method to return the id of a vertex.
     *
     * @return - Generic representing the id.
     */
    public E getId() {
        return id;
    }

    /**
     * Getter method to return the state of a vertex.
     *
     * @return - String representing the state.
     */
    public String getState() {
        return state;
    }

    /**
     * Setter method to set the state of a vertex.
     *
     * @param state - String representing the new state.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Getter method to get the index of a vertex.
     *
     * @return - Integer representing the index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Setter method to set the index of a vertex.
     *
     * @param index - Integer representing the new index.
     */
    public void setIndex(int index) {
        this.index = index;
    }
}