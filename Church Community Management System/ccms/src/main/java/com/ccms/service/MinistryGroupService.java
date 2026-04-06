package com.ccms.service;

import com.ccms.model.MinistryGroup;
import com.ccms.repository.MinistryGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MinistryGroupService {

    private static final Logger logger = LoggerFactory.getLogger(MinistryGroupService.class);

    @Autowired
    private MinistryGroupRepository ministryGroupRepository;

    // ── READ ──────────────────────────────────────────────

    public List<MinistryGroup> getAllGroups() {
        logger.debug("MinistryGroupService.getAllGroups() - Fetching all ministry groups");
        List<MinistryGroup> groups = ministryGroupRepository.findAll();
        logger.debug("MinistryGroupService.getAllGroups() - Found {} groups", groups.size());
        return groups;
    }

    public Optional<MinistryGroup> getGroupById(Long id) {
        logger.debug("MinistryGroupService.getGroupById() - Fetching group with id={}", id);
        Optional<MinistryGroup> group = ministryGroupRepository.findById(id);
        if (group.isPresent()) {
            logger.debug("MinistryGroupService.getGroupById() - Found group: {}", group.get().getGroupName());
        } else {
            logger.warn("MinistryGroupService.getGroupById() - No group found with id={}", id);
        }
        return group;
    }

    // ── CREATE ────────────────────────────────────────────

    public MinistryGroup saveGroup(MinistryGroup group) {
        logger.debug("MinistryGroupService.saveGroup() - Saving group: {}", group.getGroupName());

        // Enforce unique group name
        if (ministryGroupRepository.existsByGroupNameIgnoreCase(group.getGroupName())) {
            logger.warn("MinistryGroupService.saveGroup() - Duplicate group name: {}", group.getGroupName());
            throw new IllegalArgumentException("A ministry group with that name already exists.");
        }

        MinistryGroup saved = ministryGroupRepository.save(group);
        logger.debug("MinistryGroupService.saveGroup() - Saved group with id={}", saved.getId());
        return saved;
    }

    // ── UPDATE ────────────────────────────────────────────

    public MinistryGroup updateGroup(Long id, MinistryGroup updatedGroup) {
        logger.debug("MinistryGroupService.updateGroup() - Updating group id={}", id);

        MinistryGroup existing = ministryGroupRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("MinistryGroupService.updateGroup() - Group not found id={}", id);
                    return new IllegalArgumentException("Ministry group not found with id: " + id);
                });

        // Check duplicate name only if name changed
        if (!existing.getGroupName().equalsIgnoreCase(updatedGroup.getGroupName())
                && ministryGroupRepository.existsByGroupNameIgnoreCase(updatedGroup.getGroupName())) {
            logger.warn("MinistryGroupService.updateGroup() - Duplicate group name: {}", updatedGroup.getGroupName());
            throw new IllegalArgumentException("A ministry group with that name already exists.");
        }

        existing.setGroupName(updatedGroup.getGroupName());
        existing.setDescription(updatedGroup.getDescription());

        MinistryGroup saved = ministryGroupRepository.save(existing);
        logger.debug("MinistryGroupService.updateGroup() - Updated group id={}", saved.getId());
        return saved;
    }

    // ── DELETE ────────────────────────────────────────────

    public void deleteGroup(Long id) {
        logger.debug("MinistryGroupService.deleteGroup() - Deleting group id={}", id);

        if (!ministryGroupRepository.existsById(id)) {
            logger.error("MinistryGroupService.deleteGroup() - Group not found id={}", id);
            throw new IllegalArgumentException("Ministry group not found with id: " + id);
        }

        ministryGroupRepository.deleteById(id);
        logger.debug("MinistryGroupService.deleteGroup() - Deleted group id={}", id);
    }
}