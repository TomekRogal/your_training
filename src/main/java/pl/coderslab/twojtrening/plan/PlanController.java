package pl.coderslab.twojtrening.plan;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.coderslab.twojtrening.user.CurrentUser;

import javax.validation.Valid;

@Controller
public class PlanController {
    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @RequestMapping("/plan/all")
    public String findAll(@AuthenticationPrincipal CurrentUser customUser, Model model) {
        model.addAttribute("plans", planService.findAllPlansFromUser(customUser.getUser()));
        return "plan/all";
    }

    @RequestMapping("/plan/delete/{id}")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal CurrentUser customUser) {
        planService.deletePlanById(id, customUser.getUser());
        return "redirect:/plan/all";
    }

    @GetMapping("/plan/add")
    public String add(@AuthenticationPrincipal CurrentUser customUser, Model model) {
        Plan plan = new Plan();
        plan.setUser(customUser.getUser());
        model.addAttribute("plan", plan);
        return "plan/add";
    }

    @PostMapping("/plan/add")
    public String addProcess(@Valid Plan plan, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "plan/add";
        }
        planService.addPlan(plan);
        return "redirect:/plan/all";
    }

    @GetMapping("/plan/edit/{id}")
    public String edit(@PathVariable Long id, Model model, @AuthenticationPrincipal CurrentUser customUser) {
        model.addAttribute("plan", planService.getSinglePlanById(id, customUser.getUser()));
        return "plan/edit";

    }

    @PostMapping("/plan/edit/{id}")
    public String editProcess(@Valid Plan plan, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "plan/edit";
        }
        planService.addPlan(plan);
        return "redirect:/plan/all";
    }

    @GetMapping("/plan/show/{id}")
    public String show(@PathVariable Long id, Model model, @AuthenticationPrincipal CurrentUser customUser) {
        model.addAttribute("plan", planService.getSinglePlanById(id, customUser.getUser()));
        model.addAttribute("trainingsList", planService.getSinglePlanWithTrainingsAndExercisesById(id, customUser.getUser()));
        return "plan/show";
    }
}
