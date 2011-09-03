/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.luckycode.projetawebservice;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author michael
 */
@Entity
@Table(name = "dictionary")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Dictionary.findAll", query = "SELECT d FROM Dictionary d"),
    @NamedQuery(name = "Dictionary.findByCode", query = "SELECT d FROM Dictionary d WHERE d.code = :code"),
    @NamedQuery(name = "Dictionary.findByTranslation", query = "SELECT d FROM Dictionary d WHERE d.translation = :translation"),
    @NamedQuery(name = "Dictionary.findByDictionaryId", query = "SELECT d FROM Dictionary d WHERE d.dictionaryId = :dictionaryId")})
public class Dictionary implements Serializable {
    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2055)
    @Column(name = "code")
    private String code;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2055)
    @Column(name = "translation")
    private String translation;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "dictionary_id")
    private Integer dictionaryId;
    @JoinColumn(name = "language_code", referencedColumnName = "language_code")
    @ManyToOne(optional = false)
    private Language languageCode;

    public Dictionary() {
    }

    public Dictionary(Integer dictionaryId) {
        this.dictionaryId = dictionaryId;
    }

    public Dictionary(Integer dictionaryId, String code, String translation) {
        this.dictionaryId = dictionaryId;
        this.code = code;
        this.translation = translation;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public Integer getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(Integer dictionaryId) {
        this.dictionaryId = dictionaryId;
    }

    public Language getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(Language languageCode) {
        this.languageCode = languageCode;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dictionaryId != null ? dictionaryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Dictionary)) {
            return false;
        }
        Dictionary other = (Dictionary) object;
        if ((this.dictionaryId == null && other.dictionaryId != null) || (this.dictionaryId != null && !this.dictionaryId.equals(other.dictionaryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.luckycode.projetawebservice.Dictionary[ dictionaryId=" + dictionaryId + " ]";
    }
    
}
