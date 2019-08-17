package neuralnetwork.read;

public class Pair {
    private int param;
    private double weight;

    public Pair(int param, double weight) {
        this.param = param;
        this.weight = weight;
    }

    public int getParam() {
        return param;
    }

    public void setParam(int param) {
        this.param = param;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void subtractWeight(double x){
        weight -= x;
    }

    public void addWeight(double x){
        weight += x;
    }
}
