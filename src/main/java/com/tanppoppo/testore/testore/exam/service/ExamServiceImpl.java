package com.tanppoppo.testore.testore.exam.service;

import com.tanppoppo.testore.testore.exam.dto.ExamPaperDTO;
import com.tanppoppo.testore.testore.exam.entity.ExamPaperEntity;
import com.tanppoppo.testore.testore.exam.repository.ExamItemRepository;
import com.tanppoppo.testore.testore.exam.repository.ExamPaperRepository;
import com.tanppoppo.testore.testore.exam.repository.ExamResultRepository;
import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import com.tanppoppo.testore.testore.member.repository.MemberRepository;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ExamServiceImpl implements ExamService {

    private final ExamPaperRepository epr;
    private final ExamItemRepository eir;
    private final ExamResultRepository err;
    private final MemberRepository memberRepository;

    /**
     * 시험지 생성 정보 저장
     * @param examPaperDTO 시험지 정보를 입력합니다.
     * @param user 사용자 인증 정보를 가져옵니다.
     * @return examPaperId를 반환합니다.
     */
    @Override
    public int examCreate(ExamPaperDTO examPaperDTO, AuthenticatedUser user) {

        MemberEntity memberEntity = memberRepository.findById(user.getId())
                .orElseThrow(()->new EntityNotFoundException("회원정보를 찾을 수 없습니다."));

        ExamPaperEntity examPaperEntity = ExamPaperEntity.builder()
                .title(examPaperDTO.getTitle())
                .content(examPaperDTO.getContent())
                .creatorId(memberEntity)
                .imagePath(examPaperDTO.getImagePath())
                .ownerId(memberEntity.getMemberId())
                .build();
        epr.save(examPaperEntity);

        return examPaperEntity.getExamPaperId();

    }

    /* 시험지 메인 페이지 이동시 List 객체 반환 */

    /**
     * 시험지 메인 페이지 이동
     * @return List<ExamPaperDTO> items 를 반환합니다.
     */
    @Override
    public List<ExamPaperDTO> getListItems() {

        Sort sort = Sort.by(Sort.Direction.DESC, "examPaperId");
        List<ExamPaperEntity> examPaperEntityList = epr.findAll(sort);
        List<ExamPaperDTO> items = new ArrayList<>();

        for (ExamPaperEntity entity : examPaperEntityList) {
            ExamPaperDTO examPaperDTO = ExamPaperDTO.builder()
                    .examPaperId(entity.getExamPaperId())
                    .title(entity.getTitle())
                    .content(entity.getContent())
                    .imagePath(entity.getImagePath())
                    .examItemCount(epr.getExamItemCount(entity.getExamPaperId()))
                    .likeCount(epr.getLikeCount(entity.getExamPaperId()))
                    .shareCount(epr.getShareCount(entity.getCreatorId().getMemberId()))
                    .build();
            items.add(examPaperDTO);
        }

        return items;

    }

}
