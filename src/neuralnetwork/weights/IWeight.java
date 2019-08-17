package neuralnetwork.weights;

public interface IWeight {
    double getWeight(int... idx);
    void setWeight(int idx, double newWeight);
    void sumWeight(double sum, int... idx);
}
