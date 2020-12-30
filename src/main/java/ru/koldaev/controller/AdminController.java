package ru.koldaev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.koldaev.entity.Role;
import ru.koldaev.entity.User;
import ru.koldaev.repo.UserRepo;
import ru.koldaev.service.UserService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
//@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepo userRepo;

//    @GetMapping("/getUsersList")
    @GetMapping("/getUsersList")
    public String getUsersList(Model model) {
        List<User> all = userService.findAll();
        model.addAttribute("usersList", all);
        return "usersList";
    }

    public String getUsLi() {
        return "redirect:/admin";
    }

//    @PreAuthorize("ROLE_ADMIN")
    @GetMapping("/editprofile/{id}")
    public String getAdminEditProfilePage(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElse(null);
        model.addAttribute("user", user);
        model.addAttribute("userroles", Role.values());
        return "editProfileAdmin";
    }

    @PostMapping("/editprofile/{id}")
    public String saveChange(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            Model model,
            @RequestParam(required = true) String username,
            @RequestParam(required = true) String password,
            @RequestParam(required = true) String firstname,
            @RequestParam(required = true) String surname,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String faculty,
            @RequestParam(required = true)Map<String, String> editProfileForm,
            HttpServletResponse response
            ) throws IOException {
        User user = userService.findById(id).orElse(null);
        boolean hasError = false;
        if (StringUtils.isEmpty(username)) {
            model.addAttribute("usernameError", "Email не может быть пустым");
            hasError = true;
        }
        if (StringUtils.isEmpty(password)) {
            model.addAttribute("passwordError", "Password не может быть пустым");
            hasError = true;
        }
        if (hasError) {
            model.addAttribute("user", user);
            model.addAttribute("userroles", Role.values());
            return "editProfileAdmin";
        } else {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            List<GrantedAuthority> updatedAuthorities = new ArrayList<>();

            Set<String> roles = Arrays.stream(Role.values()).map(Role :: name).collect(Collectors.toSet());
            user.getRoles().clear();
            for (String key : editProfileForm.keySet()) {
                if (roles.contains(key)) {
                    user.getRoles().add(Role.valueOf(key));
                    updatedAuthorities.add(Role.valueOf(key));
                }
            }
            userService.saveChange(username, password, firstname, surname, course, faculty, user);
            if (user == currentUser) {
                Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), updatedAuthorities);
                SecurityContextHolder.getContext().setAuthentication(newAuth);
            }

            model.addAttribute("message", user.getFirstname() + ' ' + user.getSurname());
            return "successActionPage";
        }
    }

    @GetMapping("/createUser")
    public String createUser(Model model) {
        model.addAttribute("userroles", Role.values());return "adminCreateUser";
    }

    @PostMapping("/createUser")
    public String createUser(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = true) String username,
            @RequestParam(required = true) String password,
            @RequestParam(required = true) String firstname,
            @RequestParam(required = true) String surname,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String faculty,
            Model model,
            @RequestParam Map<String, String> createUser
    ) {
        User userFromDb = userRepo.findByUsername(username);
        if (userFromDb != null) {
            model.addAttribute("message", "Пользователь уже существует!");
            return "registration";
        }
        User user = new User();
        user.setUsername(username);
        user.setFirstname(firstname);
        user.setSurname(surname);
        user.setPassword(password);
        if (!StringUtils.isEmpty(course)) {
            user.setCourse(course);
        }
        if (!StringUtils.isEmpty(faculty)) {
            user.setFaculty(faculty);
        }
        //user.setRoles(Collections.emptySet());
        Set<Role> userRoles = new HashSet<>();
        Set<String> roles = Arrays.stream(Role.values()).map(Role :: name).collect(Collectors.toSet());
        for (String key : createUser.keySet()) {
            if (roles.contains(key)) {
                //user.getRoles().add(Role.valueOf(key));
                userRoles.add(Role.valueOf(key));
            }
        }
        user.setRoles(userRoles);
        userRepo.save(user);

        return "redirect:/admin/getUsersList";
    }

    @PostMapping("/deleteUser/{id}")
    public String deleteUser(
            @PathVariable Long id
    ) {
        userRepo.delete(userRepo.findById(id).orElse(null));
        return "redirect:/admin/getUsersList";
    }

}
