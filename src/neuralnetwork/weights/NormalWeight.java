package neuralnetwork.weights;

public class NormalWeight extends AWeight{
    private double weight;
    private double prevWeight = 1;

    public NormalWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public void setPrev(double prev, int... idx) {
        prevWeight = prev;
    }

    @Override
    public double getPrev(int... idx) {
        return prevWeight;
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
        weight += (sum + momentum * prevWeight);
        setPrev(sum + momentum * prevWeight);
    }

    public void subWeight(double sub, int... idx) {
        weight += (sub + momentum * prevWeight);
        setPrev(sub + momentum * prevWeight);
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
