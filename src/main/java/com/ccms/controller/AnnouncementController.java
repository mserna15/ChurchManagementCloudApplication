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

import com.ccms.model.Announcement;
import com.ccms.service.AnnouncementService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/announcements")
public class AnnouncementController {

    private static final Logger logger = LoggerFactory.getLogger(AnnouncementController.class);

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping
    public String listAnnouncements(Model model) {
        logger.debug("AnnouncementController.listAnnouncements() - Request received");
        model.addAttribute("announcements", announcementService.getAllAnnouncements());
        return "announcements/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        logger.debug("AnnouncementController.showAddForm() - Request received");
        model.addAttribute("announcement", new Announcement());
        return "announcements/form";
    }

    @PostMapping("/add")
    public String addAnnouncement(@Valid @ModelAttribute("announcement") Announcement announcement,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        logger.debug("AnnouncementController.addAnnouncement() - Submitting: {}", announcement.getTitle());

        if (result.hasErrors()) {
            logger.warn("AnnouncementController.addAnnouncement() - Validation errors");
            return "announcements/form";
        }

        try {
            announcementService.saveAnnouncement(announcement);
            logger.info("AnnouncementController.addAnnouncement() - Saved successfully: {}", announcement.getTitle());
            redirectAttributes.addFlashAttribute("successMessage", "Announcement posted successfully.");
        } catch (Exception e) {
            logger.error("AnnouncementController.addAnnouncement() - Unexpected error", e);
            model.addAttribute("errorMessage", "An unexpected error occurred.");
            return "announcements/form";
        }

        return "redirect:/announcements";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model,
                               RedirectAttributes redirectAttributes) {
        logger.debug("AnnouncementController.showEditForm() - id={}", id);

        return announcementService.getAnnouncementById(id).map(announcement -> {
            model.addAttribute("announcement", announcement);
            return "announcements/form";
        }).orElseGet(() -> {
            logger.warn("AnnouncementController.showEditForm() - Announcement not found id={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Announcement not found.");
            return "redirect:/announcements";
        });
    }

    @PostMapping("/edit/{id}")
    public String updateAnnouncement(@PathVariable Long id,
                                     @Valid @ModelAttribute("announcement") Announcement announcement,
                                     BindingResult result,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        logger.debug("AnnouncementController.updateAnnouncement() - id={}", id);

        if (result.hasErrors()) {
            logger.warn("AnnouncementController.updateAnnouncement() - Validation errors for id={}", id);
            return "announcements/form";
        }

        try {
            announcementService.updateAnnouncement(id, announcement);
            logger.info("AnnouncementController.updateAnnouncement() - Updated successfully id={}", id);
            redirectAttributes.addFlashAttribute("successMessage", "Announcement updated successfully.");
        } catch (Exception e) {
            logger.error("AnnouncementController.updateAnnouncement() - Unexpected error for id={}", id, e);
            model.addAttribute("errorMessage", "An unexpected error occurred.");
            return "announcements/form";
        }

        return "redirect:/announcements";
    }

    @GetMapping("/delete/{id}")
    public String deleteAnnouncement(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.debug("AnnouncementController.deleteAnnouncement() - id={}", id);

        try {
            announcementService.deleteAnnouncement(id);
            logger.info("AnnouncementController.deleteAnnouncement() - Deleted successfully id={}", id);
            redirectAttributes.addFlashAttribute("successMessage", "Announcement deleted successfully.");
        } catch (Exception e) {
            logger.error("AnnouncementController.deleteAnnouncement() - Unexpected error for id={}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred.");
        }

        return "redirect:/announcements";
    }
}