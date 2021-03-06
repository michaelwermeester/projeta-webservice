/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import be.luckycode.projetawebservice.Comment;
import be.luckycode.projetawebservice.CommentDummy;
import be.luckycode.projetawebservice.Task;
import be.luckycode.projetawebservice.User;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author michael
 */
@Stateless
@Path("comments")
public class CommentFacadeREST extends AbstractFacade<Comment> {
    @PersistenceContext(unitName = "be.luckycode_projeta-webservice_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    @Context
    SecurityContext security;

    public CommentFacadeREST() {
        super(Comment.class);
    }

    @POST
    @Override
    @Consumes({"application/xml", "application/json"})
    public void create(Comment entity) {
        super.create(entity);
    }

    @PUT
    @Override
    @Consumes({"application/xml", "application/json"})
    public void edit(Comment entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    
    // utilisé par le site web. Retourne les commentaires pour une tâche spécifié. 
    @GET
    @Path("wstask/{id}")
    public CommentDummy findCommentsByTaskIdWebsite(@PathParam("id") Integer id) {
        
        CommentDummy retCommentDummy = new CommentDummy();
        
        
        Query q = em.createNamedQuery("Comment.findByTaskId");
        q.setParameter("taskId", id);
        
        List<Comment> cList = new ArrayList<Comment>(q.getResultList());
        
        
        retCommentDummy.setListComment(cList);
        
        return retCommentDummy;
    }
    
    @GET
    @Path("task/{id}")
    @Produces("application/json")
    public String findByTaskId(@PathParam("id") Integer id) {
        
        String retVal = "";

        ObjectMapper mapper = new ObjectMapper();
        
        List<Map> commentList = new ArrayList<Map>();
        
        Query q = em.createNamedQuery("Comment.findByTaskId");
        q.setParameter("taskId", id);
        
        List<Comment> cList = new ArrayList<Comment>(q.getResultList());
        
        // créer une Map à partir de la liste.
        createMapFromCommentList(cList, commentList);
            


        HashMap<String, Object> retComments = new HashMap<String, Object>();
        retComments.put("comment", commentList);

        try {
            retVal = mapper.writeValueAsString(retComments);
        } catch (IOException ex) {
            Logger.getLogger(CommentFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        return retVal;
    }
    
    private void createMapFromCommentList(List<Comment> commentList, List<Map> commentMapList) {
        
        if (commentList.isEmpty() == false) {
            for (Comment c : commentList) {
                
                Map<String, Object> commentData = new HashMap<String, Object>();
                Map<String, Object> userStruct = new HashMap<String, Object>();
                
                userStruct.put("userId", c.getUserCreated().getUserId().toString());
                userStruct.put("username", c.getUserCreated().getUsername());
                if (c.getUserCreated().getFirstName() != null && c.getUserCreated().getFirstName().length() > 0)
                    userStruct.put("firstName", c.getUserCreated().getFirstName());
                if (c.getUserCreated().getLastName() != null && c.getUserCreated().getLastName().length() > 0)
                    userStruct.put("lastName", c.getUserCreated().getLastName());
                commentData.put("userCreated", userStruct);
                
                commentData.put("comment", c.getComment());
                commentData.put("dateCreated", CommonMethods.convertDate(c.getDateCreated()));
                
                
                commentMapList.add(commentData);
            }
        }
    }
    
    
    @GET
    @Path("{id}")
    @Produces("application/json")
    public Comment find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces("application/json")
    public List<Comment> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Comment> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
    }

    @java.lang.Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    
    @POST
    @Path("create")
    @RolesAllowed({"administrator", "developer", "user"})
    @Consumes("application/json")
    @Produces("application/json")
    public Comment createNewComment(Comment entity) {
        
        // si pas de 'user création' défini, mettre l'utilisateur authentifié.
        if (entity.getUserCreated() == null) {
            //entity.setUserCreated(new User(this.getAuthenticatedUser().getUserId()));
            entity.setUserCreated(this.getAuthenticatedUser());
        }
        
        // sauvegarder en DB.
        em.persist(entity);

        return entity;
    }
    
    // utilisé par le site web. Retourne les commentaire pour un projet spécifié. 
    @GET
    @Path("wsproject/{id}")
    public CommentDummy findCommentsByProjectIdWebsite(@PathParam("id") Integer id) {
        
        CommentDummy retCommentDummy = new CommentDummy();
        
        
        Query q = em.createNamedQuery("Comment.findByProjectId");
        q.setParameter("projectId", id);
        
        List<Comment> cList = new ArrayList<Comment>(q.getResultList());
        
        
        retCommentDummy.setListComment(cList);
        
        return retCommentDummy;
    }
    
    // returns user ID of the authenticated user.
    public User getAuthenticatedUser() {
        String username;
        username = security.getUserPrincipal().getName();

        Query q = em.createNamedQuery("User.findByUsername");
        q.setParameter("username", username);

        List<User> userList = new ArrayList<User>();
        userList = q.getResultList();

        if (userList.size() == 1) {
            return userList.get(0);
        } else {
            return null;
        }
    }
    
    
    
    @GET
    @Path("wsbug/{id}")
    public CommentDummy findCommentsByBugIdWebsite(@PathParam("id") Integer id) {
        
        CommentDummy retCommentDummy = new CommentDummy();
        
        
        Query q = em.createNamedQuery("Comment.findByBugId");
        q.setParameter("bugId", id);
        
        List<Comment> cList = new ArrayList<Comment>(q.getResultList());
        
        
        retCommentDummy.setListComment(cList);
        
        return retCommentDummy;
    }
}
