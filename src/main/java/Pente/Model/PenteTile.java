package Pente.Model;

public class PenteTile {


    String colorTile; // F/H/C for Free/Human/Computer
    boolean freeSpace; // default to true
    int id; //each tile needs an ID for when the human clicks on it.

    public PenteTile(int id){
        this.id = id;
        colorTile = "F";
        freeSpace = true;
    }

    public String getColorTile() {
        return colorTile;
    }

    public void setColorTile(String colorTile) {
        this.colorTile = colorTile;
    }

    public boolean isFreeSpace() {
        return freeSpace;
    }

    public void setFreeSpace(boolean freeSpace) {
        this.freeSpace = freeSpace;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
