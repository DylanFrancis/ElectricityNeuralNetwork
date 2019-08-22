package neuralnetwork.neurons;

public class SigmoidNeuron extends ANeuron {
    @Override
    public double fire(double... inputs) {
        return 1.0 / (1.0 + Math.exp(-1.0 * sum(inputs)));
    }
}
