package ir.amingoli.mycoustomer.model;

public class Customer {
    public Long id;
    public String name;
    public String tel;
    public String desc;
    public Long created_at = System.currentTimeMillis();

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

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
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

    public Customer() {
    }

    public Customer(Long id, String name, String tel, String desc) {
        this.id = id;
        this.name = name;
        this.tel = tel;
        this.desc = desc;
    }

    public Customer(String name, String desc, String tel) {
        this.name = name;
        this.desc = desc;
        this.tel = tel;
    }
}
