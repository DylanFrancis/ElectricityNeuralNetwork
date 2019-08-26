package neuralnetwork.weights;

public class CatWeight extends AWeight{
    private double[] weights;
    private double[] prevUpdate;

    public CatWeight(int size) {
        weights = new double[size];
        prevUpdate = new double[size];
        for (int x = 0; x < prevUpdate.length; x++){
            prevUpdate[x] = 1;
        }
    }

    public void setWeight(int idx, double newWeight){
        weights[idx] = newWeight;
    }

    @Override
    public void setPrev(double prev, int... idx) {
        prevUpdate[idx[0]] = prev;
    }

    @Override
    public double getPrev(int... idx) {
        return prevUpdate[idx[0]];
    }

    @Override
    public double getWeight(int... idx) {
        return weights[idx[0]];
    }

    @Override
    public void sumWeight(double sum, int... idx) {
//        weights[idx[0]] += (sum + momentum * prevUpdate[idx[0]]);
//        setPrev(sum + momentum * prevUpdate[idx[0]], idx);

        weights[idx[0]] += sum;
    }

    @Override
    public void subWeight(double sub, int... idx) {
//        setPrev(weights[idx[0]], idx);
//        weights[idx[0]] -= (sub + momentum * prevUpdate[idx[0]]);
//        setPrev(sub + momentum * prevUpdate[idx[0]], idx);


        weights[idx[0]] += sub;
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
