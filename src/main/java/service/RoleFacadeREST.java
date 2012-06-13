/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import be.luckycode.projetawebservice.Role;
import be.luckycode.projetawebservice.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import javax.persistence.FlushModeType;
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
@Path("roles")
public class RoleFacadeREST extends AbstractFacade<Role> {
    @PersistenceContext(unitName = "be.luckycode_projeta-webservice_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Context
    SecurityContext security;
    
    public RoleFacadeREST() {
        super(Role.class);
    }

    @PUT
    @RolesAllowed("administrator")
    @Consumes("application/json")
    public void updateRolesForUser(@QueryParam("userId") Integer userId, ArrayList<Role> roles) {
        
        Query q;
        
        if (userId != null && security.isUserInRole("administrator")) {
            q = em.createNamedQuery("User.findByUserId");
            q.setParameter("userId", userId);
        
        
            List<User> userList = new ArrayList<User>();
            userList = q.getResultList();
            
            if (userList.size() == 1) {

                // user
                User u = userList.get(0);
                
                        Collection<Role> userRoles = u.getRoleCollection();
                        
                        
                        for (Role r : userRoles) {
                            if (r.getRoleId() != null) {
                                
                                
                                
                                q = em.createNamedQuery("Role.findByRoleId");
                                q.setParameter("roleId", r.getRoleId());
            
                                List<Role> tmpRoleList = new ArrayList<Role>();
                                tmpRoleList = q.getResultList();
                                
                                Role role = tmpRoleList.get(0);
                                
                                role.getUserCollection().remove(u);
                                
                                em.merge(role);
                            }
                            
                        }
                        
                        
                        for (Role r : roles) {
                            
                            if (r.getRoleId() != null) {
                                
                                q = em.createNamedQuery("Role.findByRoleId");
                                q.setParameter("roleId", r.getRoleId());
            
                                List<Role> tmpRoleList = new ArrayList<Role>();
                                tmpRoleList = q.getResultList();
                                
                                if (tmpRoleList.size() > 0 && tmpRoleList.get(0).getRoleId() != null) {
                                
                                    Role role = tmpRoleList.get(0);
                                    role.getUserCollection().add(u);
                                
                                    em.merge(role);
                                }
                            }
                        }
                    }
        }
    }
    
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Role find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    
    // for admins only: get all available user roles.
    @GET
    @Override
    @Path("available")
    @RolesAllowed("administrator")
    @Produces("application/json")
    public List<Role> findAll() {
        return super.findAll();
    }
    
    // return user roles
    // if no username is specified: return roles for current user.
    // if username is specified: return the roles for the given user.
    @GET
    @Produces("application/json")
    public String roleByUsername(@QueryParam("username") String username, @QueryParam("userId") Integer userId) {

        if (username == null && userId == null) {
            // get username of logged in user
            username = security.getUserPrincipal().getName();
        } 
        // if current user is not in administrator role.
        else if (!security.isUserInRole("administrator")) {
            return "";
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
            return getRoleByUser(u);
        } else {
            return "";
        }
                    
    }

    // get roles by user.
    // generates a hashmap and returns it as a String.
    private String getRoleByUser(User u) {
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
            Logger.getLogger(UserFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retVal;
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Role> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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
