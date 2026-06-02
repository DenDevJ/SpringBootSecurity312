package ru.kata.spring.boot_security.demo.controller;

import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserRoleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final UserRoleService userRoleService;  // ДОБАВЛЕНО

    public AdminController(UserService userService,
                           RoleService roleService,
                           UserRoleService userRoleService) {  // ИСПРАВЛЕН конструктор
        this.userService = userService;
        this.roleService = roleService;
        this.userRoleService = userRoleService;
    }

    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("allRoles", roleService.findAll());
        return "admin";
    }

    @GetMapping("/addUser")
    public String showAddForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.findAll());
        return "addUser";
    }

    @PostMapping("/addUser")
    public String addUser(@ModelAttribute User user,
                          @RequestParam(value = "roleIds", required = false) Set<Long> roleIds) {
        userService.save(user);
        if (roleIds != null && !roleIds.isEmpty()) {
            userRoleService.setUserRoles(user.getId(), roleIds);  // ИСПРАВЛЕНО
        }
        return "redirect:/admin";
    }

    @GetMapping("/editUser")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("allRoles", roleService.findAll());
        return "editUser";
    }

    @PostMapping("/editUser")
    public String editUser(@ModelAttribute User user,
                           @RequestParam(value = "roleIds", required = false) Set<Long> roleIds) {
        userService.update(user);
        if (roleIds != null && !roleIds.isEmpty()) {
            userRoleService.setUserRoles(user.getId(), roleIds);  // ИСПРАВЛЕНО
        }
        return "redirect:/admin";
    }

    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }

    @PostMapping("/user/{userId}/role/{roleId}/add")
    public String addRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        userRoleService.addRoleToUser(userId, roleId);  // ИСПРАВЛЕНО
        return "redirect:/admin/editUser?id=" + userId;
    }

    @PostMapping("/user/{userId}/role/{roleId}/remove")
    public String removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        userRoleService.removeRoleFromUser(userId, roleId);  // ИСПРАВЛЕНО
        return "redirect:/admin/editUser?id=" + userId;
    }
}