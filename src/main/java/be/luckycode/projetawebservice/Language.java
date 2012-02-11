/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.luckycode.projetawebservice;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author michael
 */
@Entity
@Table(name = "language")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Language.findAll", query = "SELECT l FROM Language l"),
    @NamedQuery(name = "Language.findByLanguageCode", query = "SELECT l FROM Language l WHERE l.languageCode = :languageCode"),
    @NamedQuery(name = "Language.findByLanguageDescription", query = "SELECT l FROM Language l WHERE l.languageDescription = :languageDescription")})
public class Language implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "language_code")
    private String languageCode;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "language_description")
    private String languageDescription;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "languageCode")
    private Collection<Dictionary> dictionaryCollection;

    public Language() {
    }

    public Language(String languageCode) {
        this.languageCode = languageCode;
    }

    public Language(String languageCode, String languageDescription) {
        this.languageCode = languageCode;
        this.languageDescription = languageDescription;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageDescription() {
        return languageDescription;
    }

    public void setLanguageDescription(String languageDescription) {
        this.languageDescription = languageDescription;
    }

    @XmlTransient
    public Collection<Dictionary> getDictionaryCollection() {
        return dictionaryCollection;
    }

    public void setDictionaryCollection(Collection<Dictionary> dictionaryCollection) {
        this.dictionaryCollection = dictionaryCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (languageCode != null ? languageCode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Language)) {
            return false;
        }
        Language other = (Language) object;
        if ((this.languageCode == null && other.languageCode != null) || (this.languageCode != null && !this.languageCode.equals(other.languageCode))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.luckycode.projetawebservice.Language[ languageCode=" + languageCode + " ]";
    }
    
}
