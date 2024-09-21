package com.tanppoppo.testore.testore.exam.service;

import com.tanppoppo.testore.testore.common.util.ExamStatusEnum;
import com.tanppoppo.testore.testore.exam.dto.ExamPaperDTO;
import com.tanppoppo.testore.testore.exam.dto.QuestionParagraphDTO;
import com.tanppoppo.testore.testore.exam.entity.ExamPaperEntity;
import com.tanppoppo.testore.testore.exam.entity.ExamQuestionEntity;
import com.tanppoppo.testore.testore.exam.entity.ExamResultEntity;
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

import java.time.LocalDateTime;
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

        if (!user.getId().equals(examPaperEntity.getOwnerId()) && !examPaperEntity.getPublicOption()){
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

        if (!user.getId().equals(examPaperEntity.getOwnerId()) && !examPaperEntity.getPublicOption()){
            throw new AccessDeniedException("공개된 시험지가 아닙니다.");
        }

        List<ExamQuestionEntity> examQuestionEntities = eqr.findByExamPaperId(examPaperEntity);

        if(examQuestionEntities.isEmpty()) {
            throw new NoSuchElementException("시험 문제가 없습니다.");
        }

        List<QuestionParagraphDTO> paragraphDTOs = new ArrayList<>();
        int questionIndex = 0; // 문제 번호

        for (ExamQuestionEntity examQuestionEntity : examQuestionEntities) {
            questionIndex++;
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
                paragraphDTO.setExamQuestionId(questionIndex);
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

    /**
     * 시험 점수 계산
     * @author gyahury
     * @param choices 선택지를 가져옵니다.
     * @return 시험 결과 점수를 반환합니다.
     */
    @Override
    public int calculateScore(Map<String, String[]> choices) {

        ExamPaperEntity examPaperEntity = epr.findById(Integer.parseInt(choices.get("paper")[0]))
                .orElseThrow(()-> new EntityNotFoundException("시험지 정보를 찾을 수 없습니다."));

        List<ExamQuestionEntity> examQuestionEntities = eqr.findByExamPaperId(examPaperEntity);

        if(examQuestionEntities.isEmpty()) {
            throw new NoSuchElementException("시험 문제가 없습니다.");
        }

        double questionIndex = 0;
        double correctCount = 0;

        for (ExamQuestionEntity examQuestionEntity : examQuestionEntities) {

            questionIndex++;
            List<QuestionParagraphEntity> questionParagraphs = qpr.findByExamQuestionId(examQuestionEntity);

            if (questionParagraphs.isEmpty()) {
                throw new EntityNotFoundException("문제 단락 정보를 찾을 수 없습니다.");
            }
            // 선택지 questionIndex번에 대해 제출한 답변 ex) ["1","2"]
            String[] submittedAnswers = choices.get("choice_" + (int) questionIndex);

            // choice로 필터된 문제 단락
            List<QuestionParagraphEntity> filteredParagraphs = questionParagraphs.stream()
                    .filter(p -> p.getParagraphType().equals("choice"))
                    .collect(Collectors.toList());

            ArrayList<String> answers = new ArrayList<>();

            // 문제 단락을 순회
            for (int i = 0; i < filteredParagraphs.size(); i++) {
                QuestionParagraphEntity paragraph = filteredParagraphs.get(i);
                // 단락의 정답 여부 확인
                if (paragraph.getCorrect() == true) {
                    answers.add(String.valueOf(i+1));
                }
            }

            boolean isCorrect = submittedAnswers != null && Arrays.asList(submittedAnswers).equals(answers);
            if (isCorrect) {
                correctCount++;
            }
        }
        return (int) Math.round((correctCount / questionIndex * 100));
    }

    /**
     * 시험 시작 기록
     * @author gyahury
     * @param examPaperId 시험지 id를 가져옵니다.
     * @param user user 객체를 가져옵니다.
     * @return 시험 결과 id를 반환합니다.
     */
    @Override
    public int startExam(int examPaperId, AuthenticatedUser user) {

        ExamPaperEntity examPaperEntity = epr.findById(examPaperId)
                .orElseThrow(()-> new EntityNotFoundException("시험지 정보를 찾을 수 없습니다."));

        MemberEntity memberEntity = memberRepository.findById(user.getId())
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        ExamResultEntity examResultEntity = ExamResultEntity.builder()
                .examPaperId(examPaperEntity)
                .memberId(memberEntity)
                .startTime(LocalDateTime.now())
                .status(ExamStatusEnum.IN_PROGRESS)
                .build();

        err.save(examResultEntity);

        return examResultEntity.getExamResultId();

    }

    /**
     * 시험 종료 기록
     * @author gyahury
     * @param examResultId 시험 결과 id를 가져옵니다.
     * @param score 시험 점수를 가져옵니다.
     * @param user 유저 객체를 가져옵니다.
     */
    @Override
    public void endExam(int examResultId, int score, AuthenticatedUser user) {

        ExamResultEntity examResultEntity = err.findById(examResultId)
                .orElseThrow(()-> new EntityNotFoundException("시험 결과 정보를 찾을 수 없습니다."));

        if (!examResultEntity.getMemberId().getMemberId().equals(user.getId())) {
            throw new AccessDeniedException("제출 권한이 없습니다.");
        }

        examResultEntity.setExamScore(score);
        examResultEntity.setEndTime(LocalDateTime.now());
        examResultEntity.setStatus(ExamStatusEnum.COMPLETED);
    }


}
