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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
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
    
    @GET
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
        
    }

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
