package neuralnetwork.neurons;

public abstract class ANeuron implements INeuron{

    protected double sum(double...s){
        double sum = 0;
        for (int i = 0; i < s.length; i++) {
            sum += s[i];
        }
        return sum;
    }
}
