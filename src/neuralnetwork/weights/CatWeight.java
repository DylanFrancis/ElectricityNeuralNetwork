package neuralnetwork.weights;

public class CatWeight implements IWeight {
    private double[] weights;

    public CatWeight(int size) {
        weights = new double[size];
    }

    public void setWeight(int idx, double newWeight){
        weights[idx] = newWeight;
    }

    @Override
    public double getWeight(int... idx) {
        return weights[idx[0]];
    }

    @Override
    public void sumWeight(double sum, int... idx) {
        weights[idx[0]] += sum;
    }

    public void subWeight(double sub, int... idx) {
        weights[idx[0]] -= sub;
    }

    @Override
    public void display() {
        for (int i = 0; i < weights.length; i++) {
            System.out.println("    " + weights[i]);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (double weight : weights) {
            stringBuilder.append(weight).append("\n");
        }
        return stringBuilder.toString();
    }
}
