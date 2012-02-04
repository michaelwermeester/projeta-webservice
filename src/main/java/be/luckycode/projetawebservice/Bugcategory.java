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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
@Table(name = "bugcategory")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Bugcategory.findAll", query = "SELECT b FROM Bugcategory b"),
    @NamedQuery(name = "Bugcategory.findByBugcategoryId", query = "SELECT b FROM Bugcategory b WHERE b.bugcategoryId = :bugcategoryId"),
    @NamedQuery(name = "Bugcategory.findByCategoryName", query = "SELECT b FROM Bugcategory b WHERE b.categoryName = :categoryName")})
public class Bugcategory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "bugcategory_id")
    private Integer bugcategoryId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "category_name")
    private String categoryName;
    @OneToMany(mappedBy = "bugcategoryId")
    private Collection<Bug> bugCollection;
    @Size(max = 500)
    @Column(name = "comment")
    private String comment;

    public Bugcategory() {
    }

    public Bugcategory(Integer bugcategoryId) {
        this.bugcategoryId = bugcategoryId;
    }

    public Bugcategory(Integer bugcategoryId, String categoryName) {
        this.bugcategoryId = bugcategoryId;
        this.categoryName = categoryName;
    }

    public Integer getBugcategoryId() {
        return bugcategoryId;
    }

    public void setBugcategoryId(Integer bugcategoryId) {
        this.bugcategoryId = bugcategoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @XmlTransient
    public Collection<Bug> getBugCollection() {
        return bugCollection;
    }

    public void setBugCollection(Collection<Bug> bugCollection) {
        this.bugCollection = bugCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bugcategoryId != null ? bugcategoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Bugcategory)) {
            return false;
        }
        Bugcategory other = (Bugcategory) object;
        if ((this.bugcategoryId == null && other.bugcategoryId != null) || (this.bugcategoryId != null && !this.bugcategoryId.equals(other.bugcategoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.luckycode.projetawebservice.Bugcategory[ bugcategoryId=" + bugcategoryId + " ]";
    }
    
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
}
