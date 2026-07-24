package models;

public class CategoryRequest {

    private String name;
    private String slug;
    private Integer parent_id;

    public CategoryRequest(String name, String slug, Integer parent_id) {
        this.name = name;
        this.slug = slug;
        this.parent_id = parent_id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public Integer getParent_id() {
        return parent_id;
    }
}