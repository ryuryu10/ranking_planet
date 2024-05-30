package com.univ.rankingplanet.board;

import com.univ.rankingplanet.comment.Comment;
import com.univ.rankingplanet.comment.CommentRepository;
import com.univ.rankingplanet.login.UserCreateForm;
import com.univ.rankingplanet.vote.Vote;
import com.univ.rankingplanet.vote.VoteRecord;
import com.univ.rankingplanet.vote.VoteRecordRepository;
import com.univ.rankingplanet.vote.VoteRepository;
import com.univ.rankingplanet.vote.VoteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Controller
public class BoardController {
    private final BoardService boardService;
    private final VoteService voteService;
    private final VoteRecordRepository voteRecordRepository;
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final VoteRepository voteRepository;
    private final HttpSession httpSession;

    @GetMapping("/board/search")
    public String searchBoard(@RequestParam String keyword, Pageable pageable, Model model) {
        List<Board> searchBoardListResult = boardRepository.findByBoardTitleContainingOrCategoryContaining(keyword, keyword, pageable);
        model.addAttribute("boardList", searchBoardListResult);
        return "board/search_board_list";
    }

    @GetMapping(value = "/board/detail/{id}")
    public String boardDetail(@PathVariable Long id, Model model, Authentication authentication){
        Optional<Board> optionalBoard = boardService.getBoardDetail(id);
        Board board = new Board();
        if (optionalBoard.isPresent()) {
            boardService.incrementViewCount(id); // 조회수 증가
            optionalBoard = boardService.getBoardDetail(id);
            if (optionalBoard.isPresent()) {
                board = optionalBoard.get();
                model.addAttribute("board", board);
            } else {
                System.out.println("존재하지 않는 게시판");
            }
        } else {
            System.out.println("존재하지 않는 게시판");
        }
        List<Vote> voteList = voteService.getAllVotes(id); // Vote 엔티티 리스트 가져오기
        model.addAttribute("voteList", voteList); // 모델에 엔티티 리스트 추가

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal(); // 유저정보

            model.addAttribute("member", userDetails.getUsername());
            List<VoteRecord> voteNumbersByUserIdAndBoardId = voteRecordRepository.findVoteNumbersByUserIdAndBoardId(
                    userDetails.getUsername(), board.getId());
            if(voteNumbersByUserIdAndBoardId.size() != 0){
                model.addAttribute("votedNumber", voteNumbersByUserIdAndBoardId.get(0).getVoteNumber());
            }
        }
        List<Comment> commentList = commentRepository.findAllByBoardId(board.getId());
        model.addAttribute("commentList", commentList);

