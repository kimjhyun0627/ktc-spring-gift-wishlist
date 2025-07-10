package gift.controller.admin;

import gift.dto.member.AuthRequest;
import gift.dto.member.AuthResponse;
import gift.service.member.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminAuthController {

    private final MemberService authService;

    public AdminAuthController(MemberService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginForm(Model model,
            @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("authRequest", new AuthRequest("", ""));
        if (error != null) {
            model.addAttribute("error", "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return "admin/login_form";
    }

    @PostMapping("/login")
    public String loginSubmit(
            @Valid @ModelAttribute("authRequest") AuthRequest form,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "admin/login_form";
        }

        try {
            AuthResponse authResp = authService.login(form.email(), form.password());
            request.setAttribute("AUTH_TOKEN", authResp.token());
            return "redirect:/admin/dashboard";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/login_form";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.setAttribute("LOGOUT", true);
        return "redirect:/admin/login";
    }

}
