package iducs.springboot.lmoboard.controller;

import iducs.springboot.lmoboard.domain.Board;
import iducs.springboot.lmoboard.domain.BoardStatus;
import iducs.springboot.lmoboard.domain.PageRequestDTO;
import iducs.springboot.lmoboard.service.BoardPageService;
import iducs.springboot.lmoboard.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final BoardPageService boardPageService;

    @GetMapping("")
    public String getBoards(PageRequestDTO pageRequestDTO, HttpSession session, Model model) {
        if (session.getAttribute("login") != null) {
            model.addAttribute("list", boardPageService.getList(pageRequestDTO));
            return "boards/lists";
        }
        else {
            return "index";
        }
    }

    @GetMapping("/registerForm")
    public String getRegForm(HttpSession session, Model model) {
        if (session.getAttribute("login") != null) {
            model.addAttribute("dto", Board.builder().build());
            model.addAttribute("Status", BoardStatus.values());
            return "boards/registerForm";
        }
        else {
            return "index";
        }
    }

    @PostMapping
    public String postRegForm(@ModelAttribute("board") Board board) {
        boardService.register(board);
        return "redirect:/boards";
    }

    @GetMapping("/{bor_id}")
    public String getInfo(@PathVariable("bor_id") Long bor_id, HttpSession session, Model model) {
        if (session.getAttribute("login") != null) {
            Board board = boardService.getById(bor_id);
            if (board == null) {
                return "boards/lists";
            }
            model.addAttribute("dto", board);
            return "boards/info";
        }
        else {
            return "index";
        }
    }

    @GetMapping("/{bor_id}/updateForm")
    public String getUpform(@PathVariable("bor_id") Long bor_id, HttpSession session, Model model) {
        if (session.getAttribute("login") != null) {
            Board board = boardService.getById(bor_id);
            model.addAttribute("dto", board);
            return "boards/updateForm";
        }
        else {
            return "index";
        }
    }

    @PutMapping("/{bor_id}")
    public String putBoard(@ModelAttribute("board") Board board, Model model) {
        boardService.updateBoard(board);
        model.addAttribute("dto", board);
        return "redirect:/boards";
    }

    @DeleteMapping("/{idx}")
    public String delBoard(@ModelAttribute("idx") Long bor_id, HttpSession session) {
        if (session.getAttribute("login") != null) {
            boardService.deleteWithRepliesById(bor_id);
            return "redirect:/boards";
        }
        else {
            return "index";
        }
    }
}
