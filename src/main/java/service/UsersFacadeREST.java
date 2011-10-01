/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import be.luckycode.projetawebservice.Role;
import be.luckycode.projetawebservice.Users;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author michael
 */
@Stateless
@Path("be.luckycode.projetawebservice.users")
public class UsersFacadeREST extends AbstractFacade<Users> {
    @PersistenceContext(unitName = "be.luckycode_projeta-webservice_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Context
    SecurityContext security;
    
    public UsersFacadeREST() {
        super(Users.class);
    }

    @POST
    @Override
    @Consumes("application/json")
    public void create(Users entity) {
        super.create(entity);
    }

    /*@PUT
    @Override
    @Consumes("application/json")
    public void edit(Users entity) {
        super.edit(entity);
    }*/
    
    @PUT
    @Override
    @Consumes("application/json")
    public void edit(Users entity) {
        
        if (entity.getUserId() != null) {
            // fetch user to be updated.
            Users user = super.find(entity.getUserId());
            
            // if a new first name has been provided...
            if (entity.getFirstName() != null)
                // set new first name.
                user.setFirstName(entity.getFirstName());
                        
            // if a new last name has been provided...
            if (entity.getLastName() != null)
                // set new last name.
                user.setLastName(entity.getLastName());
            
            // same for email address.
            if (entity.getEmailAddress() != null)
                user.setEmailAddress(entity.getEmailAddress());
            
            // username.
            if (entity.getUsername() != null)
                user.setUsername(entity.getUsername());
            
            // update
            super.edit(user);
        }
        
        //super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public Users find(@PathParam("id") Integer id) {
        return super.find(id);
    }
    
    // return user by specifying username
    @GET
    @Path("username/{username}")
    @Produces("application/json")
    public String findByUsername(@PathParam("username") String username) {
        
        Query q = em.createNamedQuery("Users.findByUsername");
        q.setParameter("username", username);
        
        List<Users> userList = new ArrayList<Users>();
        userList = q.getResultList();
               
        if (userList.size() == 1) {
            
            Users u = userList.get(0);
            
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
                Logger.getLogger(UsersFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
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
    @GET
    @Produces("application/json")
    @Path("getLoggedInUser")
    public String getLoggedInUser() {
        
        // get username of logged in user
        String loggedInUsername = security.getUserPrincipal().getName();
        
        // get and return logged in User from username
        return this.findByUsername(loggedInUsername);
    }
    
    /*@GET
    @Override
    @Produces("application/json")
    public List<Users> findAll() {
        return super.findAll();
    }*/
    
    @GET
    @Produces("application/json")
    public String findAll2() {
        List<Users> users = super.findAll();

        String retVal = "";
        
        ObjectMapper mapper = new ObjectMapper();
        
        List<Map> userList = new ArrayList<Map>();
                
        for (Users u : users) {
            
            Map<String, Object> userData = generateUserJSON(u);
            
            userList.add(userData);
        }
        
        HashMap<String, Object> retUsers = new HashMap<String, Object>();
        retUsers.put("users", userList);
        
        try {
            retVal = mapper.writeValueAsString(retUsers);
        } catch (IOException ex) {
            Logger.getLogger(UsersFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return retVal;
    }

    private Map<String, Object> generateUserJSON(Users u) {
        
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
    public List<Users> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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
