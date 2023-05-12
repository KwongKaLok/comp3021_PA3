package hk.ust.comp3021.action;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class LabelActionList {
    private Queue<LabelAction> labelActionsQueue = new LinkedList<>();
    private ArrayList<String> processedLabels = new ArrayList<>();
    private boolean isFinished = false;
    private int numOfDeleted = 0;
    private int numOfAdded = 0;
    private int numOfUpdated = 0;

    /**
     * TODO
     * This class is used to store the list of queries in @labelActionsQueue.
     * Implement its constructor adding code to handle this requirement
     */
    public LabelActionList() {
    }

    /**
     * TODO
     * Implement this method to safely add a query to @labelActionsQueue
     */
    public void enqueue(LabelAction item) {
        synchronized (this){

            labelActionsQueue.add(item);
        }
    }
    public Queue<LabelAction> showQueue(){
        return labelActionsQueue;
    }
    /**
     * TODO
     * Implement this method to safely remove a query to @labelActionsQueue
     * @return
     * @throws InterruptedException
     */
    public LabelAction dequeue() throws InterruptedException {
        while (labelActionsQueue.isEmpty()) {
            wait(); // wait until there is an element in the queue
        }
        return labelActionsQueue.remove();
    }

    /**
     * TODO Implement this method to safely add a query to @labelActionsQueue
     * @return
     * @throws InterruptedException
     */
    public LabelAction getHead() throws InterruptedException {
        return labelActionsQueue.peek();
    }

    public int getNumOfUpdated() {
        return numOfUpdated;
    }

    public int getNumOfAdded() {
        return numOfAdded;
    }

    public int getNumOfDeleted() {
        return numOfDeleted;
    }

    public void increateNumOfAdded() {
        numOfAdded++;
    }

    public void increateNumOfupdated() {
        numOfUpdated++;
    }

    public void increateNumOfDeleted() {
        numOfDeleted++;
    }

    public void setNumOfUpdated(int numOfUpdated) {
        this.numOfUpdated = numOfUpdated;
    }

    public void setNumOfAdded(int numOfAdded) {
        this.numOfAdded = numOfAdded;
    }

    public void setNumOfDeleted(int numOfDeleted) {
        this.numOfDeleted = numOfDeleted;
    }

    public ArrayList<String> getProcessedLabels() {
        return processedLabels;
    }

    public void addProcessedLabel(String label){
        processedLabels.add(label);
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }



}
