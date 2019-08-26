package neuralnetwork.read;
import java.util.ArrayList;

public class InputPattern {
    private ArrayList<Double> inputs;
    private ArrayList<Double> outputs;
    private ArrayList<String> strings;
    private double prevAvg;
    private boolean hasPrev;

    public InputPattern() {
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
    }

    public void addInput(double input){
        inputs.add(input);
    }

    public void addInput(String[] input){
        for (int i = 0; i < input.length; i++) inputs.add(Double.parseDouble(input[i]));
        hasPrev = true;
    }

    public void addOutput(double output){
        outputs.add(output);
    }

    public void addString(String string){
        if (strings == null) strings = new ArrayList<>();
        strings.add(string);
    }

    public double getOutput(int idx){
        return outputs.get(idx);
    }

    public double getInput(int idx) {
        return inputs.get(idx);
    }

    public String getString(int idx){
        if (strings == null) return null;
        return strings.get(idx);
    }

    public void setInput(int idx, double value){
        inputs.set(idx, value);
    }

    public int getSize(){
        return inputs.size();
    }

    public double calAverageOutput(){
        final double[] total = {0};
        outputs.forEach(aDouble -> total[0] += aDouble);
        return total[0] / outputs.size();
    }

    public void setPrev(InputPattern prev) {
        if(prev == null) return;
        prevAvg = prev.calAverageOutput();
        addInput(prevAvg);
        hasPrev = true;
    }

    public boolean hasPrev() {
        return hasPrev;
    }

    public Double[] getOutputs(){
        return outputs.toArray(new Double[outputs.size()]);
    }
}
