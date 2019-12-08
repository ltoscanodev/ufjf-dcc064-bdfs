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
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
@Table(name = "dfs_file")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DfsFile.findAll", query = "SELECT d FROM DfsFile d")
    , @NamedQuery(name = "DfsFile.findById", query = "SELECT d FROM DfsFile d WHERE d.id = :id")
    , @NamedQuery(name = "DfsFile.findByName", query = "SELECT d FROM DfsFile d WHERE d.name = :name")
    , @NamedQuery(name = "DfsFile.findByExtension", query = "SELECT d FROM DfsFile d WHERE d.extension = :extension")
    , @NamedQuery(name = "DfsFile.findByPath", query = "SELECT d FROM DfsFile d WHERE d.path = :path")
    , @NamedQuery(name = "DfsFile.findByCreationTime", query = "SELECT d FROM DfsFile d WHERE d.creationTime = :creationTime")
    , @NamedQuery(name = "DfsFile.findByModificationTime", query = "SELECT d FROM DfsFile d WHERE d.modificationTime = :modificationTime")
    , @NamedQuery(name = "DfsFile.findByAccessTime", query = "SELECT d FROM DfsFile d WHERE d.accessTime = :accessTime")
    , @NamedQuery(name = "DfsFile.findByLocked", query = "SELECT d FROM DfsFile d WHERE d.locked = :locked")})
public class DfsFile implements Serializable {

    @Basic(optional = false)
    private int size;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "extension")
    private String extension;
    @Basic(optional = false)
    @Column(name = "path")
    private String path;
    @Basic(optional = false)
    @Column(name = "creation_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;
    @Column(name = "modification_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modificationTime;
    @Column(name = "access_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date accessTime;
    @Basic(optional = false)
    @Column(name = "locked")
    private short locked;
    @JoinColumn(name = "directory", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private DfsDirectory directory;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "file")
    private List<DfsFileFragment> dfsFileFragmentList;

    public DfsFile() {
    }

    public DfsFile(Integer id) {
        this.id = id;
    }

    public DfsFile(Integer id, String name, String extension, String path, Date creationTime, short locked, int size) {
        this.id = id;
        this.name = name;
        this.extension = extension;
        this.path = path;
        this.creationTime = creationTime;
        this.locked = locked;
        this.size = size;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(Date modificationTime) {
        this.modificationTime = modificationTime;
    }

    public Date getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(Date accessTime) {
        this.accessTime = accessTime;
    }

    public short getLocked() {
        return locked;
    }

    public void setLocked(short locked) {
        this.locked = locked;
    }

    public DfsDirectory getDirectory() {
        return directory;
    }

    public void setDirectory(DfsDirectory directory) {
        this.directory = directory;
    }


    @XmlTransient
    public List<DfsFileFragment> getDfsFileFragmentList() {
        return dfsFileFragmentList;
    }

    public void setDfsFileFragmentList(List<DfsFileFragment> dfsFileFragmentList) {
        this.dfsFileFragmentList = dfsFileFragmentList;
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
        if (!(object instanceof DfsFile)) {
            return false;
        }
        DfsFile other = (DfsFile) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.bdfs.model.DfsFile[ id=" + id + " ]";
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    
}
