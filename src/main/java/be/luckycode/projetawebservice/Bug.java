/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.luckycode.projetawebservice;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
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
@SequenceGenerator(name = "sequenceBug", sequenceName = "bug_bug_id_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "Bug.findAll", query = "SELECT b FROM Bug b"),
    // get root tasks by project ID & exclude personal tasks.
    @NamedQuery(name = "Bug.getBugsByProjectId", query = "SELECT b FROM Bug b WHERE b.projectId.projectId = :projectId and (b.deleted = false or b.deleted is null)"),
    
    // get bugs reported by user.
    @NamedQuery(name = "Bug.getBugsReported", query = "SELECT b FROM Bug b WHERE b.userReported.userId = :userId and (b.deleted = false or b.deleted is null)"),
    
    @NamedQuery(name = "Bug.findByBugId", query = "SELECT b FROM Bug b WHERE b.bugId = :bugId")})
public class Bug implements Serializable {
    @Basic(optional = false)
    //@NotNull
    @Column(name = "date_reported", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateReported;
    @Column(name = "priority")
    private Short priority;
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
    private User userAssigned;
    @JoinColumn(name = "user_reported", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private User userReported;
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
    @Basic(optional = false)
    @NotNull
    @Column(name = "bug_id", nullable = false, unique = true)
    @GeneratedValue(generator = "sequenceBug")
    private Integer bugId;
    @OneToMany(mappedBy = "bugId")
    private Collection<Progress> progressCollection;

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

    public User getUserAssigned() {
        return userAssigned;
    }

    public void setUserAssigned(User userAssigned) {
        this.userAssigned = userAssigned;
    }

    public User getUserReported() {
        return userReported;
    }

    public void setUserReported(User userReported) {
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
    
    @XmlTransient
    public Collection<Progress> getProgressCollection() {
        return progressCollection;
    }

    public void setProgressCollection(Collection<Progress> progressCollection) {
        this.progressCollection = progressCollection;
    }

}
