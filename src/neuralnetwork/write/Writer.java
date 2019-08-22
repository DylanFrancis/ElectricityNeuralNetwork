package neuralnetwork.write;

import neuralnetwork.weights.IWeight;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Writer {
    private FileWriter resultWriter;
    private FileWriter weightWriter;
    private int oNeurons;
    private int yNeurons;

    public Writer(int oNeurons, int yNeurons) {
        this.oNeurons = oNeurons;
        this.yNeurons = yNeurons;
        try {
            resultWriter = new FileWriter("./data/results.csv", true);
            weightWriter = new FileWriter("./data/weights.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printRate(double rate){
        try {
//            resultWriter.write("========================================================================================= \n");
            resultWriter.write("\n" + rate + ";");
            weightWriter.write("========================================================================================= \n");
            weightWriter.write(rate + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printResults(double avg, double iterations, ArrayList<IWeight> weightsHidden, IWeight[][] weightsOutput){
        try {
            resultWriter.write(avg + ";");
//            resultWriter.write(iterations + "\n");

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void flush() {
        try {
            weightWriter.flush();
            resultWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
