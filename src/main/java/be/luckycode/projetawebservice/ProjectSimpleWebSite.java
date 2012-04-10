/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.luckycode.projetawebservice;

import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author michael
 */

@XmlRootElement
public class ProjectSimpleWebSite {
    
    private String projectTitle;
    private Date startDate;
    private Date endDate;
    private String projectStatus;
    private Integer projectId;
    private List<ProjectSimpleWebSite> childProject;

    /**
     * @return the projectTitle
     */
    public String getProjectTitle() {
        return projectTitle;
    }

    /**
     * @param projectTitle the projectTitle to set
     */
    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the projectStatus
     */
    public String getProjectStatus() {
        return projectStatus;
    }

    /**
     * @param projectStatus the projectStatus to set
     */
    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    /**
     * @return the projectId
     */
    public Integer getProjectId() {
        return projectId;
    }

    /**
     * @param projectId the projectId to set
     */
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    /**
     * @return the childProject
     */
    public List<ProjectSimpleWebSite> getChildProject() {
        return childProject;
    }

    /**
     * @param childProject the childProject to set
     */
    public void setChildProject(List<ProjectSimpleWebSite> childProject) {
        this.childProject = childProject;
    }
    
    
    
}
