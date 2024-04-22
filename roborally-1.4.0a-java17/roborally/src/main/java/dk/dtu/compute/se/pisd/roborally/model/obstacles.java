package dk.dtu.compute.se.pisd.roborally.model;

public class obstacles {

    public int pitId;

    public void pit(int pitId){
        this.pitId = pitId;
    }

    public void setPitId(int pitId){
        this.pitId = pitId;
    }

    public int getPitId(){
        return pitId;
    }
}
