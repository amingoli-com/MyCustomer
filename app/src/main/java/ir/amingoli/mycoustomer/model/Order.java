package ir.amingoli.mycoustomer.model;

public class Order {
    public Long id;
    public String code;
    public Long id_coustomer;
    public String customer_name;
    public Long id_order_detail;
    public Double price;
    public String desc;
    public Boolean status;
    public Long created_at = System.currentTimeMillis();

    public Order(String code, Long id_coustomer, Long id_order_detail, Double price, String desc, Boolean status) {
        this.code = code;
        this.id_coustomer = id_coustomer;
        this.id_order_detail = id_order_detail;
        this.price = price;
        this.desc = desc;
        this.status = status;
    }

    public Order() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getId_coustomer() {
        return id_coustomer;
    }

    public void setId_coustomer(Long id_coustomer) {
        this.id_coustomer = id_coustomer;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public Long getId_order_detail() {
        return id_order_detail;
    }

    public void setId_order_detail(Long id_order_detail) {
        this.id_order_detail = id_order_detail;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }
}
