/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone.om.helpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.hadoop.ozone.OzoneAcl;
import org.apache.hadoop.ozone.OzoneConsts;
import org.apache.hadoop.ozone.audit.Auditable;
import org.apache.hadoop.ozone.om.exceptions.OMException;
import org.apache.hadoop.ozone.protocol.proto.OzoneManagerProtocolProtos.OzoneAclInfo;
import org.apache.hadoop.ozone.protocol.proto.OzoneManagerProtocolProtos.VolumeInfo;

import com.google.common.base.Preconditions;


/**
 * A class that encapsulates the OmVolumeArgs Args.
 */
public final class OmVolumeArgs extends WithMetadata implements Auditable {
  private final String adminName;
  private String ownerName;
  private final String volume;
  private long creationTime;
  private long quotaInBytes;
  private final OmOzoneAclMap aclMap;
  private long objectID;
  private long updateID;

  /**
   * Set the Object ID. If this value is already set then this function throws.
   * There is a reason why we cannot use the final here. The OmVolumeArgs is
   * deserialized from the protobuf in many places in code. We need to set
   * this object ID, after it is deserialized.
   *
   * @param obId - long
   */
  public void setObjectID(long obId) {
    if(this.objectID != 0) {
      throw new UnsupportedOperationException("Attempt to modify object ID " +
          "which is not zero. Current Object ID is " + this.objectID);
    }
    this.objectID = obId;
  }

  /**
   * Returns a monotonically increasing ID, that denotes the last update.
   * Each time an update happens, this ID is incremented.
   * @return long
   */
  public long getUpdateID() {
    return updateID;
  }

  /**
   * Sets the update ID. For each modification of this object, we will set
   * this to a value greater than the current value.
   * @param updateID  long
   */
  public void setUpdateID(long updateID) {
    this.updateID = updateID;
  }

  /**
   * A immutable identity field for this object.
   * @return  long.
   */
  public long getObjectID() {
    return objectID;
  }

  /**
   * Private constructor, constructed via builder.
   * @param adminName  - Administrator's name.
   * @param ownerName  - Volume owner's name
   * @param volume - volume name
   * @param quotaInBytes - Volume Quota in bytes.
   * @param metadata - metadata map for custom key/value data.
   * @param aclMap - User to access rights map.
   * @param creationTime - Volume creation time.
   * @param  objectID - ID of this object.
   * @param updateID - A sequence number that denotes the last update on this
   * object. This is a monotonically increasing number.
   */
  @SuppressWarnings({"checkstyle:ParameterNumber", "This is invoked from a " +
      "builder."})
  private OmVolumeArgs(String adminName, String ownerName, String volume,
                       long quotaInBytes, Map<String, String> metadata,
                       OmOzoneAclMap aclMap, long creationTime, long objectID,
                      long updateID) {
    this.adminName = adminName;
    this.ownerName = ownerName;
    this.volume = volume;
    this.quotaInBytes = quotaInBytes;
    this.metadata = metadata;
    this.aclMap = aclMap;
    this.creationTime = creationTime;
    this.objectID = objectID;
    this.updateID = updateID;
  }


  public void setOwnerName(String newOwner) {
    this.ownerName = newOwner;
  }

  public void setQuotaInBytes(long quotaInBytes) {
    this.quotaInBytes = quotaInBytes;
  }

  public void setCreationTime(long time) {
    this.creationTime = time;
  }

  public void addAcl(OzoneAcl acl) throws OMException {
    this.aclMap.addAcl(acl);
  }

  public void setAcls(List<OzoneAcl> acls) throws OMException {
    this.aclMap.setAcls(acls);
  }

  public void removeAcl(OzoneAcl acl) throws OMException {
    this.aclMap.removeAcl(acl);
  }

  /**
   * Returns the Admin Name.
   * @return String.
   */
  public String getAdminName() {
    return adminName;
  }

  /**
   * Returns the owner Name.
   * @return String
   */
  public String getOwnerName() {
    return ownerName;
  }

  /**
   * Returns the volume Name.
   * @return String
   */
  public String getVolume() {
    return volume;
  }

  /**
   * Returns creation time.
   * @return long
   */
  public long getCreationTime() {
    return creationTime;
  }

  /**
   * Returns Quota in Bytes.
   * @return long, Quota in bytes.
   */
  public long getQuotaInBytes() {
    return quotaInBytes;
  }

  public OmOzoneAclMap getAclMap() {
    return aclMap;
  }
  /**
   * Returns new builder class that builds a OmVolumeArgs.
   *
   * @return Builder
   */
  public static Builder newBuilder() {
    return new Builder();
  }

