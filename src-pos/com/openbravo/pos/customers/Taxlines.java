/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.customers;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author kh
 */
@Entity
@Table(name = "TAXLINES", catalog = "test2", schema = "")
@NamedQueries({
    @NamedQuery(name = "Taxlines.findAll", query = "SELECT t FROM Taxlines t"),
    @NamedQuery(name = "Taxlines.findById", query = "SELECT t FROM Taxlines t WHERE t.id = :id"),
    @NamedQuery(name = "Taxlines.findByReceipt", query = "SELECT t FROM Taxlines t WHERE t.receipt = :receipt"),
    @NamedQuery(name = "Taxlines.findByTaxid", query = "SELECT t FROM Taxlines t WHERE t.taxid = :taxid"),
    @NamedQuery(name = "Taxlines.findByBase", query = "SELECT t FROM Taxlines t WHERE t.base = :base"),
    @NamedQuery(name = "Taxlines.findByAmount", query = "SELECT t FROM Taxlines t WHERE t.amount = :amount")})
public class Taxlines implements Serializable {
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private String id;
    @Basic(optional = false)
    @Column(name = "RECEIPT")
    private String receipt;
    @Basic(optional = false)
    @Column(name = "TAXID")
    private String taxid;
    @Basic(optional = false)
    @Column(name = "BASE")
    private double base;
    @Basic(optional = false)
    @Column(name = "AMOUNT")
    private double amount;

    public Taxlines() {
    }

    public Taxlines(String id) {
        this.id = id;
    }

    public Taxlines(String id, String receipt, String taxid, double base, double amount) {
        this.id = id;
        this.receipt = receipt;
        this.taxid = taxid;
        this.base = base;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        String oldId = this.id;
        this.id = id;
        changeSupport.firePropertyChange("id", oldId, id);
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        String oldReceipt = this.receipt;
        this.receipt = receipt;
        changeSupport.firePropertyChange("receipt", oldReceipt, receipt);
    }

    public String getTaxid() {
        return taxid;
    }

    public void setTaxid(String taxid) {
        String oldTaxid = this.taxid;
        this.taxid = taxid;
        changeSupport.firePropertyChange("taxid", oldTaxid, taxid);
    }

    public double getBase() {
        return base;
    }

    public void setBase(double base) {
        double oldBase = this.base;
        this.base = base;
        changeSupport.firePropertyChange("base", oldBase, base);
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        double oldAmount = this.amount;
        this.amount = amount;
        changeSupport.firePropertyChange("amount", oldAmount, amount);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Taxlines)) {
            return false;
        }
        Taxlines other = (Taxlines) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.openbravo.pos.customers.Taxlines[ id=" + id + " ]";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    
}
