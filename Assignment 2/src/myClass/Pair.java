package myClass;

public class Pair<String,Integer> {
    private String l;
    private Integer r;
    public Pair(String l, Integer r){
        this.l = l;
        this.r = r;
    }
    public String getL(){ return l; }
    public Integer getR(){ return r; }
    public void setL(String l){ this.l = l; }
    public void setR(Integer r){ this.r = r; }
}