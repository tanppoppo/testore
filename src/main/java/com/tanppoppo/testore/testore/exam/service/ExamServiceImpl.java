package com.tanppoppo.testore.testore.exam.service;

import com.tanppoppo.testore.testore.common.util.ExamStatusEnum;
import com.tanppoppo.testore.testore.common.util.ItemTypeEnum;
import com.tanppoppo.testore.testore.common.util.ParagraphTypeEnum;
import com.tanppoppo.testore.testore.common.util.QuestionTypeEnum;
import com.tanppoppo.testore.testore.exam.dto.ExamPaperDTO;
import com.tanppoppo.testore.testore.exam.dto.ExamResultDTO;
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
import com.tanppoppo.testore.testore.member.repository.BookmarkRepository;
import com.tanppoppo.testore.testore.member.repository.ItemLikeRepository;
import com.tanppoppo.testore.testore.member.repository.MemberRepository;
import com.tanppoppo.testore.testore.member.repository.ReviewRepository;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
    private final MemberRepository mr;
    private final ItemLikeRepository ilr;
    private final BookmarkRepository br;
    private final ReviewRepository rr;

    /**
     * 시험지 생성 정보 저장
     * @param examPaperDTO 시험지 정보를 입력합니다.
     * @param user 사용자 인증 정보를 가져옵니다.
     * @return examPaperId를 반환합니다.
     */
    @Override
    public int examCreate(ExamPaperDTO examPaperDTO, AuthenticatedUser user) {

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(()->new EntityNotFoundException("회원정보를 찾을 수 없습니다."));

        ExamPaperEntity examPaperEntity = ExamPaperEntity.builder()
                .title(examPaperDTO.getTitle())
                .content(examPaperDTO.getContent())
                .passScore(examPaperDTO.getPassScore())
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
                    .likeCount(ilr.getLikeCount(entity.getExamPaperId()))
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

        MemberEntity memberEntity = mr.findById(examPaperEntity.getCreatorId().getMemberId())
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
                .passScore(examPaperEntity.getPassScore())
                .ownerId(examPaperEntity.getOwnerId())
                .publicOption(examPaperEntity.getPublicOption())
                .examItemCount(epr.getExamItemCount(examPaperEntity.getExamPaperId()))
                .likeCount(ilr.getLikeCount(examPaperEntity.getExamPaperId()))
                .shareCount(epr.getShareCount(examPaperEntity.getCreatorId().getMemberId()))
                .build();

        Map<String, Object> detail = new HashMap<>();
        detail.put("examPaperDTO", examPaperDTO);
        detail.put("nickname", examPaperEntity.getCreatorId().getNickname());
        detail.put("reviewCount", rr.getReviewCount(examPaperId));
        detail.put("likeState", ilr.getLikeState(memberEntity.getMemberId(), examPaperId));
        detail.put("bookmarkState", br.getBookmarkState(memberEntity.getMemberId(), examPaperId, ItemTypeEnum.EXAM));
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
                if(paragraph.getParagraphType().equals(ParagraphTypeEnum.CHOICE)){
                    choiceCounter++;
                    paragraphDTO.setChoiceIndex(choiceCounter);
                }

                paragraphDTO.setQuestionParagraphId(paragraph.getQuestionParagraphId());
                paragraphDTO.setExamQuestionId(questionIndex);
                paragraphDTO.setParagraphType(String.valueOf(paragraph.getParagraphType()));
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
                    .filter(p -> p.getParagraphType().equals(ParagraphTypeEnum.CHOICE))
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

        MemberEntity memberEntity = mr.findById(user.getId())
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

    /**
     * 시험 결과 조회
     * @author gyahury
     * @param user 유저 객체를 가져옵니다.
     * @return examResultDTOS 시험 결과 dto 리스트를 반환합니다.
     */
    @Override
    public List<ExamResultDTO> findExamResultByMemberId(AuthenticatedUser user, String keyword) {

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        List<ExamResultEntity> examResultEntities;

        if (keyword == null) {
            examResultEntities = err.findByMemberIdAndStatus(memberEntity, ExamStatusEnum.COMPLETED);
        } else {
            examResultEntities = err.findByMemberIdAndStatusAndExamPaperTitleContaining(memberEntity, ExamStatusEnum.COMPLETED, keyword);
        }

        List<ExamResultDTO> examResultDTOS = new ArrayList<>();
        for (ExamResultEntity examResultEntitie : examResultEntities) {
            // 시험지 정보 조회
            ExamPaperEntity examPaperEntity = epr.findById(examResultEntitie.getExamPaperId().getExamPaperId())
                    .orElseThrow(()-> new EntityNotFoundException("시험지 정보를 찾을 수 없습니다."));

            ExamResultDTO examResultDTO = ExamResultDTO.builder()
                    .examPaperTitle(examPaperEntity.getTitle())
                    .examPaperContent(examPaperEntity.getContent())
                    .examPaperImagePath(examPaperEntity.getImagePath())
                    .examPaperPassScore(examPaperEntity.getPassScore())
                    .examScore(examResultEntitie.getExamScore())
                    .passStatus(examPaperEntity.getPassScore() <= examResultEntitie.getExamScore() ? true : false)
                    .createdDate(examResultEntitie.getCreatedDate())
                    .timeTaken(calculateTimeTaken(examResultEntitie.getStartTime(), examResultEntitie.getEndTime()))
                    .examQuestionCount(epr.getExamItemCount(examPaperEntity.getExamPaperId()))
                    .build();

            examResultDTOS.add(examResultDTO);
        }
        return examResultDTOS;
    }

    /**
     * 문제 생성
     * @author gyahury
     * @param paragraphs 문제 단락을 가져옵니다.
     * @param userId 계정 id를 가져옵니다.
     */
    @Override
    public void createQuestion(Map<String, String[]> paragraphs, Integer userId) {

        ExamPaperEntity examPaperEntity = epr.findById(Integer.parseInt(paragraphs.get("paper")[0]))
                .orElseThrow(()-> new EntityNotFoundException("시험지 정보를 찾을 수 없습니다."));

        Map<Integer, List<String[]>> groupedData = new HashMap<>();
        Map<Integer, String[]> answers = new HashMap<>();

        // 데이터 분리
        paragraphs.forEach((key, value) -> {
            String[] parts = key.split("_");
            if (parts.length > 1) {
                if (!parts[0].equals("answer")) {
                    Integer num = Integer.parseInt(parts[1]);
                    if (parts.length > 2 && parts[0].equals("choice")) {
                        value = new String[]{String.join(" ", value) + "||||choice||||" + parts[2]};
                    }
                    else if (parts[0].equals("desc")) {
                        value = new String[]{String.join(" ", value) + "||||desc"};
                    }
                    else if (parts[0].equals("content")) {
                        value = new String[]{String.join(" ", value) + "||||content"};
                    }
                    else if (parts[0].equals("question")) {
                        value = new String[]{String.join(" ", value) + "||||question"};
                    }
                    groupedData.computeIfAbsent(num, k -> new ArrayList<>()).add(value);
                } else {
                    Integer num = Integer.parseInt(parts[1]);
                    answers.put(num, value);
                }
            }
        });

        // key: 문제 번호, values: 단락 내용
        groupedData.forEach((key, values) -> {

            // 문제 테이블부터 저장
            ExamQuestionEntity examQuestionEntity = ExamQuestionEntity.builder()
                    .questionOrder(key)
                    .examPaperId(examPaperEntity)
                    .questionScore((int) Math.round(100.0 /groupedData.size()))
                    .questionType(QuestionTypeEnum.MULTIPLE)
                    .build();

            eqr.save(examQuestionEntity);

            AtomicInteger paragraphIndex = new AtomicInteger(1);
            // 단락 테이블 저장
            values.forEach((content) -> {

                int currentIndex = paragraphIndex.getAndIncrement();
                String[] splitedContent = content[0].split("\\|\\|\\|\\|");
                QuestionParagraphEntity questionParagraphEntity = new QuestionParagraphEntity();

                ParagraphTypeEnum type = null ;

                // type 처리
                if (splitedContent[1].equals("choice")){
                    type = ParagraphTypeEnum.CHOICE;

                    // 정답 처리
                    String[] answer = answers.get(key);
                    if (Arrays.asList(answer).contains(splitedContent[2])) {
                        questionParagraphEntity.setCorrect(true);
                    };

                } else if (splitedContent[1].equals("desc")) {
                    type = ParagraphTypeEnum.DESC;
                } else if (splitedContent[1].equals("content")) {
                    type = ParagraphTypeEnum.CONTENT;
                } else if (splitedContent[1].equals("question")) {
                    type = ParagraphTypeEnum.QUESTION;
                }

                questionParagraphEntity.setParagraphOrder(currentIndex);
                questionParagraphEntity.setParagraphType(type);
                questionParagraphEntity.setParagraphContent(splitedContent[0]);
                questionParagraphEntity.setExamQuestionId(examQuestionEntity);

                qpr.save(questionParagraphEntity);
            });

        });
    }

    /**
     * 시험 문제 존재 여부 체크
     * @author gyahury
     * @param examPaperId 시험지 id를 가져옵니다.
     * @param user user 객체를 가져옵니다.
     * @return boolean 존재 여부를 반환합니다.
     */
    @Override
    public boolean checkQuestionExist(int examPaperId, AuthenticatedUser user) {

        ExamPaperEntity examPaperEntity = epr.findById(examPaperId)
                .orElseThrow(()-> new EntityNotFoundException("시험지 정보를 찾을 수 없습니다."));

        mr.findById(examPaperEntity.getCreatorId().getMemberId()).orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        if (!user.getId().equals(examPaperEntity.getOwnerId()) && !examPaperEntity.getPublicOption()){
            throw new AccessDeniedException("공개된 시험지가 아닙니다.");
        }

        List<ExamQuestionEntity> examQuestionEntities = eqr.findByExamPaperId(examPaperEntity);

        if(!examQuestionEntities.isEmpty()) {
            return true;
        }

        return false;

    }

    /**
     * 업데이트할 시험지 정보 조회
     * @param examPaperId 시험지 id를 가져옵니다.
     * @param userId user id를 가져옵니다.
     * @return examResultDTO를 반환합니다.
     */
    @Override
    public ExamResultDTO selectUpdatedPaperInfo(int examPaperId, Integer userId) {

        ExamPaperEntity examPaperEntity = epr.findById(examPaperId)
                .orElseThrow(()-> new EntityNotFoundException("시험지 정보를 찾을 수 없습니다."));

        MemberEntity memberEntity = mr.findById(examPaperEntity.getCreatorId().getMemberId())
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        if (!userId.equals(examPaperEntity.getOwnerId()) && !examPaperEntity.getPublicOption()){
            throw new AccessDeniedException("공개된 시험지가 아닙니다.");
        }

        ExamResultDTO examResultDTO = ExamResultDTO.builder()
                .examPaperId(examPaperEntity.getExamPaperId())
                .examPaperTitle(examPaperEntity.getTitle())
                .examPaperContent(examPaperEntity.getContent())
                .examPaperPassScore(examPaperEntity.getPassScore())
                .build();

        return examResultDTO;
    }

    /**
     * 시험지 업데이트
     * @author gyahury
     * @param examPaperDTO 시험지 dto를 가져옵니다.
     * @param userId 유저 id를 가져옵니다.
     */
    @Override
    public void updateExamPaper(ExamPaperDTO examPaperDTO, Integer userId) {

        ExamPaperEntity examPaperEntity = epr.findById(examPaperDTO.getExamPaperId())
                .orElseThrow(()-> new EntityNotFoundException("시험지 정보를 찾을 수 없습니다."));

        MemberEntity memberEntity = mr.findById(examPaperEntity.getCreatorId().getMemberId())
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        if (!userId.equals(examPaperEntity.getOwnerId()) && !examPaperEntity.getPublicOption()){
            throw new AccessDeniedException("공개된 시험지가 아닙니다.");
        }

        examPaperEntity.setTitle(examPaperDTO.getTitle());
        examPaperEntity.setContent(examPaperDTO.getContent());
        examPaperEntity.setPassScore(examPaperDTO.getPassScore());

    }

    /**
     * 로컬데이트타임 타입 걸린 시간 계산
     * @author gyahury
     * @param start 시작 로컬데이트타임 타입을 가져옵니다.
     * @param end 끝 로컬데이트타임 타입을 가져옵니다.
     * @return ~일 ~시간 ~분 ~초 문자열로 반환합니다.
     */
    public String calculateTimeTaken (LocalDateTime start, LocalDateTime end) {

        Duration duration = Duration.between(start, end);

        long days = duration.toDaysPart();
        int hours = duration.toHoursPart();
        int minutes = duration.toMinutesPart();
        int seconds = duration.toSecondsPart();

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days + "일 ");
        }
        if (hours > 0) {
            sb.append(hours + "시간 ");
        }
        if (minutes > 0) {
            sb.append(minutes + "분 ");
        }
        if (seconds > 0) {
            sb.append(seconds + "초");
        }

        return sb.toString().trim();
    }

    /**
     * 공개여부 설정
     * @author KIMGEON64
     * @param examPaperId 시험지 키값을 가져옵니다.
     */
    @Override
    public void controlPublicOption(int examPaperId) {

        ExamPaperEntity examPaperEntity = epr.findById(examPaperId)
                .orElseThrow(()-> new EntityNotFoundException("시험지 정보를 찾을 수 없습니다."));

        Boolean publicOption = examPaperEntity.getPublicOption();

        if (publicOption == false) {
            examPaperEntity.setPublicOption(true);
        } else {
            examPaperEntity.setPublicOption(false);
        }

        epr.save(examPaperEntity);

    }
}
