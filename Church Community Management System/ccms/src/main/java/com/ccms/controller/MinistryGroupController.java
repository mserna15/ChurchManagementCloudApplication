package com.ccms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ccms.model.MinistryGroup;
import com.ccms.service.MinistryGroupService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/groups")
public class MinistryGroupController {

    private static final Logger logger = LoggerFactory.getLogger(MinistryGroupController.class);

    @Autowired
    private MinistryGroupService ministryGroupService;

    // ── READ: List all groups ─────────────────────────────
    // GET /groups
    @GetMapping
    public String listGroups(Model model) {
        logger.debug("MinistryGroupController.listGroups() - Request received");
        model.addAttribute("groups", ministryGroupService.getAllGroups());
        return "groups/list";
    }

    // ── CREATE: Show form ─────────────────────────────────
    // GET /groups/add
    @GetMapping("/add")
    public String showAddForm(Model model) {
        logger.debug("MinistryGroupController.showAddForm() - Request received");
        model.addAttribute("group", new MinistryGroup());
        return "groups/form";
    }

    // ── CREATE: Submit form ───────────────────────────────
    // POST /groups/add
    @PostMapping("/add")
    public String addGroup(@Valid @ModelAttribute("group") MinistryGroup group,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        logger.debug("MinistryGroupController.addGroup() - Submitting new group: {}", group.getGroupName());

        if (result.hasErrors()) {
            logger.warn("MinistryGroupController.addGroup() - Validation errors");
            return "groups/form";
        }

        try {
            ministryGroupService.saveGroup(group);
            redirectAttributes.addFlashAttribute("successMessage", "Ministry group added successfully.");
            logger.debug("MinistryGroupController.addGroup() - Group saved, redirecting to /groups");
        } catch (IllegalArgumentException e) {
            logger.warn("MinistryGroupController.addGroup() - Error: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "groups/form";
        }

        return "redirect:/groups";
    }

    // ── UPDATE: Show form ─────────────────────────────────
    // GET /groups/edit/{id}
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.debug("MinistryGroupController.showEditForm() - id={}", id);

        MinistryGroup group = ministryGroupService.getGroupById(id).orElse(null);
        if (group == null) {
            logger.warn("MinistryGroupController.showEditForm() - Group not found id={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Ministry group not found.");
            return "redirect:/groups";
        }

        model.addAttribute("group", group);
        return "groups/form";
    }

    // ── UPDATE: Submit form ───────────────────────────────
    // POST /groups/edit/{id}
    @PostMapping("/edit/{id}")
    public String updateGroup(@PathVariable Long id,
                              @Valid @ModelAttribute("group") MinistryGroup group,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        logger.debug("MinistryGroupController.updateGroup() - id={}", id);

        if (result.hasErrors()) {
            logger.warn("MinistryGroupController.updateGroup() - Validation errors for id={}", id);
            return "groups/form";
        }

        try {
            ministryGroupService.updateGroup(id, group);
            redirectAttributes.addFlashAttribute("successMessage", "Ministry group updated successfully.");
            logger.debug("MinistryGroupController.updateGroup() - Updated group id={}", id);
        } catch (IllegalArgumentException e) {
            logger.warn("MinistryGroupController.updateGroup() - Error: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "groups/form";
        }

        return "redirect:/groups";
    }

    // ── DELETE ────────────────────────────────────────────
    // GET /groups/delete/{id}
    @GetMapping("/delete/{id}")
    public String deleteGroup(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.debug("MinistryGroupController.deleteGroup() - id={}", id);

        try {
            ministryGroupService.deleteGroup(id);
            redirectAttributes.addFlashAttribute("successMessage", "Ministry group deleted successfully.");
            logger.debug("MinistryGroupController.deleteGroup() - Deleted group id={}", id);
        } catch (IllegalArgumentException e) {
            logger.warn("MinistryGroupController.deleteGroup() - Error: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/groups";
    }
}