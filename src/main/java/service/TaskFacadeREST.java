/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import be.luckycode.projetawebservice.Task;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author michael
 */
@Stateless
@Path("be.luckycode.projetawebservice.task")
public class TaskFacadeREST extends AbstractFacade<Task> {
    @PersistenceContext(unitName = "be.luckycode_projeta-webservice_war_1.0-SNAPSHOTPU")
    private EntityManager em;

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

    /*@GET
    @Override
    @Produces({"application/xml", "application/json"})
    public List<Task> findAll() {
        return super.findAll();
    }*/
    
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
                Map<String, Object> taskData = new HashMap<String, Object>();
                Map<String, Object> userStruct = new HashMap<String, Object>();
                //Map<String, String> nameStruct = new HashMap<String, String>();
                
                userStruct.put("userId", t.getUserCreated().getUserId().toString());
                userStruct.put("username", t.getUserCreated().getUsername());
                taskData.put("userCreated", userStruct);
                //taskData.put("dateCreated", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(t.getDateCreated()));
                taskData.put("endDate", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(t.getEndDate()));
                taskData.put("startDate", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(t.getStartDate()));
                taskData.put("taskDescription", t.getTaskDescription());
                taskData.put("taskId", t.getTaskId().toString());
                taskData.put("taskTitle", t.getTaskTitle());

                // get child projects, if any
                getChildTasks(t, userStruct, taskData);

                taskMapList.add(taskData);
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
    
}
