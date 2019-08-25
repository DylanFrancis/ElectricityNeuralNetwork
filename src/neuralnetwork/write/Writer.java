package neuralnetwork.write;

import neuralnetwork.neurons.OutputNeuron;
import neuralnetwork.weights.IWeight;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Writer {
    private FileWriter testWriter;
    private FileWriter testResultsWriter;
    private FileWriter trainingWriter;
    private FileWriter resultWriter;
    private FileWriter weightWriter;
    private int oNeurons;
    private int yNeurons;
    private String dir = "../EvoPrac2Data/";

    public Writer(int oNeurons, int yNeurons, String name) {
        this.oNeurons = oNeurons;
        this.yNeurons = yNeurons;
        try {
            testResultsWriter = new FileWriter(dir + "testResults/testResults" + name + ".csv", true);
            testWriter = new FileWriter(dir + "test/test" + name + ".csv", true);
            trainingWriter = new FileWriter(dir +  "training/training" + name + ".csv", true);
//            resultWriter = new FileWriter("./data/results" + name + ".csv", true);
            weightWriter = new FileWriter(dir + "weights/weights" + name + ".txt", true);

            testResultsWriter.write("target;actual;difference");
            testResultsWriter.write("\n");
            trainingWriter.write("learning rate;error;iterations;epochs;size;");
            trainingWriter.write("\n");
            testWriter.write("learning rate;error;size;average difference;size");
            testWriter.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printRate(double rate){
        try {
//            resultWriter.write("========================================================================================= \n");
//            resultWriter.write("\n" + rate + ";");
            weightWriter.write("========================================================================================= \n");
            weightWriter.write(rate + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printTrainingIterationResults(double learningRate, double error, double iterations, double epochs, double size){
        try {
            trainingWriter.write(learningRate + ";" + error + ";" + iterations + ";" + epochs + ";" + size + ";");
            trainingWriter.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printTestIterationResults(double learningRate, double error, double size, double average){
        try {
            testWriter.write(learningRate + ";" + error + ";" + size + ";" + average + ";");
            testWriter.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printTestResults(Double[] target, double[] actual, double[] differences){
        assert target.length == actual.length && actual.length == differences.length;
        try {
            for (int x = 0; x < target.length; x++){
                testResultsWriter.write(target[x] + ";" + actual[x] + ";" + differences[x] + ";");
                testResultsWriter.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printWeights(double iterations, double epochs, ArrayList<IWeight> weightsHidden, IWeight[][] weightsOutput, ArrayList<OutputNeuron> outputNeurons){
        try {
//            resultWriter.write(iterations + "\n");
            weightWriter.write("epochs" + epochs + "\n");
            weightWriter.write(iterations + "\n");
            weightWriter.write("hidden\n");
            weightsHidden.forEach(iWeight -> {
                try {
                    weightWriter.write(iWeight.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            weightWriter.write("output\n");
            for (int i = 0; i < oNeurons; i++) {
                for (int y = 0; y < yNeurons; y++){
                    weightWriter.write(weightsOutput[i][y].toString());
                }
            }
            weightWriter.write("neurons\n");
            for (int i = 0; i < outputNeurons.size(); i++) {
                weightWriter.write(outputNeurons.get(i).getBiasWeight() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void flush() {
        try {
            trainingWriter.flush();
            testWriter.flush();
            testResultsWriter.flush();
            weightWriter.flush();
//            resultWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            trainingWriter.close();
            testWriter.close();
            testResultsWriter.close();
            weightWriter.close();
//            resultWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
