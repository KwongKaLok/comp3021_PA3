package hk.ust.comp3021.utils;

import java.util.Date;

public class Query {
    public enum QueryType{
        ADD,
        REMOVE,
        UPDATE,
    };
    public enum Target{
        PAPER,
        AUTHOR,
        JOURNAL,
        YEAR,
        KEYWORDS,
        TITLE,
    };
    private QueryType type;
    private Target object;
    private String value = "";
    private String query = "";
    private String condition = "";
    private boolean valid = false;
    private boolean completed = false;
    private Date completedDate;


    public Query(String query) {
        this.query = query;
        this.valid = processQuery(query);
    }

    /**
     * TODO Implement this function to check the validity of a query received from each line
     * @param query
     * @return
     */
    private boolean processQuery(String query) {

        return false;
    }
    /**
     * TODO Implement this function to find out which part of a bib format of a paper must be modified
     * @param target
     * @return
     */
    private Target getTarget(String target) {

        return null;
    }

    public Target getObject() {
        return object;
    }

    public String getQuery() {
        return query;
    }

    public QueryType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public boolean getValidity() {
        return valid;
    }

    public void setObject(Target object) {
        this.object = object;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setType(QueryType type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        this.setCompletedDate();
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompletedDate() {
        this.completedDate = new Date();
    }

    public Date getCompletedDate() {
        return completedDate;
    }
}
