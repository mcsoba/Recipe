package hu.nye.szakacskonyv;

public class NewIngredientItem {
    private String name;

    public NewIngredientItem() {
    }

    public NewIngredientItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "NewIngredientItem{" +
                "name='" + name + '\'' +
                '}';
    }
}
