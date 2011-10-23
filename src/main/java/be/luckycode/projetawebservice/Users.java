/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.luckycode.projetawebservice;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
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
@Table(name = "users")
@XmlRootElement
@SequenceGenerator(name = "sequenceUser", sequenceName = "users_user_id_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u"),
    @NamedQuery(name = "Users.findByUserId", query = "SELECT u FROM Users u WHERE u.userId = :userId"),
    @NamedQuery(name = "Users.findByUsername", query = "SELECT u FROM Users u WHERE u.username = :username"),
    @NamedQuery(name = "Users.findByPassword", query = "SELECT u FROM Users u WHERE u.password = :password"),
    @NamedQuery(name = "Users.findByFirstName", query = "SELECT u FROM Users u WHERE u.firstName = :firstName"),
    @NamedQuery(name = "Users.findByLastName", query = "SELECT u FROM Users u WHERE u.lastName = :lastName"),
    @NamedQuery(name = "Users.findByEmailAddress", query = "SELECT u FROM Users u WHERE u.emailAddress = :emailAddress"),
    @NamedQuery(name = "Users.findByAddress", query = "SELECT u FROM Users u WHERE u.address = :address"),
    @NamedQuery(name = "Users.findByPhoneNumber", query = "SELECT u FROM Users u WHERE u.phoneNumber = :phoneNumber"),
    @NamedQuery(name = "Users.findByJobTitle", query = "SELECT u FROM Users u WHERE u.jobTitle = :jobTitle")})
public class Users implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    // http://forums.netbeans.org/topic38907.html
    // remove @NotNull from the entity bean. 
    @NotNull 
    //@Column(name = "user_id")
    @Column(name = "user_id", nullable = false, unique = true)
    @GeneratedValue(generator = "sequenceUser")
    private Integer userId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "username")
    private String username;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1024)
    @Column(name = "password")
    private String password;
    @Size(max = 100)
    @Column(name = "first_name")
    private String firstName;
    @Size(max = 100)
    @Column(name = "last_name")
    private String lastName;
    @Size(max = 255)
    @Column(name = "email_address")
    private String emailAddress;
    @Size(max = 255)
    @Column(name = "address")
    private String address;
    @Size(max = 30)
    @Column(name = "phone_number")
    private String phoneNumber;
    @Size(max = 100)
    @Column(name = "job_title")
    private String jobTitle;
    @ManyToMany(mappedBy = "usersCollection")
    private Collection<Usergroup> usergroupCollection;
    @ManyToMany(mappedBy = "usersCollection")
    private Collection<Project> projectCollection;
    @ManyToMany(mappedBy = "usersCollection")
    private Collection<Role> roleCollection;
    @JoinColumn(name = "language", referencedColumnName = "language_code")
    @ManyToOne
    private Language language;
    @OneToMany(mappedBy = "userCreated")
    private Collection<BugProgress> bugProgressCollection;
    @OneToMany(mappedBy = "userCreated")
    private Collection<ProjectProgress> projectProgressCollection;
    @OneToMany(mappedBy = "userAssigned")
    private Collection<Task> taskCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userCreated")
    private Collection<Task> taskCollection1;
    @OneToMany(mappedBy = "userCreated")
    private Collection<Project> projectCollection1;
    @OneToMany(mappedBy = "userCreated")
    private Collection<TaskProgress> taskProgressCollection;
    @OneToMany(mappedBy = "userAssigned")
    private Collection<Bug> bugCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userReported")
    private Collection<Bug> bugCollection1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userCreated")
    private Collection<Comment> commentCollection;

    public Users() {
    }

    public Users(Integer userId) {
        this.userId = userId;
    }

    public Users(Integer userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws NoSuchAlgorithmException {
        
        // encode password using SHA-256 algorithm
        
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());
 
        byte byteData[] = md.digest();
 
        // convert the byte to hex
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        
        this.password = sb.toString(); 
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    @XmlTransient
    public Collection<Usergroup> getUsergroupCollection() {
        return usergroupCollection;
    }

    public void setUsergroupCollection(Collection<Usergroup> usergroupCollection) {
        this.usergroupCollection = usergroupCollection;
    }

    @XmlTransient
    public Collection<Project> getProjectCollection() {
        return projectCollection;
    }

    public void setProjectCollection(Collection<Project> projectCollection) {
        this.projectCollection = projectCollection;
    }

    @XmlTransient
    public Collection<Role> getRoleCollection() {
        return roleCollection;
    }

    public void setRoleCollection(Collection<Role> roleCollection) {
        this.roleCollection = roleCollection;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    @XmlTransient
    public Collection<BugProgress> getBugProgressCollection() {
        return bugProgressCollection;
    }

    public void setBugProgressCollection(Collection<BugProgress> bugProgressCollection) {
        this.bugProgressCollection = bugProgressCollection;
    }

    @XmlTransient
    public Collection<ProjectProgress> getProjectProgressCollection() {
        return projectProgressCollection;
    }

    public void setProjectProgressCollection(Collection<ProjectProgress> projectProgressCollection) {
        this.projectProgressCollection = projectProgressCollection;
    }

    @XmlTransient
    public Collection<Task> getTaskCollection() {
        return taskCollection;
    }

    public void setTaskCollection(Collection<Task> taskCollection) {
        this.taskCollection = taskCollection;
    }

    @XmlTransient
    public Collection<Task> getTaskCollection1() {
        return taskCollection1;
    }

    public void setTaskCollection1(Collection<Task> taskCollection1) {
        this.taskCollection1 = taskCollection1;
    }

    @XmlTransient
    public Collection<Project> getProjectCollection1() {
        return projectCollection1;
    }

    public void setProjectCollection1(Collection<Project> projectCollection1) {
        this.projectCollection1 = projectCollection1;
    }

    @XmlTransient
    public Collection<TaskProgress> getTaskProgressCollection() {
        return taskProgressCollection;
    }

    public void setTaskProgressCollection(Collection<TaskProgress> taskProgressCollection) {
        this.taskProgressCollection = taskProgressCollection;
    }

    @XmlTransient
    public Collection<Bug> getBugCollection() {
        return bugCollection;
    }

    public void setBugCollection(Collection<Bug> bugCollection) {
        this.bugCollection = bugCollection;
    }

    @XmlTransient
    public Collection<Bug> getBugCollection1() {
        return bugCollection1;
    }

    public void setBugCollection1(Collection<Bug> bugCollection1) {
        this.bugCollection1 = bugCollection1;
    }

    @XmlTransient
    public Collection<Comment> getCommentCollection() {
        return commentCollection;
    }

    public void setCommentCollection(Collection<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Users)) {
            return false;
        }
        Users other = (Users) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.luckycode.projetawebservice.Users[ userId=" + userId + " ]";
    }
    
}
