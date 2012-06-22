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
import javax.ws.rs.*;
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

    @PUT
    @Path("update")
    @RolesAllowed({"administrator", "developer"})
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
        if (entity.getUserAssigned() != null) {
            bug.setUserAssigned(entity.getUserAssigned());
        }
        if (entity.getPriority() != null) {
            bug.setPriority(entity.getPriority());
        }
        if (entity.getFixed() != null) {
            bug.setFixed(entity.getFixed());
        }
        if (entity.getBugcategoryId() != null) {
            bug.setBugcategoryId(entity.getBugcategoryId());
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
    
    // returns parent objects including its children
    /*@GET
    @Produces("application/json")
    public String findAllBugs() {

        String retVal = "";

        ObjectMapper mapper = new ObjectMapper();

        List<Map> bugListMap = new ArrayList<Map>();

        // get all bugs from database.
        Query q = em.createNamedQuery("Bug.findAll");

        List<Bug> bugList = new ArrayList<Bug>();
        bugList = q.getResultList();


        // get bugs
        getBugs(bugList, bugListMap);


        HashMap<String, Object> retProjects = new HashMap<String, Object>();
        retProjects.put("bug", bugListMap);

        try {
            retVal = mapper.writeValueAsString(retProjects);
        } catch (IOException ex) {
            Logger.getLogger(ProjectFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;
    }*/

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
    
    
    // utilisé par le site web. 
    @GET
    @Path("wsproject/{id}")
    public BugDummy findBugsByProjectIdPOJO(@PathParam("id") Integer id) {
        
        BugDummy bugDummy = new BugDummy();

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
            // statut.
            p.setProjectStatus(getStatusForBugId(b_tmp.getBugId()));
            
            listBugs.add(p);
        }
        
        bugDummy.setListBug(listBugs);
        
        return bugDummy;
    }
    
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
                    Map<String, Object> bugCategoryStruct = new HashMap<String, Object>();
        
                    userStruct.put("userId", b.getUserReported().getUserId().toString());
                    userStruct.put("username", b.getUserReported().getUsername());
                    bugData.put("userReported", userStruct);

                    bugData.put("dateReported", CommonMethods.convertDate(b.getDateReported()));
      
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
                    
                    if (b.getBugcategoryId() != null) {
                        bugCategoryStruct.put("bugcategoryId", b.getBugcategoryId().getBugcategoryId().toString());
                        bugCategoryStruct.put("categoryName", b.getBugcategoryId().getCategoryName());
                        bugData.put("bugCategory", bugCategoryStruct);
                    }

                    bugMapList.add(bugData);
                }
            }
        }
    }
    
    // retourne le statut actuel de bogue.
    private Progress getProgressForBugId(Integer bugId) {
        
        // liste des 'Progress' pour le bogue.
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
    
    // utilisé par le site web. 
    // returns bugs reported by authenticated user.
    @GET
    @Path("wsbugsreported")
    public BugDummy findBugsReportedPOJO() {
        
        BugDummy bugDummy = new BugDummy();

        List<Bug> bugList = new ArrayList<Bug>();
        Query q = em.createNamedQuery("Bug.getBugsReported");
        q.setParameter("userId", getAuthenticatedUser().getUserId());
        
        bugList = q.getResultList();
        
        List<BugSimpleWebSite> listBugs = new ArrayList<BugSimpleWebSite>();
        
        for (Bug b_tmp : bugList) {
            
            BugSimpleWebSite p = new BugSimpleWebSite();
            p.setBugTitle(b_tmp.getTitle());
            p.setBugId(b_tmp.getBugId());
            if (b_tmp.getBugcategoryId() != null)
                p.setBugType(b_tmp.getBugcategoryId().getCategoryName());
            
            //getChildBugsWebSite(p);
            // statut.
            p.setProjectStatus(getStatusForBugId(b_tmp.getBugId()));
            
            listBugs.add(p);
        }
        
        bugDummy.setListBug(listBugs);
        
        return bugDummy;
    }
    
    
    // utilisé par le site web. 
    // returns bugs belonging to projects assigned to user. 
    @GET
    @Path("wsprojectbugs")
    public BugDummy findMyProjectBugsPOJO() {
        
        BugDummy bugDummy = new BugDummy();

        List<Bug> bugList = new ArrayList<Bug>();
        Query q = em.createNativeQuery("SELECT DISTINCT bug.bug_id, bug.user_reported, bug.user_assigned, bug.project_id, bug.priority, bug.date_reported, bug.title, bug.details, bug.bugcategory_id, bug.fixed, bug.canceled, bug.deleted FROM bug INNER JOIN project ON bug.project_id = project.project_id INNER JOIN project_client ON project.project_id = project_client.project_id INNER JOIN client ON project_client.client_id = client.client_id INNER JOIN client_user ON client.client_id = client_user.client_id where client_user.user_id = ?1 and (bug.deleted = false or bug.deleted is null) and (project.deleted = false or project.deleted is null)", Bug.class);
        q.setParameter(1, getAuthenticatedUser().getUserId());
        
        bugList = q.getResultList();
        
        List<BugSimpleWebSite> listBugs = new ArrayList<BugSimpleWebSite>();
        
        for (Bug b_tmp : bugList) {
            
            BugSimpleWebSite p = new BugSimpleWebSite();
            p.setBugTitle(b_tmp.getTitle());
            p.setBugId(b_tmp.getBugId());
            if (b_tmp.getBugcategoryId() != null)
                p.setBugType(b_tmp.getBugcategoryId().getCategoryName());
            
            //getChildBugsWebSite(p);
            
            // statut.
            p.setProjectStatus(getStatusForBugId(b_tmp.getBugId()));
            
            listBugs.add(p);
        }
        
        bugDummy.setListBug(listBugs);
        
        return bugDummy;
    }
    
    
    // returns parent objects including its children
    @GET
    @Path("assigned")
    @Produces("application/json")
    public String findAllAssignedBugs(@QueryParam("status") String statusId, @QueryParam("category") String categoryId) {

        String retVal = "";

        ObjectMapper mapper = new ObjectMapper();

        List<Map> bugListMap = new ArrayList<Map>();

        String filterQuery = "";
        if (statusId != null) {
            filterQuery += " AND (" + statusId + "= (SELECT status_id FROM Progress p WHERE p.bug_id = bug.bug_id order by p.date_created DESC LIMIT 1))";
        }
        if (categoryId != null) {
            filterQuery += " AND (bug.bugcategory_id = " + categoryId + ")";
        }
        
        
        // get all bugs from database.
        //Query q = em.createNamedQuery("Bug.getBugsAssigned");
        //q.setParameter("userAssignedId", getAuthenticatedUser().getUserId());
        Query q = em.createNativeQuery("SELECT bug.bug_id, bug.user_reported, bug.user_assigned, bug.project_id, bug.priority, bug.date_reported, bug.title, bug.details, bug.bugcategory_id, bug.fixed, bug.canceled, bug.deleted FROM bug where (bug.deleted = false or bug.deleted is null) and bug.user_assigned = ?1" + filterQuery, Bug.class);
        q.setParameter(1, getAuthenticatedUser().getUserId());

        List<Bug> bugList = new ArrayList<Bug>();
        bugList = q.getResultList();


        // get bugs
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
    @Produces("application/json")
    public String findAllBugs(@QueryParam("status") String statusId, @QueryParam("category") String categoryId, @QueryParam("projectId") String projectId, @QueryParam("clientId") String clientId, @QueryParam("devId") String devId) {

        String retVal = "";

        String filterQuery = "";
        if (statusId != null) {
            filterQuery += " AND (" + statusId + "= (SELECT status_id FROM Progress p WHERE p.bug_id = bug.bug_id order by p.date_created DESC LIMIT 1))";
        }
        if (categoryId != null) {
            filterQuery += " AND (bug.bugcategory_id = " + categoryId + ")";
        }
        
        if (projectId != null) {
            filterQuery += " AND (bug.project_id = " + projectId + ")";
        }
        
        if (clientId != null) {
            filterQuery += " AND (project_client.client_id = " + clientId + ")";
        }
        
        if (devId != null) {
            filterQuery += " AND (bug.user_assigned = " + devId + ")";
        }
        
        ObjectMapper mapper = new ObjectMapper();

        List<Map> bugListMap = new ArrayList<Map>();

        Query q;
        
        if (security.isUserInRole("administrator")) {
            // get projets parents de la base de données. 
            //q = em.createNativeQuery("SELECT DISTINCT task.task_id, task.user_created, task.user_assigned, task.priority, task.start_date, task.end_date, task.start_date_real, task.end_date_real, task.parent_task_id, task.task_title, task.task_description, task.project_id, task.is_personal, task.canceled, task.deleted, task.completed FROM task WHERE task.parent_task_id IS NULL and task.is_personal = false and (task.deleted = false or task.deleted is null) and task.task_id > 1025" + filterQuery, Task.class);
            //q = em.createNativeQuery("SELECT DISTINCT bug.bug_id, bug.user_reported, bug.user_assigned, bug.project_id, bug.priority, bug.date_reported, bug.title, bug.details, bug.bugcategory_id, bug.fixed, bug.canceled, bug.deleted FROM bug where (bug.deleted = false or bug.deleted is null)" + filterQuery, Bug.class
            q = em.createNativeQuery("SELECT DISTINCT bug.bug_id, bug.user_reported, bug.user_assigned, bug.project_id, bug.priority, bug.date_reported, bug.title, bug.details, bug.bugcategory_id, bug.fixed, bug.canceled, bug.deleted FROM project RIGHT OUTER JOIN bug ON project.project_id = bug.project_id LEFT OUTER JOIN project_client ON project_client.project_id = project.project_id where (bug.deleted = false or bug.deleted is null) and (project.deleted = false or project.deleted is null)" + filterQuery, Bug.class);
            
            List<Bug> bugList = new ArrayList<Bug>();
            bugList = q.getResultList();

            // get projects and its children
            //getProjects(prjList, projectList, "Project.getChildProjects", "");
            //getBugs(bugList, bugListMap, em.createNamedQuery("Task.getChildTasks"), null);
            getBugs(bugList, bugListMap);
        } else {     // si pas administrateur, afficher que les projets qu'un utilisateur est autorisé de voir.   


            //q = em.createNativeQuery("SELECT DISTINCT task.task_id, task.user_created, task.user_assigned, task.priority, task.start_date, task.end_date, task.start_date_real, task.end_date_real, task.parent_task_id, task.task_title, task.task_description, task.project_id, task.is_personal, task.canceled, task.deleted, task.completed FROM task WHERE task.parent_task_id IS NULL and task.is_personal = false and (task.deleted = false or task.deleted is null) and task.task_id > 1025 and task.project_id is null" + filterQuery, Task.class);
            //q = em.createNativeQuery("SELECT DISTINCT task.task_id, task.user_created, task.user_assigned, task.priority, task.start_date, task.end_date, task.start_date_real, task.end_date_real, task.parent_task_id, task.task_title, task.task_description, task.project_id, task.is_personal, task.canceled, task.deleted, task.completed FROM task WHERE task.parent_task_id IS NULL and (task.deleted = false or task.deleted is null) and task.task_id > 1025 and task.project_id IN (SELECT DISTINCT project.project_id as PROJECT_ID FROM project LEFT OUTER JOIN project_user_visible ON project.project_id = project_user_visible.project_id LEFT OUTER JOIN project_usergroup_visible ON project_usergroup_visible.project_id = project.project_id LEFT OUTER JOIN project_client ON project_client.project_id = project.project_id LEFT OUTER JOIN client ON project_client.client_id = client.client_id LEFT OUTER JOIN client_user ON client_user.client_id = client.client_id LEFT OUTER JOIN usergroup ON usergroup.usergroup_id = project_usergroup_visible.usergroup_id LEFT OUTER JOIN user_usergroup ON usergroup.usergroup_id = user_usergroup.usergroup_id WHERE (project.deleted = false or project.deleted IS NULL) AND ((project.flag_public = TRUE) OR (user_usergroup.user_id = ?1) OR (client_user.user_id = ?1) OR (project_user_visible.user_id = ?1) OR (project.user_created = ?1) OR (project.user_assigned = ?1)))" + filterQuery, Task.class);
            q = em.createNativeQuery("SELECT DISTINCT bug.bug_id, bug.user_reported, bug.user_assigned, bug.project_id, bug.priority, bug.date_reported, bug.title, bug.details, bug.bugcategory_id, bug.fixed, bug.canceled, bug.deleted FROM project LEFT OUTER JOIN project_user_visible ON project.project_id = project_user_visible.project_id LEFT OUTER JOIN project_usergroup_visible ON project_usergroup_visible.project_id = project.project_id LEFT OUTER JOIN project_client ON project_client.project_id = project.project_id LEFT OUTER JOIN client ON project_client.client_id = client.client_id LEFT OUTER JOIN client_user ON client_user.client_id = client.client_id LEFT OUTER JOIN usergroup ON usergroup.usergroup_id = project_usergroup_visible.usergroup_id LEFT OUTER JOIN user_usergroup ON usergroup.usergroup_id = user_usergroup.usergroup_id INNER JOIN bug ON bug.project_id = project.project_id WHERE (project.deleted = false or project.deleted IS NULL) AND ((project.flag_public = TRUE) OR (user_usergroup.user_id = ?1) OR (client_user.user_id = ?1) OR (project_user_visible.user_id = ?1) or (project.user_assigned = ?1) or (project.user_created = ?1)) AND (bug.deleted = false or bug.deleted IS NULL) AND (project.deleted = false or project.deleted is null)" + filterQuery, Bug.class);
            q.setParameter(1, getAuthenticatedUser().getUserId());
            
            
            
            List<Bug> bugList = new ArrayList<Bug>();
            bugList = q.getResultList();
            
            //getBugs(bugList, bugListMap, em.createNamedQuery("Task.getChildTasks"), null);
            getBugs(bugList, bugListMap);
            
            /*q = em.createNativeQuery("SELECT DISTINCT project.project_id as PROJECT_ID, project.project_title as PROJECT_TITLE, project.flag_public as FLAG_PUBLIC, project.project_description as PROJECT_DESCRIPTION, project.user_created as USER_CREATED, project.date_created as DATE_CREATED, project.start_date as START_DATE, project.end_date as END_DATE, project.start_date_real as START_DATE_REAL, project.end_date_real as END_DATE_REAL, project.parent_project_id as PARENT_PROJECT_ID, project.completed as COMPLETED, project.canceled as CANCELED, project.deleted as DELETED, project.user_assigned as USER_ASSIGNED FROM project LEFT OUTER JOIN project_user_visible ON project.project_id = project_user_visible.project_id LEFT OUTER JOIN project_usergroup_visible ON project_usergroup_visible.project_id = project.project_id LEFT OUTER JOIN project_client ON project_client.project_id = project.project_id LEFT OUTER JOIN client ON project_client.client_id = client.client_id LEFT OUTER JOIN client_user ON client_user.client_id = client.client_id LEFT OUTER JOIN usergroup ON usergroup.usergroup_id = project_usergroup_visible.usergroup_id LEFT OUTER JOIN user_usergroup ON usergroup.usergroup_id = user_usergroup.usergroup_id WHERE (project.deleted = false or project.deleted IS NULL) AND ((project.flag_public = TRUE) OR (user_usergroup.user_id = ?1) OR (client_user.user_id = ?1) OR (project_user_visible.user_id = ?1) OR (project.user_created = ?1) OR (project.user_assigned = ?1)) AND project.parent_project_id IS NULL" + filterQuery, Project.class);
            //Query q = em.createNativeQuery("SELECT DISTINCT project.project_id as PROJECT_ID, project.project_title as PROJECT_TITLE, project.flag_public as FLAG_PUBLIC, project.project_description as PROJECT_DESCRIPTION, project.user_created as USER_CREATED, project.date_created as DATE_CREATED, project.start_date as START_DATE, project.end_date as END_DATE, project.start_date_real as START_DATE_REAL, project.end_date_real as END_DATE_REAL, project.parent_project_id as PARENT_PROJECT_ID, project.completed as COMPLETED, project.canceled as CANCELED, project.deleted as DELETED, project.user_assigned as USER_ASSIGNED FROM project LEFT OUTER JOIN project_user_visible ON project.project_id = project_user_visible.project_id LEFT OUTER JOIN project_usergroup_visible ON project_usergroup_visible.project_id = project.project_id LEFT OUTER JOIN project_client ON project_client.project_id = project.project_id LEFT OUTER JOIN client ON project_client.client_id = client.client_id LEFT OUTER JOIN client_user ON client_user.client_id = client.client_id LEFT OUTER JOIN usergroup ON usergroup.usergroup_id = project_usergroup_visible.usergroup_id LEFT OUTER JOIN user_usergroup ON usergroup.usergroup_id = user_usergroup.usergroup_id WHERE (project.deleted = false or project.deleted IS NULL) AND ((project.flag_public = TRUE) OR (((user_usergroup.user_id = ?1) OR (client_user.user_id = ?1) OR (project_user_visible.user_id = ?1) OR (project.user_created = ?1) OR (project.user_assigned = ?1)) " + filterQuery + ")) AND project.parent_project_id IS NULL" + filterQuery, Project.class);
            q.setParameter(1, getAuthenticatedUser().getUserId());



            List<Project> prjList = new ArrayList<Project>();
            prjList = q.getResultList();

            Query subquery = em.createNativeQuery("SELECT DISTINCT project.project_id as PROJECT_ID, project.project_title as PROJECT_TITLE, project.flag_public as FLAG_PUBLIC, project.project_description as PROJECT_DESCRIPTION, project.user_created as USER_CREATED, project.date_created as DATE_CREATED, project.start_date as START_DATE, project.end_date as END_DATE, project.start_date_real as START_DATE_REAL, project.end_date_real as END_DATE_REAL, project.parent_project_id as PARENT_PROJECT_ID, project.completed as COMPLETED, project.canceled as CANCELED, project.deleted as DELETED, project.user_assigned as USER_ASSIGNED FROM project LEFT OUTER JOIN project_user_visible ON project.project_id = project_user_visible.project_id LEFT OUTER JOIN project_usergroup_visible ON project_usergroup_visible.project_id = project.project_id LEFT OUTER JOIN project_client ON project_client.project_id = project.project_id LEFT OUTER JOIN client ON project_client.client_id = client.client_id LEFT OUTER JOIN client_user ON client_user.client_id = client.client_id LEFT OUTER JOIN usergroup ON usergroup.usergroup_id = project_usergroup_visible.usergroup_id LEFT OUTER JOIN user_usergroup ON usergroup.usergroup_id = user_usergroup.usergroup_id WHERE project.parent_project_id = ?1 AND (project.deleted = false or project.deleted IS NULL) AND ((project.flag_public = TRUE) OR (user_usergroup.user_id = ?2) OR (client_user.user_id = ?2) OR (project_user_visible.user_id = ?2) OR (project.user_created = ?1) OR (project.user_assigned = ?1))", Project.class);
            subquery.setParameter(2, getAuthenticatedUser().getUserId());

            // get projects. Pas des sous-projets. 
            getProjects(prjList, projectList, null, subquery);*/
        }
        
        
        // get all bugs from database.
        /*Query q = em.createNamedQuery("Bug.findAll");

        List<Bug> bugList = new ArrayList<Bug>();
        bugList = q.getResultList();


        // get bugs
        getBugs(bugList, bugListMap);*/


        HashMap<String, Object> retProjects = new HashMap<String, Object>();
        retProjects.put("bug", bugListMap);

        try {
            retVal = mapper.writeValueAsString(retProjects);
        } catch (IOException ex) {
            Logger.getLogger(ProjectFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;
    }
    
    // retourne le statut actuel du projet.
    private String getStatusForBugId(Integer bugId) {

        // liste des 'Progress' pour le projet.
        Query qryStatus = em.createNamedQuery("Progress.findByBugId");
        qryStatus.setParameter("bugId", bugId);
        // obtenir le plus récent.
        qryStatus.setMaxResults(1); // top 1 result
        // lire en liste.
        List<Progress> listProgress = new ArrayList<Progress>();
        listProgress = qryStatus.getResultList();

        // retourner le statut, s'il y'en a un.
        if (listProgress.size() > 0) {
            String status = "";
            if (listProgress.get(0).getPercentageComplete() != null) {
                status += listProgress.get(0).getPercentageComplete().toString() + "% - ";
            }

            return status + listProgress.get(0).getStatusId().getStatusName();
        } else {
            return "-";
        }
    }
}
