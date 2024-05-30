package com.univ.rankingplanet.main;

import com.univ.rankingplanet.board.Board;
import com.univ.rankingplanet.board.BoardService;
import com.univ.rankingplanet.login.UserCreateForm;
import com.univ.rankingplanet.login.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class MainController {
    private final UserService userService;
    private final BoardService boardService;

    private final HttpSession httpSession;

    @RequestMapping(value = "/")
    public String index(HttpServletResponse response, HttpServletRequest request) {

        return "redirect:/home.do";
    }

    /**
     * 로그인시 메인 홈페이지
     **/
    @RequestMapping(value = "/home.do")
    public String home(@RequestParam(name="criteria",defaultValue="all") String criteria, Authentication authentication, Model model, HttpServletResponse response, HttpServletRequest request) {
        /*Default Setting*/
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal(); // 유저정보
            long sessionTimeoutInSeconds = httpSession.getMaxInactiveInterval(); // 세션시간

            model.addAttribute("member", userDetails.getUsername());
            model.addAttribute("sessionTimeoutInSeconds", sessionTimeoutInSeconds);
        }
        List<Board> boardList = boardService.getBoardListOrderByCriteria(criteria);
        for(Board board : boardList){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = board.getCreatedAt().format(formatter);
            model.addAttribute("formattedCreatedAt", formattedDate);
        }
        model.addAttribute("boardList", boardList);



        return "main/home";
    }

    public HashMap<String,Object> formatMapRequest(HttpServletRequest request) {
        HashMap<String, Object> map = new HashMap<String, Object>();

        Enumeration<String> enumber = request.getParameterNames();

        while (enumber.hasMoreElements()) {
            String key = enumber.nextElement().toString();
            String value = request.getParameter(key);

            map.put(key, value);
        }

        return map;
    }
}