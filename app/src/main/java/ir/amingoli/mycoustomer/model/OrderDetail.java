package ir.amingoli.mycoustomer.model;

public class OrderDetail {
    public Long id;
    public Long id_product;
    public Long id_order_detail;
    public String name;
    public Double amount;
    public Double price_item;
    public Double price_all;
    public Long created_at;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId_product() {
        return id_product;
    }

    public void setId_product(Long id_product) {
        this.id_product = id_product;
    }

    public Long getId_order_detail() {
        return id_order_detail;
    }

    public void setId_order_detail(Long id_order_detail) {
        this.id_order_detail = id_order_detail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getPrice_item() {
        return price_item;
    }

    public void setPrice_item(Double price_item) {
        this.price_item = price_item;
    }

    public Double getPrice_all() {
        return price_all;
    }

    public void setPrice_all(Double price_all) {
        this.price_all = price_all;
    }

    public Long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }
}
