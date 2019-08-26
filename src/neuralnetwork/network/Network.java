package neuralnetwork.network;

import neuralnetwork.neurons.*;
import neuralnetwork.read.*;
import neuralnetwork.weights.CatWeight;
import neuralnetwork.weights.IWeight;
import neuralnetwork.weights.NormalWeight;
import neuralnetwork.write.Writer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.ToDoubleFunction;

public class Network {
    public static void main(String[] args) {
//        new Thread(run(0.00000000000000001)).start();
//        new Thread(run(0.00000000000001)).start();
//        new Thread(run(0.0000000000001)).start();
//        new Thread(run(0.000000000001)).start();
        new Thread(run(0.00000000001)).start();
        new Thread(run(0.0000000001)).start();
        new Thread(run(0.000000001)).start();
        new Thread(run(0.00000001)).start();
        new Thread(run(0.0000001)).start();
//        new Thread(run(0.000001)).start();
//        new Thread(run(0.00001)).start();
    }

    private static Runnable run(double l){
        return () -> {
//            for (int iterations = 1; iterations <= 100; iterations++){
//                for(int epochs = 5; epochs <= 10; epochs++){
//                    new Network(l).run(epochs, iterations, l).test(iterations, epochs);
//                }
//            }
            new Network(l).run(5000, 2, l).test(5000, 2);
        };
    }

    private DataSet dataSet;
    private int[][] inputCount;
    private Writer writer;

