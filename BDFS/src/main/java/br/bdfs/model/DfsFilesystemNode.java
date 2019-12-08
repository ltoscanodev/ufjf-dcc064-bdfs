/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ltosc
 */
@Entity
@Table(name = "dfs_filesystem_node")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DfsFilesystemNode.findAll", query = "SELECT d FROM DfsFilesystemNode d")
    , @NamedQuery(name = "DfsFilesystemNode.findById", query = "SELECT d FROM DfsFilesystemNode d WHERE d.id = :id")
    , @NamedQuery(name = "DfsFilesystemNode.findByGuid", query = "SELECT d FROM DfsFilesystemNode d WHERE d.guid = :guid")
    , @NamedQuery(name = "DfsFilesystemNode.findByAddressIp", query = "SELECT d FROM DfsFilesystemNode d WHERE d.addressIp = :addressIp")
    , @NamedQuery(name = "DfsFilesystemNode.findByAdressPort", query = "SELECT d FROM DfsFilesystemNode d WHERE d.adressPort = :adressPort")})
public class DfsFilesystemNode implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "guid")
    private String guid;
    @Basic(optional = false)
    @Column(name = "address_ip")
    private String addressIp;
    @Basic(optional = false)
    @Column(name = "adress_port")
    private int adressPort;

    public DfsFilesystemNode() {
    }

    public DfsFilesystemNode(Integer id) {
        this.id = id;
    }

    public DfsFilesystemNode(Integer id, String guid, String addressIp, int adressPort) {
        this.id = id;
        this.guid = guid;
        this.addressIp = addressIp;
        this.adressPort = adressPort;
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

    public String getAddressIp() {
        return addressIp;
    }

    public void setAddressIp(String addressIp) {
        this.addressIp = addressIp;
    }

    public int getAdressPort() {
        return adressPort;
    }

    public void setAdressPort(int adressPort) {
        this.adressPort = adressPort;
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
        if (!(object instanceof DfsFilesystemNode)) {
            return false;
        }
        DfsFilesystemNode other = (DfsFilesystemNode) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.bdfs.model.DfsFilesystemNode[ id=" + id + " ]";
    }
    
}
