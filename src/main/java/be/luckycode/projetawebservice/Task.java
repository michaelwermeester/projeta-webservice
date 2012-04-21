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
@Table(name = "task")
@XmlRootElement
@SequenceGenerator(name = "sequenceTask", sequenceName = "task_task_id_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "Task.findAll", query = "SELECT t FROM Task t"),
    // get root projects (projects which have no parent) && exclude personal tasks.
    @NamedQuery(name = "Task.getParentTasks", query = "SELECT t FROM Task t WHERE t.parentTaskId IS NULL and t.isPersonal = false"),
    // get root projects (projects which have no parent) which are personal
    @NamedQuery(name = "Task.getPersonalParentTasks", query = "SELECT t FROM Task t WHERE t.parentTaskId IS NULL and t.isPersonal = true and t.userCreated.userId = ?1"),
    // get child projects
    // get root tasks by project ID & exclude personal tasks.
    @NamedQuery(name = "Task.getParentTasksByProjectId", query = "SELECT t FROM Task t WHERE t.parentTaskId IS NULL and t.isPersonal = false and t.projectId.projectId = :projectId"),
    //
    @NamedQuery(name = "Task.getChildTasks", query = "SELECT t FROM Task t WHERE t.parentTaskId = ?1"),
    @NamedQuery(name = "Task.findByTaskId", query = "SELECT t FROM Task t WHERE t.taskId = :taskId")})
public class Task implements Serializable {
    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    @Column(name = "start_date_real")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDateReal;
    @Column(name = "end_date_real")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDateReal;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1024)
    @Column(name = "task_title")
    private String taskTitle;
    @Size(max = 2147483647)
    @Column(name = "task_description")
    private String taskDescription;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_personal")
    private boolean isPersonal;
    @Column(name = "priority")
    private Short priority;
    @Column(name = "canceled")
    private Boolean canceled;
    @Basic(optional = false)
    @NotNull
    @Column(name = "completed")
    private Boolean completed;
    @Column(name = "deleted")
    private Boolean deleted;
    @OneToMany(mappedBy = "taskId")
    private Collection<Progress> progressCollection;
    @JoinColumn(name = "user_assigned", referencedColumnName = "user_id")
    @ManyToOne
    private User userAssigned;
    @JoinColumn(name = "user_created", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private User userCreated;
    @OneToMany(mappedBy = "parentTaskId")
    private Collection<Task> taskCollection;
    @JoinColumn(name = "parent_task_id", referencedColumnName = "task_id")
    @ManyToOne
    private Task parentTaskId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "taskId")
    private Collection<TaskProgress> taskProgressCollection;
    @OneToMany(mappedBy = "taskId")
    private Collection<Comment> commentCollection;
    private static final long serialVersionUID = 1L;
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "task_id", nullable = false, unique = true)
    @GeneratedValue(generator = "sequenceTask")
    private Integer taskId;
    @JoinColumn(name = "project_id", referencedColumnName = "project_id")
    @ManyToOne
    private Project projectId;

    public Task() {
    }

    public Task(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (taskId != null ? taskId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Task)) {
            return false;
        }
        Task other = (Task) object;
        if ((this.taskId == null && other.taskId != null) || (this.taskId != null && !this.taskId.equals(other.taskId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.luckycode.projetawebservice.Task[ taskId=" + taskId + " ]";
    }

    public boolean getIsPersonal() {
        return isPersonal;
    }

    public void setIsPersonal(boolean isPersonal) {
        this.isPersonal = isPersonal;
    }

    public Short getPriority() {
        return priority;
    }

    public void setPriority(Short priority) {
        this.priority = priority;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDateReal() {
        return startDateReal;
    }

    public void setStartDateReal(Date startDateReal) {
        this.startDateReal = startDateReal;
    }

    public Date getEndDateReal() {
        return endDateReal;
    }

    public void setEndDateReal(Date endDateReal) {
        this.endDateReal = endDateReal;
    }

    public Boolean getCanceled() {
        return canceled;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public User getUserAssigned() {
        return userAssigned;
    }

    public void setUserAssigned(User userAssigned) {
        this.userAssigned = userAssigned;
    }

    public User getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(User userCreated) {
        this.userCreated = userCreated;
    }

    @XmlTransient
    public Collection<Task> getTaskCollection() {
        return taskCollection;
    }

    public void setTaskCollection(Collection<Task> taskCollection) {
        this.taskCollection = taskCollection;
    }

    public Task getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(Task parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    @XmlTransient
    public Collection<TaskProgress> getTaskProgressCollection() {
        return taskProgressCollection;
    }

    public void setTaskProgressCollection(Collection<TaskProgress> taskProgressCollection) {
        this.taskProgressCollection = taskProgressCollection;
    }

    @XmlTransient
    public Collection<Comment> getCommentCollection() {
        return commentCollection;
    }

    public void setCommentCollection(Collection<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }
    
    public Project getProjectId() {
        return projectId;
    }

    public void setProjectId(Project projectId) {
        this.projectId = projectId;
    }
    
    @XmlTransient
    public Collection<Progress> getProgressCollection() {
        return progressCollection;
    }

    public void setProgressCollection(Collection<Progress> progressCollection) {
        this.progressCollection = progressCollection;
    }
}
