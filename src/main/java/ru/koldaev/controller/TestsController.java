package ru.koldaev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.koldaev.entity.Question;
import ru.koldaev.entity.Test;
import ru.koldaev.entity.User;
import ru.koldaev.repo.QuestionRepo;
import ru.koldaev.repo.TestRepo;
import ru.koldaev.repo.UserRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class TestsController {
    @Autowired
    private TestRepo testRepo;
    @Autowired
    private QuestionRepo questionRepo;
    @Autowired
    private UserRepo userRepo;

    @GetMapping("/teacher/createTest")
    public String getCreatePage() {
        return "createTest";
    }

    @PostMapping("/teacher/createTest")
    public String getQuestionPage(
            @AuthenticationPrincipal User user,
            Model model,
            @RequestParam String testName,
            @RequestParam Integer countVariants
    ) {
        Test test = new Test();
        test.setTestName(testName);
        test.setCountVariants(countVariants);
        test.setAuthorId(user.getId());
        test.setAuthorName(user.getFirstname() + " " + user.getSurname());
        testRepo.save(test);
        model.addAttribute("id", test.getId());
        return "redirect:/teacher/createQuestion/" + test.getId();
    }

    @GetMapping("/teacher/createQuestion/{id}")
    public String getNewQuestionPage(
            Model model,
            @PathVariable Long id
    ) {
        Test test = testRepo.findById(id).orElse(null);
        model.addAttribute("currentTest", test);
        model.addAttribute("countVariants", test.getCountVariants());
        return "createQuestion";
    }

    //id - id теста
    @PostMapping("/teacher/createQuestion/{id}")
    public String addQuestion(
            Model model,
            @RequestParam Map<String, String> questionForm,
            @PathVariable Long id
            ) {

        Test test = testRepo.findById(id).orElse(null);
        Question question = new Question();
        question.setQuestion(questionForm.get("questionName"));

        for (int i = 1; i <= test.getCountVariants(); i++) {
            String answer = questionForm.get("variant" + i);
            question.getVariantsAnswer().add(answer);
        }
        int rightVariant = Integer.parseInt(questionForm.get("rightVariant"));
        question.setAnswer(questionForm.get("variant" + rightVariant));
        questionRepo.save(question);
        test.getQuestions().add(question.getId());
        testRepo.save(test);

        return "redirect:/teacher/createQuestion/" + test.getId();
    }

    @PostMapping("/teacher/saveTest/{id}")
    public String saveTest(
            Model model,
            @RequestParam Map<String, String> questionForm,
            @PathVariable Long id
    ) {
        Test test = testRepo.findById(id).orElse(null);
        Question question = new Question();
        question.setQuestion(questionForm.get("questionName"));

        for (int i = 1; i <= test.getCountVariants(); i++) {
            String answer = questionForm.get("variant" + i);
            question.getVariantsAnswer().add(answer);
        }

        int rightVariant = Integer.parseInt(questionForm.get("rightVariant"));
        question.setAnswer(questionForm.get("variant" + rightVariant));

        questionRepo.save(question);
        test.getQuestions().add(question.getId());
        test.setActive(true);
        testRepo.save(test);

        return "redirect:/";
    }

    //Получение для прохождения теста, с идентификатором id
    @GetMapping("/passTest/{id}")
    public String passTest(
            @AuthenticationPrincipal User user,
            Model model,
            @PathVariable Long id
    ) {
        Test test = testRepo.findById(id).orElse(null);
        model.addAttribute("test", test);
        List<Question> questions = new ArrayList<>();
        for(Long l : test.getQuestions()) {
            questions.add(questionRepo.findById(l).orElse(null));
        }
        model.addAttribute("questions", questions);
        return "passTest";
    }

    //Завершить прохождение теста, с идентификатором id
    @PostMapping("/passTest/{id}")
    public String getResultTest(
            @AuthenticationPrincipal User user,
            Model model,
            @PathVariable Long id,
            @RequestParam Map<String, String> testForm
    ) {
        Test test = testRepo.findById(id).orElse(null);
        //Получаем id всех вопросов в тесте
        List<Long> questionsId = test.getQuestions();
        int trueVariant = 0;
        int falseVariant = 0;
        //Проходим по всем вопросам
        for(Long l : questionsId) {
            //Если в тесте есть такой вопрос (проверка на всякий случай).
            //имя формы ввода в тесте - id вопроса (в radio - 1 имя на все варианты, и разные значения (value))
            if (testForm.containsKey(String.valueOf(l))) {
                //Получаем верный ответ на текущий вопрос
                String answer = questionRepo.findById(l).orElse(null).getAnswer();
                //Получаем из формы прохождения теста value (т.е. ответ) текущего вопроса и сравниваем с верным ответом
                if (testForm.get(String.valueOf(l)).equals(answer)) {
                    trueVariant++;
                } else {
                    falseVariant++;
                }
            } else
            {
                falseVariant++;
            }
        }
        double res = ((double) trueVariant / (double) test.getQuestions().size());
        user.getResultTests().put(test.getId(), (int) (res * 100));
        test.gerResultCurrentTest().put(user.getId(), (int) (res * 100));
        testRepo.save(test);
        userRepo.save(user);
        model.addAttribute("trueV", trueVariant);
        model.addAttribute("falseV", falseVariant);
        model.addAttribute("ball", user.getResultTests().get(test.getId()));
        model.addAttribute("testName", test.getTestName());
        return "resultPassTest";
    }
}
