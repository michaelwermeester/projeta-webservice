/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import be.luckycode.projetawebservice.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    
//    @POST
//    @Path("create")
//    @RolesAllowed("administrator")
//    @Consumes("application/json")
//    @Produces("application/json")
//    public String createNewBug(Bug entity) {
//
//        // Créer la nouvelle tâche dans la base de données.
//        em.persist(entity);
//
//        // Créer état, pourcentage d'avancement etc. par défaut.
//        initDefaultProgressForNewBug(entity);
//        
//        em.flush();
//        
//        
//
//        // SAME CODE AS IN CREATE !!!
//        List<Bug> bugList = new ArrayList<Bug>();
//        bugList.add(super.find(entity.getBugId()));
//
//        ObjectMapper mapper = new ObjectMapper();
//        List<Map> bugListMap = new ArrayList<Map>();
//
//        getBugs(bugList, bugListMap);
//
//        String retVal = "";
//
//        HashMap<String, Object> retBugs = new HashMap<String, Object>();
//        retBugs.put("bug", bugListMap);
//
//        try {
//            retVal = mapper.writeValueAsString(retBugs);
//        } catch (IOException ex) {
//            Logger.getLogger(TaskFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return retVal;
//    }

    @PUT
    //@Override
    @Path("update")
    @Consumes("application/json")
    @Produces("application/json")
    public void updateBug(Bug entity) {
        
        // fetch user to be updated.
        Bug bug = super.find(entity.getBugId());

        if (entity.getTitle() != null) {
            bug.setTitle(entity.getTitle());
        }
        if (entity.getDetails() != null) {
            bug.setDetails(entity.getDetails());
        }
        
        super.edit(bug);
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

//    @GET
//    @Override
//    @Path("test")
//    @Produces("application/json")
//    public List<Bug> findAll() {
//        
//        /*List<Bug> bugList = super.findAll();
//        
//        for (Bug b : bugList) {
//            
//        }
//        
//        return bugList;*/
//        
//        return super.findAll();
//    }
    
    // returns parent objects including its children
    @GET
    //@Path("testnew")
    @Produces("application/json")
    public String findAllBugs() {

        String retVal = "";

        ObjectMapper mapper = new ObjectMapper();

        List<Map> bugListMap = new ArrayList<Map>();



        // get root projects (projects which have no parent)
        //Query q = em.createQuery("SELECT p FROM Project p WHERE p.parentProjectId IS NULL");
        Query q = em.createNamedQuery("Bug.findAll");

        List<Bug> bugList = new ArrayList<Bug>();
        bugList = q.getResultList();


        // get projects and its children
        getBugs(bugList, bugListMap);


        HashMap<String, Object> retProjects = new HashMap<String, Object>();
        retProjects.put("bug", bugListMap);

        try {
            retVal = mapper.writeValueAsString(retProjects);
        } catch (IOException ex) {
            Logger.getLogger(ProjectFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;


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
        
        // Créer état, pourcentage d'avancement etc. par défaut.
        initDefaultProgressForNewBug(entity);
        
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
    
    // Créer état, pourcentage d'avancement etc. par défaut.
    private void initDefaultProgressForNewBug(Bug entity) {
        Progress progress = new Progress();
        progress.setPercentageComplete((short)0);
        progress.setProgressComment("Rapport de bogue créé.");
        progress.setStatusId(new Status(13));
        progress.setUserCreated(getAuthenticatedUser());
        progress.setBugId(entity);
        
        em.persist(progress);
    }
    
    private void getBugs(List<Bug> bugList, List<Map> bugMapList) {
        // if list is not empty
        if (bugList.isEmpty() == false) {
            for (Bug b : bugList) {

                // retourner/inclure seulement s'il ne s'agit pas d'une tâche supprimée.  
                if (b.getDeleted() == null || b.getDeleted() == false) {

                    Map<String, Object> bugData = new HashMap<String, Object>();
                    Map<String, Object> userStruct = new HashMap<String, Object>();
                    Map<String, Object> userAssignedStruct = new HashMap<String, Object>();
                    //Map<String, String> nameStruct = new HashMap<String, String>();

                    userStruct.put("userId", b.getUserReported().getUserId().toString());
                    userStruct.put("username", b.getUserReported().getUsername());
                    bugData.put("userReported", userStruct);

                    bugData.put("dateReported", CommonMethods.convertDate(b.getDateReported()));
                    //bugData.put("startDate", CommonMethods.convertDate(t.getStartDate()));
                    //taskData.put("endDate", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(t.getEndDate()));
                    //taskData.put("startDate", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(t.getStartDate()));

                    if (b.getDetails() != null) {
                        bugData.put("details", b.getDetails());
                    }
                    bugData.put("bugId", b.getBugId().toString());
                    bugData.put("title", b.getTitle());

                    if (b.getFixed() != null) {
                        bugData.put("fixed", b.getFixed());
                    } else {
                        bugData.put("fixed", false);
                    }
                    
                    if (b.getCanceled() != null) {
                        bugData.put("canceled", b.getCanceled());
                    } else {
                        bugData.put("canceled", false);
                    }
                    
                    if (b.getDeleted() != null) {
                        bugData.put("deleted", b.getDeleted());
                    } else {
                        bugData.put("deleted", false);
                    }

                    if (b.getPriority() != null) {
                        bugData.put("priority", b.getPriority().toString());
                    } else // retourner 1 comme priorité par défaut.
                    {
                        bugData.put("priority", "1");
                    }

                    if (b.getUserAssigned() != null) {
                        userAssignedStruct.put("userId", b.getUserAssigned().getUserId().toString());
                        userAssignedStruct.put("username", b.getUserAssigned().getUsername());
                        bugData.put("userAssigned", userAssignedStruct);
                        //taskData.put("userAssigned", t.getUserAssigned().getUserId().toString());
                    }
                    
                    // état et pourcentage.
                    Progress progress = getProgressForBugId(b.getBugId());
                    
                    if (progress != null) {
                        // état.
                        if (progress.getStatusId() != null && progress.getStatusId().getStatusName() != null) 
                            bugData.put("bugStatus", progress.getStatusId().getStatusName());
                        // pourcentage.
                        bugData.put("bugPercentage", progress.getPercentageComplete().toString());
                    }
                    
                    // nom du projet.
                    if (b.getProjectId() != null) {
                        if (b.getProjectId().getProjectTitle() != null)
                            bugData.put("projectTitle", b.getProjectId().getProjectTitle());
                    }
                    

                    // get child projects, if any
                    //getChildTasks(t, userStruct, bugData);

                    bugMapList.add(bugData);
                }
            }
        }
    }
    
    // retourne le statut actuel de la tâche.
    private Progress getProgressForBugId(Integer bugId) {
        
        // liste des 'Progress' pour le projet.
        Query qryPercentage = em.createNamedQuery("Progress.findByBugId");
        qryPercentage.setParameter("bugId", bugId);
        // obtenir le plus récent.
        qryPercentage.setMaxResults(1); // top 1 result
        // lire en liste.
        List<Progress> listProgress = new ArrayList<Progress>();
        listProgress = qryPercentage.getResultList();
        
        // retourner le statut, s'il y'en a un.
        if (listProgress.size() > 0) {
            return listProgress.get(0);
        } else {
            return null;
        }
    }
}
