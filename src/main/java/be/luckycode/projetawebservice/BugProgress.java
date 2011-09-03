/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.luckycode.projetawebservice;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author michael
 */
@Entity
@Table(name = "bug_progress")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BugProgress.findAll", query = "SELECT b FROM BugProgress b"),
    @NamedQuery(name = "BugProgress.findByDateCreated", query = "SELECT b FROM BugProgress b WHERE b.dateCreated = :dateCreated"),
    @NamedQuery(name = "BugProgress.findByPercentageComplete", query = "SELECT b FROM BugProgress b WHERE b.percentageComplete = :percentageComplete"),
    @NamedQuery(name = "BugProgress.findByProgressComment", query = "SELECT b FROM BugProgress b WHERE b.progressComment = :progressComment"),
    @NamedQuery(name = "BugProgress.findByBugProgressId", query = "SELECT b FROM BugProgress b WHERE b.bugProgressId = :bugProgressId")})
public class BugProgress implements Serializable {
    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Column(name = "date_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    @Column(name = "percentage_complete")
    private Short percentageComplete;
    @Size(max = 2147483647)
    @Column(name = "progress_comment")
    private String progressComment;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "bug_progress_id")
    private Integer bugProgressId;
    @JoinColumn(name = "user_created", referencedColumnName = "user_id")
    @ManyToOne
    private Users userCreated;
    @JoinColumn(name = "status_id", referencedColumnName = "status_id")
    @ManyToOne(optional = false)
    private Status statusId;
    @JoinColumn(name = "bug_id", referencedColumnName = "bug_id")
    @ManyToOne(optional = false)
    private Bug bugId;

    public BugProgress() {
    }

    public BugProgress(Integer bugProgressId) {
        this.bugProgressId = bugProgressId;
    }

    public BugProgress(Integer bugProgressId, Date dateCreated) {
        this.bugProgressId = bugProgressId;
        this.dateCreated = dateCreated;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Short getPercentageComplete() {
        return percentageComplete;
    }

    public void setPercentageComplete(Short percentageComplete) {
        this.percentageComplete = percentageComplete;
    }

    public String getProgressComment() {
        return progressComment;
    }

    public void setProgressComment(String progressComment) {
        this.progressComment = progressComment;
    }

    public Integer getBugProgressId() {
        return bugProgressId;
    }

    public void setBugProgressId(Integer bugProgressId) {
        this.bugProgressId = bugProgressId;
    }

    public Users getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(Users userCreated) {
        this.userCreated = userCreated;
    }

    public Status getStatusId() {
        return statusId;
    }

    public void setStatusId(Status statusId) {
        this.statusId = statusId;
    }

    public Bug getBugId() {
        return bugId;
    }

    public void setBugId(Bug bugId) {
        this.bugId = bugId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bugProgressId != null ? bugProgressId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BugProgress)) {
            return false;
        }
        BugProgress other = (BugProgress) object;
        if ((this.bugProgressId == null && other.bugProgressId != null) || (this.bugProgressId != null && !this.bugProgressId.equals(other.bugProgressId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.luckycode.projetawebservice.BugProgress[ bugProgressId=" + bugProgressId + " ]";
    }
    
}
