package neuralnetwork.read;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Data {
    private LinkedList<InputPattern> training;
    private List<InputPattern> validation;
    private List<InputPattern> test;

    public Data() {
        setup();
    }

    public DataSet getDataSet(String filename, int... outputs) throws FileNotFoundException {
        LinkedList<InputPattern> list = read("./data/" + filename, IntStream.of(outputs).boxed().collect(Collectors.toSet()));
        LinkedList<InputPattern> set = (LinkedList<InputPattern>) list.clone();
        ArrayList<InputPattern> arrayList = new ArrayList<>(list);

        double[] max = getMaxInputs(arrayList);

//        setOutput(list, outputs);

        DataSet dataSet = new DataSet(set, list, max);

        dataSet.setValidation(chopData(dataSet.getTraining(), dataSet.getValidationSize(), dataSet.getTotalSize()));
        dataSet.setTest(chopData(dataSet.getTraining(), dataSet.getTestSize(), dataSet.getTotalSize()));

        return dataSet;
    }

    private void setOutput(LinkedList<InputPattern> list, int... outputs){
        Iterator<InputPattern> iterator = list.iterator();

        while (iterator.hasNext()) {
            InputPattern cur = iterator.next();
            for (int i = 0; i < outputs.length; i++) {

            }
        }
    }

    private void setup(){
        training = new LinkedList<>();
        validation = new LinkedList<>();
        test = new LinkedList<>();
    }

    private LinkedList<InputPattern> chopData(LinkedList<InputPattern> tbc, double percent, int totalSize){
        int amt = (int) (totalSize * (percent / 100));
        LinkedList<InputPattern> newList = new LinkedList<>();

        for(int x = 0; x < amt; x++){
            newList.add(tbc.pop());
        }

        return newList;
    }

    private LinkedList<InputPattern> read(String file, Set outputs) throws FileNotFoundException {
        return new BufferedReader(new FileReader(file)).lines().map(s -> {
            InputPattern input = new InputPattern();
            String[] fields = s.split(",");
            for (int i = 0; i < fields.length; i++) {
                String field = fields[i];
                if(outputs.contains(i)) addData(field, input, "output");
                else addData(field, input, "input");
            }
            return input;
        }).collect(Collectors.toCollection(LinkedList::new));
    }

    private void addData(String s, InputPattern inputPattern, String type){
        try{
            double input = Double.parseDouble(s);
            if (type.equals("input")) inputPattern.addInput(input);
            else inputPattern.addOutput(input);
        }catch (NumberFormatException ignored){
            inputPattern.addString(s);
        }
    }

    private double[] getMaxInputs(ArrayList<InputPattern> arrayList){
        double[] max = new double[arrayList.get(1).getSize()];

        for (int i = 1; i < arrayList.size(); i++) {
            InputPattern inputPattern = arrayList.get(i);
            for (int x = 0; x < max.length; x++){
                setMax(inputPattern.getInput(x), max, x);
            }
        }
        return max;
    }

    private LinkedList<InputPattern> normaliseData(ArrayList<InputPattern> arrayList, double[] max){
        for (int i = 1; i < arrayList.size(); i++) {
            InputPattern inputPattern = arrayList.get(i);
            for (int x = 0; x < max.length; x++){
                inputPattern.setInput(x, inputPattern.getInput(x) / max[x]);
            }
        }
        return new LinkedList<>(arrayList);
    }

    private void setMax(double tbc, double[]max, int idx){
        if(max[idx] < tbc) max[idx] = tbc;
    }
}
