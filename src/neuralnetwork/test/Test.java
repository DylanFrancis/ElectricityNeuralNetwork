package neuralnetwork.test;

import neuralnetwork.network.Network;
import neuralnetwork.neurons.OutputNeuron;
import neuralnetwork.read.InputPattern;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        FileWriter writer = null;
        try {
            writer = new FileWriter("./data/test.csv", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] inputs = {"2009,3,1,1,31.85540936", "2009,3,2,2,30.3870614", "2009,3,3,3,34.71988304", "2009,3,4,4,35.87774123",
                "2009,3,5,5,36.42962963", "2009,3,6,6,36.54686891", "2009,3,7,7,36.45556287"};

        File files = new File("./data/test16/weights");

        for (int f = 0; f < files.listFiles().length; f++){

            ArrayList<String> weights = null;
            try {
                weights = new BufferedReader(new FileReader(Objects.requireNonNull(files.listFiles())[f])).lines().skip(5).collect(Collectors.toCollection(ArrayList::new));
//                weights.remove(22);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            for (int v = 0; v < inputs.length; v++){
                String[] in = inputs[v].split(",");
                in[0] = String.valueOf(Double.parseDouble(in[0]) - 1999);
                InputPattern inputPattern = new InputPattern();
                inputPattern.addInput(in);

                ArrayList<Double> hiddenWeights = new ArrayList<>();

                int i = 0;
                for (; i < weights.size(); i++) {
                    if (weights.get(i).equals("output")) {
                        i++;
                        break;
                    }
                    hiddenWeights.add(Double.parseDouble(weights.get(i)));
                }

                Network network = new Network();
                ArrayList<OutputNeuron> outputNeurons = network.getOutputNeurons();
                int yNeurons = network.getyNeurons();
                int oNeurons = network.getoNeurons();

                double[][] outputWeights = new double[oNeurons][yNeurons];

                for (int o = 0; o < oNeurons; o++){
                    for (int y = 0; y < yNeurons; y++){
                        outputWeights[o][y] = Double.parseDouble(weights.get(i));
                        i++;
                    }
                }
                i++;
                int x = 0;
                for (; i < weights.size(); i++){
                    outputNeurons.get(x).setBiasWeight(Double.parseDouble(weights.get(i)));
                    x++;
                }

                network.initialiseWeight(hiddenWeights, outputWeights, 1, 3);
                double[] hiddenResults = network.fireHiddenLayer(inputPattern);

                boolean b = false;
                for (int h = 0; h < hiddenResults.length; h++){
                    if(!(hiddenResults[h] > 0.0))
                        b = true;
                }
                if (b) continue;

                double[] outputResults = network.fireOutputLayer(hiddenResults);
                try {
                    assert writer != null;
                    output(outputResults, writer, files.listFiles()[f].getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                network.display(outputResults);
//                System.out.println();
            }
        }
    }

    private static void output(double[] out, FileWriter writer, String name) throws IOException {
        writer.write("\n");
        writer.write(name + ";");
        for (int i = 0; i < out.length; i++) {
            writer.write(out[i] + ";");
        }
        writer.flush();
    }
}
