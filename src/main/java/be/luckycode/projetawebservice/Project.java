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
@Table(name = "project")
@XmlRootElement
@SequenceGenerator(name = "sequenceProject", sequenceName = "project_project_id_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "Project.findAll", query = "SELECT p FROM Project p"),
    @NamedQuery(name = "Project.findByProjectId", query = "SELECT p FROM Project p WHERE p.projectId = :projectId"),
    @NamedQuery(name = "Project.findByProjectTitle", query = "SELECT p FROM Project p WHERE p.projectTitle = :projectTitle"),
    @NamedQuery(name = "Project.findByProjectDescription", query = "SELECT p FROM Project p WHERE p.projectDescription = :projectDescription"),
    @NamedQuery(name = "Project.findByDateCreated", query = "SELECT p FROM Project p WHERE p.dateCreated = :dateCreated"),
    @NamedQuery(name = "Project.findByStartDate", query = "SELECT p FROM Project p WHERE p.startDate = :startDate"),
    @NamedQuery(name = "Project.findByEndDate", query = "SELECT p FROM Project p WHERE p.endDate = :endDate"),
    // get root projects (projects which have no parent)
    @NamedQuery(name = "Project.getParentProjects", query = "SELECT p FROM Project p WHERE p.parentProjectId IS NULL"),
    // get child projects
    @NamedQuery(name = "Project.getChildProjects", query = "SELECT p FROM Project p WHERE p.parentProjectId = ?1"),
    @NamedQuery(name = "Project.findByFlagPublic", query = "SELECT p FROM Project p WHERE p.flagPublic = :flagPublic")})
public class Project implements Serializable {
    @Column(name =     "date_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    @Column(name =     "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Column(name =     "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    @Column(name =     "start_date_real")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDateReal;
    @Column(name =     "end_date_real")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDateReal;
    @Column(name = "completed")
    private Boolean completed;
    @Column(name = "canceled")
    private Boolean canceled;
    @Column(name = "deleted")
    private Boolean deleted;
    @JoinTable(name = "project_usergroup_visible", joinColumns = {
        @JoinColumn(name = "project_id", referencedColumnName = "project_id")}, inverseJoinColumns = {
        @JoinColumn(name = "usergroup_id", referencedColumnName = "usergroup_id")})
    @ManyToMany
    private Collection<Usergroup> usergroupCollection;
    @JoinTable(name = "project_user_visible", joinColumns = {
        @JoinColumn(name = "project_id", referencedColumnName = "project_id")}, inverseJoinColumns = {
        @JoinColumn(name = "user_id", referencedColumnName = "user_id")})
    @ManyToMany
    private Collection<User> userCollection;
    @ManyToMany(mappedBy = "projectCollection")
    private Collection<Client> clientCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "projectId")
    private Collection<ProjectProgress> projectProgressCollection;
    @OneToMany(mappedBy = "parentProjectId")
    private Collection<Project> projectCollection;
    @JoinColumn(name = "parent_project_id", referencedColumnName = "project_id")
    @ManyToOne
    private Project parentProjectId;
    @OneToMany(mappedBy = "projectId")
    private Collection<Bug> bugCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "projectId")
    private Collection<Projectversion> projectversionCollection;
    private static final long serialVersionUID = 1L;
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "project_id", nullable = false, unique = true)
    @GeneratedValue(generator = "sequenceProject")
    private Integer projectId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "project_title")
    private String projectTitle;
    @Size(max = 2147483647)
    @Column(name = "project_description")
    private String projectDescription;
    @Basic(optional = false)
    @NotNull
    @Column(name = "flag_public")
    private Boolean flagPublic;
    @JoinColumn(name = "user_created", referencedColumnName = "user_id")
    @ManyToOne
    private User userCreated;
    @JoinColumn(name = "user_assigned", referencedColumnName = "user_id")
    @ManyToOne
    private User userAssigned;
    @OneToMany(mappedBy = "projectId")
    private Collection<Task> taskCollection;
    @OneToMany(mappedBy = "projectId")
    private Collection<Progress> progressCollection;

    public Project() {
    }

    public Project(Integer projectId) {
        this.projectId = projectId;
    }

    public Project(Integer projectId, String projectTitle, boolean flagPublic) {
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.flagPublic = flagPublic;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public Boolean getFlagPublic() {
        return flagPublic;
    }

    public void setFlagPublic(Boolean flagPublic) {
        this.flagPublic = flagPublic;
    }

    public User getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(User userCreated) {
        this.userCreated = userCreated;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (projectId != null ? projectId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Project)) {
            return false;
        }
        Project other = (Project) object;
        if ((this.projectId == null && other.projectId != null) || (this.projectId != null && !this.projectId.equals(other.projectId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.luckycode.projetawebservice.Project[ projectId=" + projectId + " ]";
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
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

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
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
    public Collection<Usergroup> getUsergroupCollection() {
        return usergroupCollection;
    }

    public void setUsergroupCollection(Collection<Usergroup> usergroupCollection) {
        this.usergroupCollection = usergroupCollection;
    }

    @XmlTransient
    public Collection<User> getUserCollection() {
        return userCollection;
    }

    public void setUserCollection(Collection<User> userCollection) {
        this.userCollection = userCollection;
    }

    @XmlTransient
    public Collection<Client> getClientCollection() {
        return clientCollection;
    }

    public void setClientCollection(Collection<Client> clientCollection) {
        this.clientCollection = clientCollection;
    }

    @XmlTransient
    public Collection<ProjectProgress> getProjectProgressCollection() {
        return projectProgressCollection;
    }

    public void setProjectProgressCollection(Collection<ProjectProgress> projectProgressCollection) {
        this.projectProgressCollection = projectProgressCollection;
    }

    @XmlTransient
    public Collection<Project> getProjectCollection() {
        return projectCollection;
    }

    public void setProjectCollection(Collection<Project> projectCollection) {
        this.projectCollection = projectCollection;
    }

    public Project getParentProjectId() {
        return parentProjectId;
    }

    public void setParentProjectId(Project parentProjectId) {
        this.parentProjectId = parentProjectId;
    }

    @XmlTransient
    public Collection<Bug> getBugCollection() {
        return bugCollection;
    }

    public void setBugCollection(Collection<Bug> bugCollection) {
        this.bugCollection = bugCollection;
    }

    @XmlTransient
    public Collection<Projectversion> getProjectversionCollection() {
        return projectversionCollection;
    }

    public void setProjectversionCollection(Collection<Projectversion> projectversionCollection) {
        this.projectversionCollection = projectversionCollection;
    }
    
    public User getUserAssigned() {
        return userAssigned;
    }

    public void setUserAssigned(User userAssigned) {
        this.userAssigned = userAssigned;
    }
    
    @XmlTransient
    public Collection<Task> getTaskCollection() {
        return taskCollection;
    }

    public void setTaskCollection(Collection<Task> taskCollection) {
        this.taskCollection = taskCollection;
    }
    
    @XmlTransient
    public Collection<Progress> getProgressCollection() {
        return progressCollection;
    }

    public void setProgressCollection(Collection<Progress> progressCollection) {
        this.progressCollection = progressCollection;
    }

    
}
