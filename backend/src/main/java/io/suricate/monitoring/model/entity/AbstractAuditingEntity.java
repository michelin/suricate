package io.suricate.monitoring.model.entity;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * Base abstract class for entity auditing (CreatedBy, CreatedDate, LastModifiedBy, LastModifiedDate)
 *
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditingEntity<ID> extends AbstractEntity<ID> {

    /* **************************************************************************************************** */
    /*                                           ATTRIBUTES                                                 */
    /* **************************************************************************************************** */

    /** Created by attribute */
    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

    /** Created date attribute */
    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private Date createdDate = new Date();

    /** Last Modified by attribute */
    @LastModifiedBy
    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    /** Last Modified date attribute */
    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Date lastModifiedDate = new Date();


    /* **************************************************************************************************** */
    /*                                      GETTER'S and SETTER'S                                           */
    /* **************************************************************************************************** */

    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }
    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

}
