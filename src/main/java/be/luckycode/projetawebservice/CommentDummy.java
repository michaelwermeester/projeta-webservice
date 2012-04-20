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
public class CommentDummy {
    
    private List<Comment> listComment;
    
    /**
     * @return the listComment
     */
    public List<Comment> getListComment() {
        return listComment;
    }

    /**
     * @param listComment the listProject to set
     */
    public void setListComment(List<Comment> listComment) {
        this.listComment = listComment;
    }
    
}
