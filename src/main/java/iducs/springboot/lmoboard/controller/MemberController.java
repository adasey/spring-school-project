package iducs.springboot.lmoboard.controller;

import iducs.springboot.lmoboard.domain.Member;
import iducs.springboot.lmoboard.domain.PageRequestDTO;
import iducs.springboot.lmoboard.domain.Status;
import iducs.springboot.lmoboard.service.MemberLoginService;
import iducs.springboot.lmoboard.service.MemberPageService;
import iducs.springboot.lmoboard.service.MemberService;
import iducs.springboot.lmoboard.service.SessionOutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

// 모든 페이지는 controll을 통해 접근
@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final MemberPageService memberPageService;
    private final MemberLoginService memberLoginService;
    private final SessionOutService sessionOutService;

    @GetMapping("/login")
    public String getLoginForm(Model model, HttpSession session) {
        session.removeAttribute("isSuccess");
        model.addAttribute("member", Member.builder().build());
        return "members/login";
    }

    @PostMapping("/login")
    public String postLogin(@ModelAttribute("member") Member member, HttpServletRequest request) {
        Member dto = null;
        HttpSession session = request.getSession();
        sessionOutService.setSession(session);

        if ((dto = memberLoginService.loginByEmail(member)) != null) {
            session.setAttribute("login", dto);
            session.setAttribute("isreject", false);

            if (dto.getStatus() == Status.REJECT) {
                session.removeAttribute("login");
                session.setAttribute("isreject", true);
                return "/index";
            }

            log.info("login test log : {}", session.getAttribute("login"));

            return "redirect:/index";
        }
        else {
            session.setAttribute("isSuccess", false);
            return "members/login";
        }
    }

    @GetMapping("/logout")
    public String getLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/index";
    }

    @GetMapping("/registerForm")
    public String getRegform(Model model) {
        model.addAttribute("member", Member.builder().build());
        model.addAttribute("Status", Status.values());
        return "members/registerForm";
    }

    @PostMapping("")
    public String postMember(@ModelAttribute("member") Member member, Model model, HttpSession session) {
        memberService.create(member);
        model.addAttribute("member", member);

        if (session.getAttribute("login") == null) {
            return "index";
        }
        return "redirect:/members";
    }

    @GetMapping("")
    public String getMembers(PageRequestDTO pageRequestDTO, HttpSession session, Model model) {
        model.addAttribute("list", memberPageService.readListBy(pageRequestDTO));
        return "members/lists";
    }

    @GetMapping("/{idx}")
    public String getMember(@PathVariable("idx") Long seq, Model model) {
        Member member = memberService.readById(seq);

        if (member == null) {
            return "members/lists";
        }
        model.addAttribute("member", member);
        return "members/info";
    }

    @GetMapping("/{idx}/updateForm")
    public String getUpform(@PathVariable("idx") Long seq, Model model) {
        Member member = memberService.readById(seq);
        model.addAttribute("member", member);
        return "members/updateForm";
    }

    @PutMapping("/{idx}")
    public String putMember(@ModelAttribute("member") Member member, Model model) {
        Member dto = memberService.readById(member.getSeq());

        if (!dto.getStatus().equals(member.getStatus())) {
            if (member.getStatus() == Status.REJECT) {
                memberService.rejectUserBoard(member);
            } else {
                memberService.memberUserBoard(member);
            }
        }

        memberService.update(member);

        model.addAttribute("member", member);
        return "redirect:/members";
    }

    @DeleteMapping("/{idx}")
    public String delMember(@ModelAttribute("idx") Long seq, HttpSession session) {
        memberService.delete(memberService.readById(seq));
        Member member = (Member) session.getAttribute("login");

        if (seq.equals(member.getSeq())){
            session.invalidate();
            return "redirect:/index";
        }
        return "redirect:/members";
    }
}