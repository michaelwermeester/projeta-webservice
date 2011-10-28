/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.luckycode.projetawebservice;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author michael
 */
@Entity
@Table(name = "customer")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Customer.findAll", query = "SELECT c FROM Customer c"),
    @NamedQuery(name = "Customer.findByCustomerId", query = "SELECT c FROM Customer c WHERE c.customerId = :customerId"),
    @NamedQuery(name = "Customer.findByCustomerName", query = "SELECT c FROM Customer c WHERE c.customerName = :customerName"),
    @NamedQuery(name = "Customer.findByComment", query = "SELECT c FROM Customer c WHERE c.comment = :comment"),
    @NamedQuery(name = "Customer.findByVatNumber", query = "SELECT c FROM Customer c WHERE c.vatNumber = :vatNumber"),
    @NamedQuery(name = "Customer.findByPhoneNumber", query = "SELECT c FROM Customer c WHERE c.phoneNumber = :phoneNumber"),
    @NamedQuery(name = "Customer.findByFaxNumber", query = "SELECT c FROM Customer c WHERE c.faxNumber = :faxNumber"),
    @NamedQuery(name = "Customer.findByAddress", query = "SELECT c FROM Customer c WHERE c.address = :address")})
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "customer_id")
    private Integer customerId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "customer_name")
    private String customerName;
    @Size(max = 2147483647)
    @Column(name = "comment")
    private String comment;
    @Size(max = 50)
    @Column(name = "vat_number")
    private String vatNumber;
    @Size(max = 50)
    @Column(name = "phone_number")
    private String phoneNumber;
    @Size(max = 50)
    @Column(name = "fax_number")
    private String faxNumber;
    @Size(max = 25)
    @Column(name = "address")
    private String address;
    @JoinTable(name = "project_customer", joinColumns = {
        @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")}, inverseJoinColumns = {
        @JoinColumn(name = "project_id", referencedColumnName = "project_id")})
    @ManyToMany
    private Collection<Project> projectCollection;
    @ManyToMany(mappedBy = "customerCollection")
    private Collection<Contact> contactCollection;
    @JoinColumn(name = "primary_contact_id", referencedColumnName = "contact_id")
    @ManyToOne
    private Contact primaryContactId;

    public Customer() {
    }

    public Customer(Integer customerId) {
        this.customerId = customerId;
    }

    public Customer(Integer customerId, String customerName) {
        this.customerId = customerId;
        this.customerName = customerName;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vATnumber) {
        this.vatNumber = vATnumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @XmlTransient
    public Collection<Project> getProjectCollection() {
        return projectCollection;
    }

    public void setProjectCollection(Collection<Project> projectCollection) {
        this.projectCollection = projectCollection;
    }

    @XmlTransient
    public Collection<Contact> getContactCollection() {
        return contactCollection;
    }

    public void setContactCollection(Collection<Contact> contactCollection) {
        this.contactCollection = contactCollection;
    }

    public Contact getPrimaryContactId() {
        return primaryContactId;
    }

    public void setPrimaryContactId(Contact primaryContactId) {
        this.primaryContactId = primaryContactId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (customerId != null ? customerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) object;
        if ((this.customerId == null && other.customerId != null) || (this.customerId != null && !this.customerId.equals(other.customerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.luckycode.projetawebservice.Customer[ customerId=" + customerId + " ]";
    }
    
}
