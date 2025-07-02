package ir.amingoli.mycoustomer.model;

public class Transaction {
    public Long id;
    public Long id_customer;
    public Long id_order;
    public Long type;
    public Double amount;
    public String desc;
    public Long created_at = System.currentTimeMillis();

    public Transaction() {}

    public Transaction(Long id, Long id_customer, Long id_order, Long type, Double amount, String desc, Long created_at) {
        this.id = id;
        this.id_customer = id_customer;
        this.id_order = id_order;
        this.type = type;
        this.amount = amount;
        this.desc = desc;
        this.created_at = created_at;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId_customer() {
        return id_customer;
    }

    public void setId_customer(Long id_customer) {
        this.id_customer = id_customer;
    }

    public Long getId_order() {
        return id_order;
    }

    public void setId_order(Long id_order) {
        this.id_order = id_order;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }
}
