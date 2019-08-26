package neuralnetwork.read;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DataSet {
    private LinkedList<InputPattern> set;
    private LinkedList<InputPattern> training;
    private LinkedList<InputPattern> validation;
    private LinkedList<InputPattern> test;
    private String[] params;
    private int totalSize;
    private int testSize = 20;
    private int validationSize = 10;
    private double[] maxInputs;

    public DataSet(LinkedList<InputPattern> set, LinkedList<InputPattern> training, double[]maxInputs) {
        this.set = set;
        this.set.pop();
        this.training = training;
        this.maxInputs = maxInputs;
        totalSize = training.size() - 1;
        setParams();
        link();
        prune();
    }

    private void setParams(){
        InputPattern inputPattern = training.pop();
        params = new String[inputPattern.getSize()];
        for(int x = 0; x < params.length; x++){
            params[x] = inputPattern.getString(x);
        }
    }

    public int[][] getInputCount(int... inputs){
        HashMap<Integer, HashSet<Double>> map = new HashMap<>();

        for (int i = 0; i < inputs.length; i++) {
            map.put(inputs[i], new HashSet<>());
        }
        set.forEach(inputPattern -> {
            for (int i = 0; i < inputs.length; i++) {
                map.get(inputs[i]).add(inputPattern.getInput(inputs[i]));
            }
        });
        int[][] count = new int[inputs.length][2];
        AtomicInteger c = new AtomicInteger();
        map.forEach((key, valueSet) -> {
            count[c.get()][0] = inputs[c.get()];
            count[c.get()][1] = valueSet.size();
            c.getAndIncrement();
        });
        return count;
    }

    private void prune(){
        set.removeIf(cur -> !cur.hasPrev());
    }

    private void link(){
        Iterator<InputPattern> iterator = set.descendingIterator();
        InputPattern prev = null;
        while (iterator.hasNext()){
            InputPattern cur = iterator.next();
            cur.setPrev(prev);
            prev = cur;
        }
    }

    public LinkedList<InputPattern> getTraining() {
        return training;
    }

    public LinkedList<InputPattern> getTest() {
        return test;
    }

    public Iterator<InputPattern> trainingIterator(){
        return training.iterator();
    }

    public Iterator<InputPattern> testIterator(){
        return test.iterator();
    }

    public void setValidation(LinkedList<InputPattern> validation) {
        this.validation = validation;
    }

    public void setTest(LinkedList<InputPattern> test) {
        this.test = test;
    }

    public int getTestSize() {
        return testSize;
    }

    public int getValidationSize() {
        return validationSize;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public Iterator<InputPattern> validationIterator() {
        return validation.iterator();
    }
}
