public class Vertex<E>{

    private E id;
    private String state;

    public Vertex(){
        this.id = null;
        this.state = "";
    }

    public Vertex(E id, String state){
        this.id = id;
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