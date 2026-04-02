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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ccms.model.Member;
import com.ccms.service.MemberService;
import com.ccms.service.MinistryGroupService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/members")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    private MemberService memberService;

    @Autowired
    private MinistryGroupService ministryGroupService;

    // ── READ: List all members ────────────────────────────
    // GET /members
    @GetMapping
    public String listMembers(Model model) {
        logger.debug("MemberController.listMembers() - Request received");
        model.addAttribute("members", memberService.getAllMembers());
        return "members/list";
    }

    // ── CREATE: Show form ─────────────────────────────────
    // GET /members/add
    @GetMapping("/add")
    public String showAddForm(Model model) {
        logger.debug("MemberController.showAddForm() - Request received");
        model.addAttribute("member", new Member());
        model.addAttribute("groups", ministryGroupService.getAllGroups());
        return "members/form";
    }

    // ── CREATE: Submit form ───────────────────────────────
    // POST /members/add
    @PostMapping("/add")
    public String addMember(@Valid @ModelAttribute("member") Member member,
                            BindingResult result,
                            @RequestParam(name = "ministryGroupId", required = false) Long ministryGroupId,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        logger.debug("MemberController.addMember() - Submitting new member: {} {}", member.getFirstName(), member.getLastName());

        if (result.hasErrors()) {
            logger.warn("MemberController.addMember() - Validation errors");
            model.addAttribute("groups", ministryGroupService.getAllGroups());
            return "members/form";
        }

        try {
            memberService.saveMember(member, ministryGroupId);
            redirectAttributes.addFlashAttribute("successMessage", "Member added successfully.");
            logger.debug("MemberController.addMember() - Member saved, redirecting to /members");
        } catch (IllegalArgumentException e) {
            logger.warn("MemberController.addMember() - Error: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("groups", ministryGroupService.getAllGroups());
            return "members/form";
        }

        return "redirect:/members";
    }

    // ── UPDATE: Show form ─────────────────────────────────
    // GET /members/edit/{id}
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.debug("MemberController.showEditForm() - id={}", id);

        Member member = memberService.getMemberById(id).orElse(null);
        if (member == null) {
            logger.warn("MemberController.showEditForm() - Member not found id={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Member not found.");
            return "redirect:/members";
        }

        model.addAttribute("member", member);
        model.addAttribute("groups", ministryGroupService.getAllGroups());
        return "members/form";
    }

    // ── UPDATE: Submit form ───────────────────────────────
    // POST /members/edit/{id}
    @PostMapping("/edit/{id}")
    public String updateMember(@PathVariable Long id,
                               @Valid @ModelAttribute("member") Member member,
                               BindingResult result,
                               @RequestParam(name = "ministryGroupId", required = false) Long ministryGroupId,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        logger.debug("MemberController.updateMember() - id={}", id);

        if (result.hasErrors()) {
            logger.warn("MemberController.updateMember() - Validation errors for id={}", id);
            model.addAttribute("groups", ministryGroupService.getAllGroups());
            return "members/form";
        }

        try {
            memberService.updateMember(id, member, ministryGroupId);
            redirectAttributes.addFlashAttribute("successMessage", "Member updated successfully.");
            logger.debug("MemberController.updateMember() - Updated member id={}", id);
        } catch (IllegalArgumentException e) {
            logger.warn("MemberController.updateMember() - Error: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("groups", ministryGroupService.getAllGroups());
            return "members/form";
        }

        return "redirect:/members";
    }

    // ── DELETE ────────────────────────────────────────────
    // GET /members/delete/{id}
    @GetMapping("/delete/{id}")
    public String deleteMember(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.debug("MemberController.deleteMember() - id={}", id);

        try {
            memberService.deleteMember(id);
            redirectAttributes.addFlashAttribute("successMessage", "Member deleted successfully.");
            logger.debug("MemberController.deleteMember() - Deleted member id={}", id);
        } catch (IllegalArgumentException e) {
            logger.warn("MemberController.deleteMember() - Error: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/members";
    }
}