/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

//import be.luckycode.projetawebservice.DummyProject;
import be.luckycode.projetawebservice.*;
import java.io.IOException;
import java.text.ParseException;
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
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
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
    @Context
    SecurityContext security;

    public ProjectFacadeREST() {
        super(Project.class);
    }

    @POST
    @Path("create")
    @RolesAllowed({"administrator", "developer"})
    @Consumes("application/json")
    @Produces("application/json")
    public String createNewProject(Project entity) {

        // créer le projet en base de donnés.
        em.persist(entity);

        // initialiser les valeurs par défaut pour l'état, pourcentage, etc.
        initDefaultProgressForNewProject(entity);

        em.flush();


        List<Project> prjList = new ArrayList<Project>();
        prjList.add(entity);

        ObjectMapper mapper = new ObjectMapper();
        List<Map> projectList = new ArrayList<Map>();

        //getProjects(prjList, projectList, "Project.getChildProjects", "");
        getProjects(prjList, projectList, em.createNamedQuery("Project.getChildProjects"), null);


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

    // Créer état, pourcentage d'avancement etc. par défaut.
    private void initDefaultProgressForNewProject(Project entity) {
        Progress progress = new Progress();
        progress.setPercentageComplete((short) 0);
        progress.setProgressComment("Projet créé.");
        progress.setStatusId(new Status(1));
        progress.setUserCreated(getAuthenticatedUser());
        progress.setProjectId(entity);

        em.persist(progress);
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

    // Utilisé par le site web. Retourne tous les projets. 
    @GET
    @Path("wsprojects")
    public ProjectDummy findProjectsPOJO() {

        ProjectDummy projDummy = new ProjectDummy();
        List<ProjectSimpleWebSite> listProj = new ArrayList<ProjectSimpleWebSite>();

        if (security.isUserInRole("administrator")) {
            Query q = em.createNamedQuery("Project.getParentProjects");

            List<Project> prjList = new ArrayList<Project>();
            prjList = q.getResultList();

            for (Project p_tmp : prjList) {

                ProjectSimpleWebSite p = new ProjectSimpleWebSite();
                p.setProjectTitle(p_tmp.getProjectTitle());
                p.setProjectId(p_tmp.getProjectId());
                if (p_tmp.getStartDate() != null) {
                    p.setStartDate(p_tmp.getStartDate());
                }
                if (p_tmp.getEndDate() != null) {
                    p.setEndDate(p_tmp.getEndDate());
                }

                // statut
                p.setProjectStatus(getStatusForProjectId(p_tmp.getProjectId()));

                getChildProjectsWebSite(p, "Project.getChildProjects", null);

                listProj.add(p);
            }
        } else { // si pas administrateur, afficher que les projets qu'un utilisateur est autorisé de voir.   
            Query q = em.createNativeQuery("SELECT DISTINCT project.project_id as PROJECT_ID, project.project_title as PROJECT_TITLE, project.flag_public as FLAG_PUBLIC, project.project_description as PROJECT_DESCRIPTION, project.user_created as USER_CREATED, project.date_created as DATE_CREATED, project.start_date as START_DATE, project.end_date as END_DATE, project.start_date_real as START_DATE_REAL, project.end_date_real as END_DATE_REAL, project.parent_project_id as PARENT_PROJECT_ID, project.completed as COMPLETED, project.canceled as CANCELED, project.deleted as DELETED, project.user_assigned as USER_ASSIGNED FROM project LEFT OUTER JOIN project_user_visible ON project.project_id = project_user_visible.project_id LEFT OUTER JOIN project_usergroup_visible ON project_usergroup_visible.project_id = project.project_id LEFT OUTER JOIN project_client ON project_client.project_id = project.project_id LEFT OUTER JOIN client ON project_client.client_id = client.client_id LEFT OUTER JOIN client_user ON client_user.client_id = client.client_id LEFT OUTER JOIN usergroup ON usergroup.usergroup_id = project_usergroup_visible.usergroup_id LEFT OUTER JOIN user_usergroup ON usergroup.usergroup_id = user_usergroup.usergroup_id WHERE (project.deleted = false or project.deleted IS NULL) AND ((project.flag_public = TRUE) OR (user_usergroup.user_id = ?1) OR (client_user.user_id = ?1) OR (project_user_visible.user_id = ?1) OR (project.user_created = ?1) OR (project.user_assigned = ?1)) AND project.parent_project_id IS NULL", Project.class);
            q.setParameter(1, getAuthenticatedUser().getUserId());

            List<Project> prjList = new ArrayList<Project>();
            prjList = q.getResultList();

            Query subquery = em.createNativeQuery("SELECT DISTINCT project.project_id as PROJECT_ID, project.project_title as PROJECT_TITLE, project.flag_public as FLAG_PUBLIC, project.project_description as PROJECT_DESCRIPTION, project.user_created as USER_CREATED, project.date_created as DATE_CREATED, project.start_date as START_DATE, project.end_date as END_DATE, project.start_date_real as START_DATE_REAL, project.end_date_real as END_DATE_REAL, project.parent_project_id as PARENT_PROJECT_ID, project.completed as COMPLETED, project.canceled as CANCELED, project.deleted as DELETED, project.user_assigned as USER_ASSIGNED FROM project LEFT OUTER JOIN project_user_visible ON project.project_id = project_user_visible.project_id LEFT OUTER JOIN project_usergroup_visible ON project_usergroup_visible.project_id = project.project_id LEFT OUTER JOIN project_client ON project_client.project_id = project.project_id LEFT OUTER JOIN client ON project_client.client_id = client.client_id LEFT OUTER JOIN client_user ON client_user.client_id = client.client_id LEFT OUTER JOIN usergroup ON usergroup.usergroup_id = project_usergroup_visible.usergroup_id LEFT OUTER JOIN user_usergroup ON usergroup.usergroup_id = user_usergroup.usergroup_id WHERE project.parent_project_id = ?1 AND (project.deleted = false or project.deleted IS NULL) AND ((project.flag_public = TRUE) OR (user_usergroup.user_id = ?2) OR (client_user.user_id = ?2) OR (project_user_visible.user_id = ?2) OR (project.user_created = ?2) OR (project.user_assigned = ?2))", Project.class);
            subquery.setParameter(2, getAuthenticatedUser().getUserId());


            for (Project p_tmp : prjList) {

                ProjectSimpleWebSite p = new ProjectSimpleWebSite();
                p.setProjectTitle(p_tmp.getProjectTitle());
                p.setProjectId(p_tmp.getProjectId());
                if (p_tmp.getStartDate() != null) {
                    p.setStartDate(p_tmp.getStartDate());
                }
                if (p_tmp.getEndDate() != null) {
                    p.setEndDate(p_tmp.getEndDate());
                }

                // statut
                p.setProjectStatus(getStatusForProjectId(p_tmp.getProjectId()));

                getChildProjectsWebSite(p, null, subquery);

                listProj.add(p);
            }

            // get projects. Pas des sous-projets. 
            //getProjects(prjList, projectList, null, subquery);

        }

        projDummy.setListProject(listProj);

        return projDummy;
    }

    // Utilisé par le site web. Retourne tous les publics projets. 
    @GET
    @Path("wsprojectspublic")
    public ProjectDummy findPublicProjectsPOJO() {

        ProjectDummy projDummy = new ProjectDummy();

        Query q = em.createNamedQuery("Project.getParentPublicProjects");

        List<Project> prjList = new ArrayList<Project>();
        prjList = q.getResultList();


        List<ProjectSimpleWebSite> listProj = new ArrayList<ProjectSimpleWebSite>();

        for (Project p_tmp : prjList) {

            ProjectSimpleWebSite p = new ProjectSimpleWebSite();
            p.setProjectTitle(p_tmp.getProjectTitle());
            p.setProjectId(p_tmp.getProjectId());
            if (p_tmp.getStartDate() != null) {
                p.setStartDate(p_tmp.getStartDate());
            }
            if (p_tmp.getEndDate() != null) {
                p.setEndDate(p_tmp.getEndDate());
            }

            // statut
            p.setProjectStatus(getStatusForProjectId(p_tmp.getProjectId()));

            getChildProjectsWebSite(p, "Project.getChildPublicProjects", null);

            listProj.add(p);
        }

        projDummy.setListProject(listProj);

        return projDummy;
    }

    // Utilisé par le site web. Retourne les projets enfants. 
    private void getChildProjectsWebSite(ProjectSimpleWebSite p, String namedQuery, Query nativeSubQuery) {

        Query qry_child_projects;
        List<Project> childPrjList = new ArrayList<Project>();

        // get child projects
        if (namedQuery != null) {
            qry_child_projects = em.createNamedQuery(namedQuery);
            Project p_qry = new Project(p.getProjectId());
            qry_child_projects.setParameter(1, p_qry);

            childPrjList = qry_child_projects.getResultList();
        } else {
            qry_child_projects = nativeSubQuery;
            qry_child_projects.setParameter(1, p.getProjectId());

            childPrjList = qry_child_projects.getResultList();

            // get child projects
            //getProjects(childPrjList, childProjectList, null, qry_child_projects);
        }





        List<ProjectSimpleWebSite> listSubProject = new ArrayList<ProjectSimpleWebSite>();

        for (Project p_tmp : childPrjList) {

            ProjectSimpleWebSite p_sub = new ProjectSimpleWebSite();
            p_sub.setProjectTitle(p_tmp.getProjectTitle());
            p_sub.setProjectId(p_tmp.getProjectId());
            if (p_tmp.getStartDate() != null) {
                p_sub.setStartDate(p_tmp.getStartDate());
            }
            if (p_tmp.getEndDate() != null) {
                p_sub.setEndDate(p_tmp.getEndDate());
            }

            // statut.
            p_sub.setProjectStatus(getStatusForProjectId(p_tmp.getProjectId()));

            if (namedQuery != null) {
                getChildProjectsWebSite(p_sub, namedQuery, null);
            } else {
                getChildProjectsWebSite(p_sub, null, nativeSubQuery);
            }

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
            String status = "";
            if (listProgress.get(0).getPercentageComplete() != null) {
                status += listProgress.get(0).getPercentageComplete().toString() + "% - ";
            }

            return status + listProgress.get(0).getStatusId().getStatusName();
        } else {
            return "-";
        }
    }

    // retourne le statut actuel du projet.
    private Short getPercentageCompleteForProjectId(Integer projectId) {

        // liste des 'Progress' pour le projet.
        Query qryPercentage = em.createNamedQuery("Progress.findByProjectId");
        qryPercentage.setParameter("projectId", projectId);
        // obtenir le plus récent.
        qryPercentage.setMaxResults(1); // top 1 result
        // lire en liste.
        List<Progress> listProgress = new ArrayList<Progress>();
        listProgress = qryPercentage.getResultList();

        // retourner le statut, s'il y'en a un.
        if (listProgress.size() > 0) {
            return listProgress.get(0).getPercentageComplete();
        } else {
            return 0;
        }
    }

    // retourne le statut actuel du projet.
    private Progress getProgressForProjectId(Integer projectId) {

        // liste des 'Progress' pour le projet.
        Query qryPercentage = em.createNamedQuery("Progress.findByProjectId");
        qryPercentage.setParameter("projectId", projectId);
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

    // returns parent objects including its children
    @GET
    @Produces("application/json")
    public String findAllProjects() {

        String retVal = "";

        ObjectMapper mapper = new ObjectMapper();

        List<Map> projectList = new ArrayList<Map>();

        // si utilisateur authentifié est un administrateur -> afficher tous les projets. 
        if (security.isUserInRole("administrator")) {
            // get projets parents de la base de données. 
            Query q = em.createNamedQuery("Project.getParentProjects");

            List<Project> prjList = new ArrayList<Project>();
            prjList = q.getResultList();

            // get projects and its children
            //getProjects(prjList, projectList, "Project.getChildProjects", "");
            getProjects(prjList, projectList, em.createNamedQuery("Project.getChildProjects"), null);
        } else {     // si pas administrateur, afficher que les projets qu'un utilisateur est autorisé de voir.   
            Query q = em.createNativeQuery("SELECT DISTINCT project.project_id as PROJECT_ID, project.project_title as PROJECT_TITLE, project.flag_public as FLAG_PUBLIC, project.project_description as PROJECT_DESCRIPTION, project.user_created as USER_CREATED, project.date_created as DATE_CREATED, project.start_date as START_DATE, project.end_date as END_DATE, project.start_date_real as START_DATE_REAL, project.end_date_real as END_DATE_REAL, project.parent_project_id as PARENT_PROJECT_ID, project.completed as COMPLETED, project.canceled as CANCELED, project.deleted as DELETED, project.user_assigned as USER_ASSIGNED FROM project LEFT OUTER JOIN project_user_visible ON project.project_id = project_user_visible.project_id LEFT OUTER JOIN project_usergroup_visible ON project_usergroup_visible.project_id = project.project_id LEFT OUTER JOIN project_client ON project_client.project_id = project.project_id LEFT OUTER JOIN client ON project_client.client_id = client.client_id LEFT OUTER JOIN client_user ON client_user.client_id = client.client_id LEFT OUTER JOIN usergroup ON usergroup.usergroup_id = project_usergroup_visible.usergroup_id LEFT OUTER JOIN user_usergroup ON usergroup.usergroup_id = user_usergroup.usergroup_id WHERE (project.deleted = false or project.deleted IS NULL) AND ((project.flag_public = TRUE) OR (user_usergroup.user_id = ?1) OR (client_user.user_id = ?1) OR (project_user_visible.user_id = ?1) OR (project.user_created = ?1) OR (project.user_assigned = ?1)) AND project.parent_project_id IS NULL", Project.class);
            q.setParameter(1, getAuthenticatedUser().getUserId());

            List<Project> prjList = new ArrayList<Project>();
            prjList = q.getResultList();

            String subqueryStr = "SELECT DISTINCT project.project_id as PROJECT_ID, project.project_title as PROJECT_TITLE, project.flag_public as FLAG_PUBLIC, project.project_description as PROJECT_DESCRIPTION, project.user_created as USER_CREATED, project.date_created as DATE_CREATED, project.start_date as START_DATE, project.end_date as END_DATE, project.start_date_real as START_DATE_REAL, project.end_date_real as END_DATE_REAL, project.parent_project_id as PARENT_PROJECT_ID, project.completed as COMPLETED, project.canceled as CANCELED, project.deleted as DELETED, project.user_assigned as USER_ASSIGNED FROM project LEFT OUTER JOIN project_user_visible ON project.project_id = project_user_visible.project_id LEFT OUTER JOIN project_usergroup_visible ON project_usergroup_visible.project_id = project.project_id LEFT OUTER JOIN project_client ON project_client.project_id = project.project_id LEFT OUTER JOIN client ON project_client.client_id = client.client_id LEFT OUTER JOIN client_user ON client_user.client_id = client.client_id LEFT OUTER JOIN usergroup ON usergroup.usergroup_id = project_usergroup_visible.usergroup_id LEFT OUTER JOIN user_usergroup ON usergroup.usergroup_id = user_usergroup.usergroup_id WHERE project.parent_project_id = ?1 AND (project.deleted = false or project.deleted IS NULL) AND ((project.flag_public = TRUE) OR (user_usergroup.user_id = ?2) OR (client_user.user_id = ?2) OR (project_user_visible.user_id = ?2) OR (project.user_created = ?2) OR (project.user_assigned = ?2))";
            //Query subquery = em.createNativeQuery("SELECT DISTINCT project.project_id as PROJECT_ID, project.project_title as PROJECT_TITLE, project.flag_public as FLAG_PUBLIC, project.project_description as PROJECT_DESCRIPTION, project.user_created as USER_CREATED, project.date_created as DATE_CREATED, project.start_date as START_DATE, project.end_date as END_DATE, project.start_date_real as START_DATE_REAL, project.end_date_real as END_DATE_REAL, project.parent_project_id as PARENT_PROJECT_ID, project.completed as COMPLETED, project.canceled as CANCELED, project.deleted as DELETED, project.user_assigned as USER_ASSIGNED FROM project LEFT OUTER JOIN project_user_visible ON project.project_id = project_user_visible.project_id LEFT OUTER JOIN project_usergroup_visible ON project_usergroup_visible.project_id = project.project_id LEFT OUTER JOIN project_client ON project_client.project_id = project.project_id LEFT OUTER JOIN client ON project_client.client_id = client.client_id LEFT OUTER JOIN client_user ON client_user.client_id = client.client_id LEFT OUTER JOIN usergroup ON usergroup.usergroup_id = project_usergroup_visible.usergroup_id LEFT OUTER JOIN user_usergroup ON usergroup.usergroup_id = user_usergroup.usergroup_id WHERE project.parent_project_id = ?1 AND (project.deleted = false or project.deleted IS NULL) AND ((project.flag_public = TRUE) OR (user_usergroup.user_id = ?2) OR (client_user.user_id = ?2) OR (project_user_visible.user_id = ?2) OR (project.user_created = ?1) OR (project.user_assigned = ?1))", Project.class);
            Query subquery = em.createNativeQuery(subqueryStr, Project.class); 
            subquery.setParameter(2, getAuthenticatedUser().getUserId());

            // get projects. Pas des sous-projets. 
            //getProjects(prjList, projectList, null, subquery);
            getProjects(prjList, projectList, null, subqueryStr);
            
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

    //private void getProjects(List<Project> prjList, List<Map> projectList, Query namedsubquery, Query nativesubquery) {
    private void getProjects(List<Project> prjList, List<Map> projectList, Query namedsubquery, String nativesubquery) {
        // if list is not empty
        if (prjList.isEmpty() == false) {
            for (Project p : prjList) {
                // retourner/inclure seulement s'il ne s'agit pas d'un projet supprimé.  
                if (p.getDeleted() == null || p.getDeleted() == false) {
                    Map<String, Object> projectData = new HashMap<String, Object>();
                    Map<String, Object> userStruct = new HashMap<String, Object>();

                    if (p.getUserCreated() != null) {

                        userStruct.put("userId", p.getUserCreated().getUserId().toString());
                        userStruct.put("username", p.getUserCreated().getUsername());
                        projectData.put("userCreated", userStruct);
                    }

                    if (p.getUserAssigned() != null) {

                        userStruct.put("userId", p.getUserAssigned().getUserId().toString());
                        userStruct.put("username", p.getUserAssigned().getUsername());
                        if (p.getUserAssigned().getFirstName() != null) {
                            userStruct.put("firstName", p.getUserAssigned().getFirstName());
                        }
                        if (p.getUserAssigned().getLastName() != null) {
                            userStruct.put("lastName", p.getUserAssigned().getLastName());
                        }
                        projectData.put("userAssigned", userStruct);
                    }

                    if (p.getDateCreated() != null) {
                        projectData.put("dateCreated", CommonMethods.convertDate(p.getDateCreated()));
                    }

                    if (p.getFlagPublic() != null) {
                        projectData.put("flagPublic", p.getFlagPublic());
                    }

                    if (p.getEndDate() != null) {
                        projectData.put("endDate", CommonMethods.convertDate(p.getEndDate()));
                    }

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

                    // état et pourcentage.
                    Progress progress = getProgressForProjectId(p.getProjectId());

                    if (progress != null) {
                        // état.
                        if (progress.getStatusId() != null && progress.getStatusId().getStatusName() != null) {
                            projectData.put("projectStatus", progress.getStatusId().getStatusName());
                        }
                        // pourcentage.
                        if (progress.getPercentageComplete() != null) {
                            projectData.put("projectPercentage", progress.getPercentageComplete().toString());
                        }
                    }

                    // get child projects, if any
                    if (namedsubquery != null || nativesubquery != null) {
                        getChildProjects(p, userStruct, projectData, namedsubquery, nativesubquery);
                    }

//                    if (namedQuery.length() > 0) {
//                        getChildProjects(p, userStruct, projectData, namedQuery, "");
//                    }
//                    
//                    // get child projects, if any
//                    if (nativeQuery.length() > 0) {
//                        getChildProjects(p, userStruct, projectData, "", nativeQuery);
//                    }

                    projectList.add(projectData);
                }
            }
        }

    }

    // méthode getChildProjects principale. Utiliser celle-ci
    //private void getChildProjects(Project p, Map<String, Object> userStruct, Map<String, Object> projectData, Query namedSubQuery, Query nativeSubQuery) {
    private void getChildProjects(Project p, Map<String, Object> userStruct, Map<String, Object> projectData, Query namedSubQuery, String nativeSubQuery) {

        List<Map> childProjectList = new ArrayList<Map>();

        Query qry_child_projects;

        // get child projects
        if (namedSubQuery != null) {
            //Query qry_child_projects = em.createNamedQuery(namedSubQuery);
            qry_child_projects = namedSubQuery;
            qry_child_projects.setParameter(1, p);

            List<Project> childPrjList = new ArrayList<Project>();
            childPrjList = qry_child_projects.getResultList();

            // get child projects
            getProjects(childPrjList, childProjectList, qry_child_projects, null);
        } else {
            
            //qry_child_projects = nativeSubQuery;
            qry_child_projects = em.createNativeQuery(nativeSubQuery, Project.class);  
            qry_child_projects.setParameter(1, p.getProjectId());
            
            qry_child_projects.setParameter(2, getAuthenticatedUser().getUserId());

            List<Project> childPrjList = new ArrayList<Project>();
            childPrjList = qry_child_projects.getResultList();

            // get child projects
            //getProjects(childPrjList, childProjectList, null, qry_child_projects);
            getProjects(childPrjList, childProjectList, null, nativeSubQuery);
        }


        //List<Project> childPrjList = new ArrayList<Project>();
        //childPrjList = qry_child_projects.getResultList();

        //// get child projects
        //getProjects(childPrjList, childProjectList, qry_child_projects, null);

        // 
        if (childProjectList.isEmpty() == false) {

            projectData.put("childProject", childProjectList);
        }
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

    @PUT
    @Path("update")
    @RolesAllowed({"administrator", "developer"})
    @Consumes("application/json")
    @Produces("application/json")
    public String updateProject(Project entity) {

        // fetch user to be updated.
        Project project = super.find(entity.getProjectId());

        if (entity.getProjectTitle() != null) {
            project.setProjectTitle(entity.getProjectTitle());
        }
        if (entity.getProjectDescription() != null) {
            project.setProjectDescription(entity.getProjectDescription());
        }
        if (entity.getStartDate() != null) {
            project.setStartDate(entity.getStartDate());
        }
        if (entity.getEndDate() != null) {
            project.setEndDate(entity.getEndDate());
        }
        if (entity.getStartDateReal() != null) {
            project.setStartDateReal(entity.getStartDateReal());
        }
        if (entity.getEndDateReal() != null) {
            project.setEndDateReal(entity.getEndDateReal());
        }
        if (entity.getUserAssigned() != null) {
            project.setUserAssigned(entity.getUserAssigned());
        }
        if (entity.getCompleted() != null) {
            project.setCompleted(entity.getCompleted());
        }
        if (entity.getFlagPublic() != null) {
            project.setFlagPublic(entity.getFlagPublic());
        }
        

        super.edit(project);



        // retourne le projet qui a été mis à jour. 
        List<Project> prjList = new ArrayList<Project>();
        prjList.add(super.find(project.getProjectId()));

        ObjectMapper mapper = new ObjectMapper();
        List<Map> projectList = new ArrayList<Map>();

        //getProjects(prjList, projectList, "Project.getChildProjects");
        getProjects(prjList, projectList, em.createNamedQuery("Project.getChildProjects"), null);

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

    // returns parent objects including its children
    @GET
    @Path("public")
    @RolesAllowed({"user", "administrator", "developer"})
    @Produces("application/json")
    public String findAllPublicProjects(@QueryParam("status") String statusId, @QueryParam("minDate") String minDate, @QueryParam("maxDate") String maxDate) {

        String retVal = "";

        ObjectMapper mapper = new ObjectMapper();

        List<Map> projectList = new ArrayList<Map>();

        // filter
        String filterQuery = "";
        if (statusId != null) {
            filterQuery += " AND (" + statusId + "= (SELECT status_id FROM Progress p WHERE p.project_id = project.project_id order by p.date_created DESC LIMIT 1))";
        }


        if (minDate != null) {
            filterQuery += " AND (project.start_date >= " + minDate + ")";
        }
        if (maxDate != null) {
            filterQuery += " AND (project.end_date <= " + maxDate + ")";
        }
        
        // get root public projects (projects which have no parent)
        //Query q = em.createNamedQuery("Project.getParentPublicProjects");
        Query q = em.createNativeQuery("SELECT DISTINCT project.project_id as PROJECT_ID, project.project_title as PROJECT_TITLE, project.flag_public as FLAG_PUBLIC, project.project_description as PROJECT_DESCRIPTION, project.user_created as USER_CREATED, project.date_created as DATE_CREATED, project.start_date as START_DATE, project.end_date as END_DATE, project.start_date_real as START_DATE_REAL, project.end_date_real as END_DATE_REAL, project.parent_project_id as PARENT_PROJECT_ID, project.completed as COMPLETED, project.canceled as CANCELED, project.deleted as DELETED, project.user_assigned as USER_ASSIGNED FROM project WHERE project.parent_project_id IS NULL and project.flag_public = true and (project.deleted = false or project.deleted is null)" + filterQuery, Project.class);

        
        List<Project> prjList = new ArrayList<Project>();
        prjList = q.getResultList();

        // get projects and its children
        //getProjects(prjList, projectList, "Project.getChildPublicProjects", "");
        getProjects(prjList, projectList, em.createNamedQuery("Project.getChildPublicProjects"), null);

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
    @RolesAllowed({"administrator", "developer"})
    @Path("updateUsersVisibleForProject")
    @Consumes("application/json")
    public void updateUsersVisibleForProject(@QueryParam("projectId") Integer projectId, ArrayList<User> users) {

        Query q;

        if (projectId != null && (security.isUserInRole("administrator") || security.isUserInRole("developer"))) {
            q = em.createNamedQuery("Project.findByProjectId");
            q.setParameter("projectId", projectId);


            List<Project> projectList = new ArrayList<Project>();
            projectList = q.getResultList();

            if (projectList.size() == 1) {

                // user
                Project project = projectList.get(0);

                if (users.get(0).getUserId() == null) {
                    ArrayList<User> list = new ArrayList<User>();
                    project.setUserCollection(list);
                } else {
                    project.setUserCollection(users);
                }
                em.merge(project);

                // mettre à jour visibilité du projet parent. 
                if (project.getParentProjectId() != null) {
                    updateUsersVisibleForParentProject(project);
                }

            } else {
            }

        }
    }

    // met à jour la visibilité des utilisateurs du projet parent. 
    private void updateUsersVisibleForParentProject(Project p) {

        Query q = em.createNamedQuery("Project.findByProjectId");
        q.setParameter("projectId", p.getParentProjectId().getProjectId());

        List<Project> projectList = q.getResultList();
        Project parentProject = projectList.get(0);

        Boolean found = false;

        for (User u : p.getUserCollection()) {

            found = false;

            for (User uParent : parentProject.getUserCollection()) {

                if (uParent.getUserId() == u.getUserId()) {
                    found = true;
                    break;
                }
            }

            if (found == false) {

                parentProject.getUserCollection().add(u);

                em.merge(parentProject);
            }
        }

        // mettre à jour visibilité du projet parent. 
        if (parentProject.getParentProjectId() != null) {
            updateUsersVisibleForParentProject(parentProject);
        }
    }

    @PUT
    @RolesAllowed({"administrator", "developer"})
    @Path("updateUsergroupsVisibleForProject")
    @Consumes("application/json")
    public void updateUsergroupsVisibleForProject(@QueryParam("projectId") Integer projectId, ArrayList<Usergroup> usergroups) {

        Query q;

        if (projectId != null && (security.isUserInRole("administrator") || security.isUserInRole("developer"))) {
            q = em.createNamedQuery("Project.findByProjectId");
            q.setParameter("projectId", projectId);


            List<Project> projectList = new ArrayList<Project>();
            projectList = q.getResultList();

            if (projectList.size() == 1) {

                // user
                Project project = projectList.get(0);

                if (usergroups.get(0).getUsergroupId() == null) {
                    ArrayList<Usergroup> list = new ArrayList<Usergroup>();
                    project.setUsergroupCollection(list);
                } else {
                    project.setUsergroupCollection(usergroups);
                }
                em.merge(project);

                // mettre à jour visibilité du projet parent. 
                if (project.getParentProjectId() != null) {
                    updateUsergroupsVisibleForParentProject(project);
                }

            } else {
            }

        }
    }

    // met à jour la visibilité des groupes d'utilisateurs du projet parent. 
    private void updateUsergroupsVisibleForParentProject(Project p) {

        Query q = em.createNamedQuery("Project.findByProjectId");
        q.setParameter("projectId", p.getParentProjectId().getProjectId());

        List<Project> projectList = q.getResultList();
        Project parentProject = projectList.get(0);

        Boolean found = false;

        for (Usergroup ug : p.getUsergroupCollection()) {

            found = false;

            for (Usergroup ugParent : parentProject.getUsergroupCollection()) {

                if (ugParent.getUsergroupId() == ug.getUsergroupId()) {
                    found = true;
                    break;
                }
            }

            if (found == false) {

                parentProject.getUsergroupCollection().add(ug);

                em.merge(parentProject);
            }
        }

        // mettre à jour visibilité du projet parent. 
        if (parentProject.getParentProjectId() != null) {
            updateUsergroupsVisibleForParentProject(parentProject);
        }
    }

    @PUT
    @RolesAllowed({"administrator", "developer"})
    @Path("updateClientsVisibleForProject")
    @Consumes("application/json")
    public void updateClientsVisibleForProject(@QueryParam("projectId") Integer projectId, ArrayList<Client> clients) {

        Query q;

        if (projectId != null && (security.isUserInRole("administrator") || security.isUserInRole("developer"))) {
            q = em.createNamedQuery("Project.findByProjectId");
            q.setParameter("projectId", projectId);


            List<Project> projectList = new ArrayList<Project>();
            projectList = q.getResultList();

            if (projectList.size() == 1) {

                // user
                Project project = projectList.get(0);

                if (clients.get(0).getClientId() == null) {
                    ArrayList<Client> clientList = new ArrayList<Client>();
                    project.setClientCollection(clientList);
                } else {
                    project.setClientCollection(clients);
                }
                em.merge(project);

                // mettre à jour visibilité du projet parent. 
                if (project.getParentProjectId() != null) {
                    updateClientsVisibleForParentProject(project);
                }

            } else {
            }

        }
    }

    // met à jour la visibilité des clients du projet parent. 
    private void updateClientsVisibleForParentProject(Project p) {

        Query q = em.createNamedQuery("Project.findByProjectId");
        q.setParameter("projectId", p.getParentProjectId().getProjectId());

        List<Project> projectList = q.getResultList();
        Project parentProject = projectList.get(0);

        Boolean found = false;

        for (Client c : p.getClientCollection()) {

            found = false;

            for (Client cParent : parentProject.getClientCollection()) {

                if (cParent.getClientId() == c.getClientId()) {
                    found = true;
                    break;
                }
            }

            if (found == false) {

                parentProject.getClientCollection().add(c);

                em.merge(parentProject);
            }
        }

        // mettre à jour visibilité du projet parent. 
        if (parentProject.getParentProjectId() != null) {

            updateClientsVisibleForParentProject(parentProject);
        }
    }

    // retourne les projets assignés à un utilisateur. 
    @GET
    @RolesAllowed({"administrator", "developer"})
    @Path("assigned")
    @Produces("application/json")
    public String findAllAssignedProjects(@QueryParam("status") String statusId, @QueryParam("minDate") String minDate, @QueryParam("maxDate") String maxDate) {

        String retVal = "";

        ObjectMapper mapper = new ObjectMapper();

        List<Map> projectList = new ArrayList<Map>();

        // filter
        String filterQuery = "";
        if (statusId != null) {
            filterQuery += " AND (" + statusId + "= (SELECT status_id FROM Progress p WHERE p.project_id = project.project_id order by p.date_created DESC LIMIT 1))";
        }


        if (minDate != null) {
            filterQuery += " AND (project.start_date >= " + minDate + ")";
        }
        if (maxDate != null) {
            filterQuery += " AND (project.end_date <= " + maxDate + ")";
        }
        
        
        // get projets attribués à l'utilisateur. 
        //Query q = em.createNamedQuery("Project.getAssignedProjects");
        Query q = em.createNativeQuery("SELECT DISTINCT project.project_id as PROJECT_ID, project.project_title as PROJECT_TITLE, project.flag_public as FLAG_PUBLIC, project.project_description as PROJECT_DESCRIPTION, project.user_created as USER_CREATED, project.date_created as DATE_CREATED, project.start_date as START_DATE, project.end_date as END_DATE, project.start_date_real as START_DATE_REAL, project.end_date_real as END_DATE_REAL, project.parent_project_id as PARENT_PROJECT_ID, project.completed as COMPLETED, project.canceled as CANCELED, project.deleted as DELETED, project.user_assigned as USER_ASSIGNED FROM project WHERE project.user_assigned = ?1 and (project.deleted = false or project.deleted is null)" + filterQuery, Project.class);
        q.setParameter(1, getAuthenticatedUser().getUserId());

        List<Project> prjList = new ArrayList<Project>();
        prjList = q.getResultList();

        // get projects. Pas des sous-projets. 
        getProjects(prjList, projectList, null, null);


        HashMap<String, Object> retProjects = new HashMap<String, Object>();
        retProjects.put("project", projectList);

        try {
            retVal = mapper.writeValueAsString(retProjects);
        } catch (IOException ex) {
            Logger.getLogger(ProjectFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;
    }

    // retourne les projets assignés à un utilisateur. 
    // TEST METHOD. CAN BE REMOVED!!!
    @GET
    @RolesAllowed({"administrator", "developer"})
    @Path("testproj")
    @Produces("application/json")
    public String findAllProjectsTEST() {

        String retVal = "";

        ObjectMapper mapper = new ObjectMapper();

        List<Map> projectList = new ArrayList<Map>();

        // get projets attribués à l'utilisateur. 
        //Query q = em.createNamedQuery("Project.findAllProjectsUser");
        // WORKS !!!
        //Query q = em.createNativeQuery("SELECT DISTINCT project.project_id as PROJECT_ID, project.project_title as PROJECT_TITLE, project.flag_public as FLAG_PUBLIC FROM project_user_visible LEFT OUTER JOIN project ON project_user_visible.project_id = project.project_id", Project.class);


        Query q = em.createNativeQuery("SELECT DISTINCT project.project_id as PROJECT_ID, project.project_title as PROJECT_TITLE, project.flag_public as FLAG_PUBLIC, project.project_description as PROJECT_DESCRIPTION, project.user_created as USER_CREATED, project.date_created as DATE_CREATED, project.start_date as START_DATE, project.end_date as END_DATE, project.start_date_real as START_DATE_REAL, project.end_date_real as END_DATE_REAL, project.parent_project_id as PARENT_PROJECT_ID, project.completed as COMPLETED, project.canceled as CANCELED, project.deleted as DELETED, project.user_assigned as USER_ASSIGNED FROM project LEFT OUTER JOIN project_user_visible ON project.project_id = project_user_visible.project_id LEFT OUTER JOIN project_usergroup_visible ON project_usergroup_visible.project_id = project.project_id LEFT OUTER JOIN project_client ON project_client.project_id = project.project_id LEFT OUTER JOIN client ON project_client.client_id = client.client_id LEFT OUTER JOIN client_user ON client_user.client_id = client.client_id LEFT OUTER JOIN usergroup ON usergroup.usergroup_id = project_usergroup_visible.usergroup_id LEFT OUTER JOIN user_usergroup ON usergroup.usergroup_id = user_usergroup.usergroup_id WHERE (project.deleted = false or project.deleted IS NULL) AND ((project.flag_public = TRUE) OR (user_usergroup.user_id = ?1) OR (client_user.user_id = ?1) OR (project_user_visible.user_id = ?1) OR (project.user_created = ?1) OR (project.user_assigned = ?1)) AND project.parent_project_id IS NULL", Project.class);
        q.setParameter(1, getAuthenticatedUser().getUserId());
        //q.setParameter(2, getAuthenticatedUser().getUserId());
        //q.setParameter(3, getAuthenticatedUser().getUserId());

        //Query q = em.createNativeQuery("SELECT DISTINCT project as PROJECT FROM project_user_visible LEFT OUTER JOIN project ON project_user_visible.project_id = project.project_id", Project.class);
        //q.setParameter("userAssignedId", getAuthenticatedUser().getUserId());

        List<Project> prjList = new ArrayList<Project>();
        prjList = q.getResultList();

        String subqueryStr = "SELECT DISTINCT project.project_id as PROJECT_ID, project.project_title as PROJECT_TITLE, project.flag_public as FLAG_PUBLIC, project.project_description as PROJECT_DESCRIPTION, project.user_created as USER_CREATED, project.date_created as DATE_CREATED, project.start_date as START_DATE, project.end_date as END_DATE, project.start_date_real as START_DATE_REAL, project.end_date_real as END_DATE_REAL, project.parent_project_id as PARENT_PROJECT_ID, project.completed as COMPLETED, project.canceled as CANCELED, project.deleted as DELETED, project.user_assigned as USER_ASSIGNED FROM project LEFT OUTER JOIN project_user_visible ON project.project_id = project_user_visible.project_id LEFT OUTER JOIN project_usergroup_visible ON project_usergroup_visible.project_id = project.project_id LEFT OUTER JOIN project_client ON project_client.project_id = project.project_id LEFT OUTER JOIN client ON project_client.client_id = client.client_id LEFT OUTER JOIN client_user ON client_user.client_id = client.client_id LEFT OUTER JOIN usergroup ON usergroup.usergroup_id = project_usergroup_visible.usergroup_id LEFT OUTER JOIN user_usergroup ON usergroup.usergroup_id = user_usergroup.usergroup_id WHERE project.parent_project_id = ?1 AND (project.deleted = false or project.deleted IS NULL) AND ((project.flag_public = TRUE) OR (user_usergroup.user_id = ?2) OR (client_user.user_id = ?2) OR (project_user_visible.user_id = ?2) OR (project.user_created = ?1) OR (project.user_assigned = ?1))";
        Query subquery = em.createNativeQuery(subqueryStr, Project.class);
        subquery.setParameter(2, getAuthenticatedUser().getUserId());

        // get projects. Pas des sous-projets. 
        getProjects(prjList, projectList, null, subqueryStr);


        HashMap<String, Object> retProjects = new HashMap<String, Object>();
        retProjects.put("project", projectList);

        try {
            retVal = mapper.writeValueAsString(retProjects);
        } catch (IOException ex) {
            Logger.getLogger(ProjectFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;
    }

    // returns parent objects including its children
    // exactly the same than findAllProjects (only difference is that it adds the filter).
    @GET
    @Path("filter")
    @Produces("application/json")
    public String findAllProjectsFilter(@QueryParam("status") String statusId, @QueryParam("minDate") String minDate, @QueryParam("maxDate") String maxDate, @QueryParam("clientId") String clientId, @QueryParam("devId") String devId) {

        String retVal = "";

        ObjectMapper mapper = new ObjectMapper();

        List<Map> projectList = new ArrayList<Map>();
        Query q;

        String filterQuery = "";
        if (statusId != null) {
            filterQuery += " AND (" + statusId + "= (SELECT status_id FROM Progress p WHERE p.project_id = project.project_id order by p.date_created DESC LIMIT 1))";
        }


        if (minDate != null) {
            filterQuery += " AND (project.start_date >= " + minDate + ")";
        }
        if (maxDate != null) {
            filterQuery += " AND (project.end_date <= " + maxDate + ")";
        }
        
        if (clientId != null) {
            filterQuery += " AND (project_client.client_id = " + clientId + ")";
        }
        
        if (devId != null) {
            filterQuery += " AND (project.user_assigned = " + devId + ")";
        }

        // si utilisateur authentifié est un administrateur -> afficher tous les projets. 
        if (security.isUserInRole("administrator")) {
            // get projets parents de la base de données. 
            //q = em.createNamedQuery("Project.getParentProjects");
            //q = em.createNativeQuery("SELECT DISTINCT project.project_id as PROJECT_ID, project.project_title as PROJECT_TITLE, project.flag_public as FLAG_PUBLIC, project.project_description as PROJECT_DESCRIPTION, project.user_created as USER_CREATED, project.date_created as DATE_CREATED, project.start_date as START_DATE, project.end_date as END_DATE, project.start_date_real as START_DATE_REAL, project.end_date_real as END_DATE_REAL, project.parent_project_id as PARENT_PROJECT_ID, project.completed as COMPLETED, project.canceled as CANCELED, project.deleted as DELETED, project.user_assigned as USER_ASSIGNED FROM project WHERE project.parent_project_id IS NULL" + filterQuery, Project.class);
            q = em.createNativeQuery("SELECT DISTINCT project.project_id as PROJECT_ID, project.project_title as PROJECT_TITLE, project.flag_public as FLAG_PUBLIC, project.project_description as PROJECT_DESCRIPTION, project.user_created as USER_CREATED, project.date_created as DATE_CREATED, project.start_date as START_DATE, project.end_date as END_DATE, project.start_date_real as START_DATE_REAL, project.end_date_real as END_DATE_REAL, project.parent_project_id as PARENT_PROJECT_ID, project.completed as COMPLETED, project.canceled as CANCELED, project.deleted as DELETED, project.user_assigned as USER_ASSIGNED FROM project_client RIGHT OUTER JOIN project ON project_client.project_id = project.project_id WHERE project.parent_project_id IS NULL and (project.deleted = false or project.deleted IS NULL)" + filterQuery, Project.class);

            List<Project> prjList = new ArrayList<Project>();
            prjList = q.getResultList();

            // get projects and its children
            //getProjects(prjList, projectList, "Project.getChildProjects", "");
            getProjects(prjList, projectList, em.createNamedQuery("Project.getChildProjects"), null);
        } else {     // si pas administrateur, afficher que les projets qu'un utilisateur est autorisé de voir.   



            q = em.createNativeQuery("SELECT DISTINCT project.project_id as PROJECT_ID, project.project_title as PROJECT_TITLE, project.flag_public as FLAG_PUBLIC, project.project_description as PROJECT_DESCRIPTION, project.user_created as USER_CREATED, project.date_created as DATE_CREATED, project.start_date as START_DATE, project.end_date as END_DATE, project.start_date_real as START_DATE_REAL, project.end_date_real as END_DATE_REAL, project.parent_project_id as PARENT_PROJECT_ID, project.completed as COMPLETED, project.canceled as CANCELED, project.deleted as DELETED, project.user_assigned as USER_ASSIGNED FROM project LEFT OUTER JOIN project_user_visible ON project.project_id = project_user_visible.project_id LEFT OUTER JOIN project_usergroup_visible ON project_usergroup_visible.project_id = project.project_id LEFT OUTER JOIN project_client ON project_client.project_id = project.project_id LEFT OUTER JOIN client ON project_client.client_id = client.client_id LEFT OUTER JOIN client_user ON client_user.client_id = client.client_id LEFT OUTER JOIN usergroup ON usergroup.usergroup_id = project_usergroup_visible.usergroup_id LEFT OUTER JOIN user_usergroup ON usergroup.usergroup_id = user_usergroup.usergroup_id WHERE (project.deleted = false or project.deleted IS NULL) AND ((project.flag_public = TRUE) OR (user_usergroup.user_id = ?1) OR (client_user.user_id = ?1) OR (project_user_visible.user_id = ?1) OR (project.user_created = ?1) OR (project.user_assigned = ?1)) AND project.parent_project_id IS NULL" + filterQuery, Project.class);
            //Query q = em.createNativeQuery("SELECT DISTINCT project.project_id as PROJECT_ID, project.project_title as PROJECT_TITLE, project.flag_public as FLAG_PUBLIC, project.project_description as PROJECT_DESCRIPTION, project.user_created as USER_CREATED, project.date_created as DATE_CREATED, project.start_date as START_DATE, project.end_date as END_DATE, project.start_date_real as START_DATE_REAL, project.end_date_real as END_DATE_REAL, project.parent_project_id as PARENT_PROJECT_ID, project.completed as COMPLETED, project.canceled as CANCELED, project.deleted as DELETED, project.user_assigned as USER_ASSIGNED FROM project LEFT OUTER JOIN project_user_visible ON project.project_id = project_user_visible.project_id LEFT OUTER JOIN project_usergroup_visible ON project_usergroup_visible.project_id = project.project_id LEFT OUTER JOIN project_client ON project_client.project_id = project.project_id LEFT OUTER JOIN client ON project_client.client_id = client.client_id LEFT OUTER JOIN client_user ON client_user.client_id = client.client_id LEFT OUTER JOIN usergroup ON usergroup.usergroup_id = project_usergroup_visible.usergroup_id LEFT OUTER JOIN user_usergroup ON usergroup.usergroup_id = user_usergroup.usergroup_id WHERE (project.deleted = false or project.deleted IS NULL) AND ((project.flag_public = TRUE) OR (((user_usergroup.user_id = ?1) OR (client_user.user_id = ?1) OR (project_user_visible.user_id = ?1) OR (project.user_created = ?1) OR (project.user_assigned = ?1)) " + filterQuery + ")) AND project.parent_project_id IS NULL" + filterQuery, Project.class);
            q.setParameter(1, getAuthenticatedUser().getUserId());



            List<Project> prjList = new ArrayList<Project>();
            prjList = q.getResultList();

            String subqueryStr = "SELECT DISTINCT project.project_id as PROJECT_ID, project.project_title as PROJECT_TITLE, project.flag_public as FLAG_PUBLIC, project.project_description as PROJECT_DESCRIPTION, project.user_created as USER_CREATED, project.date_created as DATE_CREATED, project.start_date as START_DATE, project.end_date as END_DATE, project.start_date_real as START_DATE_REAL, project.end_date_real as END_DATE_REAL, project.parent_project_id as PARENT_PROJECT_ID, project.completed as COMPLETED, project.canceled as CANCELED, project.deleted as DELETED, project.user_assigned as USER_ASSIGNED FROM project LEFT OUTER JOIN project_user_visible ON project.project_id = project_user_visible.project_id LEFT OUTER JOIN project_usergroup_visible ON project_usergroup_visible.project_id = project.project_id LEFT OUTER JOIN project_client ON project_client.project_id = project.project_id LEFT OUTER JOIN client ON project_client.client_id = client.client_id LEFT OUTER JOIN client_user ON client_user.client_id = client.client_id LEFT OUTER JOIN usergroup ON usergroup.usergroup_id = project_usergroup_visible.usergroup_id LEFT OUTER JOIN user_usergroup ON usergroup.usergroup_id = user_usergroup.usergroup_id WHERE project.parent_project_id = ?1 AND (project.deleted = false or project.deleted IS NULL) AND ((project.flag_public = TRUE) OR (user_usergroup.user_id = ?2) OR (client_user.user_id = ?2) OR (project_user_visible.user_id = ?2) OR (project.user_created = ?2) OR (project.user_assigned = ?2))";
            Query subquery = em.createNativeQuery("SELECT DISTINCT project.project_id as PROJECT_ID, project.project_title as PROJECT_TITLE, project.flag_public as FLAG_PUBLIC, project.project_description as PROJECT_DESCRIPTION, project.user_created as USER_CREATED, project.date_created as DATE_CREATED, project.start_date as START_DATE, project.end_date as END_DATE, project.start_date_real as START_DATE_REAL, project.end_date_real as END_DATE_REAL, project.parent_project_id as PARENT_PROJECT_ID, project.completed as COMPLETED, project.canceled as CANCELED, project.deleted as DELETED, project.user_assigned as USER_ASSIGNED FROM project LEFT OUTER JOIN project_user_visible ON project.project_id = project_user_visible.project_id LEFT OUTER JOIN project_usergroup_visible ON project_usergroup_visible.project_id = project.project_id LEFT OUTER JOIN project_client ON project_client.project_id = project.project_id LEFT OUTER JOIN client ON project_client.client_id = client.client_id LEFT OUTER JOIN client_user ON client_user.client_id = client.client_id LEFT OUTER JOIN usergroup ON usergroup.usergroup_id = project_usergroup_visible.usergroup_id LEFT OUTER JOIN user_usergroup ON usergroup.usergroup_id = user_usergroup.usergroup_id WHERE project.parent_project_id = ?1 AND (project.deleted = false or project.deleted IS NULL) AND ((project.flag_public = TRUE) OR (user_usergroup.user_id = ?2) OR (client_user.user_id = ?2) OR (project_user_visible.user_id = ?2) OR (project.user_created = ?2) OR (project.user_assigned = ?2))", Project.class);
            subquery.setParameter(2, getAuthenticatedUser().getUserId());

            // get projects. Pas des sous-projets. 
            getProjects(prjList, projectList, null, subqueryStr);
        }

        HashMap<String, Object> retProjects = new HashMap<String, Object>();
        retProjects.put("project", projectList);

        try {
            retVal = mapper.writeValueAsString(retProjects);
        } catch (IOException ex) {
            Logger.getLogger(ProjectFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;
        //return q.toString();

    }
}
