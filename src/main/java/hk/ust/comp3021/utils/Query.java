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
        String[] parts = query.split(",");
        if (parts.length < 3 || parts.length > 4) {
            return false;
        }

        String queryType = parts[0].trim();
        this.object = getTarget(parts[1].trim());
        System.out.println("JI");
        System.out.println(queryType);
        System.out.println(QueryType.REMOVE.name());
        if (queryType.equals(QueryType.ADD.name())) {
            if (parts.length != 3) {
                return false;
            }
            this.type = QueryType.ADD;
            this.value = parts[2].trim();

            return true;
        } else if (queryType.equals(QueryType.REMOVE.name())) {
            System.out.println("remove");
            if (parts.length != 3) {
                return false;
            }
            this.type = QueryType.REMOVE;
            this.condition = parts[2].trim();
            return true;
        } else if (queryType.equals(QueryType.UPDATE.name())) {
            if (parts.length != 4) {
                return false;
            }
            this.type = QueryType.UPDATE;
            this.condition = parts[2].trim();
            this.value = parts[3].trim();
            return true;
        } else {
            return false;
        }
    }
    /**
     * TODO Implement this function to find out which part of a bib format of a paper must be modified
     * @param target
     * @return
     */
    private Target getTarget(String target) {

        if (target.equalsIgnoreCase("PAPER")) {
            return Target.PAPER;
        } else if (target.equalsIgnoreCase("AUTHOR")) {
            return Target.AUTHOR;
        } else if (target.equalsIgnoreCase("JOURNAL")) {
            return Target.JOURNAL;
        } else if (target.equalsIgnoreCase("YEAR")) {
            return Target.YEAR;
        } else if (target.equalsIgnoreCase("KEYWORDS")) {
            return Target.KEYWORDS;
        } else if (target.equalsIgnoreCase("TITLE")) {
            return Target.TITLE;
        } else {
            return null;
        }
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
