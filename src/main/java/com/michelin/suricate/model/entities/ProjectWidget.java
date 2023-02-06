/*
 *
 *  * Copyright 2012-2021 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.michelin.suricate.model.entities;

import com.michelin.suricate.model.entities.generic.AbstractAuditingEntity;
import com.michelin.suricate.model.enums.WidgetStateEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class ProjectWidget extends AbstractAuditingEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String data;

    @Column
    private int gridRow;

    @Column
    private int gridColumn;

    @Column
    private int width;

    @Column
    private int height;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String customStyle;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String backendConfig;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String log;

    @Column
    private Date lastExecutionDate;

    @Column
    private Date lastSuccessDate;

    @Column
    @Enumerated(EnumType.STRING)
    private WidgetStateEnum state;

    @ToString.Exclude
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "projectGridId", referencedColumnName = "ID")
    private ProjectGrid projectGrid;

    @ToString.Exclude
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "widgetId", referencedColumnName = "ID")
    private Widget widget;

    @Override
    public int hashCode() { return super.hashCode(); }

    @Override
    public boolean equals(Object other) { return super.equals(other); }
}
