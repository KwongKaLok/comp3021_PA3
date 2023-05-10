package hk.ust.comp3021.action;

import java.util.ArrayList;
import java.util.Date;

import hk.ust.comp3021.person.User;

public class LabelAction extends Action {
    private Action.ActionType actionType;
    private String label;
    private String newLabel;
    private String paperID;
    private boolean isSuccessful = false;

    /***
     * TODO
     * Implement suitable code in the constructor to achieve the goal of performing add,
     * update and delete for labels at the same time
     * @param id
     * @param user
     * @param time
     * @param actionType
     * @param label
     */
    public LabelAction(String id, User user, Date time, ActionType actionType, String label) {
        super(id, user, time, actionType);
        // TODO Add suitable code here for the actions on labels
        this.actionType = actionType;
        this.label=label;
    }

    public Action.ActionType getActionType() {
        return actionType;
    }

    public String getLabel() {
        return label;
    }

    public String getNewLabel() {
        return newLabel;
    }

    public String getPaperID() {
        return paperID;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setActionType(Action.ActionType actionType) {
        this.actionType = actionType;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setNewLabel(String newLabel) {
        this.newLabel = newLabel;
    }

    public void setPaperID(String paperID) {
        this.paperID = paperID;
    }

    public void setSuccessful(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    /**
     * TODO
     * Retrieve a list of labels from the user
     * @param labels: a string of labels entered by the user separated by comma
     * @return an arraylist of labels
     *
     */
    private ArrayList<String> processInputLabels(String labels) {
        ArrayList<String> labelList = new ArrayList<String>();
        labelList.add(labels);
        return labelList;
    }

}