    public Network() {
        try {
            Data data = new Data();
            dataSet = data.getDataSet("hourlydata.csv", 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        outputNeurons = new ArrayList<>();
        for (int i = 0; i < oNeurons; i++) outputNeurons.add(new OutputNeuron());
        hiddenNeurons = new ArrayList<>();
        for (int i = 0; i < yNeurons; i++) hiddenNeurons.add(new SigmoidNeuron());
    }

    public Network(double LEARNING_RATE) {
        this.LEARNING_RATE = LEARNING_RATE;

        try {
            Data data = new Data();
            dataSet = data.getDataSet("HourlyData.csv", 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27);
            writer = new Writer(oNeurons, yNeurons, this.LEARNING_RATE + "--" + ThreadLocalRandom.current().nextDouble(20));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        outputNeurons = new ArrayList<>();
        for (int i = 0; i < oNeurons; i++) outputNeurons.add(new OutputNeuron());
        hiddenNeurons = new ArrayList<>();
        for (int i = 0; i < yNeurons; i++) hiddenNeurons.add(new SigmoidNeuron());
    }

    private final int oNeurons      = 24;
    private final int yNeurons      = 5;
    private double LEARNING_RATE;

    private ArrayList<IWeight> weightsHidden;
    private ArrayList<OutputNeuron> outputNeurons;
    private ArrayList<ANeuron> hiddenNeurons;
    private IWeight[][] weightsOutput;  // outputNeuron -- outputWeight

    /**
     * Controls all testing
     * Calls doTest to fire for an input pattern
     */
    private Network run(int epochs, int iterations, double learningRate){


        this.LEARNING_RATE = learningRate;
        writer.printRate(LEARNING_RATE);

        initialiseWeights(1, 3);


        double valErrorAvg  = 0.0;
        double valStd       = 0.0;
        ArrayList<Double> valErrors = new ArrayList<>();
        int e = 0;
        while (e < epochs){
            int i = 0;
            while (i < iterations) {
                Iterator<InputPattern> trainingIterator = dataSet.trainingIterator();
                double error = 0.0;
                while (trainingIterator.hasNext()) {
                    InputPattern cur = trainingIterator.next();
                    error += doTest(cur);
                }
                writer.printTrainingIterationResults(LEARNING_RATE, error, i, e, dataSet.getTraining().size());
                i++;

            }

            Iterator<InputPattern> validationIterator = dataSet.validationIterator();
            double valError = 0.0;
            while (validationIterator.hasNext()){
                InputPattern cur = validationIterator.next();
                double[] results = validate(cur);
                valError += results[1];
            }

            valErrors.add(valError);
            valErrorAvg = valErrors.stream().mapToDouble(value -> value).sum() / valErrors.size();
            final double vg = valErrorAvg;
            valStd = valErrors.stream().mapToDouble(value -> Math.pow(value - vg, 2)).sum() / valErrors.size();

            if (valError > valErrorAvg + valStd){
                return this;
            }
            e++;
        }
        return this;
    }

    private double test(double epochs, double iterations){
        double average = 0.0;
        double error = 0.0;
        Iterator<InputPattern> testIterator = dataSet.testIterator();
        while (testIterator.hasNext()){
            InputPattern cur = testIterator.next();
            double[] results = validate(cur);
            average += results[0];
            error += results[1];
        }
        average /= dataSet.getTest().size();

        writer.printTestIterationResults(LEARNING_RATE, error, dataSet.getTestSize(), average);

        writer.printWeights(iterations, epochs, weightsHidden, weightsOutput, outputNeurons);
        writer.flush();

        return average;
    }

    /**
     * TODO: calculate and return SSE
     * Validation of weights for an input pattern
     * @param cur input pattern being validated
     * @return average difference of outputs for cur for each output neuron
     */
    private double[] validate(InputPattern cur){
        double[] hiddenResults = fireHiddenLayer(cur);
        double[] outputResults = fireOutputLayer(hiddenResults);

        double[] differences = new double[oNeurons];
        double total = 0.0;
        double error;
        error = SSE(cur.getOutputs(), outputResults);
        for (int x = 0; x < oNeurons; x++){
            differences[x] = cur.getOutput(x) - outputResults[x];
            total += differences[x];
        }
        writer.printTestResults(cur.getOutputs(), outputResults, differences);
        return new double[] {total / oNeurons, error};
    }

    /**
     * Fires for one input pattern
     * @param cur input pattern
     */
    private double doTest(InputPattern cur){

        double[] hiddenResults = fireHiddenLayer(cur);
        double[] outputResults = fireOutputLayer(hiddenResults);

        double sse = SSE(cur.getOutputs(), outputResults);

        adjustOutputWeights(outputResults, hiddenResults, cur);
        adjustHiddenWeights(outputResults, hiddenResults, cur);

        return sse;
    }

    private void adjustHiddenWeights(double[] outputResults, double[] hiddenResults, InputPattern cur){
        for (int k = 0; k < oNeurons; k++){
            double t = cur.getOutput(k);
            double o = outputResults[k];
            double a = (t - o) * o * (1 - o);

            for (int j = 0; j < hiddenResults.length; j++){
                double w = weightsOutput[k][j].getWeight();
                double y = hiddenResults[j];
                double z = cur.getInput(j);

                double b = a * w;

                double change = b * (1.0 - y) * z;
                change = LEARNING_RATE * change;

                IWeight weight = weightsHidden.get(j);
                weight.subWeight(change, (int) z - 1);
            }
        }
    }



    private void adjustOutputWeights(double[] outputResults, double[] hiddenResults, InputPattern cur){
        assert oNeurons == outputResults.length;
        for (int i = 0; i < oNeurons; i++) {
            double t = cur.getOutput(i);
            double o = outputResults[i];
            for (int x = 0; x < cur.getSize(); x++){
                double y = hiddenResults[x];
                double changeInWeight = LEARNING_RATE * ((t - o) * y);
                weightsOutput[i][x].subWeight(changeInWeight);
            }
            outputNeurons.get(i).subBiasWeight(LEARNING_RATE * (t - o));
        }
    }

    public double[] fireHiddenLayer(InputPattern cur){
        // result (y) -- input (z)
        double[] hiddenResults = new double[cur.getSize()];
        for (int x = 0; x < cur.getSize(); x++){
            double z = cur.getInput(x);
            double v;
            IWeight weight = weightsHidden.get(x);

            if (weight instanceof NormalWeight){
                v = weight.getWeight();
                hiddenResults[x] = fireHiddenNeuron(x, v * z);
                continue;
            }

            if (weight instanceof CatWeight){
                z--;
                v = weight.getWeight((int) z);
                hiddenResults[x] = fireHiddenNeuron(x, v);
                continue;
            }

        }

        return hiddenResults;
    }

    private double fireHiddenNeuron(int neuron, double... inputs) {
        return hiddenNeurons.get(neuron).fire(inputs);
    }

    public double[] fireOutputLayer(double... inputs){
        double[] outputResults = new double[oNeurons];
        for (int x = 0; x < oNeurons; x++){
            double total = 0;
            for (int i = 0; i < inputs.length; i++) total += weightsOutput[x][i].getWeight() + inputs[i];
            outputResults[x] = outputNeurons.get(x).fire(total);
        }
        return outputResults;
    }

    /**
     * For initialising random weights
     * @param cat
     */
    private void initialiseWeights(int... cat){
        weightsHidden = new ArrayList<>();
        inputCount = dataSet.getInputCount(cat);
        for (int x = 0; x < yNeurons; x++) {
            if (setCategorical(x, inputCount, cat))
                continue;
            // set as is
            weightsHidden.add(new NormalWeight(getRandom()));
        }
        weightsOutput = new IWeight[oNeurons][yNeurons];

        for (int x = 0; x < oNeurons; x++)
            for (int y = 0; y < yNeurons; y++) weightsOutput[x][y] = new NormalWeight(getRandom());
    }

    @SuppressWarnings("Duplicates")
    private boolean setCategorical(int curPatternIdx, int[][] inputCount, int... catIdx){
        for (int i = 0; i < catIdx.length; i++) {
            // set categorical
            if (curPatternIdx == catIdx[i]){
                int categories = -1;
                for (int y = 0; y < inputCount.length; y++){
                    if (inputCount[y][0] != curPatternIdx) continue;
                    categories = inputCount[y][1];
                    break;
                }
                CatWeight weight = new CatWeight(categories);

                for(int z = 0; z < categories; z++){
                    weight.setWeight(z, getRandom());
                }

                weightsHidden.add(weight);
                return true;
            }
        }
        return false;
    }

    /**
     * For testing specific weights
     * @param hWeights
     * @param oWeights
     * @param cat
     */
    public void initialiseWeight(ArrayList<Double> hWeights, double[][] oWeights, int... cat){
        weightsHidden = new ArrayList<>();
        inputCount = dataSet.getInputCount(cat);
        int ins = 0;
        for (int x = 0; x < hWeights.size(); x++) {
            int t = setCategorical(hWeights, x, ins, inputCount, cat);
            if (t != -1) {
                x = t - 1;
                ins++;
                continue;
            }
            // set as is
            weightsHidden.add(new NormalWeight(hWeights.get(x)));
            ins++;
        }

        weightsOutput = new IWeight[oNeurons][yNeurons];

        for (int w = 0; w < oNeurons; w++){
            for (int y = 0; y < yNeurons; y++){
                weightsOutput[w][y] = new NormalWeight(oWeights[w][y]);
            }
        }
    }

    @SuppressWarnings("Duplicates")
    private int setCategorical(ArrayList<Double> hWeights, int curPatternIdx, int ins, int[][] inputCount, int... catIdx){
        for (int i = 0; i < catIdx.length; i++) {
            // set categorical
            if (ins == catIdx[i]){
                int categories = -1;
                for (int y = 0; y < inputCount.length; y++){
                    if (inputCount[y][0] != ins) continue;
                    categories = inputCount[y][1];
                    break;
                }
                CatWeight weight = new CatWeight(categories);

                categories += (curPatternIdx - 1);
                int p = 0;
                for(; curPatternIdx <= categories; curPatternIdx++){
                    weight.setWeight(p, hWeights.get(curPatternIdx));
                    p++;
                }

                weightsHidden.add(weight);
                return curPatternIdx;
            }
        }
        return -1;
    }


    private double getRandom(){
        return ThreadLocalRandom.current().nextDouble(-1, 1);
    }

    public int getoNeurons() {
        return oNeurons;
    }

    public int getyNeurons() {
        return yNeurons;
    }

    public ArrayList<OutputNeuron> getOutputNeurons() {
        return outputNeurons;
    }

    private double SSE(Double[] target, double[] actual){
        assert target.length == actual.length;

        double total = 0.0;
        for (int x = 0; x < target.length; x++){
            total += Math.pow(target[x] - actual[x], 2);
        }

        return 0.5 * total;
    }
}
