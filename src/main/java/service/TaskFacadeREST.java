/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import be.luckycode.projetawebservice.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
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
    @Produces("application/json")
    public Task find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    /*
     * @GET @Override @Produces({"application/xml", "application/json"}) public
     * List<Task> findAll() { return super.findAll(); }
     */
    @GET
    @Path("personal")
    @Produces("application/json")
    public String findPersonalTasks(@QueryParam("status") String statusId, @QueryParam("minDate") String minDate, @QueryParam("maxDate") String maxDate) {

        String retVal = "";

        ObjectMapper mapper = new ObjectMapper();

        List<Map> taskList = new ArrayList<Map>();

        
        // filter.
        String filterQuery = "";
        if (statusId != null) {
            filterQuery += " AND (" + statusId + "= (SELECT status_id FROM Progress p WHERE p.task_id = task.task_id order by p.date_created DESC LIMIT 1))";
        }


        if (minDate != null) {
            filterQuery += " AND (task.start_date >= " + minDate + ")";
        }
        if (maxDate != null) {
            filterQuery += " AND (task.end_date <= " + maxDate + ")";
        }
        
        // get root tasks (tasks which have no parent) and which are personal.
        //Query q = em.createNamedQuery("Task.getPersonalParentTasks");
        Query q = em.createNativeQuery("SELECT DISTINCT task.task_id, task.user_created, task.user_assigned, task.priority, task.start_date, task.end_date, task.start_date_real, task.end_date_real, task.parent_task_id, task.task_title, task.task_description, task.project_id, task.is_personal, task.canceled, task.deleted, task.completed FROM task WHERE task.parent_task_id IS NULL and task.is_personal = true and (task.deleted = false or task.deleted is null) and task.user_created = ?1 and task.task_id > 1025" + filterQuery, Task.class);
        q.setParameter(1, this.getAuthenticatedUser().getUserId());
        
        List<Task> tList = new ArrayList<Task>();
        tList = q.getResultList();


        // get projects and its children
        getTasks(tList, taskList, em.createNamedQuery("Task.getChildTasks"), null);


        HashMap<String, Object> retTasks = new HashMap<String, Object>();
        retTasks.put("task", taskList);

        try {
            retVal = mapper.writeValueAsString(retTasks);
        } catch (IOException ex) {
            Logger.getLogger(TaskFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;
    }

    /*@GET
    @Produces("application/json")
    public String findAllTasks() {

        String retVal = "";

        ObjectMapper mapper = new ObjectMapper();

        List<Map> taskList = new ArrayList<Map>();



        // get root tasks (tasks which have no parent)
        Query q = em.createNamedQuery("Task.getParentTasks");

        List<Task> tList = new ArrayList<Task>();
        tList = q.getResultList();


        // get projects and its children
        getTasks(tList, taskList, em.createNamedQuery("Task.getChildTasks"), null);


        HashMap<String, Object> retTasks = new HashMap<String, Object>();
        retTasks.put("task", taskList);

        try {
            retVal = mapper.writeValueAsString(retTasks);
        } catch (IOException ex) {
            Logger.getLogger(TaskFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;


    }*/

    private void getTasks(List<Task> taskList, List<Map> taskMapList, Query namedsubquery, Query nativesubquery) {
        // if list is not empty
        if (taskList.isEmpty() == false) {
            for (Task t : taskList) {

                // retourner/inclure seulement s'il ne s'agit pas d'une tâche supprimée.  
                if (t.getDeleted() == null || t.getDeleted() == false) {

                    Map<String, Object> taskData = new HashMap<String, Object>();
                    Map<String, Object> userStruct = new HashMap<String, Object>();
                    Map<String, Object> userAssignedStruct = new HashMap<String, Object>();

                    userStruct.put("userId", t.getUserCreated().getUserId().toString());
                    userStruct.put("username", t.getUserCreated().getUsername());
                    taskData.put("userCreated", userStruct);

                    taskData.put("endDate", CommonMethods.convertDate(t.getEndDate()));
                    taskData.put("startDate", CommonMethods.convertDate(t.getStartDate()));

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
                        userAssignedStruct.put("userId", t.getUserAssigned().getUserId().toString());
                        userAssignedStruct.put("username", t.getUserAssigned().getUsername());
                        taskData.put("userAssigned", userAssignedStruct);
                    }

                    // état et pourcentage.
                    Progress progress = getProgressForTaskId(t.getTaskId());

                    if (progress != null) {
                        // état.
                        if (progress.getStatusId() != null && progress.getStatusId().getStatusName() != null) {
                            taskData.put("taskStatus", progress.getStatusId().getStatusName());
                        }
                        // pourcentage.
                        taskData.put("taskPercentage", progress.getPercentageComplete().toString());
                    }

                    // nom du projet.
                    if (t.getProjectId() != null) {
                        if (t.getProjectId().getProjectTitle() != null) {
                            taskData.put("projectTitle", t.getProjectId().getProjectTitle());
                        }
                    }


                    // get child tasks, if any
                    if (namedsubquery != null || nativesubquery != null) {
                        getChildTasks(t, userStruct, taskData, namedsubquery, nativesubquery);
                    }
                    // get child projects, if any
                    //getChildTasks(t, userStruct, taskData);

                    taskMapList.add(taskData);
                }
            }
        }
    }

    private void getChildTasks(Task t, Map<String, Object> userStruct, Map<String, Object> taskData, Query namedSubQuery, Query nativeSubQuery) {
        List<Map> childTaskMapList = new ArrayList<Map>();

        // get child projects
        Query qry_child_tasks = em.createNamedQuery("Task.getChildTasks");
        qry_child_tasks.setParameter(1, t);


        List<Task> childTaskList = new ArrayList<Task>();
        childTaskList = qry_child_tasks.getResultList();

        // get child projects
        getTasks(childTaskList, childTaskMapList, namedSubQuery, nativeSubQuery);

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

        // Créer la nouvelle tâche dans la base de données.
        em.persist(entity);

        // Créer état, pourcentage d'avancement etc. par défaut.
        initDefaultProgressForNewTask(entity);

        em.flush();



        // Retourne la tâche créé. 
        List<Task> tskList = new ArrayList<Task>();
        tskList.add(super.find(entity.getTaskId()));

        ObjectMapper mapper = new ObjectMapper();
        List<Map> taskList = new ArrayList<Map>();

        getTasks(tskList, taskList, em.createNamedQuery("Task.getChildTasks"), null);

        String retVal = "";

        HashMap<String, Object> retTasks = new HashMap<String, Object>();
        retTasks.put("task", taskList);

        try {
            retVal = mapper.writeValueAsString(retTasks);
        } catch (IOException ex) {
            Logger.getLogger(TaskFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;
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

        if (entity.getTaskTitle() != null) {
            task.setTaskTitle(entity.getTaskTitle());
        }
        if (entity.getTaskDescription() != null) {
            task.setTaskDescription(entity.getTaskDescription());
        }
        if (entity.getCompleted() != null) {
            task.setCompleted(entity.getCompleted());
        }
        if (entity.getStartDate() != null) {
            task.setStartDate(entity.getStartDate());
        }
        if (entity.getEndDate() != null) {
            task.setEndDate(entity.getEndDate());
        }


        super.edit(task);


        // retourne la tâche qui vient d'être mis-à-jour. 
        List<Task> tskList = new ArrayList<Task>();
        tskList.add(super.find(task.getTaskId()));

        ObjectMapper mapper = new ObjectMapper();
        List<Map> taskList = new ArrayList<Map>();

        getTasks(tskList, taskList, em.createNamedQuery("Task.getChildTasks"), null);

        String retVal = "";

        HashMap<String, Object> retTasks = new HashMap<String, Object>();
        retTasks.put("task", taskList);

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

        List<Task> taskList = new ArrayList<Task>();
        Query q = em.createNamedQuery("Task.getParentTasksByProjectId");
        q.setParameter("projectId", id);

        taskList = q.getResultList();

        List<ProjectSimpleWebSite> listProj = new ArrayList<ProjectSimpleWebSite>();

        for (Task t_tmp : taskList) {

            ProjectSimpleWebSite p = new ProjectSimpleWebSite();
            p.setProjectTitle(t_tmp.getTaskTitle());
            p.setProjectId(t_tmp.getTaskId());
            if (t_tmp.getStartDate() != null) {
                p.setStartDate(t_tmp.getStartDate());
            }
            if (t_tmp.getEndDate() != null) {
                p.setEndDate(t_tmp.getEndDate());
            }

            // statut.
            p.setProjectStatus(getStatusForTaskId(t_tmp.getTaskId()));

            getChildTasksWebSite(p);

            listProj.add(p);
        }

        projDummy.setListProject(listProj);

        return projDummy;
    }

    // utilisé par le site web. Retourne les tâches enfants d'un projet spécifié. 
    private void getChildTasksWebSite(ProjectSimpleWebSite p) {
        // get child projects
        Query qry_child_tasks = em.createNamedQuery("Task.getChildTasks");
        Task p_qry = new Task(p.getProjectId());
        qry_child_tasks.setParameter(1, p_qry);

        List<Task> childTaskList = new ArrayList<Task>();
        childTaskList = qry_child_tasks.getResultList();

        List<ProjectSimpleWebSite> listSubProject = new ArrayList<ProjectSimpleWebSite>();

        for (Task t_tmp : childTaskList) {

            ProjectSimpleWebSite p_sub = new ProjectSimpleWebSite();
            p_sub.setProjectTitle(t_tmp.getTaskTitle());
            p_sub.setProjectId(t_tmp.getTaskId());
            if (t_tmp.getStartDate() != null) {
                p_sub.setStartDate(t_tmp.getStartDate());
            }
            if (t_tmp.getEndDate() != null) {
                p_sub.setEndDate(t_tmp.getEndDate());
            }

            // statut.
            p_sub.setProjectStatus(getStatusForTaskId(t_tmp.getTaskId()));

            getChildTasksWebSite(p_sub);

            listSubProject.add(p_sub);
        }

        p.setChildProject(listSubProject);
    }

    // retourne le statut actuel du projet.
    private String getStatusForTaskId(Integer taskId) {

        // liste des 'Progress' pour le projet.
        Query qryStatus = em.createNamedQuery("Progress.findByTaskId");
        qryStatus.setParameter("taskId", taskId);
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

    // retourne le statut actuel de la tâche.
    private Progress getProgressForTaskId(Integer taskId) {

        // liste des 'Progress' pour le projet.
        Query qryPercentage = em.createNamedQuery("Progress.findByTaskId");
        qryPercentage.setParameter("taskId", taskId);
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

    // Créer état, pourcentage d'avancement etc. par défaut.
    private void initDefaultProgressForNewTask(Task entity) {
        Progress progress = new Progress();
        progress.setPercentageComplete((short) 0);
        progress.setProgressComment("Tâche créé.");
        progress.setStatusId(new Status(9));
        progress.setUserCreated(getAuthenticatedUser());
        progress.setTaskId(entity);

        em.persist(progress);
    }

    // FOR WEBSITE !!!
    @GET
    @Path("wsprojecttasks")
    public ProjectDummy findMyProjectTasksPOJO() {

        ProjectDummy projDummy = new ProjectDummy();

        List<Task> taskList = new ArrayList<Task>();
        Query q = em.createNativeQuery("SELECT DISTINCT task.task_id, task.user_created, task.user_assigned, task.priority, task.start_date, task.end_date, task.start_date_real, task.end_date_real, task.parent_task_id, task.task_title, task.task_description, task.project_id, task.is_personal, task.canceled, task.completed, task.deleted FROM task INNER JOIN project ON task.project_id = project.project_id INNER JOIN project_client ON project.project_id = project_client.project_id INNER JOIN client ON project_client.client_id = client.client_id INNER JOIN client_user ON client.client_id = client_user.client_id where client_user.user_id = ?1 and (task.deleted = false or task.deleted is null)", Task.class);
        q.setParameter(1, getAuthenticatedUser().getUserId());

        taskList = q.getResultList();

        List<ProjectSimpleWebSite> listProj = new ArrayList<ProjectSimpleWebSite>();

        for (Task t_tmp : taskList) {

            ProjectSimpleWebSite p = new ProjectSimpleWebSite();
            p.setProjectTitle(t_tmp.getTaskTitle());
            p.setProjectId(t_tmp.getTaskId());
            if (t_tmp.getStartDate() != null) {
                p.setStartDate(t_tmp.getStartDate());
            }
            if (t_tmp.getEndDate() != null) {
                p.setEndDate(t_tmp.getEndDate());
            }

            // statut.
            p.setProjectStatus(getStatusForTaskId(t_tmp.getTaskId()));

            getChildTasksWebSite(p);

            listProj.add(p);
        }

        projDummy.setListProject(listProj);

        return projDummy;
    }

    @GET
    @Path("assigned")
    @Produces("application/json")
    public String findAllAssignedTasks(@QueryParam("status") String statusId, @QueryParam("minDate") String minDate, @QueryParam("maxDate") String maxDate) {

        String retVal = "";

        ObjectMapper mapper = new ObjectMapper();

        List<Map> taskList = new ArrayList<Map>();

        // filter.
        String filterQuery = "";
        if (statusId != null) {
            filterQuery += " AND (" + statusId + "= (SELECT status_id FROM Progress p WHERE p.task_id = task.task_id order by p.date_created DESC LIMIT 1))";
        }


        if (minDate != null) {
            filterQuery += " AND (task.start_date >= " + minDate + ")";
        }
        if (maxDate != null) {
            filterQuery += " AND (task.end_date <= " + maxDate + ")";
        }
        
        
        // get tasks attribués à l'utilisateur. 
        //Query q = em.createNamedQuery("Task.getAssignedTasks");
        Query q = em.createNativeQuery("SELECT DISTINCT task.task_id, task.user_created, task.user_assigned, task.priority, task.start_date, task.end_date, task.start_date_real, task.end_date_real, task.parent_task_id, task.task_title, task.task_description, task.project_id, task.is_personal, task.canceled, task.deleted, task.completed FROM task where (task.deleted = false or task.deleted is null) and task.is_personal = false and task.user_assigned = ?1" + filterQuery, Task.class);
        q.setParameter(1, getAuthenticatedUser().getUserId());

        List<Task> tList = new ArrayList<Task>();
        tList = q.getResultList();


        // get projects and its children
        getTasks(tList, taskList, null, null);


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
    public String findAllTasks(@QueryParam("status") String statusId, @QueryParam("minDate") String minDate, @QueryParam("maxDate") String maxDate, @QueryParam("projectId") String projectId) {

        String retVal = "";

        ObjectMapper mapper = new ObjectMapper();

        List<Map> taskList = new ArrayList<Map>();
        Query q;

        // filter.
        String filterQuery = "";
        if (statusId != null) {
            filterQuery += " AND (" + statusId + "= (SELECT status_id FROM Progress p WHERE p.task_id = task.task_id order by p.date_created DESC LIMIT 1))";
        }


        if (minDate != null) {
            filterQuery += " AND (task.start_date >= " + minDate + ")";
        }
        if (maxDate != null) {
            filterQuery += " AND (task.end_date <= " + maxDate + ")";
        }
        
        if (projectId != null) {
            filterQuery += " AND (task.project_id = " + projectId + ")";
        }
        

        
        // si utilisateur authentifié est un administrateur -> afficher tous les projets. 
        if (security.isUserInRole("administrator")) {
            // get projets parents de la base de données. 
            //q = em.createNamedQuery("Project.getParentProjects");
            //q = em.createNativeQuery("SELECT DISTINCT task.task_id, task.user_created, task.user_assigned, task.priority, task.start_date, task.end_date, task.start_date_real, task.end_date_real, task.parent_task_id, task.task_title, task.task_description, task.project_id, task.is_personal, task.canceled, task.deleted, task.completed FROM task WHERE task.parent_task_id IS NULL and task.is_personal = false and (task.deleted = false or task.deleted is null) and task.task_id > 1025 and task.project_id is null" + filterQuery, Task.class);
            q = em.createNativeQuery("SELECT DISTINCT task.task_id, task.user_created, task.user_assigned, task.priority, task.start_date, task.end_date, task.start_date_real, task.end_date_real, task.parent_task_id, task.task_title, task.task_description, task.project_id, task.is_personal, task.canceled, task.deleted, task.completed FROM task WHERE task.parent_task_id IS NULL and task.is_personal = false and (task.deleted = false or task.deleted is null) and task.task_id > 1025" + filterQuery, Task.class);

            List<Task> tList = new ArrayList<Task>();
            tList = q.getResultList();

            // get projects and its children
            //getProjects(prjList, projectList, "Project.getChildProjects", "");
            getTasks(tList, taskList, em.createNamedQuery("Task.getChildTasks"), null);
        } else {     // si pas administrateur, afficher que les projets qu'un utilisateur est autorisé de voir.   


            //q = em.createNativeQuery("SELECT DISTINCT task.task_id, task.user_created, task.user_assigned, task.priority, task.start_date, task.end_date, task.start_date_real, task.end_date_real, task.parent_task_id, task.task_title, task.task_description, task.project_id, task.is_personal, task.canceled, task.deleted, task.completed FROM task WHERE task.parent_task_id IS NULL and task.is_personal = false and (task.deleted = false or task.deleted is null) and task.task_id > 1025 and task.project_id is null" + filterQuery, Task.class);
            //q = em.createNativeQuery("SELECT DISTINCT task.task_id, task.user_created, task.user_assigned, task.priority, task.start_date, task.end_date, task.start_date_real, task.end_date_real, task.parent_task_id, task.task_title, task.task_description, task.project_id, task.is_personal, task.canceled, task.deleted, task.completed FROM task WHERE task.parent_task_id IS NULL and (task.deleted = false or task.deleted is null) and task.task_id > 1025 and task.project_id IN (SELECT DISTINCT project.project_id as PROJECT_ID FROM project LEFT OUTER JOIN project_user_visible ON project.project_id = project_user_visible.project_id LEFT OUTER JOIN project_usergroup_visible ON project_usergroup_visible.project_id = project.project_id LEFT OUTER JOIN project_client ON project_client.project_id = project.project_id LEFT OUTER JOIN client ON project_client.client_id = client.client_id LEFT OUTER JOIN client_user ON client_user.client_id = client.client_id LEFT OUTER JOIN usergroup ON usergroup.usergroup_id = project_usergroup_visible.usergroup_id LEFT OUTER JOIN user_usergroup ON usergroup.usergroup_id = user_usergroup.usergroup_id WHERE (project.deleted = false or project.deleted IS NULL) AND ((project.flag_public = TRUE) OR (user_usergroup.user_id = ?1) OR (client_user.user_id = ?1) OR (project_user_visible.user_id = ?1) OR (project.user_created = ?1) OR (project.user_assigned = ?1)))" + filterQuery, Task.class);
            q = em.createNativeQuery("SELECT DISTINCT task.task_id, task.user_created, task.user_assigned, task.priority, task.start_date, task.end_date, task.start_date_real, task.end_date_real, task.parent_task_id, task.task_title, task.task_description, task.project_id, task.is_personal, task.canceled, task.deleted, task.completed FROM project LEFT OUTER JOIN project_user_visible ON project.project_id = project_user_visible.project_id LEFT OUTER JOIN project_usergroup_visible ON project_usergroup_visible.project_id = project.project_id LEFT OUTER JOIN project_client ON project_client.project_id = project.project_id LEFT OUTER JOIN client ON project_client.client_id = client.client_id LEFT OUTER JOIN client_user ON client_user.client_id = client.client_id LEFT OUTER JOIN usergroup ON usergroup.usergroup_id = project_usergroup_visible.usergroup_id LEFT OUTER JOIN user_usergroup ON usergroup.usergroup_id = user_usergroup.usergroup_id INNER JOIN task ON task.project_id = project.project_id WHERE (project.deleted = false or project.deleted IS NULL) AND ((project.flag_public = TRUE) OR (user_usergroup.user_id = ?1) OR (client_user.user_id = ?1) OR (project_user_visible.user_id = ?1)) AND (task.deleted = false or task.deleted IS NULL) and task.is_personal = false" + filterQuery, Task.class);
            q.setParameter(1, getAuthenticatedUser().getUserId());
            
            
            
            List<Task> tList = new ArrayList<Task>();
            tList = q.getResultList();
            
            getTasks(tList, taskList, em.createNamedQuery("Task.getChildTasks"), null);
            
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

        HashMap<String, Object> retProjects = new HashMap<String, Object>();
        retProjects.put("task", taskList);

        try {
            retVal = mapper.writeValueAsString(retProjects);
        } catch (IOException ex) {
            Logger.getLogger(ProjectFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;



//        // get root tasks (tasks which have no parent)
//        q = em.createNamedQuery("Task.getParentTasks");
//
//        List<Task> tList = new ArrayList<Task>();
//        tList = q.getResultList();
//
//
//        // get projects and its children
//        getTasks(tList, taskList, em.createNamedQuery("Task.getChildTasks"), null);
//
//
//        HashMap<String, Object> retTasks = new HashMap<String, Object>();
//        retTasks.put("task", taskList);
//
//        try {
//            retVal = mapper.writeValueAsString(retTasks);
//        } catch (IOException ex) {
//            Logger.getLogger(TaskFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return retVal;


    }
}
