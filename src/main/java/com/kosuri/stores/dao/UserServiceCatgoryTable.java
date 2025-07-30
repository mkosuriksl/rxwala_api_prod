package com.kosuri.stores.dao;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_service_catgory_table")
public class UserServiceCatgoryTable {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;  // Auto-generated ID

    @Column(name = "userId", unique = true, nullable = false)
    private String userId;

    @Convert(converter = StringListConverter.class)
    @Column(name = "service_categories")
    private List<String> serviceCategories;

    @Column(name = "dashboard_role")
    private String dashboardRole;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
        if (this.serviceCategories != null && !this.serviceCategories.isEmpty()) {
            this.dashboardRole = String.join("_", this.serviceCategories).toUpperCase();
        }
    }

}
