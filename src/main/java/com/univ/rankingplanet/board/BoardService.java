package com.univ.rankingplanet.board;

import com.univ.rankingplanet.like.LikeRecord;
import com.univ.rankingplanet.like.LikeRecordRepository;
import com.univ.rankingplanet.like.LikeRecordService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final LikeRecordRepository likeRecordRepository;
    private final LikeRecordService likeRecordService;

//    @Transactional
    public Board createBoard(String boardTitle, String boardContent, boolean voteFlag, String category, String userId, LocalDateTime voteEnd, String voteTitle){
        Board board = new Board();
        board.setBoardTitle(boardTitle);
        board.setBoardContent(boardContent);
        board.setVoteFlag(voteFlag);
        if(voteFlag == true){ // 게시판 생성시 투표도 생성했을 경우
            board.setVoteProgress(1); // 1 == 투표 진행 중
        }else {
            board.setVoteProgress(0); // 0 == 투표 없음
        }
        board.setCategory(category);
        board.setCreatedAt(LocalDateTime.now());
        board.setUserId(userId);
        board.setVoteStart(LocalDateTime.now());
        board.setVoteEnd(voteEnd);
        board.setVoteTitle(voteTitle);
        this.boardRepository.save(board);
        return board;
    }

    public Optional<Board> getBoardDetail(Long id){
        return this.boardRepository.findById(id);
    }

    // 카테고리 파라미터로 넣을 것
    public List<Board> getBoardList(){
        return this.boardRepository.findAll();
    }

    public List<Board> getBoardListOrderByCriteria(String criteria) {
        return this.boardRepository.findAllOrderByCriteria(criteria);
    }

    public void incrementViewCount(Long id) {
        boardRepository.incrementViewCount(id);
    }

    @Transactional
    public boolean likeBoard(Long id, String userId) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (optionalBoard.isPresent()) {
            Board board = optionalBoard.get();
            if (!likeRecordService.isLikedByUser(userId, id)) { // 이미 좋아요를 누른 경우에는 처리하지 않음
                board.setLikeCount(board.getLikeCount() + 1);
                boardRepository.save(board);
                LikeRecord likeRecord = new LikeRecord();
                likeRecord.setBoardId(id);
                likeRecord.setUserId(userId);
                likeRecordRepository.save(likeRecord);
                return true;
            }else{
                board.setLikeCount(board.getLikeCount() - 1);
                boardRepository.save(board);
                likeRecordRepository.deleteByUserIdAndBoardId(userId, id);
                return false;
            }
        }
        return false;
    }

    public int getLikeCount(Long boardId) {
        return boardRepository.findLikeCountById(boardId);
    }

    public Board updateBoard(Long boardId, String boardTitle, String boardContent, boolean voteFlag, String category, String userId, LocalDateTime voteEnd, String voteTitle) {
        Optional<Board> optBoard = boardRepository.findById(boardId);
        Board board = optBoard.get();
        board.setBoardTitle(boardTitle);
        board.setBoardContent(boardContent);
        board.setVoteFlag(voteFlag);
        board.setCategory(category);
        board.setCreatedAt(LocalDateTime.now());
        board.setUserId(userId);
        board.setVoteStart(LocalDateTime.now());
        board.setVoteEnd(voteEnd);
        board.setVoteTitle(voteTitle);

        // 게시글 업데이트
        boardRepository.save(board);
        return board;
    }

    public Board updateBoardVoteProgress(Long boardId){
        Optional<Board> optBoard = boardRepository.findById(boardId);
        Board board = optBoard.get();
        board.setVoteProgress(2);
        boardRepository.save(board);
        return board;
    }

    @Transactional
    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }

    public List<Board> getBoardsByCategoryAndOrderByCriteria(String criteria, String category) {
        if(criteria == "all"){
            return boardRepository.findAllByCategory(category);
        }
        return boardRepository.findBoardsByCategoryAndOrderByCriteria(criteria, category);
    }
}