        if(board.getVoteProgress() == 2){ // 투표가 끝난 경우의 페이지 리턴
            List<Vote> votesWithMaxCount = voteService.getVotesWithMaxCountByBoardId(id);
            model.addAttribute("votesWithMaxCount", votesWithMaxCount);
            return "board/board_vote_end";
        }
        return "board/board_detail";
    }

    @GetMapping(value = "/board/update/{boardId}")
    public String boardUpdate(@PathVariable Long boardId, Authentication authentication, Model model) {
        Optional<Board> optionalBoard = boardService.getBoardDetail(boardId);
        Board board = new Board();
        if (optionalBoard.isPresent()) {
            optionalBoard = boardService.getBoardDetail(boardId);
            if (optionalBoard.isPresent()) {
                board = optionalBoard.get();
                model.addAttribute("board", board);
            } else {
                System.out.println("존재하지 않는 게시판입니다.");
            }
        } else {
            System.out.println("존재하지 않는 게시판입니다.");
        }
        List<Vote> voteList = voteService.getAllVotes(boardId); // Vote 엔티티 리스트 가져오기
        model.addAttribute("voteList", voteList); // 모델에 엔티티 리스트 추가

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDateTime = board.getVoteEnd().format(formatter);
        model.addAttribute("formattedVoteEnd", formattedDateTime);
        model.addAttribute("voteEnd", formattedDateTime);

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal(); // 유저정보

            model.addAttribute("member", userDetails.getUsername());
            List<VoteRecord> voteNumbersByUserIdAndBoardId = voteRecordRepository.findVoteNumbersByUserIdAndBoardId(
                    userDetails.getUsername(), board.getId());
            if(voteNumbersByUserIdAndBoardId.size() != 0){
                model.addAttribute("votedNumber", voteNumbersByUserIdAndBoardId.get(0).getVoteNumber());
            }
        }

        return "board/board_update";
    }

    @PostMapping("/board/update/{boardId}")
    public ResponseEntity<Long> updateBoard(@Valid @RequestBody BoardCreateForm boardCreateForm, @PathVariable Long boardId ,BindingResult bindingResult,Authentication authentication){
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal(); // 유저정보
        Board board = boardService.updateBoard(boardId, boardCreateForm.getBoardTitle(), boardCreateForm.getBoardContent(), boardCreateForm.getVoteFlag(), boardCreateForm.getCategory(), userDetails.getUsername()
                , boardCreateForm.getVoteEnd(), boardCreateForm.getVoteTitle());

        return ResponseEntity.ok(boardId); // 생성된 게시판 ID를 응답으로 반환
    }

    @GetMapping(value = "/board/list/{category}")
    public String boardList(@PathVariable String category, @RequestParam(name="criteria",defaultValue="all") String criteria, Model model){
//        List<Board> boardList = boardService.getBoardListByCategory(category);
        System.out.println("1" + category);
        System.out.println("2" + criteria);
        List<Board> boardList = null;
        boardList = boardService.getBoardsByCategoryAndOrderByCriteria(criteria, category);
        model.addAttribute("category", category);
        model.addAttribute("criteria", criteria);
        model.addAttribute("boardList", boardList);
        return "board/category_board_list";
    }

    @GetMapping(value = "/board/create")
    public String board(HttpServletResponse response, HttpServletRequest request, Model model) {
        model.addAttribute("boardCreateForm", new BoardCreateForm());
        return "board/create_board";
    }

    @PostMapping("/board/create")
    public ResponseEntity<Long> boardCreate(@Valid @RequestBody BoardCreateForm boardCreateForm, BindingResult bindingResult,Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal(); // 유저정보
        Board board = boardService.createBoard(boardCreateForm.getBoardTitle(), boardCreateForm.getBoardContent(), boardCreateForm.getVoteFlag(), boardCreateForm.getCategory(), userDetails.getUsername()
                , boardCreateForm.getVoteEnd(), boardCreateForm.getVoteTitle());
        return ResponseEntity.ok(board.getId()); // 생성된 게시판 ID를 응답으로 반환
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<String> likeBoard(@PathVariable Long id, Authentication authentication) {
        // 게시글에 대한 좋아요 수 증가 처리
        UserDetails userDetails = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            userDetails = (UserDetails) authentication.getPrincipal();
        }

        boolean success = boardService.likeBoard(id, userDetails.getUsername());
        if (success) {
            return ResponseEntity.ok().body("좋아요를 누르셨습니다.");
        } else {
            return ResponseEntity.ok().body("좋아요를 취소하셨습니다.");
        }
    }

    @PostMapping("/board/saveVoteItems")
    public ResponseEntity<?> saveVoteItems(
            @RequestParam("boardId") Long boardId,
            @RequestParam("texts") List<String> texts,
            @RequestParam("images") List<MultipartFile> uploadFile) {

        // 이미지 및 텍스트 처리 로직 구현
        for (int i = 0; i < texts.size(); i++) {
            String text = texts.get(i);
            MultipartFile image = (uploadFile.size() > i) ? uploadFile.get(i) : null;

            // 이미지 저장 처리
            if (image != null && !image.isEmpty()) {
                try {
                    // 이미지를 저장할 경로 설정
                    String uploadDir = "/Users/pms327/Downloads/image"; // 이미지를 저장할 실제 경로로 변경해야 합니다.

                    // 이미지 파일의 원래 파일 이름 가져오기
                    String originalFileName = image.getOriginalFilename();

                    // 이미지 파일의 확장자 추출
                    String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

                    // 새로운 파일 이름 생성
                    String newFileName = UUID.randomUUID().toString() + fileExtension;

                    // 이미지 파일 저장
                    Path filePath = Paths.get(uploadDir + File.separator + newFileName);
                    Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    voteService.saveVote(boardId, i + 1, text,"/board/display?fileName=" + filePath.toString(), originalFileName, 0L);

                } catch (IOException e) {
                    // 이미지 저장 중 오류 발생 시 예외 처리
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 저장 중 오류 발생");
                }
            }
        }

        return ResponseEntity.ok("투표 항목 저장 완료");
    }

    @PostMapping("/board/updateVoteItems")
    public ResponseEntity<?> updateVoteItems(
            @RequestParam("voteId") List<Long> voteId,
            @RequestParam("boardId") Long boardId,
            @RequestParam("texts") List<String> texts,
            @RequestParam("images") List<MultipartFile> uploadFile) {

        // 이미지 및 텍스트 처리 로직 구현
        for (int i = 0; i < texts.size(); i++) {
            String text = texts.get(i);
            MultipartFile image = (uploadFile.size() > i) ? uploadFile.get(i) : null;
            // 이미지 저장 처리
            if(voteId.get(i) != -1){
                if (image != null && !image.isEmpty()) {
                    try {
                        // 이미지를 저장할 경로 설정
                        String uploadDir = "/Users/pms327/Downloads/image"; // 이미지를 저장할 실제 경로로 변경해야 합니다.

                        // 이미지 파일의 원래 파일 이름 가져오기
                        String originalFileName = image.getOriginalFilename();

                        // 이미지 파일의 확장자 추출
                        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

                        // 새로운 파일 이름 생성
                        String newFileName = UUID.randomUUID().toString() + fileExtension;

                        // 이미지 파일 저장
                        Path filePath = Paths.get(uploadDir + File.separator + newFileName);
                        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        voteService.updateVote(boardId, voteId.get(i), text,"/board/display?fileName=" + filePath.toString(), originalFileName);

                    } catch (IOException e) {
                        // 이미지 저장 중 오류 발생 시 예외 처리
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 저장 중 오류 발생");
                    }
                }
            }
        }

        return ResponseEntity.ok("투표 항목 저장 완료");
    }

    public String deleteFile(String filename) {
        try {
            // 파일 경로를 지정합니다. 경로는 파일의 실제 위치로 변경해야 합니다.
            Path filePath = Paths.get("/Users/pms327/Downloads/image", filename);

            // 파일이 존재하는지 확인하고 삭제합니다.
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return "파일이 성공적으로 삭제되었습니다.";
            } else {
                return "파일이 존재하지 않습니다.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "파일 삭제 중 오류가 발생했습니다.";
        }
    }

    @PostMapping("/board/voteProgress/update/{boardId}")
    public void updateVoteProgress(@PathVariable Long boardId){

    }

    @DeleteMapping("/board/delete/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping(value = "/uploadTest")
    public ResponseEntity<Map<String, Object>> uploadTestPOST(MultipartFile[] uploadFile) {

        // 내가 업로드 파일을 저장할 경로
        String uploadFolder = "/Users/pms327/Downloads/image";
        SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String formatDate = sdt.format(date);

        String datePath = formatDate.replace("-", File.separator);

        File uploadPath = new File(uploadFolder, datePath);

        if (uploadPath.exists() == false) {
            uploadPath.mkdirs();
        }

        Map<String, Object> response = new HashMap<>();

        for (MultipartFile multipartFile : uploadFile) {
            String uploadFileName = multipartFile.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            uploadFileName = uuid + "_" + uploadFileName;

            File saveFile = new File(uploadPath, uploadFileName);

            try {
                multipartFile.transferTo(saveFile);
                response.put("fileName", uploadFileName);
                response.put("uuid", uuid);
                response.put("uploadPath", uploadPath.getPath());
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/board/display")
    public ResponseEntity<byte[]> showImageGET(
            @RequestParam("fileName") String fileName
    ) {
        File file = new File(fileName);

        ResponseEntity<byte[]> result = null;

        try {

            HttpHeaders header = new HttpHeaders();

        /*
        Files.probeContentType() 해당 파일의 Content 타입을 인식(image, text/plain ...)
        없으면 null 반환

        file.toPath() -> file 객체를 Path객체로 변환

        */
            header.add("Content-type", Files.probeContentType(file.toPath()));

            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
