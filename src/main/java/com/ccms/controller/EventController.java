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

import com.ccms.model.Event;
import com.ccms.service.EventService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/events")
public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventService eventService;
    // ── READ: List all Events ────────────────────────────
    // GET /Events
    @GetMapping
    public String listEvents(Model model) {
        logger.debug("EventController.listEvents() - Request received");
        model.addAttribute("events", eventService.getAllEvents());
        return "events/list";
    }
    // ── CREATE: Show form ─────────────────────────────────
    // GET /Events/add
    @GetMapping("/add")
    public String showAddForm(Model model) {
        logger.debug("EventController.showAddForm() - Request received");
        model.addAttribute("event", new Event());
        return "events/form";
    }

      // ── CREATE: Submit form ───────────────────────────────
    // POST /Events/add
    @PostMapping("/add")
    public String addEvent(@Valid @ModelAttribute("event") Event event,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        logger.debug("EventController.addEvent() - Submitting event: {}", event.getTitle());

        if (result.hasErrors()) {
            logger.warn("EventController.addEvent() - Validation errors");
            return "events/form";
        }

        try {
            eventService.saveEvent(event);
            logger.info("EventController.addEvent() - Event saved successfully: {}", event.getTitle());
            redirectAttributes.addFlashAttribute("successMessage", "Event added successfully.");
        } catch (Exception e) {
            logger.error("EventController.addEvent() - Unexpected error", e);
            model.addAttribute("errorMessage", "An unexpected error occurred.");
            return "events/form";
        }

        return "redirect:/events";
    }
    // ── UPDATE: Show form ─────────────────────────────────
    // GET /Events/edit/{id}
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model,
                               RedirectAttributes redirectAttributes) {
        logger.debug("EventController.showEditForm() - id={}", id);

        return eventService.getEventById(id).map(event -> {
            model.addAttribute("event", event);
            return "events/form";
        }).orElseGet(() -> {
            logger.warn("EventController.showEditForm() - Event not found id={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "Event not found.");
            return "redirect:/events";
        });
    }

    // ── UPDATE: Submit form ───────────────────────────────
    // POST /events/edit/{id}
    @PostMapping("/edit/{id}")
    public String updateEvent(@PathVariable Long id,
                              @Valid @ModelAttribute("event") Event event,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        logger.debug("EventController.updateEvent() - id={}", id);

        if (result.hasErrors()) {
            logger.warn("EventController.updateEvent() - Validation errors for id={}", id);
            return "events/form";
        }

        try {
            eventService.updateEvent(id, event);
            logger.info("EventController.updateEvent() - Event updated successfully id={}", id);
            redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully.");
        } catch (Exception e) {
            logger.error("EventController.updateEvent() - Unexpected error for id={}", id, e);
            model.addAttribute("errorMessage", "An unexpected error occurred.");
            return "events/form";
        }

        return "redirect:/events";
    }
 // ── DELETE ────────────────────────────────────────────
    // GET /events/delete/{id}
    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.debug("EventController.deleteEvent() - id={}", id);

        try {
            eventService.deleteEvent(id);
            logger.info("EventController.deleteEvent() - Event deleted successfully id={}", id);
            redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully.");
        } catch (Exception e) {
            logger.error("EventController.deleteEvent() - Unexpected error for id={}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred.");
        }

        return "redirect:/events";
    }
}