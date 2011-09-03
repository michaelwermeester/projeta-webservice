/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.luckycode.projetawebservice;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

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
    @Column(name = "priority")
    private Short priority;
    @Basic(optional = false)
    @NotNull
    @Column(name = "date_reported")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateReported;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "title")
    private String title;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "details")
    private String details;
    @Column(name = "fixed")
    private Boolean fixed;
    @Column(name = "canceled")
    private Boolean canceled;
    @Column(name = "deleted")
    private Boolean deleted;
    @JoinTable(name = "bug_projectversion", joinColumns = {
        @JoinColumn(name = "bug_id", referencedColumnName = "bug_id")}, inverseJoinColumns = {
        @JoinColumn(name = "projectversion_id", referencedColumnName = "projectversion_id")})
    @ManyToMany
    private Collection<Projectversion> projectversionCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bugId")
    private Collection<BugProgress> bugProgressCollection;
    @JoinColumn(name = "user_assigned", referencedColumnName = "user_id")
    @ManyToOne
    private Users userAssigned;
    @JoinColumn(name = "user_reported", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Users userReported;
    @JoinColumn(name = "project_id", referencedColumnName = "project_id")
    @ManyToOne
    private Project projectId;
    @JoinColumn(name = "bugcategory_id", referencedColumnName = "bugcategory_id")
    @ManyToOne
    private Bugcategory bugcategoryId;
    @OneToMany(mappedBy = "duplicateOfBugId")
    private Collection<Bug> bugCollection;
    @JoinColumn(name = "duplicate_of_bug_id", referencedColumnName = "bug_id")
    @ManyToOne
    private Bug duplicateOfBugId;
    @OneToMany(mappedBy = "bugId")
    private Collection<Comment> commentCollection;
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

    public Short getPriority() {
        return priority;
    }

    public void setPriority(Short priority) {
        this.priority = priority;
    }

    public Date getDateReported() {
        return dateReported;
    }

    public void setDateReported(Date dateReported) {
        this.dateReported = dateReported;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Boolean getFixed() {
        return fixed;
    }

    public void setFixed(Boolean fixed) {
        this.fixed = fixed;
    }

    public Boolean getCanceled() {
        return canceled;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @XmlTransient
    public Collection<Projectversion> getProjectversionCollection() {
        return projectversionCollection;
    }

    public void setProjectversionCollection(Collection<Projectversion> projectversionCollection) {
        this.projectversionCollection = projectversionCollection;
    }

    @XmlTransient
    public Collection<BugProgress> getBugProgressCollection() {
        return bugProgressCollection;
    }

    public void setBugProgressCollection(Collection<BugProgress> bugProgressCollection) {
        this.bugProgressCollection = bugProgressCollection;
    }

    public Users getUserAssigned() {
        return userAssigned;
    }

    public void setUserAssigned(Users userAssigned) {
        this.userAssigned = userAssigned;
    }

    public Users getUserReported() {
        return userReported;
    }

    public void setUserReported(Users userReported) {
        this.userReported = userReported;
    }

    public Project getProjectId() {
        return projectId;
    }

    public void setProjectId(Project projectId) {
        this.projectId = projectId;
    }

    public Bugcategory getBugcategoryId() {
        return bugcategoryId;
    }

    public void setBugcategoryId(Bugcategory bugcategoryId) {
        this.bugcategoryId = bugcategoryId;
    }

    @XmlTransient
    public Collection<Bug> getBugCollection() {
        return bugCollection;
    }

    public void setBugCollection(Collection<Bug> bugCollection) {
        this.bugCollection = bugCollection;
    }

    public Bug getDuplicateOfBugId() {
        return duplicateOfBugId;
    }

    public void setDuplicateOfBugId(Bug duplicateOfBugId) {
        this.duplicateOfBugId = duplicateOfBugId;
    }

    @XmlTransient
    public Collection<Comment> getCommentCollection() {
        return commentCollection;
    }

    public void setCommentCollection(Collection<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }
    
}
