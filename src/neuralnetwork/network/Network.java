package neuralnetwork.network;

import neuralnetwork.neurons.*;
import neuralnetwork.read.*;
import neuralnetwork.weights.CatWeight;
import neuralnetwork.weights.IWeight;
import neuralnetwork.weights.NormalWeight;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class Network {
    private ArrayList<INeuron> hiddenLayer;
    private ArrayList<INeuron> outputLayer;
    private DataSet dataSet;
    private int[] z;
    private int[] cat;
    private int[][] inputCount;



    public static void main(String[] args) {
        new Network().setup();
    }

    protected void setup(){
        try {
            Data data = new Data();
            dataSet = data.getDataSet("hourlydata.csv", 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27);



            run();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private final int oNeurons = 24;
    private final int yNeurons = 24;
//    private ArrayList<IWeight> weightsOutput;
    private ArrayList<IWeight> weightsHidden;
    private IWeight[][] weightsOutput;
    private OutputNeuron outputNeuron;

    private ANeuron yearNeuron;
    private MonthNeuron monthNeuron;
    private DayNeuron dayNeuron;

    /**
     * Controls all testing
     * Calls doTest to fire for an input pattern
     */
    private void run(){
        outputNeuron = new OutputNeuron();



        initialiseWeights(1, 3);


        yearNeuron = new ANeuron() {
            @Override
            public double fire(double... inputs) {
                return 0;
            }
        };
        monthNeuron = new MonthNeuron();
        dayNeuron = new DayNeuron();

        while (true){
            Iterator<InputPattern> trainingIterator = dataSet.trainingIterator();
            while (trainingIterator.hasNext()){
                InputPattern cur = trainingIterator.next();
                doTest(cur, outputNeuron);
            }
        }
    }

    /**
     * Fires for one input pattern
     * @param cur
     * @param others
     */
    private InputPattern doTest(InputPattern cur, OutputNeuron outputNeuron, InputPattern... others){




        return cur;
    }

    private void hiddenLayer(){
        double y1 = yearNeuron.fire();
        double y2 = monthNeuron.fire();
        double y3 = dayNeuron.fire();
    }

    private void outputLayer(double... inputs){
        for (int x = 0; x < oNeurons; x++){
            double total = 0;
            for (int i = 0; i < inputs.length; i++) {
                total += weightsOutput[x][i].getWeight() + inputs[i];
            }
            double result = outputNeuron.fire(total);
        }
    }

    private void adjustWeights(){

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

//        weightsOutput = new ArrayList<>();
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
