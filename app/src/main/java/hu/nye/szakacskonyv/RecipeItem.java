package hu.nye.szakacskonyv;

public class RecipeItem {
    private int image;
    private String title;
    private int recipe_id;
    public RecipeItem() {
    }

    public RecipeItem(int image, String title, int recipe_id) {
        this.image = image;
        this.title = title;
        this.recipe_id = recipe_id;
    }

    public RecipeItem(int image, String title) {
        this.image = image;
        this.title = title;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public int getRecipeId() {return recipe_id; };

    public void setImage(int image) {
        this.image = image;
    }
}
