package com.ccms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ccms.model.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // Check for duplicate email (used during create/update validation)
    Optional<Member> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    // Get all members belonging to a specific ministry group
    List<Member> findByMinistryGroupId(Long ministryGroupId);
}