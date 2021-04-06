package hu.nye.szakacskonyv;

public class CategoryItem {
    int background;
    String categoryName;
    String recipes;

    public CategoryItem() {
    }

    public CategoryItem(int background, String categoryName, String recipes) {
        this.background = background;
        this.categoryName = categoryName;
        this.recipes = recipes;
    }
    public CategoryItem(int background, String categoryName) {
        this.background = background;
        this.categoryName = categoryName;

    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getRecipes() {
        return recipes;
    }

    public void setRecipes(String recipes) {
        this.recipes = recipes;
    }
}
