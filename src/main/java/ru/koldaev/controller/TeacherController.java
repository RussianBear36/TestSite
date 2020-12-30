package ru.koldaev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.koldaev.entity.Test;
import ru.koldaev.entity.User;
import ru.koldaev.repo.TestRepo;
import ru.koldaev.repo.UserRepo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private TestRepo testRepo;
    @Autowired
    private UserRepo userRepo;

    @GetMapping("/myCreatingTests")
    public String getMyCreatingTests(
            @AuthenticationPrincipal User user,
            Model model
    ) {
        List<Test> byAuthorId = testRepo.findByAuthorId(user.getId());
        model.addAttribute("myTests", byAuthorId);
        return "readyTestsList";
    }

    @GetMapping("/resultTest/{id}")
    public String getResultMyTest(
            @PathVariable Long id,
            Model model
    ) {
        Test byId = testRepo.findById(id).orElse(null);
        Map<String, Integer> result = new HashMap<>();
        for (Long l : byId.gerResultCurrentTest().keySet()) {
            User user = userRepo.findById(l).orElse(null);
            String username = user.getFirstname() + " " + user.getSurname();
            result.put(username, byId.gerResultCurrentTest().get(l));
        }
        model.addAttribute("resultTest", result);
        model.addAttribute("testName", byId.getTestName());
        return "resultsTeacherTests";
    }

    @PostMapping("/deleteTest/{id}")
    public String deleteTest(
            @PathVariable Long id,
            Model model
    ) {
        Test test = testRepo.findById(id).orElse(null);
        testRepo.delete(test);
        return "redirect:/teacher/myCreatingTests";
    }

}
