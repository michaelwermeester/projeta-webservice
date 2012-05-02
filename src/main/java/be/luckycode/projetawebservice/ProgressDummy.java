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
public class ProgressDummy {
    
    private List<Progress> listProgress;

    /**
     * @return the listProgress
     */
    public List<Progress> getListProgress() {
        return listProgress;
    }

    /**
     * @param listProgress the listProgress to set
     */
    public void setListProgress(List<Progress> listProgress) {
        this.listProgress = listProgress;
    }
}
