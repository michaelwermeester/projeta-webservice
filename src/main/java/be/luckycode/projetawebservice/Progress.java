/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.luckycode.projetawebservice;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author michael
 */
@Entity
@Table(name = "progress")
@XmlRootElement
@SequenceGenerator(name = "sequenceProgress", sequenceName = "progress_progress_id_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "Progress.findAll", query = "SELECT p FROM Progress p"),
    @NamedQuery(name = "Progress.findByIdProgress", query = "SELECT p FROM Progress p WHERE p.idProgress = :idProgress"),
    @NamedQuery(name = "Progress.findByDateCreated", query = "SELECT p FROM Progress p WHERE p.dateCreated = :dateCreated"),
    @NamedQuery(name = "Progress.findByPercentageComplete", query = "SELECT p FROM Progress p WHERE p.percentageComplete = :percentageComplete"),
    // findByBugId
    @NamedQuery(name = "Progress.findByBugId", query = "SELECT p FROM Progress p WHERE p.bugId.bugId = :bugId order by p.dateCreated ASC"),
    
    // findByTaskId
    @NamedQuery(name = "Progress.findByTaskId", query = "SELECT p FROM Progress p WHERE p.taskId.taskId = :taskId order by p.dateCreated ASC"),
    // findByProjectId
    @NamedQuery(name = "Progress.findByProjectId", query = "SELECT p FROM Progress p WHERE p.projectId.projectId = :projectId order by p.dateCreated DESC"),
    
    @NamedQuery(name = "Progress.findByProgressComment", query = "SELECT p FROM Progress p WHERE p.progressComment = :progressComment")})
public class Progress implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "id_progress", nullable = false, unique = true)
    @GeneratedValue(generator = "sequenceProgress")
    private Integer idProgress;
    @Basic(optional = false)
    //@NotNull
    @Column(name = "date_created", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    @Column(name = "percentage_complete")
    private Short percentageComplete;
    @Size(max = 2147483647)
    @Column(name = "progress_comment")
    private String progressComment;
    @JoinColumn(name = "user_created", referencedColumnName = "user_id")
    @ManyToOne
    private User userCreated;
    @JoinColumn(name = "task_id", referencedColumnName = "task_id")
    @ManyToOne
    private Task taskId;
    @JoinColumn(name = "status_id", referencedColumnName = "status_id")
    @ManyToOne(optional = false)
    private Status statusId;
    @JoinColumn(name = "project_id", referencedColumnName = "project_id")
    @ManyToOne
    private Project projectId;
    @JoinColumn(name = "bug_id", referencedColumnName = "bug_id")
    @ManyToOne
    private Bug bugId;

    public Progress() {
    }

    public Progress(Integer idProgress) {
        this.idProgress = idProgress;
    }

    public Progress(Integer idProgress, Date dateCreated) {
        this.idProgress = idProgress;
        this.dateCreated = dateCreated;
    }

    public Integer getIdProgress() {
        return idProgress;
    }

    public void setIdProgress(Integer idProgress) {
        this.idProgress = idProgress;
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

    public User getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(User userCreated) {
        this.userCreated = userCreated;
    }

    public Task getTaskId() {
        return taskId;
    }

    public void setTaskId(Task taskId) {
        this.taskId = taskId;
    }

    public Status getStatusId() {
        return statusId;
    }

    public void setStatusId(Status statusId) {
        this.statusId = statusId;
    }

    public Project getProjectId() {
        return projectId;
    }

    public void setProjectId(Project projectId) {
        this.projectId = projectId;
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
        hash += (idProgress != null ? idProgress.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Progress)) {
            return false;
        }
        Progress other = (Progress) object;
        if ((this.idProgress == null && other.idProgress != null) || (this.idProgress != null && !this.idProgress.equals(other.idProgress))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.luckycode.projetawebservice.Progress[ idProgress=" + idProgress + " ]";
    }
    
}
