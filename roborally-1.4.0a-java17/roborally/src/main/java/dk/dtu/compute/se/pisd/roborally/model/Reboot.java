package dk.dtu.compute.se.pisd.roborally.model;

public class Reboot {
    private static int x = 7;
    private static int y = 0;



    public Reboot(Board board, int x, int y){
        Reboot.x = x;
        Reboot.y = y;
    }
    public void setReboot(int x, int y){
        Reboot.x = x;
        Reboot.y = y;
    }


    public static int getX(){
        return x;
    }

    public static int getY(){
        return y;
    }



}
