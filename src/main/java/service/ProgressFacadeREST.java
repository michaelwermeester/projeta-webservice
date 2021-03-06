/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import be.luckycode.projetawebservice.Comment;
import be.luckycode.projetawebservice.Progress;
import be.luckycode.projetawebservice.ProgressDummy;
import be.luckycode.projetawebservice.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author michael
 */
@Stateless
@Path("progress")
public class ProgressFacadeREST extends AbstractFacade<Progress> {
    @PersistenceContext(unitName = "be.luckycode_projeta-webservice_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    @Context
    SecurityContext security;

    public ProgressFacadeREST() {
        super(Progress.class);
    }

    @PUT
    @Override
    @Consumes({"application/xml", "application/json"})
    public void edit(Progress entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Progress find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces("application/json")
    public List<Progress> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Progress> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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
    @RolesAllowed({"administrator", "developer"})
    @Consumes("application/json")
    @Produces("application/json")
    public Progress createNewProgress(Progress entity) {

        //entity.setDateCreated(new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
        
        // si pas de 'user création' défini, mettre l'utilisateur authentifié.
        if (entity.getUserCreated() == null) {
            entity.setUserCreated(this.getAuthenticatedUser());
        }
        
        // sauvegarder en DB.
        em.persist(entity);

        return entity;
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
    
    
    // Utilisé par le site web. Retourne les états d'avanvancement pour une tâche spécifié. 
    @GET
    @Path("wstask/{id}")
    public ProgressDummy findProgressByTaskIdWebsite(@PathParam("id") Integer id) {
        
        ProgressDummy retProgressDummy = new ProgressDummy();
        
        
        Query q = em.createNamedQuery("Progress.findByTaskId");
        q.setParameter("taskId", id);
        
        List<Progress> cList = new ArrayList<Progress>(q.getResultList());
        
        
        retProgressDummy.setListProgress(cList);
        
        return retProgressDummy;
    }
    
    // Utilisé par le site web. Retourne les états d'avanvancement pour un projet spécifié. 
    @GET
    @Path("wsproject/{id}")
    public ProgressDummy findProgressByProjectIdWebsite(@PathParam("id") Integer id) {
        
        ProgressDummy retProgressDummy = new ProgressDummy();
        
        
        Query q = em.createNamedQuery("Progress.findByProjectId");
        q.setParameter("projectId", id);
        
        List<Progress> cList = new ArrayList<Progress>(q.getResultList());
        
        
        retProgressDummy.setListProgress(cList);
        
        return retProgressDummy;
    }
    
    @GET
    @Path("wsbug/{id}")
    public ProgressDummy findProgressByBugIdWebsite(@PathParam("id") Integer id) {
        
        ProgressDummy retProgressDummy = new ProgressDummy();
        
        
        Query q = em.createNamedQuery("Progress.findByBugId");
        q.setParameter("bugId", id);
        
        List<Progress> cList = new ArrayList<Progress>(q.getResultList());
        
        
        retProgressDummy.setListProgress(cList);
        
        return retProgressDummy;
    }
}
