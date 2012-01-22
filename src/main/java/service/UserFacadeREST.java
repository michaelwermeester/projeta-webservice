/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import be.luckycode.projetawebservice.Role;
import be.luckycode.projetawebservice.User;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
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
@Path("users")
public class UserFacadeREST extends AbstractFacade<User> {
    @PersistenceContext(unitName = "be.luckycode_projeta-webservice_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Context
    SecurityContext security;
    
    public UserFacadeREST() {
        super(User.class);
    }

    /*@POST
    @Override
    @Consumes("application/json")
    public void create(Users entity) {
        super.create(entity);
    }*/
    
    @POST
    //@Override
    @Path("create")
    @RolesAllowed("administrator")
    @Consumes("application/json")
    @Produces("application/json")
    public String createNewUser(User entity) {
        
        em.persist(entity);
        
        em.flush();
        // return newly created user.        
        //return this.findByUsername(entity.getUsername(), null);
        return generateUserJSONString(entity);
        
    }

    // creates JSON ready to be returned from web-service from a given user.
    private String generateUserJSONString(User entity) {
        
        ObjectMapper mapper = new ObjectMapper();
        
        List<Map> userMap = new ArrayList<Map>();
        
        // generate JSON representation for USER object.
        Map<String, Object> userData = generateUserJSON(entity);
        
        userMap.add(userData);
        
        String retVal = "";
        
        HashMap<String, Object> retUsers = new HashMap<String, Object>();
        
        retUsers.put("users", userMap);
        
        try {
            retVal = mapper.writeValueAsString(retUsers);
        } catch (IOException ex) {
            Logger.getLogger(UserFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return retVal;
    }

    /*@PUT
    @Override
    @Consumes("application/json")
    public void edit(Users entity) {
        super.edit(entity);
    }*/
    
    @PUT
    @Override
    @Path("update")
    @Consumes("application/json")
    public void edit(User entity) {

        if (entity.getUserId() != null) {

            // user may only update its own user details.
            // administrators may update all.
            if ((this.getAuthenticatedUser().getUserId() == entity.getUserId())
                    || security.isUserInRole("administrator")) {

                // fetch user to be updated.
                User user = super.find(entity.getUserId());

                // if a new first name has been provided...
                if (entity.getFirstName() != null) // set new first name.
                {
                    user.setFirstName(entity.getFirstName());
                }

                // if a new last name has been provided...
                if (entity.getLastName() != null) // set new last name.
                {
                    user.setLastName(entity.getLastName());
                }

                // same for email address.
                if (entity.getEmailAddress() != null) {
                    user.setEmailAddress(entity.getEmailAddress());
                }

                // username.
                if (entity.getUsername() != null) {
                    user.setUsername(entity.getUsername());
                }

                // update
                super.edit(user);
            }
        }

        //super.edit(entity);
    }
    
    @PUT
    @Path("setPassword")
    @RolesAllowed("administrator")
    @Consumes("application/json")
    @Produces("text/plain")
    public String setPassword(User entity) throws NoSuchAlgorithmException {

        // fetch user to be updated.
        User user = super.find(entity.getUserId());

        // if a new first name has been provided...
        if (entity.getPassword() != null) // set new first name.
        {
            user.setPassword(entity.getPassword());
        }

        // update user.
        super.edit(user);
        
        return "ok";
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public User find(@PathParam("id") Integer id) {
        return super.find(id);
    }
    
    // checks if a username exists.
    // returns 1 if username exists and 0 if it doesn't.
    @GET
    @RolesAllowed("administrator")
    @Path("exists/{username}")
    @Produces("application/json")
    public String userExists(@PathParam("username") String username) {
        
        // minimum length of 2 characters.
        //if (username.length() < 2)
        //    return "1";
        
        Query q = em.createNamedQuery("User.findByUsername");
        q.setParameter("username", username);
        
        List<User> userList = new ArrayList<User>();
        userList = q.getResultList();
               
        // if username exists, return 1
        if (userList.size() == 1 ) {
            return "1";
        } else {
            return "0";
        }
    }
    
    
    // return user by specifying username
    @GET
    @Produces("application/json")
    public String findByUsername(@QueryParam("username") String username, @QueryParam("userId") Integer userId) {
        
        
        if (username == null && userId == null) {
            // get username of logged in user
            String loggedInUsername = security.getUserPrincipal().getName();
        
            // get and return logged in User from username
            //return this.findByUsername(loggedInUsername, null);
            username = loggedInUsername;
        }
        
        Query q;
        if (userId == null) {
            q = em.createNamedQuery("User.findByUsername");
            q.setParameter("username", username);
        } else {
            q = em.createNamedQuery("User.findByUserId");
            q.setParameter("userId", userId);
        }
        
        List<User> userList = new ArrayList<User>();
        userList = q.getResultList();
               
        if (userList.size() == 1) {
            
            User u = userList.get(0);
            
            ObjectMapper mapper = new ObjectMapper();
        
            List<Map> userMap = new ArrayList<Map>();
            
            Map<String, Object> userData = generateUserJSON(u);
            
            userMap.add(userData);
            
            String retVal = "";
            
            HashMap<String, Object> retUsers = new HashMap<String, Object>();
            retUsers.put("users", userMap);
        
            try {
                retVal = mapper.writeValueAsString(retUsers);
            } catch (IOException ex) {
                Logger.getLogger(UserFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
            }
        
            return retVal;
        } else {
            return "";
        }
                    
    }

    /*// return user roles by specifying username
    @GET
    @Path("username/{username}/roles")
    @Produces("application/json")
    public String roleByUsername(@PathParam("username") String username) {
        
        Query q = em.createNamedQuery("Users.findByUsername");
        q.setParameter("username", username);
        
        List<Users> userList = new ArrayList<Users>();
        userList = q.getResultList();
               
        if (userList.size() == 1) {

            // user
            Users u = userList.get(0);
            
            // get roles for user
            Collection<Role> roleList = u.getRoleCollection();
            
            ObjectMapper mapper = new ObjectMapper();
        
            List<Map> roleMap = new ArrayList<Map>();
            
            
            for (Role r : roleList) {
                
                Map<String, Object> roleData = new HashMap<String, Object>();
                
                roleData.put("roleId", r.getRoleId().toString());
                roleData.put("code", r.getCode());
                        
                roleMap.add(roleData);
            }
            
            
            String retVal = "";
            
            HashMap<String, Object> retUserRoles = new HashMap<String, Object>();
            retUserRoles.put("role", roleMap);
        
            try {
                retVal = mapper.writeValueAsString(retUserRoles);
            } catch (IOException ex) {
                Logger.getLogger(UsersFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
            }
        
            return retVal;
        } else {
            return "";
        }
                    
    }*/
    
    // returns the logged in user
    /*@GET
    @Produces("application/json")
    @Path("getLoggedInUser")
    public String getLoggedInUser() {
        
        // get username of logged in user
        String loggedInUsername = security.getUserPrincipal().getName();
        
        // get and return logged in User from username
        return this.findByUsername(loggedInUsername);
    }*/
    
    /*@GET
    @Override
    @Produces("application/json")
    public List<Users> findAll() {
        return super.findAll();
    }*/
    
    @GET
    @Produces("application/json")
    @Path("all")
    public String findAll2() {
        List<User> users = super.findAll();

        String retVal = "";
        
        ObjectMapper mapper = new ObjectMapper();
        
        List<Map> userList = new ArrayList<Map>();
                
        for (User u : users) {
            
            Map<String, Object> userData = generateUserJSON(u);
            
            userList.add(userData);
        }
        
        HashMap<String, Object> retUsers = new HashMap<String, Object>();
        retUsers.put("users", userList);
        
        try {
            retVal = mapper.writeValueAsString(retUsers);
        } catch (IOException ex) {
            Logger.getLogger(UserFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return retVal;
    }

    private Map<String, Object> generateUserJSON(User u) {
        
        Map<String, Object> userData = new HashMap<String, Object>();
        
        userData.put("userId", u.getUserId().toString());
        userData.put("username", u.getUsername());
        
        if (u.getFirstName() != null)
            userData.put("firstName", u.getFirstName());
        if (u.getLastName() != null)
            userData.put("lastName", u.getLastName());
        if (u.getEmailAddress() != null)
            userData.put("emailAddress", u.getEmailAddress());
        
        return userData;
    }

    @GET
    @Path("{from}/{to}")
    @Produces("application/json")
    public List<User> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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
    
    // 
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
    
}
