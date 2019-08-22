package neuralnetwork.read;

import java.util.ArrayList;

public class InputPattern {
    private ArrayList<Double> inputs;
    private ArrayList<Double> outputs;
    private ArrayList<String> strings;

    public InputPattern() {
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
    }

    public void addInput(double input){
        inputs.add(input);
    }

    public void addInput(String[] input){
        for (String v : input) {
            inputs.add(Double.parseDouble(v));
        }
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

    public double getLastInput(){
        return inputs.get(inputs.size() - 1);
    }

    public int getSize(){
        return inputs.size();
    }
}
