package com.todayworker.springboot.web.service;

import com.todayworker.springboot.domain.board.exception.BoardErrorCode;
import com.todayworker.springboot.domain.board.exception.BoardException;
import com.todayworker.springboot.domain.board.jpa.entity.CommentEntity;
import com.todayworker.springboot.domain.board.jpa.repository.BoardJpaRepository;
import com.todayworker.springboot.domain.board.jpa.repository.CommentJpaRepository;
import com.todayworker.springboot.domain.board.vo.ReplyVO;
import com.todayworker.springboot.utils.DateUtils;
import com.todayworker.springboot.utils.UuidUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
@RequiredArgsConstructor
public class CommentService implements CommentServiceIF {

    private final BoardJpaRepository boardJpaRepository;
    private final CommentJpaRepository commentJpaRepository;

    @Override
    @Transactional
    public boolean registerReply(ReplyVO vo) {
        if (vo.getBno() == null) {
            throw new BoardException(
                BoardErrorCode.of(HttpStatus.BAD_REQUEST, BoardErrorCode.INVALID_BOARD,
                    "게시글 ID(bno)가 Null 일 수는 없습니다."));
        }

        vo.prePersist();  //parentId 초기화
        vo.setRno(UuidUtils.generateNoDashUUID());
        vo.setRegDate(DateUtils.getDatetimeString());

        boardJpaRepository.findBoardEntityByBno(vo.getBno()).ifPresent(it -> {
            it.modifyCommentEntitiesFromReply(vo);
            boardJpaRepository.save(it);
        });

        return true;
    }

    @Override
    public boolean updateReply(ReplyVO vo) {
        if (vo.getBno() == null) {
            throw new BoardException(
                BoardErrorCode.of(HttpStatus.BAD_REQUEST, BoardErrorCode.INVALID_BOARD,
                    "게시글 ID(bno)가 Null 일 수는 없습니다."));
        }

        if (vo.getRno() == null) {
            throw new BoardException(
                BoardErrorCode.of(HttpStatus.BAD_REQUEST, BoardErrorCode.INVALID_BOARD,
                    "댓글 ID(rno)가 Null 일 수는 없습니다."));
        }

        CommentEntity updateComment = commentJpaRepository.findCommentEntityByRno(vo.getRno())
            .get();
        updateComment.modifyComment(vo);
        commentJpaRepository.save(updateComment);

        return true;
    }

    @Override
    public boolean deleteReply(ReplyVO vo) {
        if (vo.getBno() == null) {
            throw new IllegalArgumentException("유효하지 않은 댓글 삭제 요청 [bno=null]");
        }

        if (vo.getRno() == null) {
            throw new IllegalArgumentException("유효하지 않은 댓글 삭제 요청 [rno=null]");
        }

        CommentEntity comment = commentJpaRepository.findCommentEntityByRno(vo.getRno())
            .orElseThrow(() -> new BoardException(
                BoardErrorCode.of(HttpStatus.NOT_FOUND, BoardErrorCode.NON_EXIST_REPLY,
                    vo.getRno())));
        
        comment.changeStatusToDeleted();

        commentJpaRepository.save(comment);
        return true;
    }

}
