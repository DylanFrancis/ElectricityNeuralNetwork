package neuralnetwork.weights;

public class NormalWeight implements IWeight {
    private double weight;

    public NormalWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public double getWeight(int... idx) {
        return weight;
    }

    @Override
    public void setWeight(int idx, double newWeight) {
        weight = newWeight;
    }

    @Override
    public void sumWeight(double sum, int... idx) {
        weight += sum;
    }

    public void subWeight(double sub, int... idx) {
        weight += sub;
    }

    @Override
    public void display() {
        System.out.println("    " + weight);
    }

    @Override
    public String toString() {
        return weight + "\n";
    }
}
