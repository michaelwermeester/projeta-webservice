/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import be.luckycode.projetawebservice.Project;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
@Path("be.luckycode.projetawebservice.project")
public class ProjectFacadeREST extends AbstractFacade<Project> {
    @PersistenceContext(unitName = "be.luckycode_projeta-webservice_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    public ProjectFacadeREST() {
        super(Project.class);
    }

    @POST
    @Override
    @Consumes("application/json")
    public void create(Project entity) {
        super.create(entity);
    }

    @PUT
    @Override
    @Consumes("application/json")
    public void edit(Project entity) {
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
    public Project find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    /*@GET
    @Override
    @Produces("application/json")
    public List<Project> findAll() {
        return super.findAll();
    }*/
    
    // returns parent objects including its children
    @GET
    @Produces("application/json")
    public String findAll4() {
        
        String retVal = "";
        
        ObjectMapper mapper = new ObjectMapper();
        
        List<Map> projectList = new ArrayList<Map>();
        
        
        
        // get root projects (projects which have no parent)
        //Query q = em.createQuery("SELECT p FROM Project p WHERE p.parentProjectId IS NULL");
        Query q = em.createNamedQuery("Project.getParentProjects");
        
        List<Project> prjList = new ArrayList<Project>();
        prjList = q.getResultList();
        
        
        // get projects and its children
        getProjects(prjList, projectList);
        
        
        HashMap<String, Object> retProjects = new HashMap<String, Object>();
        retProjects.put("project", projectList);
        
        try {
            retVal = mapper.writeValueAsString(retProjects);
        } catch (IOException ex) {
            Logger.getLogger(ProjectFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return retVal;
        
 
    }

    private void getProjects(List<Project> prjList, List<Map> projectList) {
        // if list is not empty
        if (prjList.isEmpty() == false) {
            for (Project p : prjList) {
                Map<String, Object> projectData = new HashMap<String, Object>();
                Map<String, Object> userStruct = new HashMap<String, Object>();
                //Map<String, String> nameStruct = new HashMap<String, String>();
                userStruct.put("userId", p.getUserCreated().getUserId().toString());
                userStruct.put("username", p.getUserCreated().getUsername());
                projectData.put("userCreated", userStruct);
                projectData.put("dateCreated", CommonMethods.convertDate(p.getDateCreated()));
                //projectData.put("dateCreated", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(p.getDateCreated()));
                projectData.put("flagPublic", p.getFlagPublic());
                //projectData.put("endDate", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(p.getEndDate()));
                projectData.put("endDate", CommonMethods.convertDate(p.getEndDate()));
                //projectData.put("startDate", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(p.getStartDate()));
                projectData.put("startDate", CommonMethods.convertDate(p.getStartDate()));
                projectData.put("projectDescription", p.getProjectDescription());
                projectData.put("projectId", p.getProjectId().toString());
                projectData.put("projectTitle", p.getProjectTitle());

                // get child projects, if any
                getChildProjects(p, userStruct, projectData);

                projectList.add(projectData);
            }
        }
  
    }

    private void getChildProjects(Project p, Map<String, Object> userStruct, Map<String, Object> projectData) {
        List<Map> childProjectList = new ArrayList<Map>();
        
        // get child projects
        Query qry_child_projects = em.createNamedQuery("Project.getChildProjects");
        qry_child_projects.setParameter(1, p);
        
    
        List<Project> childPrjList = new ArrayList<Project>();
        childPrjList = qry_child_projects.getResultList();
        
        // if list is not empty
        /*if (childPrjList.isEmpty() == false) {
            for (Project childProject : childPrjList) {
                Map<String, Object> childProjectData = new HashMap<String, Object>();
                Map<String, Object> childUserStruct = new HashMap<String, Object>();
                //Map<String, String> nameStruct = new HashMap<String, String>();
                childUserStruct.put("userId", childProject.getUserCreated().getUserId().toString());
                childUserStruct.put("username", childProject.getUserCreated().getUsername());
                childProjectData.put("userCreated", userStruct);
                childProjectData.put("dateCreated", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(childProject.getDateCreated()));
                childProjectData.put("flagPublic", childProject.getFlagPublic());
                childProjectData.put("endDate", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(childProject.getEndDate()));
                childProjectData.put("startDate", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(childProject.getStartDate()));
                childProjectData.put("projectDescription", childProject.getProjectDescription());
                childProjectData.put("projectId", childProject.getProjectId().toString());
                childProjectData.put("projectTitle", childProject.getProjectTitle());

                // get child projects
                getChildProjects(childProject, childUserStruct, childProjectData);

                childProjectList.add(childProjectData);
            }
        }*/
        
        // get child projects
        getProjects(childPrjList, childProjectList);
        
        // 
        if (childProjectList.isEmpty() == false) {
            
            projectData.put("childProject", childProjectList);
        }
    }
    
    // returns parent objects including its children
    /*@GET
    @Produces("application/json")
    public String findAll3() {
        
        String retVal = "";
        
        ObjectMapper mapper = new ObjectMapper();
        
        List<Map> projectList = new ArrayList<Map>();
        
        
        
        // get root projects (projects which have no parent)
        //Query q = em.createQuery("SELECT p FROM Project p WHERE p.parentProjectId IS NULL");
        Query q = em.createNamedQuery("Project.getParentProjects");
        
        List<Project> prjList = new ArrayList<Project>();
        prjList = q.getResultList();
        
        
        
        
        for (Project p : prjList) {
            Map<String, Object> projectData = new HashMap<String, Object>();
            Map<String, Object> userStruct = new HashMap<String, Object>();
            //Map<String, String> nameStruct = new HashMap<String, String>();
            userStruct.put("userId", p.getUserCreated().getUserId().toString());
            userStruct.put("username", p.getUserCreated().getUsername());
            projectData.put("userCreated", userStruct);
            projectData.put("dateCreated", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(p.getDateCreated()));
            projectData.put("flagPublic", p.getFlagPublic());
            projectData.put("endDate", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(p.getEndDate()));
            projectData.put("startDate", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(p.getStartDate()));
            projectData.put("projectDescription", p.getProjectDescription());
            projectData.put("projectId", p.getProjectId().toString());
            projectData.put("projectTitle", p.getProjectTitle());
            
            List<Map> childProjectList = new ArrayList<Map>();
            
            // get child projects
            Query qry_child_projects = em.createNamedQuery("Project.getChildProjects");
            qry_child_projects.setParameter(1, p);
            
        
            List<Project> childPrjList = new ArrayList<Project>();
            childPrjList = qry_child_projects.getResultList();
            
            for (Project childProject : childPrjList) {
                Map<String, Object> childProjectData = new HashMap<String, Object>();
                Map<String, Object> childUserStruct = new HashMap<String, Object>();
                //Map<String, String> nameStruct = new HashMap<String, String>();
                childUserStruct.put("userId", childProject.getUserCreated().getUserId().toString());
                childUserStruct.put("username", childProject.getUserCreated().getUsername());
                childProjectData.put("userCreated", userStruct);
                childProjectData.put("dateCreated", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(childProject.getDateCreated()));
                childProjectData.put("flagPublic", childProject.getFlagPublic());
                childProjectData.put("endDate", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(childProject.getEndDate()));
                childProjectData.put("startDate", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(childProject.getStartDate()));
                childProjectData.put("projectDescription", childProject.getProjectDescription());
                childProjectData.put("projectId", childProject.getProjectId().toString());
                childProjectData.put("projectTitle", childProject.getProjectTitle());
                
                childProjectList.add(childProjectData);
            }
            
            if (childProjectList.isEmpty() == false) {
                projectData.put("childProject", childProjectList);
            }
            
            projectList.add(projectData);
        }
        
        HashMap<String, Object> retProjects = new HashMap<String, Object>();
        retProjects.put("project", projectList);
        
        try {
            retVal = mapper.writeValueAsString(retProjects);
        } catch (IOException ex) {
            Logger.getLogger(ProjectFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return retVal;
        
 
    }*/
    
    // returns all projects (ignores if the project is a child or parent)
    /*@GET
    @Produces("application/json")
    public String findAll2() {
        List<Project> projects = super.findAll();

        String retVal = "";
        
        ObjectMapper mapper = new ObjectMapper();
        
        List<Map> projectList = new ArrayList<Map>();
                
        for (Project p : projects) {
            Map<String, Object> projectData = new HashMap<String, Object>();
            Map<String, Object> userStruct = new HashMap<String, Object>();
            //Map<String, String> nameStruct = new HashMap<String, String>();
            userStruct.put("userId", p.getUserCreated().getUserId().toString());
            userStruct.put("username", p.getUserCreated().getUsername());
            projectData.put("userCreated", userStruct);
            projectData.put("dateCreated", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(p.getDateCreated()));
            projectData.put("flagPublic", p.getFlagPublic());
            projectData.put("endDate", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(p.getEndDate()));
            projectData.put("startDate", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(p.getStartDate()));
            projectData.put("projectDescription", p.getProjectDescription());
            projectData.put("projectId", p.getProjectId().toString());
            projectData.put("projectTitle", p.getProjectTitle());
            
            projectList.add(projectData);
        }
        
        HashMap<String, Object> retProjects = new HashMap<String, Object>();
        retProjects.put("project", projectList);
        
        try {
            retVal = mapper.writeValueAsString(retProjects);
        } catch (IOException ex) {
            Logger.getLogger(ProjectFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return retVal;
        
    }*/

    @GET
    @Path("{from}/{to}")
    @Produces("application/json")
    public List<Project> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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
