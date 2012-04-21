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
public class TaskDummy {
    
    private List<ProjectSimpleWebSite> listTask;
    
    /**
     * @return the listTask
     */
    public List<ProjectSimpleWebSite> getListTask() {
        return listTask;
    }

    /**
     * @param listTask the listTask to set
     */
    public void setListTask(List<ProjectSimpleWebSite> listTask) {
        this.listTask = listTask;
    }
}
