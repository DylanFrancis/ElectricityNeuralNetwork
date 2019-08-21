package neuralnetwork.network;

import neuralnetwork.neurons.*;
import neuralnetwork.read.*;
import neuralnetwork.weights.CatWeight;
import neuralnetwork.weights.IWeight;
import neuralnetwork.weights.NormalWeight;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class Network {
    private DataSet dataSet;
    private int[][] inputCount;

    public static void main(String[] args) {
        new Network().setup();
    }

    protected void setup(){
        try {
            resultWriter = new FileWriter("./data/results.txt", true);
            weightWriter = new FileWriter("./data/weights.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {

            for (int t = 0; t < 3; t++) {
                double learningRate;

                for (int y = 1; y <= 1000000000; y *= 10) {
                    learningRate = 0.1 / 100000000000000.0;
                    learningRate *= y;

                    printRate(learningRate);

                    System.out.println(learningRate);

                    for (int x = 1; x <= 200; x += 1) {
                        Data data = new Data();
                        dataSet = data.getDataSet("hourlydata.csv", 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27);
                        double a = Math.abs(run(x, learningRate));

                        printResults(a, x);
                    }

                    weightWriter.flush();
                    resultWriter.flush();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private FileWriter resultWriter;
    private FileWriter weightWriter;

    private void printRate(double rate){
        try {
            resultWriter.write("========================================================================================= \n");
            resultWriter.write(rate + "\n");
            weightWriter.write("========================================================================================= \n");
            weightWriter.write(rate + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printResults(double avg, double iterations){
        try {
            resultWriter.write(avg + "\n");
//            resultWriter.write(iterations + "\n");

            weightWriter.write(iterations + "\n");
            weightWriter.write("hidden weights \n");
            weightsHidden.forEach(iWeight -> {
                try {
                    weightWriter.write(iWeight.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            weightWriter.write("output weights \n");
            for (int i = 0; i < oNeurons; i++) {
                for (int y = 0; y < yNeurons; y++){
                    weightWriter.write(weightsOutput[i][y].toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final int oNeurons = 24;
    private final int yNeurons = 4;
    private final int zInputs = 30;
    private double LEARNING_RATE = 0.00000000001;
    private int iterations = 20;

    private ArrayList<IWeight> weightsHidden;
    private IWeight[][] weightsOutput;  // outputNeuron -- outputWeight
    private OutputNeuron outputNeuron;

    private ANeuron yearNeuron;
    private MonthNeuron monthNeuron;
    private DayNeuron dayNeuron;
    private MDayNeuron mDayNeuron;
    /**
     * Controls all testing
     * Calls doTest to fire for an input pattern
     */
    private double run(int iterations, double learningRate){
        outputNeuron = new OutputNeuron();

        this.LEARNING_RATE = learningRate;

        initialiseWeights(1, 3);


        yearNeuron = new YearNeuron();
        monthNeuron = new MonthNeuron();
        dayNeuron = new DayNeuron();
        mDayNeuron = new MDayNeuron();

        int i = 0;

        while (i < iterations){
            Iterator<InputPattern> trainingIterator = dataSet.trainingIterator();
            while (trainingIterator.hasNext()){
                InputPattern cur = trainingIterator.next();
                doTest(cur, outputNeuron);
            }

            //TODO: validation iterations
//            average = 0;
////            display();
//            Iterator<InputPattern> validationIterator = dataSet.validationIterator();
//            while (validationIterator.hasNext()){
//                InputPattern cur = validationIterator.next();
//                average += validate(cur);
//            }
//            average = average / dataSet.getValidation().size();
//            System.out.println(average / dataSet.getValidation().size());
            i++;

        }

        double average = 0;
        Iterator<InputPattern> testIterator = dataSet.testIterator();
        while (testIterator.hasNext()){
            InputPattern cur = testIterator.next();
            average += validate(cur);
        }
        average /= dataSet.getTest().size();
        return average;
    }

    private void display(){
        System.out.println();
        for (int i = 0; i < weightsHidden.size(); i++) {
            System.out.println("Weight " + i);
            weightsHidden.get(i).display();
        }
    }

    /**
     * TODO: calculate and return SSE
     * Validation of an weights for an input pattern
     * @param cur input pattern being validated
     * @return average difference of outputs for cur for each output neuron
     */
    private double validate(InputPattern cur){
        double[] hiddenResults = hiddenLayer(cur);
        double[] outputResults = outputLayer(hiddenResults);

        double[] differences = new double[oNeurons];
        double total = 0;
        for (int x = 0; x < oNeurons; x++){
            differences[x] = cur.getOutput(x) - outputResults[x];
//            System.out.println(x +". " + differences[x]);
            total += differences[x];
        }
//        System.out.println("Average: " + total / oNeurons);
        return total / oNeurons;
    }

    /**
     * Fires for one input pattern
     * @param cur input pattern
     * @param others other input patterns to be added as inputs
     */
    private InputPattern doTest(InputPattern cur, OutputNeuron outputNeuron, InputPattern... others){

        double[] hiddenResults = hiddenLayer(cur);
        double[] outputResults = outputLayer(hiddenResults);

        adjustOutputWeights(outputResults, hiddenResults, cur);
        adjustHiddenWeights(outputResults, hiddenResults, cur);

        return cur;
    }

    private void adjustHiddenWeights(double[] outputResults, double[] hiddenResults, InputPattern cur){
        assert yNeurons == hiddenResults.length;

        for (int k = 0; k < oNeurons; k++){
            double t = cur.getOutput(k);
            double o = outputResults[k];
            double a = -1.0 * (t - o) * o * (1 - o);

            for (int j = 0; j < yNeurons; j++){
                double w = weightsOutput[k][j].getWeight();
                double y = hiddenResults[j];
                double z = cur.getInput(j);

                double b = a * w;

                double change = b * (1.0 - y) * z;
                change = LEARNING_RATE * change;

                IWeight weight = weightsHidden.get(j);

                change = weight.getWeight((int) z - 1) - change;

                assert !Double.isNaN(change);
                assert !Double.isInfinite(change);

                weight.setWeight((int) z - 1, change);
            }
        }
    }



    private void adjustOutputWeights(double[] outputResults, double[] hiddenResults, InputPattern cur){
        assert oNeurons == outputResults.length;

        for (int i = 0; i < oNeurons; i++) {
            for (int x = 0; x < yNeurons; x++){
                double curWeight = weightsOutput[i][x].getWeight();
                double t = cur.getOutput(i);
                double o = outputResults[i];
                double y = hiddenResults[x];

                double changeInWeight = doAdjustOutputWeight(t, o, y);

                assert !Double.isNaN(changeInWeight);
                assert !Double.isInfinite(changeInWeight);

                double newWeight = curWeight - LEARNING_RATE * changeInWeight;

                assert !Double.isNaN(newWeight);
                assert !Double.isInfinite(newWeight);

                weightsOutput[i][x].setWeight(-1, newWeight);
            }
        }


    }

    private double doAdjustOutputWeight(double t, double o, double y){
        return -1.0 * (t - o) * o * (1.0 - o) * y;
    }

    private double[] hiddenLayer(InputPattern cur){
        // result (y) -- input (z)
        double[] hiddenResults = new double[yNeurons];

        assert yNeurons == cur.getSize();

        for (int x = 0; x < cur.getSize(); x++){

            double z = cur.getInput(x);
            Double v = null;
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
        switch (neuron){
            case 0:
                return yearNeuron.fire(inputs);
            case 1:
                return monthNeuron.fire(inputs);
            case 2:
                return dayNeuron.fire(inputs);
            case 3:
                return mDayNeuron.fire(inputs);
        }
        System.out.println("missing neuron");
        return 0;
    }

    private double[] outputLayer(double... inputs){
        double[] outputResults = new double[oNeurons];

        for (int x = 0; x < oNeurons; x++){

            double total = 0;

            for (int i = 0; i < inputs.length; i++) {
                total += weightsOutput[x][i].getWeight() + inputs[i];
            }

            outputResults[x] = outputNeuron.fire(total);
        }

        return outputResults;
    }

    private void initialiseWeights(int... cat){
        weightsHidden = new ArrayList<>();

        inputCount = dataSet.getInputCount(cat);

        InputPattern inputPattern = dataSet.getTraining().getFirst();

        for (int x = 0; x < inputPattern.getSize(); x++) {
            if (setCategorical(x, inputCount, cat))
                continue;
            // set as is
            weightsHidden.add(new NormalWeight(getRandom()));
        }

        weightsOutput = new IWeight[oNeurons][yNeurons];

        for (int x = 0; x < oNeurons; x++){
            for (int y = 0; y < yNeurons; y++){
                weightsOutput[x][y] = new NormalWeight(getRandom());
            }
        }
    }

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

    private double getRandom(){
        return ThreadLocalRandom.current().nextDouble();
    }
}
