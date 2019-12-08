/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.model;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ltosc
 */
@Entity
@Table(name = "dfs_file_fragment")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DfsFileFragment.findAll", query = "SELECT d FROM DfsFileFragment d")
    , @NamedQuery(name = "DfsFileFragment.findById", query = "SELECT d FROM DfsFileFragment d WHERE d.id = :id")
    , @NamedQuery(name = "DfsFileFragment.findByGuid", query = "SELECT d FROM DfsFileFragment d WHERE d.guid = :guid")
    , @NamedQuery(name = "DfsFileFragment.findByOffset", query = "SELECT d FROM DfsFileFragment d WHERE d.offset = :offset")
    , @NamedQuery(name = "DfsFileFragment.findBySize", query = "SELECT d FROM DfsFileFragment d WHERE d.size = :size")})
public class DfsFileFragment implements Serializable {

    @Column(name = "last_access")
    @Temporal(TemporalType.DATE)
    private Date lastAccess;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "guid")
    private String guid;
    @Basic(optional = false)
    @Column(name = "offset")
    private int offset;
    @Basic(optional = false)
    @Column(name = "size")
    private int size;
    @JoinTable(name = "dfs_file_fragment_data_node", joinColumns = {
        @JoinColumn(name = "file_fragment", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "data_node", referencedColumnName = "id")})
    @ManyToMany
    private List<DfsDataNode> dfsDataNodeList;
    @JoinColumn(name = "file", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private DfsFile file;

    public DfsFileFragment() {
    }

    public DfsFileFragment(Integer id) {
        this.id = id;
    }

    public DfsFileFragment(Integer id, String guid, int offset, int size) {
        this.id = id;
        this.guid = guid;
        this.offset = offset;
        this.size = size;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @XmlTransient
    public List<DfsDataNode> getDfsDataNodeList() {
        return dfsDataNodeList;
    }

    public void setDfsDataNodeList(List<DfsDataNode> dfsDataNodeList) {
        this.dfsDataNodeList = dfsDataNodeList;
    }

    public DfsFile getFile() {
        return file;
    }

    public void setFile(DfsFile file) {
        this.file = file;
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
        if (!(object instanceof DfsFileFragment)) {
            return false;
        }
        DfsFileFragment other = (DfsFileFragment) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.bdfs.model.DfsFileFragment[ id=" + id + " ]";
    }

    public Date getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Date lastAccess) {
        this.lastAccess = lastAccess;
    }
    
}
