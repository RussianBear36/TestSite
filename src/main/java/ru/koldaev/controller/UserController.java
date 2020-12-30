package ru.koldaev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.koldaev.entity.Role;
import ru.koldaev.entity.Test;
import ru.koldaev.entity.User;
import ru.koldaev.repo.TestRepo;
import ru.koldaev.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private TestRepo testRepo;

    //Редактирование профиля пользователя
    @GetMapping("/editprofile")
    public String editUserProfile(
            @AuthenticationPrincipal User user,
            Model model
    ) {
        model.addAttribute("user", user);
        model.addAttribute("userroles", Role.values());
        return "editProfile";
    }

    //Редактирование профиля пользователя
    @PostMapping("/editprofile")
    public String saveChange(
            @AuthenticationPrincipal User user,
            Model model,
            @RequestParam(required = true) String username,
            @RequestParam(required = true) String password,
            @RequestParam(required = false) String firstname,
            @RequestParam(required = false) String surname,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String faculty
            ) {
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
            return "editProfile";
        } else {
            userService.saveChange(username, password, firstname, surname, course, faculty, user);
            return "redirect:/";
        }
    }

    //Получение списка всех доступных для прохождения тестов
    @GetMapping("/getTestsList")
    public String getTestsList(Model model) {
        List<Test> all = testRepo.findAll();
        Set<Test> collect = all.stream().filter(Test::isActive).collect(Collectors.toSet());
        model.addAttribute("tests", collect);
        return "testsList";
    }

    //Результаты всех тестов пройденных пользователем
    @GetMapping("/resultsTests")
    public String getResultsTests(
        @AuthenticationPrincipal User user,
        Model model
    ) {
        Map<Test, Integer> testList = new HashMap<>();
        for (Long l : user.getResultTests().keySet()) {
            Test t = testRepo.findById(l).orElse(null);
            if (t != null) {
                testList.put(t, user.getResultTests().get(l));
            }
        }
        model.addAttribute("tests", testList);
        return "resultsMyTests";
    }

}
