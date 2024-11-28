package jo.toybreeze.domain;

import java.io.Serializable;
import java.util.List;

public class Query implements Serializable {
    private static final String TAG = Query.class.getSimpleName();
    private String keyword;
    private String age;
    private List<String> category;
    private List<String> tags;

    public Query(String keyword) {
        this.keyword = keyword;
    }

    public Query(String age, List<String> category, List<String> tags) {
        this.age = age;
        this.category = category;
        this.tags = tags;
    }

    public Query(String keyword, String age, List<String> category, List<String> tags) {
        this.keyword = keyword;
        this.age = age;
        this.category = category;
        this.tags = tags;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getAge() {
        return age;
    }

    public List<String> getCategory() {
        return category;
    }

    public List<String> getTags() {
        return tags;
    }
}
