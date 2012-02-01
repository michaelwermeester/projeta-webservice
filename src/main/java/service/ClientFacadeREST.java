/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import be.luckycode.projetawebservice.Client;
import java.io.IOException;
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
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author michael
 */
@Stateless
@Path("clients")
public class ClientFacadeREST extends AbstractFacade<Client> {
    @PersistenceContext(unitName = "be.luckycode_projeta-webservice_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    public ClientFacadeREST() {
        super(Client.class);
    }

    @POST
    @Override
    @Consumes({"application/xml", "application/json"})
    public void create(Client entity) {
        super.create(entity);
    }

    @PUT
    @Override
    @Consumes({"application/xml", "application/json"})
    public void edit(Client entity) {
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
    public Client find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Path("all")
    @Produces("application/json")
    public List<Client> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Client> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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
    
    
    @GET
    @Path("names")
    @Produces("application/json")
    public String getClientNames() {
        List<Client> clients = super.findAll();

        String retVal = "";
        
        ObjectMapper mapper = new ObjectMapper();
        
        List<Map> clientList = new ArrayList<Map>();
                
        for (Client cli : clients) {
            
            Map<String, Object> clientData = generateClientJSON(cli);
            
            clientList.add(clientData);
        }
        
        HashMap<String, Object> retClients = new HashMap<String, Object>();
        retClients.put("clients", clientList);
        
        try {
            retVal = mapper.writeValueAsString(retClients);
        } catch (IOException ex) {
            Logger.getLogger(UserFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return retVal;
    }
    
    private Map<String, Object> generateClientJSON(Client client) {
        
        Map<String, Object> clientData = new HashMap<String, Object>();
        
        clientData.put("clientId", client.getClientId().toString());
        clientData.put("clientName", client.getClientName());
        
        /*if (client.getFirstName() != null)
            clientData.put("firstName", client.getFirstName());
        if (client.getLastName() != null)
            clientData.put("lastName", client.getLastName());
        if (client.getEmailAddress() != null)
            clientData.put("emailAddress", client.getEmailAddress());*/
        
        return clientData;
    }
}
