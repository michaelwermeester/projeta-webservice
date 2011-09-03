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
@Table(name = "projectversion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Projectversion.findAll", query = "SELECT p FROM Projectversion p"),
    @NamedQuery(name = "Projectversion.findByProjectversionId", query = "SELECT p FROM Projectversion p WHERE p.projectversionId = :projectversionId"),
    @NamedQuery(name = "Projectversion.findByVersionName", query = "SELECT p FROM Projectversion p WHERE p.versionName = :versionName")})
public class Projectversion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "projectversion_id")
    private Integer projectversionId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "version_name")
    private String versionName;
    @ManyToMany(mappedBy = "projectversionCollection")
    private Collection<Bug> bugCollection;
    @JoinColumn(name = "project_id", referencedColumnName = "project_id")
    @ManyToOne(optional = false)
    private Project projectId;

    public Projectversion() {
    }

    public Projectversion(Integer projectversionId) {
        this.projectversionId = projectversionId;
    }

    public Projectversion(Integer projectversionId, String versionName) {
        this.projectversionId = projectversionId;
        this.versionName = versionName;
    }

    public Integer getProjectversionId() {
        return projectversionId;
    }

    public void setProjectversionId(Integer projectversionId) {
        this.projectversionId = projectversionId;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    @XmlTransient
    public Collection<Bug> getBugCollection() {
        return bugCollection;
    }

    public void setBugCollection(Collection<Bug> bugCollection) {
        this.bugCollection = bugCollection;
    }

    public Project getProjectId() {
        return projectId;
    }

    public void setProjectId(Project projectId) {
        this.projectId = projectId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (projectversionId != null ? projectversionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Projectversion)) {
            return false;
        }
        Projectversion other = (Projectversion) object;
        if ((this.projectversionId == null && other.projectversionId != null) || (this.projectversionId != null && !this.projectversionId.equals(other.projectversionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.luckycode.projetawebservice.Projectversion[ projectversionId=" + projectversionId + " ]";
    }
    
}
