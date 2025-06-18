package com.example.connector.dto;

public class CustomerDto {
    private String cust_id;
    private String email;
    private String firstname;
    private String lastname;

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

    @Override
    public String toString() {
        return "CustomerDTO{" +
                "Cust_ID=" + cust_id +
                ", email=" + email +
                ", firstname=" + firstname +
                ", lastname=" + lastname +
                '}';
    }
}
