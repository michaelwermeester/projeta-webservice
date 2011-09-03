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
@Table(name = "task_progress")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TaskProgress.findAll", query = "SELECT t FROM TaskProgress t"),
    @NamedQuery(name = "TaskProgress.findByDateCreated", query = "SELECT t FROM TaskProgress t WHERE t.dateCreated = :dateCreated"),
    @NamedQuery(name = "TaskProgress.findByPercentageComplete", query = "SELECT t FROM TaskProgress t WHERE t.percentageComplete = :percentageComplete"),
    @NamedQuery(name = "TaskProgress.findByProgressComment", query = "SELECT t FROM TaskProgress t WHERE t.progressComment = :progressComment"),
    @NamedQuery(name = "TaskProgress.findByTaskProgressId", query = "SELECT t FROM TaskProgress t WHERE t.taskProgressId = :taskProgressId")})
public class TaskProgress implements Serializable {
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
    @Basic(optional = false)
    @NotNull
    @Column(name = "task_progress_id")
    private Integer taskProgressId;
    @JoinColumn(name = "user_created", referencedColumnName = "user_id")
    @ManyToOne
    private Users userCreated;
    @JoinColumn(name = "task_id", referencedColumnName = "task_id")
    @ManyToOne(optional = false)
    private Task taskId;
    @JoinColumn(name = "status_id", referencedColumnName = "status_id")
    @ManyToOne(optional = false)
    private Status statusId;

    public TaskProgress() {
    }

    public TaskProgress(Integer taskProgressId) {
        this.taskProgressId = taskProgressId;
    }

    public TaskProgress(Integer taskProgressId, Date dateCreated) {
        this.taskProgressId = taskProgressId;
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

    public Integer getTaskProgressId() {
        return taskProgressId;
    }

    public void setTaskProgressId(Integer taskProgressId) {
        this.taskProgressId = taskProgressId;
    }

    public Users getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(Users userCreated) {
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (taskProgressId != null ? taskProgressId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TaskProgress)) {
            return false;
        }
        TaskProgress other = (TaskProgress) object;
        if ((this.taskProgressId == null && other.taskProgressId != null) || (this.taskProgressId != null && !this.taskProgressId.equals(other.taskProgressId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.luckycode.projetawebservice.TaskProgress[ taskProgressId=" + taskProgressId + " ]";
    }
    
}
