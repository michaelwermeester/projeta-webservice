/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.luckycode.projetawebservice;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author michael
 */
@XmlRootElement
public class BugSimpleWebSite {
    
    
    private String bugTitle;
    private Date startDate;
    private Date endDate;
    private String bugType;
    private Integer bugId;
    private String projectStatus;

    /**
     * @return the bugTitle
     */
    public String getBugTitle() {
        return bugTitle;
    }

    /**
     * @param bugTitle the bugTitle to set
     */
    public void setBugTitle(String bugTitle) {
        this.bugTitle = bugTitle;
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
     * @return the bugType
     */
    public String getBugType() {
        return bugType;
    }

    /**
     * @param bugType the bugType to set
     */
    public void setBugType(String bugType) {
        this.bugType = bugType;
    }

    /**
     * @return the bugId
     */
    public Integer getBugId() {
        return bugId;
    }

    /**
     * @param bugId the bugId to set
     */
    public void setBugId(Integer bugId) {
        this.bugId = bugId;
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
}
