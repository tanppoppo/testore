package com.tanppoppo.testore.testore.board.service;

import com.tanppoppo.testore.testore.board.dto.BoardDTO;
import com.tanppoppo.testore.testore.board.dto.CommentDTO;
import com.tanppoppo.testore.testore.board.entity.BoardEntity;
import com.tanppoppo.testore.testore.board.entity.CommentEntity;
import com.tanppoppo.testore.testore.board.repository.BoardRepository;
import com.tanppoppo.testore.testore.board.repository.CommentRepository;
import com.tanppoppo.testore.testore.common.util.BoardTypeEnum;
import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import com.tanppoppo.testore.testore.member.repository.MemberRepository;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService {

    private final BoardRepository br;
    private final MemberRepository mr;
    private final CommentRepository cr;

    /**
     * 최근 5개의 공지사항을 가져오는 메서드
     * @author dhkdtjs1541
     * @return 최근 작성된 5개의 게시글 목록
     */
    @Override
    public List<BoardDTO> getRecentNotices() {
        log.info("최근 3개의 공지사항을 가져옵니다.");
        List<BoardEntity> boardEntities = br.findTop3ByBoardTypeOrderByCreatedDateDesc(BoardTypeEnum.NOTICE);

        List<BoardDTO> items = new ArrayList<>();

        for (BoardEntity entity : boardEntities) {
            BoardDTO boardDTO = BoardDTO.builder()
                    .boardId(entity.getBoardId())
                    .title(entity.getTitle())
                    .content(entity.getContent())
                    .nickname(entity.getMember().getNickname())
                    .createdDate(entity.getCreatedDate())
                    .updatedDate(entity.getUpdateDate())
                    .build();
            items.add(boardDTO);
        }

        return items;

    }

    /**
     * 새로운 게시글을 저장하는 메서드
     * @author gyahury
     * @param boardDTO 게시글 정보가 담긴 DTO
     * @param userId 게시글 작성자의 사용자 ID
     * @throws EntityNotFoundException 회원이 존재하지 않을 경우 발생
     * @throws AccessDeniedException 관리자만 공지사항을 작성할 수 있음
     */
    @Transactional
    @Override
    public void saveBoard(BoardDTO boardDTO, Integer userId) {

        MemberEntity memberEntity = mr.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

        if ("NOTICE".equals(boardDTO.getBoardType()) && !(memberEntity.getMembershipLevel() == 99)) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        BoardEntity boardEntity = BoardEntity.builder()
                .member(memberEntity)
                .boardType(BoardTypeEnum.valueOf(boardDTO.getBoardType()))
                .title(boardDTO.getTitle())
                .content(boardDTO.getContent())
                .build();

        br.save(boardEntity);

    }

    /**
     * 게시글 상세 정보를 조회하는 메서드
     * @author dhkdtjs1541
     * @param boardId 게시글의 ID
     * @return 게시글 상세 정보가 담긴 {@link BoardDTO}
     * @throws EntityNotFoundException 해당 게시글을 찾을 수 없을 경우 예외 발생
     */
    @Override
    public BoardDTO getBoardDetail(int boardId) {
        log.info("게시글 상세 정보를 가져옵니다 : {}", boardId);

        BoardEntity boardEntity = br.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        boardEntity.setViewCount(boardEntity.getViewCount() + 1);

        BoardDTO boardDTO = BoardDTO.builder()
                .boardId(boardEntity.getBoardId())
                .title(boardEntity.getTitle())
                .content(boardEntity.getContent())
                .memberId(boardEntity.getMember().getMemberId())
                .nickname(boardEntity.getMember().getNickname())
                .viewCount(boardEntity.getViewCount())
                .createdDate(boardEntity.getCreatedDate())
                .updatedDate(boardEntity.getUpdateDate())
                .build();

        return boardDTO;
    }

    /**
     * 게시글을 수정하는 메서드
     * @author dhkdtjs1541
     * @param boardDTO 수정할 게시글 정보가 담긴 DTO
     * @param userId 현재 사용자의 ID
     * @throws EntityNotFoundException 해당 게시글을 찾을 수 없을 경우 발생
     * @throws RuntimeException 게시글 작성자만 수정할 수 있음.
     */
    @Transactional
    @Override
    public void updateBoard(BoardDTO boardDTO, Integer userId) {
        log.info("게시글을 수정합니다 : {}", boardDTO);

        BoardEntity boardEntity = br.findById(boardDTO.getBoardId())
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        if(!boardEntity.getMember().getMemberId().equals(userId)) {
            throw new AccessDeniedException("게시글 작성자만 수정할 수 있습니다.");
        }

        boardEntity.setTitle(boardDTO.getTitle());
        boardEntity.setContent(boardDTO.getContent());

        br.save(boardEntity);
    }

    /**
     * 게시글을 삭제하는 메서드
     * @author dhkdtjs1541
     * @param boardId 게시글 ID
     * @param userId 현재 사용자 ID
     * @throws EntityNotFoundException 해당 게시글을 찾을 수 없을 경우 발생
     * @throws RuntimeException 작성자가 아닌 사용자가 게시글을 삭제하려고 할 경우 예외 발생
     */
    @Transactional
    @Override
    public void deleteBoard(int boardId, Integer userId) {
        log.info("게시글을 삭제합니다 : {}", boardId);

        MemberEntity memberEntity = mr.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        BoardEntity boardEntity = br.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        if (!boardEntity.getMember().getMemberId().equals(userId) || !(memberEntity.getMembershipLevel() == 99)) {
            throw new AccessDeniedException("게시글 작성자만 삭제할 수 있습니다.");
        }

        br.delete(boardEntity);
    }

    /**
     * 자신의 게시글이거나 관리자인지 체크
     * @author gyahury
     * @param boardId 게시글 id를 가져옵니다.
     * @param user 유저 객체를 가져옵니다.
     * @throws AccessDeniedException 권한 에러를 던집니다.
     */
    @Override
    public void IsMyBoard(int boardId, AuthenticatedUser user) {
        BoardEntity boardEntity = br.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        if(!boardEntity.getMember().getMemberId().equals(memberEntity.getMemberId()) && memberEntity.getMembershipLevel() != 99){
            throw new AccessDeniedException("권한이 없습니다.");
        }

    }

    /**
     * 댓글 작성
     * @author dhkdtjs1541
     * @param commentDTO 댓글 정보가 담긴 DTO
     */
    @Transactional
    @Override
    public void createComment(CommentDTO commentDTO) {
        log.info("댓글을 작성합니다. 게시글 ID: {}, 작성자 ID: {}", commentDTO.getBoardId(), commentDTO.getMemberId());

        BoardEntity boardEntity = br.findById(commentDTO.getBoardId())
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글을 찾을 수 없습니다."));

        MemberEntity memberEntity = mr.findById(commentDTO.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));

        CommentEntity commentEntity = CommentEntity.builder()
                .content(commentDTO.getContent())
                .board(boardEntity)
                .member(memberEntity)
                .build();

        cr.save(commentEntity);

        log.info("댓글이 성공적으로 저장되었습니다. 댓글 ID: {}", commentEntity.getCommentId());
    }

    /**
     * 댓글 삭제
     * @author dhkdtjs1541
     * @param commentId 댓글 ID
     * @param userId 현재 사용자 ID
     * @throws EntityNotFoundException 해당 댓글을 찾을 수 없을 경우 발생
     * @throws AccessDeniedException 작성자가 아닌 사용자가 댓글을 삭제하려고 할 경우 예외 발생
     */
    @Transactional
    @Override
    public void deleteComment(Integer commentId, Integer userId) {
        log.info("댓글 삭제 요청. 댓글 ID: {}, 사용자 ID: {}", commentId, userId);

        CommentEntity commentEntity = cr.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글이 없습니다."));

        if (!commentEntity.getMember().getMemberId().equals(userId)) {
            log.warn("댓글 삭제 권한 없음. 댓글 작성자 ID: {}, 요청자 ID: {}", commentEntity.getMember().getMemberId(), userId);
            throw new AccessDeniedException("댓글 삭제 권한이 없습니다.");
        }

        cr.delete(commentEntity);
    }

    /**
     * 게시글에 달린 댓글 목록 조회
     * @author dhkdtjs1541
     * @param boardId 게시글 ID
     * @return 댓글 목록
     */
    @Override
    public List<CommentDTO> getCommentList(int boardId) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createdDate");
        List<CommentEntity> commentEntityList = cr.findByBoard(boardId, sort);

        List<CommentDTO> commentDTOList = new ArrayList<>();
        for (CommentEntity entity : commentEntityList) {
            CommentDTO dto = CommentDTO.builder()
                    .commentId(entity.getCommentId())
                    .boardId(entity.getBoard().getBoardId())
                    .memberId(entity.getMember().getMemberId())
                    .content(entity.getContent())
                    .createdDate(entity.getCreatedDate())
                    .updatedDate(entity.getUpdatedDate())
                    .nickname(entity.getMember().getNickname())
                    .build();
            commentDTOList.add(dto);
        }
        return commentDTOList;
    }

    /**
     * 댓글 ID로 댓글 정보를 조회
     * @author dhkdtjs1541
     * @param commentId 댓글 ID
     * @return 조회된 댓글 정보
     * @throws EntityNotFoundException 해당 댓글을 찾을 수 없을 경우 발생
     */
    @Override
    public CommentDTO getCommentById(int commentId) {

        CommentEntity commentEntity = cr.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));


        return CommentDTO.builder()
                .commentId(commentEntity.getCommentId())
                .content(commentEntity.getContent())
                .memberId(commentEntity.getMember().getMemberId()) // 작성자 ID
                .nickname(commentEntity.getMember().getNickname()) // 닉네임
                .createdDate(commentEntity.getCreatedDate())
                .updatedDate(commentEntity.getUpdatedDate())
                .build();
    }

    /**
     * 댓글 수정
     * @author dhkdtjs1541
     * @param commentDTO 수정할 댓글 정보가 담긴 DTO
     * @throws EntityNotFoundException 수정할 댓글을 찾을 수 없을 경우 발생
     * @throws AccessDeniedException 댓글 작성자만 댓글을 수정할 수 있음
     */
    @Transactional
    @Override
    public void updateComment(CommentDTO commentDTO) {

        CommentEntity commentEntity = cr.findById(commentDTO.getCommentId())
                .orElseThrow(() -> new EntityNotFoundException("수정할 댓글이 없습니다."));

        if (!commentEntity.getMember().getMemberId().equals(commentDTO.getMemberId())) {
            throw new AccessDeniedException("댓글 수정 권한이 없습니다.");
        }

        commentEntity.setContent(commentDTO.getContent());
        cr.save(commentEntity);
    }
}