package com.ccms.controller;

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

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping
    public String listAnnouncements(Model model) {
        model.addAttribute("announcements", announcementService.getAllAnnouncements());
        return "announcements/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("announcement", new Announcement());
        return "announcements/form";
    }

    @PostMapping("/add")
    public String addAnnouncement(@Valid @ModelAttribute("announcement") Announcement announcement,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (result.hasErrors()) return "announcements/form";
        announcementService.saveAnnouncement(announcement);
        redirectAttributes.addFlashAttribute("successMessage", "Announcement posted successfully.");
        return "redirect:/announcements";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model,
                               RedirectAttributes redirectAttributes) {
        return announcementService.getAnnouncementById(id).map(announcement -> {
            model.addAttribute("announcement", announcement);
            return "announcements/form";
        }).orElseGet(() -> {
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
        if (result.hasErrors()) return "announcements/form";
        announcementService.updateAnnouncement(id, announcement);
        redirectAttributes.addFlashAttribute("successMessage", "Announcement updated successfully.");
        return "redirect:/announcements";
    }

    @GetMapping("/delete/{id}")
    public String deleteAnnouncement(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        announcementService.deleteAnnouncement(id);
        redirectAttributes.addFlashAttribute("successMessage", "Announcement deleted successfully.");
        return "redirect:/announcements";
    }
}