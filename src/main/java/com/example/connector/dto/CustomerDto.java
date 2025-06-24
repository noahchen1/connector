package com.example.connector.dto;

public class CustomerDto {
    private int internal_id;
    private String cust_id;
    private String email;
    private String firstname;
    private String lastname;
    private int subsidiary;

    public Integer getInternalId() {
        return internal_id;
    }

    public String getCustId() {
        return cust_id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public int getSubsidiary() {
        return subsidiary;
    }

    public void setInternalId(int internal_id) {
        this.internal_id = internal_id;
    }

    public void setCustId(String custId) {
        this.cust_id = custId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setSubsidiary(int subsidiary) {
        this.subsidiary = subsidiary;
    }

    @Override
    public String toString() {
        return "CustomerDTO{" +
                "Internal_Id=" + internal_id +
                ", Cust_ID=" + cust_id +
                ", email=" + email +
                ", firstname=" + firstname +
                ", lastname=" + lastname +
                '}';
    }
}
