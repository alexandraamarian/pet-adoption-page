package org.ubb.cloud_storage_service.db;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "objects_metadata")
public class ObjectMetadata extends DatedEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, name = "object_id")
    private UUID objectId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String objectName;

    @Column(nullable = false)
    private String mimeType;

    @ManyToOne
    @JoinColumn(name = "container_id", nullable = false)
    private Container container;

    public ObjectMetadata()
    {
    }

    public UUID getObjectId()
    {
        return objectId;
    }

    public void setObjectId(UUID objectId)
    {
        this.objectId = objectId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getObjectName()
    {
        return objectName;
    }

    public void setObjectName(String objectName)
    {
        this.objectName = objectName;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    public Container getContainer()
    {
        return container;
    }

    public void setContainer(Container container)
    {
        this.container = container;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectMetadata that = (ObjectMetadata) o;
        return Objects.equals(objectId, that.objectId) && Objects.equals(userName, that.userName) && Objects.equals(objectName, that.objectName) && Objects.equals(mimeType, that.mimeType) && Objects.equals(container, that.container);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(objectId, userName, objectName, mimeType, container);
    }

    @Override
    public String toString()
    {
        return "ObjectMetadata{" +
                "objectId=" + objectId +
                ", userName='" + userName + '\'' +
                ", objectName='" + objectName + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", container=" + container +
                "} " + super.toString();
    }
}
