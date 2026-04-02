package com.ccms.repository;

import com.ccms.model.MinistryGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MinistryGroupRepository extends JpaRepository<MinistryGroup, Long> {

    // Check for duplicate group name (used during create/update validation)
    Optional<MinistryGroup> findByGroupNameIgnoreCase(String groupName);

    boolean existsByGroupNameIgnoreCase(String groupName);
}