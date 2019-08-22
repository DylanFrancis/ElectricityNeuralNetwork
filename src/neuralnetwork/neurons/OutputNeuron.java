package neuralnetwork.neurons;

public class OutputNeuron extends ANeuron{
    @Override
    public double fire(double... inputs) {
        return sum(inputs);
    }
}
