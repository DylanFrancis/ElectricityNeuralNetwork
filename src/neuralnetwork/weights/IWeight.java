package neuralnetwork.weights;

public interface IWeight {
    void setPrev(double prev, int... idx);
    double getPrev(int... idx);
    double getWeight(int... idx);
    void setWeight(int idx, double newWeight);
    void sumWeight(double sum, int... idx);
    void subWeight(double sub, int... idx);
    void display();
}
