/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.luckycode.projetawebservice;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author michael
 */
@Entity
@Table(name = "bug")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Bug.findAll", query = "SELECT b FROM Bug b"),
    @NamedQuery(name = "Bug.findByBugId", query = "SELECT b FROM Bug b WHERE b.bugId = :bugId")})
public class Bug implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "bug_id")
    private Integer bugId;

    public Bug() {
    }

    public Bug(Integer bugId) {
        this.bugId = bugId;
    }

    public Integer getBugId() {
        return bugId;
    }

    public void setBugId(Integer bugId) {
        this.bugId = bugId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bugId != null ? bugId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Bug)) {
            return false;
        }
        Bug other = (Bug) object;
        if ((this.bugId == null && other.bugId != null) || (this.bugId != null && !this.bugId.equals(other.bugId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.luckycode.projetawebservice.Bug[ bugId=" + bugId + " ]";
    }
    
}
