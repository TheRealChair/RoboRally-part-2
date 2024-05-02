package dk.dtu.compute.se.pisd.roborally.model;

public class Pits {

    private int x;
    private int y;


    public static void addPit(int x, int y){
        Board.pits[x][y] = true;
        Board.spaces[x][y].setPit(true);
    }


}
