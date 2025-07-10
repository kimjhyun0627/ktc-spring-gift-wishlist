package gift.controller.admin;

import static gift.util.RoleUtil.extractRole;

import gift.dto.member.MemberForm;
import gift.entity.member.Member;
import gift.entity.member.value.Role;
import gift.exception.custom.MemberNotFoundException;
import gift.service.member.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/members")
public class AdminMemberController {

    private final MemberService memberService;

    public AdminMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public String list(HttpServletRequest req, Model model) {
        List<Member> members = memberService.getAllMembers(extractRole(req));
        model.addAttribute("members", members);
        return "admin/member_list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm(null, "", "", Role.USER));
        return "admin/member_form";
    }

    @PostMapping("/new")
    public String create(
            @Valid @ModelAttribute("memberForm") MemberForm form,
            BindingResult br,
            RedirectAttributes ra,
            HttpServletRequest req
    ) {
        if (br.hasErrors()) {
            return "admin/member_form";
        }
        Member created = memberService.createMember(
                form.email(), form.password(), form.role(), extractRole(req)
        );
        ra.addFlashAttribute("info", "회원이 등록되었습니다: " + created.getEmail());
        return "redirect:/admin/members";
    }

    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            Model model,
            HttpServletRequest req
    ) {
        Member m = memberService.getMemberById(id, extractRole(req))
                .orElseThrow(() -> new MemberNotFoundException(id.toString()));
        model.addAttribute("memberForm", new MemberForm(
                m.getId().id(), m.getEmail().email(), m.getPassword().password(), m.getRole()
        ));
        return "admin/member_form";
    }

    @PutMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("memberForm") MemberForm form,
            BindingResult br,
            RedirectAttributes ra,
            HttpServletRequest req
    ) {
        if (br.hasErrors()) {
            return "admin/member_form";
        }
        Member updated = memberService.updateMember(
                id, form.email(), form.password(), form.role(), extractRole(req)
        );
        ra.addFlashAttribute("info", "회원 정보가 수정되었습니다: " + updated.getEmail());
        return "redirect:/admin/members";
    }

    @DeleteMapping("/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes ra,
            HttpServletRequest req
    ) {
        memberService.deleteMember(id, extractRole(req));
        ra.addFlashAttribute("info", "회원이 삭제되었습니다. ID=" + id);
        return "redirect:/admin/members";
    }
}
