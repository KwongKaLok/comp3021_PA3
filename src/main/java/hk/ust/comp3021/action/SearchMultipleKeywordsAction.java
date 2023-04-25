package hk.ust.comp3021.action;

import java.util.ArrayList;
import java.util.Date;
import hk.ust.comp3021.person.User;
import hk.ust.comp3021.resource.Paper;

public class SearchMultipleKeywordsAction extends Action{
    public static int numThreads = 5;
    private ArrayList<String> words = new ArrayList<>();
    private ArrayList<Paper> results = new ArrayList<>();
    private int foundResult = 0;
    private boolean isFound = false;

    /**
     * TODO Implement suitable code in the constructor to achieve the goal of searching for multiple words at the same time
     * @param id
     * @param user
     * @param time
     * @param actionType
     */

    public SearchMultipleKeywordsAction(String id, User user, Date time) {
        super(id, user, time, ActionType.SEARCH_SMART);
        // TODO Auto-generated constructor stub
    }

    public ArrayList<Paper> getResults() {
        return results;
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public boolean isFound() {
        return isFound;
    }

    public void setWords(ArrayList<String> words) {
        this.words = words;
    }

    public void setResults(ArrayList<Paper> results) {
        this.results = results;
    }

    public void setFoundResult(int foundResult) {
        this.foundResult = foundResult;
    }
    /**
     * TODO Implement code to make sure the repeated results do not exist
     * @param paper
     */
    public void addFoundResult(Paper paper) {
    }

    public void increaseFound() {
        foundResult++;
    }

    public void setFound(boolean isFound) {
        this.isFound = isFound;
    }

    public static int getNumThreads() {
        return numThreads;
    }

    public int getFoundResult() {
        return foundResult;
    }
}
