package com.tanppoppo.testore.testore.board.service;

import com.tanppoppo.testore.testore.board.dto.BoardDTO;
import com.tanppoppo.testore.testore.board.entity.BoardEntity;
import com.tanppoppo.testore.testore.board.repository.BoardRepository;
import com.tanppoppo.testore.testore.common.util.BoardTypeEnum;
import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import com.tanppoppo.testore.testore.member.repository.MemberRepository;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService {

    private final BoardRepository br;
    private final MemberRepository mr;

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

        BoardDTO boardDTO = BoardDTO.builder()
                .boardId(boardEntity.getBoardId())
                .title(boardEntity.getTitle())
                .content(boardEntity.getContent())
                .memberId(boardEntity.getMember().getMemberId())
                .nickname(boardEntity.getMember().getNickname())
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

}