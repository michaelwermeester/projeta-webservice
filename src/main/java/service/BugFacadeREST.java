/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import be.luckycode.projetawebservice.*;
import java.util.ArrayList;
import java.util.List;
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

/**
 *
 * @author michael
 */
@Stateless
@Path("bugs")
public class BugFacadeREST extends AbstractFacade<Bug> {
    @PersistenceContext(unitName = "be.luckycode_projeta-webservice_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    @Context
    SecurityContext security;

    public BugFacadeREST() {
        super(Bug.class);
    }

    /*@POST
    @Override
    @Consumes("application/json")
    public void create(Bug entity) {
        super.create(entity);
    }*/

    @PUT
    @Override
    @Consumes("application/json")
    public void edit(Bug entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public Bug find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces("application/json")
    public List<Bug> findAll() {
        
        /*List<Bug> bugList = super.findAll();
        
        for (Bug b : bugList) {
            
        }
        
        return bugList;*/
        
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Bug> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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
    @Consumes("application/json")
    @Produces("application/json")
    public Bug createNewBug(Bug entity) {
        
        // si pas de 'user création' défini, mettre l'utilisateur authentifié.
        if (entity.getUserReported() == null) {
            //entity.setUserCreated(new User(this.getAuthenticatedUser().getUserId()));
            entity.setUserReported(this.getAuthenticatedUser());
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
    
    
    // FOR WEBSITE !!!
    @GET
    @Path("wsproject/{id}")
    public BugDummy findBugsByProjectIdPOJO(@PathParam("id") Integer id) {
        
        BugDummy bugDummy = new BugDummy();
        //projDummy.setListProject(super.findAll());
        
        //List<Project> listProjTmp = super.findAll();
        //Query q = em.createNamedQuery("Project.getParentProjects");

        List<Bug> bugList = new ArrayList<Bug>();
        Query q = em.createNamedQuery("Bug.getBugsByProjectId");
        q.setParameter("projectId", id);
        
        bugList = q.getResultList();
        
        List<BugSimpleWebSite> listBugs = new ArrayList<BugSimpleWebSite>();
        
        for (Bug b_tmp : bugList) {
            
            BugSimpleWebSite p = new BugSimpleWebSite();
            p.setBugTitle(b_tmp.getTitle());
            p.setBugId(b_tmp.getBugId());
            if (b_tmp.getBugcategoryId() != null)
                p.setBugType(b_tmp.getBugcategoryId().getCategoryName());
            
            //getChildBugsWebSite(p);
            
            listBugs.add(p);
        }
        
        bugDummy.setListBug(listBugs);
        
        return bugDummy;
    }
    
    // FOR WEBSITE !!!
    /*private void getChildBugsWebSite(ProjectSimpleWebSite p) {
        // get child projects
        Query qry_child_bugs = em.createNamedQuery("Bug.getChildBugs");
        Bug p_qry = new Bug(p.getProjectId());
        qry_child_bugs.setParameter(1, p_qry);

        List<Bug> childBugList = new ArrayList<Bug>();
        childBugList = qry_child_bugs.getResultList();
        
        List<ProjectSimpleWebSite> listSubProject = new ArrayList<ProjectSimpleWebSite>();
        
        for (Bug b_tmp : childBugList) {
            
            ProjectSimpleWebSite p_sub = new ProjectSimpleWebSite();
            p_sub.setProjectTitle(b_tmp.getTitle());
            p_sub.setProjectId(b_tmp.getBugId());
            
            getChildBugsWebSite(p_sub);
            
            listSubProject.add(p_sub);
        }
        
        p.setChildProject(listSubProject);
    }*/
}
