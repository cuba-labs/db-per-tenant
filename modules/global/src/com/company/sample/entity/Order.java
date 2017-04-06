package com.company.sample.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "SAMPLE_ORDER")
@Entity(name = "sample$Order")
public class Order extends StandardEntity {
    private static final long serialVersionUID = -2520400466434122794L;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_")
    protected Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID")
    protected Customer customer;

    @Column(name = "AMOUNT")
    protected BigDecimal amount;

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }


}