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
@Table(name = "project_progress")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProjectProgress.findAll", query = "SELECT p FROM ProjectProgress p"),
    @NamedQuery(name = "ProjectProgress.findByDateCreated", query = "SELECT p FROM ProjectProgress p WHERE p.dateCreated = :dateCreated"),
    @NamedQuery(name = "ProjectProgress.findByPercentageComplete", query = "SELECT p FROM ProjectProgress p WHERE p.percentageComplete = :percentageComplete"),
    @NamedQuery(name = "ProjectProgress.findByProgressComment", query = "SELECT p FROM ProjectProgress p WHERE p.progressComment = :progressComment"),
    @NamedQuery(name = "ProjectProgress.findByProjectProgressId", query = "SELECT p FROM ProjectProgress p WHERE p.projectProgressId = :projectProgressId")})
public class ProjectProgress implements Serializable {
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
    @Column(name = "project_progress_id")
    private Integer projectProgressId;
    @JoinColumn(name = "user_created", referencedColumnName = "user_id")
    @ManyToOne
    private Users userCreated;
    @JoinColumn(name = "status_id", referencedColumnName = "status_id")
    @ManyToOne(optional = false)
    private Status statusId;
    @JoinColumn(name = "project_id", referencedColumnName = "project_id")
    @ManyToOne(optional = false)
    private Project projectId;

    public ProjectProgress() {
    }

    public ProjectProgress(Integer projectProgressId) {
        this.projectProgressId = projectProgressId;
    }

    public ProjectProgress(Integer projectProgressId, Date dateCreated) {
        this.projectProgressId = projectProgressId;
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

    public Integer getProjectProgressId() {
        return projectProgressId;
    }

    public void setProjectProgressId(Integer projectProgressId) {
        this.projectProgressId = projectProgressId;
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

    public Project getProjectId() {
        return projectId;
    }

    public void setProjectId(Project projectId) {
        this.projectId = projectId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (projectProgressId != null ? projectProgressId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProjectProgress)) {
            return false;
        }
        ProjectProgress other = (ProjectProgress) object;
        if ((this.projectProgressId == null && other.projectProgressId != null) || (this.projectProgressId != null && !this.projectProgressId.equals(other.projectProgressId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.luckycode.projetawebservice.ProjectProgress[ projectProgressId=" + projectProgressId + " ]";
    }
    
}
