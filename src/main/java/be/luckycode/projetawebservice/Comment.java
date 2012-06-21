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
@Table(name = "comment")
@XmlRootElement
@SequenceGenerator(name = "sequenceComment", sequenceName = "comment_comment_id_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "Comment.findAll", query = "SELECT c FROM Comment c"),
    @NamedQuery(name = "Comment.findByCommentId", query = "SELECT c FROM Comment c WHERE c.commentId = :commentId"),
    @NamedQuery(name = "Comment.findByComment", query = "SELECT c FROM Comment c WHERE c.comment = :comment"),
    // retourner les commentaires liés à un tâche.
    @NamedQuery(name = "Comment.findByTaskId", query = "SELECT c FROM Comment c WHERE c.taskId.taskId = :taskId order by c.dateCreated ASC"),
    @NamedQuery(name = "Comment.findByBugId", query = "SELECT c FROM Comment c WHERE c.bugId.bugId = :bugId order by c.dateCreated ASC"),
    // retourner les commentaires liés à un projet.
    @NamedQuery(name = "Comment.findByProjectId", query = "SELECT c FROM Comment c WHERE c.projectId.projectId = :projectId order by c.dateCreated ASC"),
    
    @NamedQuery(name = "Comment.findByDateCreated", query = "SELECT c FROM Comment c WHERE c.dateCreated = :dateCreated")})
public class Comment implements Serializable {
    @Basic(optional = false)
    //@NotNull
    @Column(name = "date_created", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    private static final long serialVersionUID = 1L;
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "comment_id", nullable = false, unique = true)
    @GeneratedValue(generator = "sequenceComment")
    private Integer commentId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "comment")
    private String comment;
    @JoinColumn(name = "user_created", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private User userCreated;
    @JoinColumn(name = "task_id", referencedColumnName = "task_id")
    @ManyToOne
    private Task taskId;
    @JoinColumn(name = "bug_id", referencedColumnName = "bug_id")
    @ManyToOne
    private Bug bugId;
    @JoinColumn(name = "project_id", referencedColumnName = "project_id")
    @ManyToOne
    private Project projectId;

    public Comment() {
    }

    public Comment(Integer commentId) {
        this.commentId = commentId;
    }

    public Comment(Integer commentId, String comment, Date dateCreated) {
        this.commentId = commentId;
        this.comment = comment;
        this.dateCreated = dateCreated;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
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

    public Bug getBugId() {
        return bugId;
    }
    
    public Project getProjectId() {
        return projectId;
    }

    public void setProjectId(Project projectId) {
        this.projectId = projectId;
    }

    public void setBugId(Bug bugId) {
        this.bugId = bugId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (commentId != null ? commentId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Comment)) {
            return false;
        }
        Comment other = (Comment) object;
        if ((this.commentId == null && other.commentId != null) || (this.commentId != null && !this.commentId.equals(other.commentId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.luckycode.projetawebservice.Comment[ commentId=" + commentId + " ]";
    }
    
}
