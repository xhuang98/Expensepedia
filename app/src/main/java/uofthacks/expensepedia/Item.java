package uofthacks.expensepedia;

import android.view.View;

import java.util.List;

public class Item {
    public String name;
    public Double price;
    public String category;
    public boolean checked = false;
    View v = null;

    public Item(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    public Item(String name, Double price, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void addItemsToCategory(List<Item> itemList, String category) {

    }
}
