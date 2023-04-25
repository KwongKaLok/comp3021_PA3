package hk.ust.comp3021.action;

import hk.ust.comp3021.resource.Paper;
import hk.ust.comp3021.person.User;
import java.util.*;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SortPaperAction extends Action {
    public enum SortBase {
        ID,
        TITLE,
        AUTHOR,
        JOURNAL,
    };

    public enum SortKind {
        ASCENDING,
        DESCENDING,
    };

    private SortBase base;

    private SortKind kind;

    private final List<Paper> actionResult = new ArrayList<>();

    public SortPaperAction(String id, User user, Date time, SortBase base, SortKind kind) {
        super(id, user, time, ActionType.SORT_PAPER);
        this.base = base;
        this.kind = kind;
    }

    public SortBase getBase() {
        return base;
    }

    public void setBase(SortBase base) {
        this.base = base;
    }

    public SortKind getKind() {
        return kind;
    }

    public void setKind(SortKind kind) {
        this.kind = kind;
    }

    public List<Paper> getActionResult() {
        return actionResult;
    }

    public void appendToActionResult(Paper paper) {
        this.actionResult.add(paper);
    }

    public Consumer<Paper> appendToActionResultByLambda = paper -> this.actionResult.add(paper);

    public Predicate<SortKind> kindPredicate = kind -> kind == SortKind.DESCENDING;

    public Comparator<Paper> comparator;

    public Supplier<List<Paper>> sortFunc = () -> {
        List<Paper> resultList = this.actionResult.stream()
                .sorted(Comparator.nullsLast(comparator))
                .collect(Collectors.toList());
        this.actionResult.clear();
        this.actionResult.addAll(resultList);
        return this.actionResult;
    };

}
