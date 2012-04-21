/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import be.luckycode.projetawebservice.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
@Path("tasks")
public class TaskFacadeREST extends AbstractFacade<Task> {

    @PersistenceContext(unitName = "be.luckycode_projeta-webservice_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    @Context
    SecurityContext security;

    public TaskFacadeREST() {
        super(Task.class);
    }

    @POST
    @Override
    @Consumes({"application/xml", "application/json"})
    public void create(Task entity) {
        super.create(entity);
    }

    @PUT
    @Override
    @Consumes({"application/xml", "application/json"})
    public void edit(Task entity) {
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
    public Task find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    /*
     * @GET @Override @Produces({"application/xml", "application/json"}) public
     * List<Task> findAll() { return super.findAll();
    }
     */
    
    @GET
    @Path("personal")
    @Produces("application/json")
    public String findPersonalTasks() {
        
        String retVal = "";

        ObjectMapper mapper = new ObjectMapper();

        List<Map> taskList = new ArrayList<Map>();



        // get root projects (projects which have no parent) and which are personal.
        Query q = em.createNamedQuery("Task.getPersonalParentTasks");
        q.setParameter(1, this.getAuthenticatedUser().getUserId());

        List<Task> tList = new ArrayList<Task>();
        tList = q.getResultList();


        // get projects and its children
        getTasks(tList, taskList);


        HashMap<String, Object> retTasks = new HashMap<String, Object>();
        retTasks.put("task", taskList);

        try {
            retVal = mapper.writeValueAsString(retTasks);
        } catch (IOException ex) {
            Logger.getLogger(TaskFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;
    }
    
    
    @GET
    @Produces("application/json")
    public String findAll4() {

        String retVal = "";

        ObjectMapper mapper = new ObjectMapper();

        List<Map> taskList = new ArrayList<Map>();



        // get root projects (projects which have no parent)
        Query q = em.createNamedQuery("Task.getParentTasks");

        List<Task> tList = new ArrayList<Task>();
        tList = q.getResultList();


        // get projects and its children
        getTasks(tList, taskList);


        HashMap<String, Object> retTasks = new HashMap<String, Object>();
        retTasks.put("task", taskList);

        try {
            retVal = mapper.writeValueAsString(retTasks);
        } catch (IOException ex) {
            Logger.getLogger(TaskFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;


    }

    private void getTasks(List<Task> taskList, List<Map> taskMapList) {
        // if list is not empty
        if (taskList.isEmpty() == false) {
            for (Task t : taskList) {

                // retourner/inclure seulement s'il ne s'agit pas d'une tâche supprimée.  
                if (t.getDeleted() == null || t.getDeleted() == false) {

                    Map<String, Object> taskData = new HashMap<String, Object>();
                    Map<String, Object> userStruct = new HashMap<String, Object>();
                    //Map<String, Object> userAssignedStruct = new HashMap<String, Object>();
                    //Map<String, String> nameStruct = new HashMap<String, String>();

                    userStruct.put("userId", t.getUserCreated().getUserId().toString());
                    userStruct.put("username", t.getUserCreated().getUsername());
                    taskData.put("userCreated", userStruct);

                    taskData.put("endDate", CommonMethods.convertDate(t.getEndDate()));
                    taskData.put("startDate", CommonMethods.convertDate(t.getStartDate()));
                    //taskData.put("endDate", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(t.getEndDate()));
                    //taskData.put("startDate", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(t.getStartDate()));

                    if (t.getTaskDescription() != null) {
                        taskData.put("taskDescription", t.getTaskDescription());
                    }
                    taskData.put("taskId", t.getTaskId().toString());
                    taskData.put("taskTitle", t.getTaskTitle());

                    taskData.put("completed", t.getCompleted());

                    taskData.put("isPersonal", t.getIsPersonal());

                    if (t.getPriority() != null) {
                        taskData.put("priority", t.getPriority().toString());
                    } else // retourner 1 comme priorité par défaut.
                    {
                        taskData.put("priority", "1");
                    }

                    if (t.getUserAssigned() != null) {
                        //userAssignedStruct.put("userId", t.getUserCreated().getUserId().toString());
                        //userAssignedStruct.put("username", t.getUserCreated().getUsername());
                        //taskData.put("userAssigned", userAssignedStruct);
                        taskData.put("userAssigned", t.getUserAssigned().getUserId().toString());
                    }

                    // get child projects, if any
                    getChildTasks(t, userStruct, taskData);

                    taskMapList.add(taskData);
                }
            }
        }
    }

    private void getChildTasks(Task t, Map<String, Object> userStruct, Map<String, Object> taskData) {
        List<Map> childTaskMapList = new ArrayList<Map>();

        // get child projects
        Query qry_child_tasks = em.createNamedQuery("Task.getChildTasks");
        qry_child_tasks.setParameter(1, t);


        List<Task> childTaskList = new ArrayList<Task>();
        childTaskList = qry_child_tasks.getResultList();

        // get child projects
        getTasks(childTaskList, childTaskMapList);

        // 
        if (childTaskMapList.isEmpty() == false) {

            taskData.put("childTask", childTaskMapList);
        }
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Task> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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
    @RolesAllowed("administrator")
    @Consumes("application/json")
    @Produces("application/json")
    public String createNewTask(Task entity) {

        em.persist(entity);

        em.flush();

        return "OK, created.";
    }

    @POST
    @Path("delete/{id}")
    public void deleteTaskById(@PathParam("id") Integer id) {

        Task task = super.find(id);
        // marquer "Supprimé"
        task.setDeleted(Boolean.TRUE);

        // enregistrer en DB.
        super.edit(task);
    }

    @POST
    @Path("delete")
    public void deleteTask(Task entity) {

        Task task = super.find(entity.getTaskId());
        // marquer "Supprimé"
        task.setDeleted(Boolean.TRUE);

        // enregistrer en DB.
        super.edit(task);
    }
    
    @PUT
    @Path("update")
    @RolesAllowed("administrator")
    @Consumes("application/json")
    @Produces("application/json")
    public String updateTask(Task entity) {


        // fetch user to be updated.
        Task task = super.find(entity.getTaskId());

        if (task.getTaskTitle() != null) {
            task.setTaskTitle(entity.getTaskTitle());
        }
        if (task.getTaskDescription() != null) {
            task.setTaskDescription(entity.getTaskDescription());
        }
        if (task.getCompleted() != null) {
            task.setCompleted(entity.getCompleted());
        }


        super.edit(task);


        // TODO : mark childtask as completed.
        

        // SAME CODE AS IN CREATE !!!
        List<Task> tskList = new ArrayList<Task>();
        tskList.add(super.find(task.getTaskId()));

        ObjectMapper mapper = new ObjectMapper();
        List<Map> taskList = new ArrayList<Map>();

        getTasks(tskList, taskList);

        String retVal = "";

        HashMap<String, Object> retTasks = new HashMap<String, Object>();
        retTasks.put("project", taskList);

        try {
            retVal = mapper.writeValueAsString(retTasks);
        } catch (IOException ex) {
            Logger.getLogger(TaskFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;
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
    public ProjectDummy findTasksByProjectIdPOJO(@PathParam("id") Integer id) {
        
        ProjectDummy projDummy = new ProjectDummy();
        //projDummy.setListProject(super.findAll());
        
        //List<Project> listProjTmp = super.findAll();
        //Query q = em.createNamedQuery("Project.getParentProjects");

        List<Task> taskList = new ArrayList<Task>();
        Query q = em.createNamedQuery("Task.getParentTasksByProjectId");
        q.setParameter("projectId", id);
        
        taskList = q.getResultList();
        
        List<ProjectSimpleWebSite> listProj = new ArrayList<ProjectSimpleWebSite>();
        
        for (Task t_tmp : taskList) {
            
            ProjectSimpleWebSite p = new ProjectSimpleWebSite();
            p.setProjectTitle(t_tmp.getTaskTitle());
            p.setProjectId(t_tmp.getTaskId());

            //getChildProjectsWebSite(p);
            
            listProj.add(p);
        }
        
        projDummy.setListProject(listProj);
        
        return projDummy;
    }
    
    // FOR WEBSITE !!!
    /*private void getChildProjectsWebSite(ProjectSimpleWebSite p) {
        // get child projects
        Query qry_child_projects = em.createNamedQuery("Project.getChildProjects");
        Project p_qry = new Project(p.getProjectId());
        qry_child_projects.setParameter(1, p_qry);

        List<Project> childPrjList = new ArrayList<Project>();
        childPrjList = qry_child_projects.getResultList();
        
        List<ProjectSimpleWebSite> listSubProject = new ArrayList<ProjectSimpleWebSite>();
        
        for (Project p_tmp : childPrjList) {
            
            ProjectSimpleWebSite p_sub = new ProjectSimpleWebSite();
            p_sub.setProjectTitle(p_tmp.getProjectTitle());
            p_sub.setProjectId(p_tmp.getProjectId());
            
            getChildProjectsWebSite(p_sub);
            
            listSubProject.add(p_sub);
        }
        
        p.setChildProject(listSubProject);
    }*/
}
