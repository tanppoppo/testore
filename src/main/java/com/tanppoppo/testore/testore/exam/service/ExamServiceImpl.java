package com.tanppoppo.testore.testore.exam.service;

import com.tanppoppo.testore.testore.exam.dto.ExamPaperDTO;
import com.tanppoppo.testore.testore.exam.dto.QuestionParagraphDTO;
import com.tanppoppo.testore.testore.exam.entity.ExamPaperEntity;
import com.tanppoppo.testore.testore.exam.entity.ExamQuestionEntity;
import com.tanppoppo.testore.testore.exam.entity.QuestionParagraphEntity;
import com.tanppoppo.testore.testore.exam.repository.ExamQuestionRepository;
import com.tanppoppo.testore.testore.exam.repository.ExamPaperRepository;
import com.tanppoppo.testore.testore.exam.repository.ExamResultRepository;
import com.tanppoppo.testore.testore.exam.repository.QuestionParagraphRepository;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ExamServiceImpl implements ExamService {

    private final ExamPaperRepository epr;
    private final ExamQuestionRepository eqr;
    private final QuestionParagraphRepository qpr;
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

    /**
     * 시험지 메인 페이지 이동
     * @param user 인증된 회원 정보를 가져옵니다.
     * @return List형식의 items를 반환합니다.
     */
    @Override
    public List<ExamPaperDTO> getListItems(AuthenticatedUser user) {

        Sort sort = Sort.by(Sort.Direction.DESC, "examPaperId");
        List<ExamPaperEntity> examPaperEntityList = epr.findByOwnerId(user.getId(), sort);
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

    /**
     * 시험지 상세 페이지 이동
     * @param examPaperId examPaperId 시험지 키값을 가져옵니다.
     * @return Map 객체 detail 을 반환합니다.
     */
    @Override
    public Map<String, Object>  selectPaperDetail(int examPaperId, AuthenticatedUser user) {

        ExamPaperEntity examPaperEntity = epr.findById(examPaperId)
                .orElseThrow(()-> new EntityNotFoundException("시험지 정보를 찾을 수 없습니다."));

        MemberEntity memberEntity = memberRepository.findById(examPaperEntity.getCreatorId().getMemberId())
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        if (user.getId() != examPaperEntity.getOwnerId() && !examPaperEntity.getPublicOption()){
            throw new AccessDeniedException("공개된 시험지가 아닙니다.");
        }

        ExamPaperDTO examPaperDTO = ExamPaperDTO.builder()
                .examPaperId(examPaperEntity.getExamPaperId())
                .creatorId(examPaperEntity.getCreatorId().getMemberId())
                .imagePath(examPaperEntity.getImagePath())
                .title(examPaperEntity.getTitle())
                .createdDate(examPaperEntity.getCreatedDate())
                .content(examPaperEntity.getContent())
                .ownerId(examPaperEntity.getOwnerId())
                .publicOption(examPaperEntity.getPublicOption())
                .examItemCount(epr.getExamItemCount(examPaperEntity.getExamPaperId()))
                .likeCount(epr.getLikeCount(examPaperEntity.getExamPaperId()))
                .shareCount(epr.getShareCount(examPaperEntity.getCreatorId().getMemberId()))
                .build();

        Map<String, Object> detail = new HashMap<>();
        detail.put("examPaperDTO", examPaperDTO);
        detail.put("nickname", examPaperEntity.getCreatorId().getNickname());
        detail.put("reviewCount", epr.getReviewCount(examPaperId));
        detail.put("likeState", epr.getLikeState(memberEntity.getMemberId(), examPaperId));
        return detail;

    }

    /**
     *  특정 사용자가 소유한 시험지의 총 개수를 반환함.
     *
     * @author dhkdtjs1541
     * @param memberId 시험지를 소유한 사용자의 ID
     * @return 사용자가 소유한 시험지의 개수
     */
    @Override
    public Integer countExamPapersByOwnerId(Integer memberId) {
        return epr.countExamPapersByOwnerId(memberId);
    }

    /**
     * 시험 문제 조회
     * @author gyahury
     * @param examPaperId 시험지 id를 가져옵니다.
     * @return paragraphDTOs 시험지 dto list를 반환합니다.
     */
    @Override
    public Map<Integer, List<QuestionParagraphDTO>> selectQuestionParagraph(int examPaperId, AuthenticatedUser user) {

        ExamPaperEntity examPaperEntity = epr.findById(examPaperId)
                .orElseThrow(()-> new EntityNotFoundException("시험지 정보를 찾을 수 없습니다."));

        if (user.getId() != examPaperEntity.getOwnerId() && !examPaperEntity.getPublicOption()){
            throw new AccessDeniedException("공개된 시험지가 아닙니다.");
        }

        List<ExamQuestionEntity> examQuestionEntities = eqr.findByExamPaperId(examPaperEntity);
        List<QuestionParagraphDTO> paragraphDTOs = new ArrayList<>();

        for (ExamQuestionEntity examQuestionEntity : examQuestionEntities) {
            List<QuestionParagraphEntity> questionParagraphs = qpr.findByExamQuestionId(examQuestionEntity);
            int choiceCounter = 0;
            for (QuestionParagraphEntity paragraph : questionParagraphs) {
                QuestionParagraphDTO paragraphDTO = new QuestionParagraphDTO();

                // 선택지 인덱스 추가
                if(paragraph.getParagraphType().equals("choice")){
                    choiceCounter++;
                    paragraphDTO.setChoiceIndex(choiceCounter);
                }

                paragraphDTO.setQuestionParagraphId(paragraph.getQuestionParagraphId());
                paragraphDTO.setExamQuestionId(paragraph.getExamQuestionId().getExamQuestionId());
                paragraphDTO.setParagraphType(paragraph.getParagraphType());
                paragraphDTO.setParagraphContent(paragraph.getParagraphContent());
                paragraphDTO.setParagraphOrder(paragraph.getParagraphOrder());
                paragraphDTO.setCorrect(paragraph.getCorrect());
                paragraphDTOs.add(paragraphDTO);
            }
        }

        // 시험 문제 그룹화 및 정렬
        Map<Integer, List<QuestionParagraphDTO>> groupedParagraphDTOS = paragraphDTOs.stream()
                .sorted(Comparator.comparingInt(QuestionParagraphDTO::getParagraphOrder))
                .collect(Collectors.groupingBy(QuestionParagraphDTO::getExamQuestionId));

        return groupedParagraphDTOS;
    }


}
