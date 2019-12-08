/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ltosc
 */
@Entity
@Table(name = "dfs_shared_directory")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DfsSharedDirectory.findAll", query = "SELECT d FROM DfsSharedDirectory d")
    , @NamedQuery(name = "DfsSharedDirectory.findById", query = "SELECT d FROM DfsSharedDirectory d WHERE d.id = :id")})
public class DfsSharedDirectory implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinTable(name = "dfs_user_shared_directory", joinColumns = {
        @JoinColumn(name = "shared_directory", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "user", referencedColumnName = "id")})
    @ManyToMany
    private List<DfsUser> dfsUserList;
    @JoinColumn(name = "shared_directory", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private DfsDirectory sharedDirectory;

    public DfsSharedDirectory() {
    }

    public DfsSharedDirectory(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @XmlTransient
    public List<DfsUser> getDfsUserList() {
        return dfsUserList;
    }

    public void setDfsUserList(List<DfsUser> dfsUserList) {
        this.dfsUserList = dfsUserList;
    }

    public DfsDirectory getSharedDirectory() {
        return sharedDirectory;
    }

    public void setSharedDirectory(DfsDirectory sharedDirectory) {
        this.sharedDirectory = sharedDirectory;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DfsSharedDirectory)) {
            return false;
        }
        DfsSharedDirectory other = (DfsSharedDirectory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.bdfs.model.DfsSharedDirectory[ id=" + id + " ]";
    }
    
}
