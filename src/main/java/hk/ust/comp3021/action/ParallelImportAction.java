package hk.ust.comp3021.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import hk.ust.comp3021.person.User;
import hk.ust.comp3021.resource.Paper;

public class ParallelImportAction extends Action {
    public static int maxFileNumber = 10;

    private List<UploadPaperAction> importActions = new ArrayList<>();
    public static HashMap<String, Paper> importedPapers = new HashMap<>();
    private ArrayList<String> filePaths = new ArrayList<>();
    private boolean isCompleted = true;

    /**
     * TODO Complete the constructor by adding codes to import  @filepaths concurrently
     * @param id: id
     * @param user: who is performing the action
     * @param time: operation time
     */

    public ParallelImportAction(String id, User user, Date time) {
        super(id, user, time, ActionType.UPLOAD_PARALLEL);
    }


    public void setFilePaths(ArrayList<String> filePaths) {
        this.filePaths = filePaths;
    }


    public HashMap<String, Paper> getImportedPapers() {
        return importedPapers;
    }

    public void setImportedPapers(HashMap<String, Paper> importActions) {
        this.importedPapers = importActions;
    }


    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public ArrayList<String> getFilePaths() {
        return filePaths;
    }

    public int maxNumberofThreads() {
        return maxFileNumber;
    }
}
