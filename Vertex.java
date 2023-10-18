public class Vertex<E>{

    private E id;
    private String state;
    private int index;

    public Vertex(){
        this.id = null;
        this.state = "";
        this.index = 0;
    }

    public Vertex(E id, int index, String state){
        this.id = id;
        this.index = index;
        this.state = state;
    }

    public E getId() {
        return id;
    }

    public void setId(E id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}