/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

//import be.luckycode.projetawebservice.DummyProject;
import be.luckycode.projetawebservice.Progress;
import be.luckycode.projetawebservice.Project;
import be.luckycode.projetawebservice.ProjectDummy;
import be.luckycode.projetawebservice.ProjectSimpleWebSite;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author michael
 */
@Stateless
@Path("projects")
public class ProjectFacadeREST extends AbstractFacade<Project> {

    @PersistenceContext(unitName = "be.luckycode_projeta-webservice_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    public ProjectFacadeREST() {
        super(Project.class);
    }

    /*
     * @POST @Override @Consumes("application/json") public void create(Project
     * entity) { super.create(entity);
    }
     */
    @POST
    @Path("create")
    @RolesAllowed("administrator")
    @Consumes("application/json")
    @Produces("application/json")
    public String createNewProject(Project entity) {

        em.persist(entity);

        em.flush();

        // test -> return project id...
        //return entity.getProjectId().toString();
        //Project p = super.find(entity.getProjectId());
        List<Project> prjList = new ArrayList<Project>();
        prjList.add(entity);

        ObjectMapper mapper = new ObjectMapper();
        List<Map> projectList = new ArrayList<Map>();

        getProjects(prjList, projectList);

//        Map<String, Object> projectData = new HashMap<String, Object>();
//                Map<String, Object> userStruct = new HashMap<String, Object>();
//                //Map<String, String> nameStruct = new HashMap<String, String>();
//                if (p.getUserCreated() != null) {
//                    
//                    userStruct.put("userId", p.getUserCreated().getUserId().toString());
//                    userStruct.put("username", p.getUserCreated().getUsername());
//                    projectData.put("userCreated", userStruct);
//                }
//                
//                if (p.getDateCreated() != null)
//                    projectData.put("dateCreated", CommonMethods.convertDate(p.getDateCreated()));
//                //projectData.put("dateCreated", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(p.getDateCreated()));
//                if (p.getFlagPublic() != null)
//                    projectData.put("flagPublic", p.getFlagPublic());
//                //projectData.put("endDate", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(p.getEndDate()));
//                if (p.getEndDate() != null)
//                    projectData.put("endDate", CommonMethods.convertDate(p.getEndDate()));
//                //projectData.put("startDate", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(p.getStartDate()));
//                if (p.getStartDate() != null) 
//                    projectData.put("startDate", CommonMethods.convertDate(p.getStartDate()));
//                projectData.put("projectDescription", p.getProjectDescription());
//                projectData.put("projectId", p.getProjectId().toString());
//                projectData.put("projectTitle", p.getProjectTitle());
//                
//                // new/optional
//                if (p.getCompleted() != null)
//                    projectData.put("completed", p.getCompleted());
//                else
//                    projectData.put("completed", false);
//                if (p.getCanceled() != null)
//                    projectData.put("canceled", p.getCanceled());
//                else
//                    projectData.put("canceled", false);
//                if (p.getStartDateReal() != null)
//                    projectData.put("startDateReal", CommonMethods.convertDate(p.getStartDateReal()));
//                if (p.getEndDateReal() != null)
//                    projectData.put("endDateReal", CommonMethods.convertDate(p.getEndDateReal()));
//                    
//                projectList.add(projectData);

        String retVal = "";

        HashMap<String, Object> retProjects = new HashMap<String, Object>();
        retProjects.put("project", projectList);

        try {
            retVal = mapper.writeValueAsString(retProjects);
        } catch (IOException ex) {
            Logger.getLogger(ProjectFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;
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

    @POST
    @Path("delete/{id}")
    public void deleteProjectById(@PathParam("id") Integer id) {

        Project project = super.find(id);
        // marquer "Supprimé"
        project.setDeleted(Boolean.TRUE);

        // enregistrer en DB.
        super.edit(project);
    }

    @POST
    @Path("delete")
    public void deleteProject(Project entity) {

        Project project = super.find(entity.getProjectId());
        // marquer "Supprimé"
        project.setDeleted(Boolean.TRUE);

        // enregistrer en DB.
        super.edit(project);
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public Project find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    /*
     * @GET @Override @Produces("application/json") public List<Project>
     * findAll() { return super.findAll();
    }
     */
    
    // FOR WEBSITE !!!
    @GET
    @Path("wsprojects")
    public ProjectDummy findProjectsPOJO() {
        
        ProjectDummy projDummy = new ProjectDummy();
        //projDummy.setListProject(super.findAll());
        
        //List<Project> listProjTmp = super.findAll();
        Query q = em.createNamedQuery("Project.getParentProjects");

        List<Project> prjList = new ArrayList<Project>();
        prjList = q.getResultList();
        
        
        List<ProjectSimpleWebSite> listProj = new ArrayList<ProjectSimpleWebSite>();
        
        for (Project p_tmp : prjList) {
            
            ProjectSimpleWebSite p = new ProjectSimpleWebSite();
            p.setProjectTitle(p_tmp.getProjectTitle());
            p.setProjectId(p_tmp.getProjectId());
            
            // statut
            p.setProjectStatus(getStatusForProjectId(p_tmp.getProjectId()));

            getChildProjectsWebSite(p);
            
            listProj.add(p);
        }
        
        projDummy.setListProject(listProj);
        
        return projDummy;
    }
    
    // FOR WEBSITE !!!
    private void getChildProjectsWebSite(ProjectSimpleWebSite p) {
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
            
            // statut.
            p_sub.setProjectStatus(getStatusForProjectId(p_tmp.getProjectId()));
            
            getChildProjectsWebSite(p_sub);
            
            listSubProject.add(p_sub);
        }
        
        p.setChildProject(listSubProject);
    }
    
    // retourne le statut actuel du projet.
    private String getStatusForProjectId(Integer projectId) {
        
        // liste des 'Progress' pour le projet.
        Query qryStatus = em.createNamedQuery("Progress.findByProjectId");
        qryStatus.setParameter("projectId", projectId);
        // obtenir le plus récent.
        qryStatus.setMaxResults(1); // top 1 result
        // lire en liste.
        List<Progress> listProgress = new ArrayList<Progress>();
        listProgress = qryStatus.getResultList();
        
        // retourner le statut, s'il y'en a un.
        if (listProgress.size() > 0) {
            return listProgress.get(0).getStatusId().getStatusName();
        } else {
            return "-";
        }
    }
    
    /*@GET
    @Path("test")
    //public List<Project> findProjectsPOJO() {
    @Produces("application/xml")
    public DummyProject findProjectsPOJO() {
        //return super.findAll();
        //return super.findAll();
        
        DummyProject dc = new DummyProject();
        dc.setListProjects(super.findAll());
        
        return dc;
        
        //Project p = new Project();
        //p.setProjectCollection(new ArrayList(super.findAll()));
        //int size = p.getProjectCollection().size();
        //String s = Integer.toString(size);
        //p.setProjectTitle(s);
        
        //return p;
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
                // retourner/inclure seulement s'il ne s'agit pas d'un projet supprimé.  
                if (p.getDeleted() == null || p.getDeleted() == false) {
                    Map<String, Object> projectData = new HashMap<String, Object>();
                    Map<String, Object> userStruct = new HashMap<String, Object>();
                    //Map<String, String> nameStruct = new HashMap<String, String>();
                    if (p.getUserCreated() != null) {

                        userStruct.put("userId", p.getUserCreated().getUserId().toString());
                        userStruct.put("username", p.getUserCreated().getUsername());
                        projectData.put("userCreated", userStruct);
                    }

                    if (p.getDateCreated() != null) {
                        projectData.put("dateCreated", CommonMethods.convertDate(p.getDateCreated()));
                    }
                    //projectData.put("dateCreated", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(p.getDateCreated()));
                    if (p.getFlagPublic() != null) {
                        projectData.put("flagPublic", p.getFlagPublic());
                    }
                    //projectData.put("endDate", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(p.getEndDate()));
                    if (p.getEndDate() != null) {
                        projectData.put("endDate", CommonMethods.convertDate(p.getEndDate()));
                    }
                    //projectData.put("startDate", new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(p.getStartDate()));
                    if (p.getStartDate() != null) {
                        projectData.put("startDate", CommonMethods.convertDate(p.getStartDate()));
                    }
                    projectData.put("projectDescription", p.getProjectDescription());
                    projectData.put("projectId", p.getProjectId().toString());
                    projectData.put("projectTitle", p.getProjectTitle());

                    // new/optional
                    if (p.getCompleted() != null) {
                        projectData.put("completed", p.getCompleted());
                    } else {
                        projectData.put("completed", false);
                    }
                    if (p.getCanceled() != null) {
                        projectData.put("canceled", p.getCanceled());
                    } else {
                        projectData.put("canceled", false);
                    }
                    if (p.getStartDateReal() != null) {
                        projectData.put("startDateReal", CommonMethods.convertDate(p.getStartDateReal()));
                    }
                    if (p.getEndDateReal() != null) {
                        projectData.put("endDateReal", CommonMethods.convertDate(p.getEndDateReal()));
                    }

                    // get child projects, if any
                    getChildProjects(p, userStruct, projectData);

                    // retourner/inclure seulement s'il ne s'agit pas d'un projet supprimé.  
                    //if (p.getDeleted() == null || p.getDeleted() == false) {

                    projectList.add(projectData);
                }
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
        /*
         * if (childPrjList.isEmpty() == false) { for (Project childProject :
         * childPrjList) { Map<String, Object> childProjectData = new
         * HashMap<String, Object>(); Map<String, Object> childUserStruct = new
         * HashMap<String, Object>(); //Map<String, String> nameStruct = new
         * HashMap<String, String>(); childUserStruct.put("userId",
         * childProject.getUserCreated().getUserId().toString());
         * childUserStruct.put("username",
         * childProject.getUserCreated().getUsername());
         * childProjectData.put("userCreated", userStruct);
         * childProjectData.put("dateCreated", new
         * SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(childProject.getDateCreated()));
         * childProjectData.put("flagPublic", childProject.getFlagPublic());
         * childProjectData.put("endDate", new
         * SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(childProject.getEndDate()));
         * childProjectData.put("startDate", new
         * SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(childProject.getStartDate()));
         * childProjectData.put("projectDescription",
         * childProject.getProjectDescription());
         * childProjectData.put("projectId",
         * childProject.getProjectId().toString());
         * childProjectData.put("projectTitle", childProject.getProjectTitle());
         *
         * // get child projects getChildProjects(childProject, childUserStruct,
         * childProjectData);
         *
         * childProjectList.add(childProjectData); }
        }
         */

        // get child projects
        getProjects(childPrjList, childProjectList);

        // 
        if (childProjectList.isEmpty() == false) {

            projectData.put("childProject", childProjectList);
        }
    }

    // returns parent objects including its children
    /*
     * @GET @Produces("application/json") public String findAll3() {
     *
     * String retVal = "";
     *
     * ObjectMapper mapper = new ObjectMapper();
     *
     * List<Map> projectList = new ArrayList<Map>();
     *
     *
     *
     * // get root projects (projects which have no parent) //Query q =
     * em.createQuery("SELECT p FROM Project p WHERE p.parentProjectId IS
     * NULL"); Query q = em.createNamedQuery("Project.getParentProjects");
     *
     * List<Project> prjList = new ArrayList<Project>(); prjList =
     * q.getResultList();
     *
     *
     *
     *
     * for (Project p : prjList) { Map<String, Object> projectData = new
     * HashMap<String, Object>(); Map<String, Object> userStruct = new
     * HashMap<String, Object>(); //Map<String, String> nameStruct = new
     * HashMap<String, String>(); userStruct.put("userId",
     * p.getUserCreated().getUserId().toString()); userStruct.put("username",
     * p.getUserCreated().getUsername()); projectData.put("userCreated",
     * userStruct); projectData.put("dateCreated", new
     * SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(p.getDateCreated()));
     * projectData.put("flagPublic", p.getFlagPublic());
     * projectData.put("endDate", new
     * SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(p.getEndDate()));
     * projectData.put("startDate", new
     * SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(p.getStartDate()));
     * projectData.put("projectDescription", p.getProjectDescription());
     * projectData.put("projectId", p.getProjectId().toString());
     * projectData.put("projectTitle", p.getProjectTitle());
     *
     * List<Map> childProjectList = new ArrayList<Map>();
     *
     * // get child projects Query qry_child_projects =
     * em.createNamedQuery("Project.getChildProjects");
     * qry_child_projects.setParameter(1, p);
     *
     *
     * List<Project> childPrjList = new ArrayList<Project>(); childPrjList =
     * qry_child_projects.getResultList();
     *
     * for (Project childProject : childPrjList) { Map<String, Object>
     * childProjectData = new HashMap<String, Object>(); Map<String, Object>
     * childUserStruct = new HashMap<String, Object>(); //Map<String, String>
     * nameStruct = new HashMap<String, String>(); childUserStruct.put("userId",
     * childProject.getUserCreated().getUserId().toString());
     * childUserStruct.put("username",
     * childProject.getUserCreated().getUsername());
     * childProjectData.put("userCreated", userStruct);
     * childProjectData.put("dateCreated", new
     * SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(childProject.getDateCreated()));
     * childProjectData.put("flagPublic", childProject.getFlagPublic());
     * childProjectData.put("endDate", new
     * SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(childProject.getEndDate()));
     * childProjectData.put("startDate", new
     * SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(childProject.getStartDate()));
     * childProjectData.put("projectDescription",
     * childProject.getProjectDescription()); childProjectData.put("projectId",
     * childProject.getProjectId().toString());
     * childProjectData.put("projectTitle", childProject.getProjectTitle());
     *
     * childProjectList.add(childProjectData); }
     *
     * if (childProjectList.isEmpty() == false) {
     * projectData.put("childProject", childProjectList); }
     *
     * projectList.add(projectData); }
     *
     * HashMap<String, Object> retProjects = new HashMap<String, Object>();
     * retProjects.put("project", projectList);
     *
     * try { retVal = mapper.writeValueAsString(retProjects); } catch
     * (IOException ex) {
     * Logger.getLogger(ProjectFacadeREST.class.getName()).log(Level.SEVERE,
     * null, ex); }
     *
     * return retVal;
     *
     *
     * }
     */
    // returns all projects (ignores if the project is a child or parent)
    /*
     * @GET @Produces("application/json") public String findAll2() {
     * List<Project> projects = super.findAll();
     *
     * String retVal = "";
     *
     * ObjectMapper mapper = new ObjectMapper();
     *
     * List<Map> projectList = new ArrayList<Map>();
     *
     * for (Project p : projects) { Map<String, Object> projectData = new
     * HashMap<String, Object>(); Map<String, Object> userStruct = new
     * HashMap<String, Object>(); //Map<String, String> nameStruct = new
     * HashMap<String, String>(); userStruct.put("userId",
     * p.getUserCreated().getUserId().toString()); userStruct.put("username",
     * p.getUserCreated().getUsername()); projectData.put("userCreated",
     * userStruct); projectData.put("dateCreated", new
     * SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(p.getDateCreated()));
     * projectData.put("flagPublic", p.getFlagPublic());
     * projectData.put("endDate", new
     * SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(p.getEndDate()));
     * projectData.put("startDate", new
     * SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(p.getStartDate()));
     * projectData.put("projectDescription", p.getProjectDescription());
     * projectData.put("projectId", p.getProjectId().toString());
     * projectData.put("projectTitle", p.getProjectTitle());
     *
     * projectList.add(projectData); }
     *
     * HashMap<String, Object> retProjects = new HashMap<String, Object>();
     * retProjects.put("project", projectList);
     *
     * try { retVal = mapper.writeValueAsString(retProjects); } catch
     * (IOException ex) {
     * Logger.getLogger(ProjectFacadeREST.class.getName()).log(Level.SEVERE,
     * null, ex); }
     *
     * return retVal;
     *
     * }
     */
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

    @PUT
    @Path("update")
    @RolesAllowed("administrator")
    @Consumes("application/json")
    @Produces("application/json")
    public String updateProject(Project entity) {


        // fetch user to be updated.
        Project project = super.find(entity.getProjectId());

        if (project.getProjectTitle() != null) {
            project.setProjectTitle(entity.getProjectTitle());
        }
        if (project.getProjectDescription() != null) {
            project.setProjectDescription(entity.getProjectDescription());
        }


        super.edit(project);



        // SAME CODE AS IN CREATE !!!
        List<Project> prjList = new ArrayList<Project>();
        prjList.add(super.find(project.getProjectId()));

        ObjectMapper mapper = new ObjectMapper();
        List<Map> projectList = new ArrayList<Map>();

        getProjects(prjList, projectList);

        String retVal = "";

        HashMap<String, Object> retProjects = new HashMap<String, Object>();
        retProjects.put("project", projectList);

        try {
            retVal = mapper.writeValueAsString(retProjects);
        } catch (IOException ex) {
            Logger.getLogger(ProjectFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;
    }
}
