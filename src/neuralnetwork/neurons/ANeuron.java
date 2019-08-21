package neuralnetwork.neurons;

import java.util.concurrent.ThreadLocalRandom;

public abstract class ANeuron implements INeuron{
    protected double bias = -1;
    protected double biasWeight;

    public ANeuron() {
        biasWeight = ThreadLocalRandom.current().nextDouble();
    }

    @Override
    public double fire(double... inputs) {
        return sum(inputs);
    }

    public void sumBiasWeight(double biasChange){
        biasWeight += biasChange;
    }

    public void subBiasWeight(double biasChange){
        biasWeight -= biasChange;
    }

    protected double sum(double...s){
        double sum = 0;
        for (int i = 0; i < s.length; i++) {
            sum += s[i];
        }
        return sum + (bias * biasWeight);
    }
}
