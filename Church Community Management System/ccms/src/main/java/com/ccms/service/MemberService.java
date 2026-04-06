package com.ccms.service;

import com.ccms.model.Member;
import com.ccms.model.MinistryGroup;
import com.ccms.repository.MemberRepository;
import com.ccms.repository.MinistryGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MinistryGroupRepository ministryGroupRepository;

    // ── READ ──────────────────────────────────────────────

    public List<Member> getAllMembers() {
        logger.debug("MemberService.getAllMembers() - Fetching all members");
        List<Member> members = memberRepository.findAll();
        logger.debug("MemberService.getAllMembers() - Found {} members", members.size());
        return members;
    }

    public Optional<Member> getMemberById(Long id) {
        logger.debug("MemberService.getMemberById() - Fetching member id={}", id);
        Optional<Member> member = memberRepository.findById(id);
        if (member.isPresent()) {
            logger.debug("MemberService.getMemberById() - Found member: {} {}", member.get().getFirstName(), member.get().getLastName());
        } else {
            logger.warn("MemberService.getMemberById() - No member found with id={}", id);
        }
        return member;
    }

    public List<Member> getMembersByGroup(Long groupId) {
        logger.debug("MemberService.getMembersByGroup() - Fetching members for groupId={}", groupId);
        List<Member> members = memberRepository.findByMinistryGroupId(groupId);
        logger.debug("MemberService.getMembersByGroup() - Found {} members in group id={}", members.size(), groupId);
        return members;
    }

    // ── CREATE ────────────────────────────────────────────

    public Member saveMember(Member member, Long ministryGroupId) {
        logger.debug("MemberService.saveMember() - Saving member: {} {}", member.getFirstName(), member.getLastName());

        // Enforce unique email
        if (memberRepository.existsByEmailIgnoreCase(member.getEmail())) {
            logger.warn("MemberService.saveMember() - Duplicate email: {}", member.getEmail());
            throw new IllegalArgumentException("A member with that email already exists.");
        }

        // Attach ministry group if provided
        if (ministryGroupId != null) {
            MinistryGroup group = ministryGroupRepository.findById(ministryGroupId)
                    .orElseThrow(() -> new IllegalArgumentException("Ministry group not found with id: " + ministryGroupId));
            member.setMinistryGroup(group);
        }

        Member saved = memberRepository.save(member);
        logger.debug("MemberService.saveMember() - Saved member with id={}", saved.getId());
        return saved;
    }

    // ── UPDATE ────────────────────────────────────────────

    public Member updateMember(Long id, Member updatedMember, Long ministryGroupId) {
        logger.debug("MemberService.updateMember() - Updating member id={}", id);

        Member existing = memberRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("MemberService.updateMember() - Member not found id={}", id);
                    return new IllegalArgumentException("Member not found with id: " + id);
                });

        // Check duplicate email only if email changed
        if (!existing.getEmail().equalsIgnoreCase(updatedMember.getEmail())
                && memberRepository.existsByEmailIgnoreCase(updatedMember.getEmail())) {
            logger.warn("MemberService.updateMember() - Duplicate email: {}", updatedMember.getEmail());
            throw new IllegalArgumentException("A member with that email already exists.");
        }

        existing.setFirstName(updatedMember.getFirstName());
        existing.setLastName(updatedMember.getLastName());
        existing.setEmail(updatedMember.getEmail());
        existing.setPhone(updatedMember.getPhone());
        existing.setJoinDate(updatedMember.getJoinDate());

        // Update ministry group association
        if (ministryGroupId != null) {
            MinistryGroup group = ministryGroupRepository.findById(ministryGroupId)
                    .orElseThrow(() -> new IllegalArgumentException("Ministry group not found with id: " + ministryGroupId));
            existing.setMinistryGroup(group);
        } else {
            existing.setMinistryGroup(null); // allow removal from group
        }

        Member saved = memberRepository.save(existing);
        logger.debug("MemberService.updateMember() - Updated member id={}", saved.getId());
        return saved;
    }

    // ── DELETE ────────────────────────────────────────────

    public void deleteMember(Long id) {
        logger.debug("MemberService.deleteMember() - Deleting member id={}", id);

        if (!memberRepository.existsById(id)) {
            logger.error("MemberService.deleteMember() - Member not found id={}", id);
            throw new IllegalArgumentException("Member not found with id: " + id);
        }

        memberRepository.deleteById(id);
        logger.debug("MemberService.deleteMember() - Deleted member id={}", id);
    }
}