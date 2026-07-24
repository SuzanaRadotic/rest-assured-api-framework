package models;

import java.util.List;

public class CategoryResponse {

    private Integer id;
    private Integer parent_id;
    private String name;
    private String slug;
    private List<String> sub_categories;

    public Integer getId() {
        return id;
    }

    public Integer getParent_id() {
        return parent_id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public List<String> getSub_categories() {
        return sub_categories;
    }
}