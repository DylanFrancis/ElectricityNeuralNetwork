package neuralnetwork.test;

import neuralnetwork.network.Network;
import neuralnetwork.read.InputPattern;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        FileWriter writer = null;
        try {
            writer = new FileWriter("./data/test.csv", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] inputs = {"2009,3,1,1", "2009,3,2,2", "2009,3,3,3", "2009,3,4,4", "2009,3,5,5", "2009,3,6,6", "2009,3,7,7"};

        for (int v = 0; v < inputs.length; v++){
            String[] in = inputs[v].split(",");
            in[0] = String.valueOf(Double.parseDouble(in[0]) - 1999);
            InputPattern inputPattern = new InputPattern();
            inputPattern.addInput(in);

            ArrayList<Double> hiddenWeights = new ArrayList<>();

            int i = 1;
            for (; i < args.length; i++) {
                if (args[i].equals("output")) {
                    i++;
                    break;
                }
                hiddenWeights.add(Double.parseDouble(args[i]));
            }

            Network network = new Network();

            int yNeurons = network.getyNeurons();
            int oNeurons = network.getoNeurons();

            double[][] outputWeights = new double[oNeurons][yNeurons];


            for (int x = 0; x < oNeurons; x++) {
                for (int y = 0; y < yNeurons; y++) {
                    outputWeights[x][y] = Double.parseDouble(args[i]);
                    i++;
                }
            }


            network.initialiseWeight(hiddenWeights, outputWeights, 1, 3);
            double[] hiddenResults = network.hiddenLayer(inputPattern);
            double[] outputResults = network.outputLayer(hiddenResults);
            try {
                assert writer != null;
                output(outputResults, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            network.display(outputResults);
            System.out.println();
        }
    }

    private static void output(double[] out, FileWriter writer) throws IOException {
        writer.write("\n");
        for (int i = 0; i < out.length; i++) {
            writer.write(out[i] + ";");
        }
        writer.flush();
    }
}