  @Override
  public Map<String, String> toAuditMap() {
    Map<String, String> auditMap = new LinkedHashMap<>();
    auditMap.put(OzoneConsts.ADMIN, this.adminName);
    auditMap.put(OzoneConsts.OWNER, this.ownerName);
    auditMap.put(OzoneConsts.VOLUME, this.volume);
    auditMap.put(OzoneConsts.CREATION_TIME, String.valueOf(this.creationTime));
    auditMap.put(OzoneConsts.QUOTA_IN_BYTES, String.valueOf(this.quotaInBytes));
    auditMap.put(OzoneConsts.OBJECT_ID, String.valueOf(this.getObjectID()));
    auditMap.put(OzoneConsts.UPDATE_ID, String.valueOf(this.getUpdateID()));
    return auditMap;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OmVolumeArgs that = (OmVolumeArgs) o;
    return Objects.equals(this.objectID, that.objectID);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.objectID);
  }

  /**
   * Builder for OmVolumeArgs.
   */
  public static class Builder {
    private String adminName;
    private String ownerName;
    private String volume;
    private long creationTime;
    private long quotaInBytes;
    private Map<String, String> metadata;
    private OmOzoneAclMap aclMap;
    private long objectID;
    private long updateID;

    /**
     * Sets the Object ID for this Object.
     * Object ID are unique and immutable identifier for each object in the
     * System.
     * @param id - long
     */
    public Builder setObjectID(long id) {
      this.objectID = id;
      return this;
    }

    /**
     * Sets the update ID for this Object. Update IDs are monotonically
     * increasing values which are updated each time there is an update.
     * @param id - long
     */
    public Builder setUpdateID(long id) {
      this.updateID = id;
      return this;
    }



    /**
     * Constructs a builder.
     */
    public Builder() {
      metadata = new HashMap<>();
      aclMap = new OmOzoneAclMap();
    }

    public Builder setAdminName(String admin) {
      this.adminName = admin;
      return this;
    }

    public Builder setOwnerName(String owner) {
      this.ownerName = owner;
      return this;
    }

    public Builder setVolume(String volumeName) {
      this.volume = volumeName;
      return this;
    }

    public Builder setCreationTime(long createdOn) {
      this.creationTime = createdOn;
      return this;
    }

    public Builder setQuotaInBytes(long quota) {
      this.quotaInBytes = quota;
      return this;
    }

    public Builder addMetadata(String key, String value) {
      metadata.put(key, value); // overwrite if present.
      return this;
    }

    public Builder addAllMetadata(Map<String, String> additionalMetaData) {
      if (additionalMetaData != null) {
        metadata.putAll(additionalMetaData);
      }
      return this;
    }

    public Builder addOzoneAcls(OzoneAclInfo acl) throws IOException {
      aclMap.addAcl(acl);
      return this;
    }

    /**
     * Constructs a CreateVolumeArgument.
     * @return CreateVolumeArgs.
     */
    public OmVolumeArgs build() {
      Preconditions.checkNotNull(adminName);
      Preconditions.checkNotNull(ownerName);
      Preconditions.checkNotNull(volume);
      return new OmVolumeArgs(adminName, ownerName, volume, quotaInBytes,
          metadata, aclMap, creationTime, objectID, updateID);
    }

  }

  public VolumeInfo getProtobuf() {
    List<OzoneAclInfo> aclList = aclMap.ozoneAclGetProtobuf();
    return VolumeInfo.newBuilder()
        .setAdminName(adminName)
        .setOwnerName(ownerName)
        .setVolume(volume)
        .setQuotaInBytes(quotaInBytes)
        .addAllMetadata(KeyValueUtil.toProtobuf(metadata))
        .addAllVolumeAcls(aclList)
        .setCreationTime(
            creationTime == 0 ? System.currentTimeMillis() : creationTime)
        .setObjectID(objectID)
        .setUpdateID(updateID)
        .build();
  }

  public static OmVolumeArgs getFromProtobuf(VolumeInfo volInfo)
      throws OMException {
    OmOzoneAclMap aclMap =
        OmOzoneAclMap.ozoneAclGetFromProtobuf(volInfo.getVolumeAclsList());
    return new OmVolumeArgs(
        volInfo.getAdminName(),
        volInfo.getOwnerName(),
        volInfo.getVolume(),
        volInfo.getQuotaInBytes(),
        KeyValueUtil.getFromProtobuf(volInfo.getMetadataList()),
        aclMap,
        volInfo.getCreationTime(),
        volInfo.getObjectID(),
        volInfo.getUpdateID());
  }

  /**
   * Return a new copy of the object.
   */
  public OmVolumeArgs copyObject() {
    Map<String, String> cloneMetadata = new HashMap<>();
    if (metadata != null) {
      metadata.forEach((k, v) -> cloneMetadata.put(k, v));
    }

    OmOzoneAclMap cloneAclMap = aclMap.copyObject();

    return new OmVolumeArgs(adminName, ownerName, volume, quotaInBytes,
        cloneMetadata, cloneAclMap, creationTime, objectID, updateID);
  }
}
