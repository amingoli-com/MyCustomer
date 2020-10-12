package ir.amingoli.mycoustomer.model;

public class Product {
    public Long id;
    public String name;
    public Double price;
    public Double price_all;
    public Double amount;
    public Long created_at = System.currentTimeMillis();

    public int position = -1;

    public Product() {}
    public Product(Long id, String name, Double price, Double amount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPrice_all() {
        return price_all;
    }

    public void setPrice_all(Double price_all) {
        this.price_all = price_all;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
