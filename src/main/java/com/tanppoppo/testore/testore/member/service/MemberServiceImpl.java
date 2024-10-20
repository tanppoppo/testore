package com.tanppoppo.testore.testore.member.service;

import com.tanppoppo.testore.testore.board.entity.BoardEntity;
import com.tanppoppo.testore.testore.board.repository.BoardRepository;
import com.tanppoppo.testore.testore.common.util.ItemTypeEnum;
import com.tanppoppo.testore.testore.common.util.NotificationTypeEnum;
import com.tanppoppo.testore.testore.exam.entity.ExamPaperEntity;
import com.tanppoppo.testore.testore.exam.repository.ExamPaperRepository;
import com.tanppoppo.testore.testore.member.dto.MemberDTO;
import com.tanppoppo.testore.testore.member.dto.NotificationDTO;
import com.tanppoppo.testore.testore.member.entity.*;
import com.tanppoppo.testore.testore.member.repository.*;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;
import com.tanppoppo.testore.testore.word.entity.WordBookEntity;
import com.tanppoppo.testore.testore.word.repository.WordBookRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository mr;
    private final BCryptPasswordEncoder PasswordEncoder;
    private final JavaMailSender mailSender;
    private final PointRepository pr;
    private final BookmarkRepository br;
    private final ItemLikeRepository ilr;
    private final ExamPaperRepository epr;
    private final WordBookRepository wbr;
    private final NotificationRepository nr;
    private final BoardRepository bor;
    private final ConcurrentHashMap<Integer, SseEmitter> sseMap = new ConcurrentHashMap<>();

    /**
     * 회원 가입
     * @author gyahury
     * @param memberDTO 멤버 전달 객체를 가져옵니다.
     */
    @Transactional
    @Override
    public void joinMember(MemberDTO memberDTO) {

        if (mr.existsByEmail(memberDTO.getEmail())) {
            throw new IllegalStateException("이미 사용 중인 이메일입니다.");
        }

        MemberEntity entity = MemberEntity.builder()
                .email(memberDTO.getEmail())
                .nickname(memberDTO.getNickname())
                .memberPassword(PasswordEncoder.encode(memberDTO.getMemberPassword()))
                .build();

        String token = generateEmailVerificationToken(entity);

        try {
            sendEmailVerification(entity.getEmail(), token);
        } catch (Exception e) {
            log.error("회원가입 중 이메일 발송 실패 : {}", e.getMessage());
            throw new IllegalStateException("회원가입 중 문제가 발생했습니다. 나중에 다시 시도해 주세요.");
        }

    }

    /**
     *  이메일 인증 토큰을 통해 회원을 조회하는 메서드
     * @author dhkdtjs1541
     * @param token 이메일 인증 토큰
     * @return 회원 엔티티 객체
     */
    @Override
    public MemberEntity findByEmailVerificationToken(String token) {
        return mr.findByEmailVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));
    }

    /**
     * 이메일 인증 토큰을 생성하는 메서드
     * @author dhkdtjs1541
     * @param member 회원 엔티티
     * @return 이메일 인증 토큰
     */
    @Transactional
    @Override
    public String generateEmailVerificationToken(MemberEntity member) {

        String newToken = generateVerificationToken();
        member.setEmailVerificationToken(newToken);
        member.setTokenExpirationTime(LocalDateTime.now().plusMinutes(10));
        mr.save(member);
        return newToken;

    }

    /**
     * 이메일 인증 메일을 발송하는 메서드
     * @author dhkdtjs1541
     * @param email 회원의 이메일
     * @param token 이메일 인증 토큰
     */
    public void sendEmailVerification(String email, String token) {

        try {
            String verificationUrl = "http://localhost:9993/member/verify-email?token=" + token;
            log.info("Verification URL: {}", verificationUrl);

            String subject = "TESTORE 계정 이메일 인증";
            String text = "<p>회원가입을 위해 아래 링크를 클릭하여 이메일 인증을 완료해주세요.</p><br>"
                    + "<a href=\"" + verificationUrl + "\">이메일 인증 링크</a>";

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom("TESTORE <kws22621@gmail.com>");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text, true);

            mailSender.send(mimeMessage);
            log.info("이메일 인증 메일을 성공적으로 전송했습니다. : {}", email);

        } catch (Exception e) {
            log.error("이메일 전송 실패 : {}", e.getMessage());
            throw new RuntimeException("이메일 발송에 실패했습니다. 다시 시도해 주세요.");
        }

    }

    /**
     * 이메일 인증 처리 메서드
     * 사용자가 제공한 토큰을 검증하고, 이메일 인증을 완료 처리
     * @author dhkdtjs1541
     * @param token 이메일 인증 토큰
     * @return 이메일 인증 성공 여부
     * @throws IllegalArgumentException 유효하지 않은 토큰일 경우 발생
     * @throws IllegalStateException 토큰이 만료된 경우 발생
     */
    @Transactional
    @Override
    public boolean verifyEmail(String token) {

        MemberEntity member = mr.findByEmailVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

        if (LocalDateTime.now().isAfter(member.getTokenExpirationTime())) {
            throw new IllegalStateException("토큰이 만료되었습니다.");
        }

        member.setStatus(true);
        member.setEmailVerificationToken(null);
        member.setTokenExpirationTime(null);
        mr.save(member);

        return true;

    }

    /**
     *  이메일 인증 메일을 재전송하는 메서드
     * @author dhkdtjs1541
     * @param email 회원의 이메일
     * @throws IllegalStateException 이메일이 이미 인증된 경우 예외 발생
     */
    @Transactional
    @Override
    public void resendVerificationEmail(String email) {

        MemberEntity member = mr.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다"));;

        if (member.getStatus()) {
            throw new IllegalStateException("이메일 주소가 이미 인증되었습니다.");
        }

        String newToken = generateEmailVerificationToken(member);
        sendEmailVerification(member.getEmail(), newToken);

    }

    /**
     * 회원의 포인트 합계를 계산하는 메서드
     *
     * @author dhkdtjs1541
     * @param memberId 회원의 ID
     * @return 회원의 포인트 합계
     * @throws RuntimeException 회원을 찾을 수 없을 경우 발생
     */
    @Override
    public int getPointSumByMemberId(int memberId) {
        MemberEntity member = mr.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
        List<PointEntity> points = pr.findByMemberId(member);
        return points.stream().mapToInt(PointEntity::getPointChange).sum(); // 포인트의 합계 계산
    }

    /**
     * 이메일 인증 토큰 생성 메서드
     * UUID를 사용하여 고유한 토큰 생성
     * @author dhkdtjs1541
     * @return 생성된 토큰
     */
    private String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * 시험지 북마크 추가 및 삭제 기능
     * @param examPaperId 시험지 아이디를 가져옵니다.
     * @param user 인증된 회원정보를 가져옵니다.
     */
    @Transactional
    @Override
    public void createAndDeleteBookmarkByMemberId(Integer examPaperId, AuthenticatedUser user) {

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을수 없습니다."));

        ExamPaperEntity examPaperEntity = epr.findById(examPaperId)
                .orElseThrow(()-> new EntityNotFoundException("시험지 정보를 찾을수 없습니다."));

        if (!user.getId().equals(examPaperEntity.getOwnerId()) && !examPaperEntity.getPublicOption()){
            throw new AccessDeniedException("공개된 시험지가 아닙니다.");
        }

        BookmarkEntity selectBookmarkByMemberId = br.selectBookmarkByMemberId(memberEntity.getMemberId(), examPaperEntity.getExamPaperId(), ItemTypeEnum.EXAM);

        if (selectBookmarkByMemberId == null) {
            BookmarkEntity bookmarkEntity = BookmarkEntity.builder()
                    .memberId(memberEntity)
                    .itemId(examPaperEntity.getExamPaperId())
                    .itemType(ItemTypeEnum.EXAM)
                    .build();
            br.save(bookmarkEntity);
        } else {
            br.delete(selectBookmarkByMemberId);
        }

    }

    /**
     * 시험지 좋아요 추가 및 삭제 기능
     * @param examPaperId 시험지 아이디를 가져옵니다.
     * @param user 인증된 회원정보를 가져옵니다.
     */
    @Transactional
    @Override
    public void createAndDeleteItemLikeByMemberId(Integer examPaperId, AuthenticatedUser user) {

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을수 없습니다."));

        ExamPaperEntity examPaperEntity = epr.findById(examPaperId)
                .orElseThrow(()-> new EntityNotFoundException("시험지 정보를 찾을수 없습니다."));

        if (!user.getId().equals(examPaperEntity.getOwnerId()) && !examPaperEntity.getPublicOption()){
            throw new AccessDeniedException("공개된 시험지가 아닙니다.");
        }

        ItemLikeEntity selectItemLikeByMemberId = ilr.selectItemLikeByMemberId(memberEntity.getMemberId(), examPaperEntity.getExamPaperId(), ItemTypeEnum.EXAM);

        if (selectItemLikeByMemberId == null) {
            ItemLikeEntity itemLikeEntity = ItemLikeEntity.builder()
                    .memberId(memberEntity)
                    .itemId(examPaperEntity.getExamPaperId())
                    .itemType(ItemTypeEnum.EXAM)
                    .build();
            ilr.save(itemLikeEntity);

            // 알림 추가
            if(!memberEntity.getMemberId().equals(examPaperEntity.getOwnerId())) {
                saveNotification(memberEntity.getMemberId(), examPaperId, NotificationTypeEnum.EXAMPAPER_LIKE);
            }
        } else {
            ilr.delete(selectItemLikeByMemberId);
        }

    }

    /**
     * 북마크 추가 및 삭제 기능
     *
     * @param wordBookId 단어장 아이디를 가져옵니다.
     * @param user       인증된 회원정보를 가져옵니다.
     */
    @Transactional
    @Override
    public void createAndDeleteWordBookBookmarkByMemberId(Integer wordBookId, AuthenticatedUser user) {

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("회원 정보를 찾을수 없습니다."));

        WordBookEntity wordBookEntity = wbr.findById(wordBookId)
                .orElseThrow(() -> new EntityNotFoundException("단어장 정보를 찾을수 없습니다."));

        if (!user.getId().equals(wordBookEntity.getOwnerId()) && !wordBookEntity.getPublicOption()){
            throw new AccessDeniedException("공개된 단어장이 아닙니다.");
        }

        BookmarkEntity selectBookmarkByMemberId = br.selectBookmarkByMemberId(memberEntity.getMemberId(), wordBookEntity.getWordBookId(), ItemTypeEnum.WORD);

        if (selectBookmarkByMemberId == null) {
            BookmarkEntity bookmarkEntity = BookmarkEntity.builder()
                    .memberId(memberEntity)
                    .itemId(wordBookEntity.getWordBookId())
                    .itemType(ItemTypeEnum.WORD)
                    .build();
            br.save(bookmarkEntity);
        } else {
            br.delete(selectBookmarkByMemberId);
        }

    }

    /**
     * 좋아요 추가 및 삭제 기능
     *
     * @param wordBookId 단어장 아이디를 가져옵니다.
     * @param user       인증된 회원정보를 가져옵니다.
     */
    @Transactional
    @Override
    public void createAndDeleteWordBookItemLikeByMemberId(Integer wordBookId, AuthenticatedUser user) {

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("회원 정보를 찾을수 없습니다."));

        WordBookEntity wordBookEntity = wbr.findById(wordBookId)
                .orElseThrow(() -> new EntityNotFoundException("단어장 정보를 찾을수 없습니다."));

        if (!user.getId().equals(wordBookEntity.getOwnerId()) && !wordBookEntity.getPublicOption()){
            throw new AccessDeniedException("공개된 단어장이 아닙니다.");
        }

        ItemLikeEntity selectItemLikeByMemberId = ilr.selectItemLikeByMemberId(memberEntity.getMemberId(), wordBookEntity.getWordBookId(), ItemTypeEnum.WORD);

        if (selectItemLikeByMemberId == null) {
            ItemLikeEntity itemLikeEntity = ItemLikeEntity.builder()
                    .memberId(memberEntity)
                    .itemId(wordBookEntity.getWordBookId())
                    .itemType(ItemTypeEnum.WORD)
                    .build();
            ilr.save(itemLikeEntity);

            if(!memberEntity.getMemberId().equals(wordBookEntity.getOwnerId())) {
                saveNotification(memberEntity.getMemberId(), wordBookId, NotificationTypeEnum.WORDBOOK_LIKE);
            }
        } else {
            ilr.delete(selectItemLikeByMemberId);
        }
    }

    /**
     * 알림 저장 메서드
     * @author dhkdtjs1541
     * @param userId 알림을 생성한 회원 ID
     * @param itemId 좋아요가 눌린 아이템(시험지 또는 단어장) ID
     * @param type 알림 유형
     * @throws EntityNotFoundException 주어진 ID에 해당하는 회원
     */
    @Transactional
    @Override
    public void saveNotification(Integer userId, Integer itemId, NotificationTypeEnum type) {

        ExamPaperEntity examPaperEntity;
        WordBookEntity wordBookEntity;
        BoardEntity boardEntity;
        MemberEntity recipientIdMemberEntity = new MemberEntity();

        MemberEntity senderMemberEntity = mr.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        if (type.equals(NotificationTypeEnum.EXAMPAPER_LIKE)) {

            examPaperEntity = epr.findById(itemId)
                    .orElseThrow(() -> new EntityNotFoundException("시험지를 찾을 수 없습니다."));

            recipientIdMemberEntity = mr.findById(examPaperEntity.getOwnerId())
                    .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        } else if (type.equals(NotificationTypeEnum.WORDBOOK_LIKE)) {

            wordBookEntity = wbr.findById(itemId)
                    .orElseThrow(() -> new EntityNotFoundException("단어장을 찾을 수 없습니다."));

            recipientIdMemberEntity = mr.findById(wordBookEntity.getOwnerId())
                    .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        } else if (type.equals(NotificationTypeEnum.BOARD_COMMENT)) {

            boardEntity = bor.findById(itemId)
                    .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

            recipientIdMemberEntity = mr.findById(boardEntity.getMember().getMemberId())
                    .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        }

        NotificationEntity notificationEntity = NotificationEntity.builder()
                .senderId(senderMemberEntity)
                .recipientId(recipientIdMemberEntity)
                .itemId(itemId)
                .notificationType(type)
                .build();

        nr.save(notificationEntity);

    }

    /**
     * 회원의 최근 10개 알림 가져오고, 읽지 않은 알림 읽음 상태로 변경하는 메서드
     * @author dhkdtjs1541
     * @param memberId 알림을 조회할 회원의 ID
     * @return 읽음 상태로 변경된 알림 리스트(DTO 형식)
     */
    @Transactional
    @Override
    public List<NotificationDTO> getNotificationsAndMarkAsRead(Integer memberId) {

        // 최신 20개 제외한 알람을 삭제
        nr.deleteOldNotifications(memberId);

        // 최신 20개의 알림을 가져옴
        List<NotificationEntity> notificationEntities = nr.findByRecipientId_MemberIdOrderByNotificationIdDesc(memberId);

        List<NotificationDTO> notificationDTOS = new ArrayList<>();

        for (NotificationEntity notificationEntity : notificationEntities) {

            // 읽지 않은 알림이 있으면 읽음으로 표시
            if (!notificationEntity.getIsRead()) {
                notificationEntity.setIsRead(true);
            }

            String itemNickname = "";

            switch(notificationEntity.getNotificationType()) {
                case EXAMPAPER_LIKE:
                    ExamPaperEntity examPaperEntity = epr.findById(notificationEntity.getItemId())
                            .orElseThrow(() -> new EntityNotFoundException("시험지를 찾을 수 없습니다."));
                    itemNickname = examPaperEntity.getTitle();
                    break;
                case WORDBOOK_LIKE:
                    WordBookEntity wordBookEntity = wbr.findById(notificationEntity.getItemId())
                            .orElseThrow(() -> new EntityNotFoundException("단어장을 찾을 수 없습니다."));
                    itemNickname = wordBookEntity.getTitle();
                    break;
                case BOARD_COMMENT:
                    BoardEntity boardEntity = bor.findById(notificationEntity.getItemId())
                            .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
                    itemNickname = boardEntity.getTitle();
                default:
                    break;
            }

            // 알림을 DTO로 변환
            NotificationDTO notificationDTO = NotificationDTO.builder()
                    .notificationId(notificationEntity.getNotificationId())
                    .senderId(notificationEntity.getSenderId().getMemberId())
                    .notificationType(notificationEntity.getNotificationType().name())
                    .isRead(notificationEntity.getIsRead())
                    .recipientId(notificationEntity.getRecipientId().getMemberId())
                    .senderNickname(notificationEntity.getSenderId().getNickname())
                    .itemTitle(itemNickname)
                    .build();
            notificationDTOS.add(notificationDTO);

        }

        return notificationDTOS;

    }

    /**
     * 알림 조회
     * @author gyahury
     * @param userId userId를 가져옵니다.
     * @return 알림이 존재하는지 여부를 반환합니다.
     */
    @Override
    public Boolean getUnreadNotification(Integer userId) {
        return nr.existsAnyUnreadNotifications(userId);
    }

}
