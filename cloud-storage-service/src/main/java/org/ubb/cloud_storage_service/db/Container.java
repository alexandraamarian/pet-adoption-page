package org.ubb.cloud_storage_service.db;


import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "containers")
public class Container extends DatedEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID containerId;

    @Column(nullable = false)
    private String userName;

    @OneToMany(mappedBy = "container", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ObjectMetadata> objectMetadataList;

    public Container()
    {
    }

    public UUID getContainerId()
    {
        return containerId;
    }

    public void setContainerId(UUID containerId)
    {
        this.containerId = containerId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public List<ObjectMetadata> getObjectMetadataList()
    {
        return objectMetadataList;
    }

    public void setObjectMetadataList(List<ObjectMetadata> objectMetadataList)
    {
        this.objectMetadataList = objectMetadataList;
    }

    @Override
    public boolean equals(Object o)
    {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Container container = (Container) o;
        return Objects.equals(containerId, container.containerId) && Objects.equals(userName, container.userName) && Objects.equals(objectMetadataList, container.objectMetadataList);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(containerId, userName, objectMetadataList);
    }

    @Override
    public String toString()
    {
        return "Container{" +
                "containerId=" + containerId +
                ", userName='" + userName + '\'' +
                ", objectMetadataList=" + objectMetadataList +
                "} " + super.toString();
    }
}
