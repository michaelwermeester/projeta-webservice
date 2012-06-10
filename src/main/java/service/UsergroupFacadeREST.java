/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import be.luckycode.projetawebservice.Project;
import be.luckycode.projetawebservice.User;
import be.luckycode.projetawebservice.Usergroup;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author michael
 */
@Stateless
@Path("usergroups")
public class UsergroupFacadeREST extends AbstractFacade<Usergroup> {

    @PersistenceContext(unitName = "be.luckycode_projeta-webservice_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    @Context
    SecurityContext security;

    public UsergroupFacadeREST() {
        super(Usergroup.class);
    }

    @POST
    @Override
    @RolesAllowed({"administrator", "developer"})
    @Consumes("application/json")
    public void create(Usergroup entity) {
        super.create(entity);
    }

    /*@PUT
    @Override
    @RolesAllowed({"administrator", "developer"})
    @Consumes("application/json")
    public void edit(Usergroup entity) {
        super.edit(entity);
    }*/

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public Usergroup find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    // Returns all user groups.
    @GET
    @Override
    @Path("all")
    @Produces("application/json")
    public List<Usergroup> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces("application/json")
    public List<Usergroup> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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

    // return a user's usergroups
    // if no username is specified: return usergroups for current user.
    // if username is specified: return the usergroups for the given user.
    @GET
    @Produces("application/json")
    public String usergroupByUsername(@QueryParam("username") String username, @QueryParam("userId") Integer userId) {

        //UserFacadeREST userFacade = new UserFacadeREST();

        User u = getUser(username, userId);
        
        return getUsergroupByUser(u);
    }
    
    public User getUser(String username, Integer userId) {
        if (username == null && userId == null) {
            // get username of logged in user
            username = security.getUserPrincipal().getName();
        } 
        // if current user is not in administrator role.
        else if (!security.isUserInRole("administrator")) {
            return null;
        }
        
        Query q;
        
        if (userId != null && security.isUserInRole("administrator")) {
            q = em.createNamedQuery("User.findByUserId");
            q.setParameter("userId", userId);
        }
        else {
            q = em.createNamedQuery("User.findByUsername");
            q.setParameter("username", username);
        }
        
        List<User> userList = new ArrayList<User>();
        userList = q.getResultList();
               
        if (userList.size() == 1) {

            // user
            User u = userList.get(0);
            
            // get roles by user.
            return u;
        } else {
            return null;
        }
    }

    // get usergroups by user.
    // generates a hashmap and returns it as a String.
    private String getUsergroupByUser(User u) {
        // get roles for user
        Collection<Usergroup> usergroupList = u.getUsergroupCollection();
        ObjectMapper mapper = new ObjectMapper();
        List<Map> usergroupMap = new ArrayList<Map>();
        for (Usergroup ug : usergroupList) {

            Map<String, Object> usergroupData = new HashMap<String, Object>();

            usergroupData.put("usergroupId", ug.getUsergroupId().toString());
            usergroupData.put("code", ug.getCode());
            usergroupData.put("comment", ug.getComment());

            usergroupMap.add(usergroupData);
        }
        String retVal = "";
        HashMap<String, Object> retUserUsergroups = new HashMap<String, Object>();
        retUserUsergroups.put("usergroup", usergroupMap);
        try {
            retVal = mapper.writeValueAsString(retUserUsergroups);
        } catch (IOException ex) {
            Logger.getLogger(UserFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retVal;
    }

    @PUT
    @RolesAllowed("administrator")
    @Path("updateGroupsForUser")
    @Consumes("application/json")
    public void updateUsergroupsForUser(@QueryParam("userId") Integer userId, ArrayList<Usergroup> usergroups) {
        //public String updateRolesForUser(User user) {
        //super.edit(entity);

        Query q;

        if (userId != null && security.isUserInRole("administrator")) {
            q = em.createNamedQuery("User.findByUserId");
            q.setParameter("userId", userId);


            List<User> userList = new ArrayList<User>();
            userList = q.getResultList();

            if (userList.size() == 1) {

                // user
                User u = userList.get(0);


                Collection<Usergroup> userUsergroup = u.getUsergroupCollection();


                for (Usergroup ug : userUsergroup) {

                    if (ug.getUsergroupId() != null) {



                        q = em.createNamedQuery("Usergroup.findByUsergroupId");
                        q.setParameter("usergroupId", ug.getUsergroupId());

                        List<Usergroup> tmpUsergroupList = new ArrayList<Usergroup>();
                        tmpUsergroupList = q.getResultList();

                        Usergroup usergroup = tmpUsergroupList.get(0);

                        usergroup.getUserCollection().remove(u);

                        em.merge(usergroup);
                    }

                }


                for (Usergroup ug : usergroups) {

                    if (ug.getUsergroupId() != null) {

                        //if (roles.contains(r) == false) {
                        //em.persist(r.getUsersCollection().add(u));

                        q = em.createNamedQuery("Usergroup.findByUsergroupId");
                        q.setParameter("usergroupId", ug.getUsergroupId());

                        List<Usergroup> tmpUsergroupList = new ArrayList<Usergroup>();
                        tmpUsergroupList = q.getResultList();

                        if (tmpUsergroupList.size() > 0 && tmpUsergroupList.get(0).getUsergroupId() != null) {

                            Usergroup usergroup = tmpUsergroupList.get(0);
                            usergroup.getUserCollection().add(u);

                            em.merge(usergroup);
                        }
                        //}
                    }
                }

            } else {
            }

        }
    }
    
    @PUT
    @RolesAllowed("administrator")
    @Path("updateUsersForGroup")
    @Consumes("application/json")
    public void updateUsersForGroup(@QueryParam("usergroupId") Integer usergroupId, ArrayList<User> users) {
        //public String updateRolesForUser(User user) {
        //super.edit(entity);

        Query q;

        if (usergroupId != null && security.isUserInRole("administrator")) {
            q = em.createNamedQuery("Usergroup.findByUsergroupId");
            q.setParameter("usergroupId", usergroupId);


            List<Usergroup> usergroupList = new ArrayList<Usergroup>();
            usergroupList = q.getResultList();

            if (usergroupList.size() == 1) {

                Usergroup ug = usergroupList.get(0);
                
                if (users.get(0).getUserId() == null) {
                    ArrayList<User> list = new ArrayList<User>();
                    ug.setUserCollection(list);
                } else {
                    ug.setUserCollection(users);
                }
                // user
                
                //ug.setUserCollection(users);
                em.merge(ug);
            } else {
            }

        }
    }
    
    
    // return list of usergroups assigned to a project.
    @GET
    @Path("project/{projectid}")
    @Produces("application/json")
    public List<Usergroup> findByProject(@PathParam("projectid") Integer projectid) {

        String retVal = "";

        ObjectMapper mapper = new ObjectMapper();

        Query q;
        q = em.createNamedQuery("Project.findByProjectId");
        q.setParameter("projectId", projectid);

        List<Project> projectList = new ArrayList<Project>();
        projectList = q.getResultList();

        List<Usergroup> usergroupList = new ArrayList<Usergroup>();
        
        if (projectList.size() == 1) {

            Collection<Usergroup> usergroups = projectList.get(0).getUsergroupCollection();

            
            
            
            for (Usergroup ug : usergroups) {
                usergroupList.add(ug);
            }
            
        } else {
            //return "";
        }
        
        return usergroupList;
    }
}
