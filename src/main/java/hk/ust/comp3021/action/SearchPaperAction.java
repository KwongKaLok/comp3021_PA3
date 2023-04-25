package hk.ust.comp3021.action;

import hk.ust.comp3021.resource.Paper;
import hk.ust.comp3021.person.User;
import java.util.*;
import java.util.function.*;

public class SearchPaperAction extends Action {
    public enum SearchPaperKind {
        ID,
        TITLE,
        AUTHOR,
        JOURNAL,
    };

    private String searchContent;
    private SearchPaperKind kind;

    private final ArrayList<Paper> actionResult = new ArrayList<>();

    public SearchPaperAction(String id, User user, Date time, String searchContent, SearchPaperKind kind) {
        super(id, user, time, ActionType.SEARCH_PAPER);
        this.searchContent = searchContent;
        this.kind = kind;
    }

    public String getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    public SearchPaperKind getKind() {
        return kind;
    }

    public void setKind(SearchPaperKind kind) {
        this.kind = kind;
    }

    public ArrayList<Paper> getActionResult() {
        return actionResult;
    }

    public void appendToActionResult(Paper paper) {
        this.actionResult.add(paper);
    }

    public Consumer<Paper> appendToActionResultByLambda = paper -> this.actionResult.add(paper);

    public Predicate<String> isEqual = (string) -> Predicate.isEqual(this.searchContent).test(string);

    public Predicate<ArrayList<String>> isContain = arrayList -> arrayList.contains(this.searchContent);
}
