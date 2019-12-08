/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author igor6
 */
@Entity
@Table(name = "dfs_data_node")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DfsDataNode.findAll", query = "SELECT d FROM DfsDataNode d")
    , @NamedQuery(name = "DfsDataNode.findById", query = "SELECT d FROM DfsDataNode d WHERE d.id = :id")
    , @NamedQuery(name = "DfsDataNode.findByAddressIp", query = "SELECT d FROM DfsDataNode d WHERE d.addressIp = :addressIp")
    , @NamedQuery(name = "DfsDataNode.findByAddressPort", query = "SELECT d FROM DfsDataNode d WHERE d.addressPort = :addressPort")
    , @NamedQuery(name = "DfsDataNode.findByConnected", query = "SELECT d FROM DfsDataNode d WHERE d.connected = :connected")
    , @NamedQuery(name = "DfsDataNode.findByLastAccess", query = "SELECT d FROM DfsDataNode d WHERE d.lastAccess = :lastAccess")})
public class DfsDataNode implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "address_ip")
    private String addressIp;
    @Basic(optional = false)
    @Column(name = "address_port")
    private int addressPort;
    @Basic(optional = false)
    @Column(name = "connected")
    private short connected;
    @Basic(optional = false)
    @Column(name = "last_access")
    @Temporal(TemporalType.DATE)
    private Date lastAccess;

    public DfsDataNode() {
    }

    public DfsDataNode(Integer id) {
        this.id = id;
    }

    public DfsDataNode(Integer id, String addressIp, int addressPort, short connected, Date lastAccess) {
        this.id = id;
        this.addressIp = addressIp;
        this.addressPort = addressPort;
        this.connected = connected;
        this.lastAccess = lastAccess;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddressIp() {
        return addressIp;
    }

    public void setAddressIp(String addressIp) {
        this.addressIp = addressIp;
    }

    public int getAddressPort() {
        return addressPort;
    }

    public void setAddressPort(int addressPort) {
        this.addressPort = addressPort;
    }

    public short getConnected() {
        return connected;
    }

    public void setConnected(short connected) {
        this.connected = connected;
    }

    public Date getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Date lastAccess) {
        this.lastAccess = lastAccess;
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
        if (!(object instanceof DfsDataNode)) {
            return false;
        }
        DfsDataNode other = (DfsDataNode) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.bdfs.model.DfsDataNode[ id=" + id + " ]";
    }
    
}
