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
public class ProjectDummy {
    
    //private Project project;
    
    //private List<Project> listProject;
    private List<ProjectSimpleWebSite> listProject;
    
    /**
     * @return the listProject
     */
    //public List<Project> getListProject() {
    public List<ProjectSimpleWebSite> getListProject() {
        return listProject;
    }

    /**
     * @param listProject the listProject to set
     */
    //public void setListProject(List<Project> listProject) {
    public void setListProject(List<ProjectSimpleWebSite> listProject) {
        this.listProject = listProject;
    }
}
