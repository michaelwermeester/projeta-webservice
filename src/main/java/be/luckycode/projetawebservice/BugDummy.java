/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.luckycode.projetawebservice;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author michael
 */
@XmlRootElement
public class BugDummy {
    
    private List<BugSimpleWebSite> listBug;
    
    /**
     * @return the listProject
     */
    //public List<Project> getListProject() {
    public List<BugSimpleWebSite> getListBug() {
        return listBug;
    }

    /**
     * @param listProject the listProject to set
     */
    //public void setListProject(List<Project> listProject) {
    public void setListBug(List<BugSimpleWebSite> listBug) {
        this.listBug = listBug;
    }
    
}
