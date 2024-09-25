package com.tanppoppo.testore.testore.member.service;

import com.tanppoppo.testore.testore.common.util.ItemTypeEnum;
import com.tanppoppo.testore.testore.exam.entity.ExamPaperEntity;
import com.tanppoppo.testore.testore.exam.repository.ExamPaperRepository;
import com.tanppoppo.testore.testore.member.dto.MemberDTO;
import com.tanppoppo.testore.testore.member.entity.BookmarkEntity;
import com.tanppoppo.testore.testore.member.entity.ItemLikeEntity;
import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import com.tanppoppo.testore.testore.member.entity.PointEntity;
import com.tanppoppo.testore.testore.member.repository.BookmarkRepository;
import com.tanppoppo.testore.testore.member.repository.ItemLikeRepository;
import com.tanppoppo.testore.testore.member.repository.MemberRepository;
import com.tanppoppo.testore.testore.member.repository.PointRepository;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    @Override
    public void joinMember(MemberDTO memberDTO) {

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

            helper.setFrom("<TESTORE> testore@gmail.com");
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
     * 북마크 추가 및 삭제 기능
     * @param examPaperId 시험지 아이디를 가져옵니다.
     * @param user 인증된 회원정보를 가져옵니다.
     */
    @Override
    public void createAndDeleteBookmarkByMemberId(Integer examPaperId, AuthenticatedUser user) {

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을수 없습니다."));

        ExamPaperEntity examPaperEntity = epr.findById(examPaperId)
                .orElseThrow(()-> new EntityNotFoundException("시험지 정보를 찾을수 없습니다."));

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
     * 좋아요 추가 및 삭제 기능
     * @param examPaperId 시험지 아이디를 가져옵니다.
     * @param user 인증된 회원정보를 가져옵니다.
     */
    @Override
    public void createAndDeleteItemLikeByMemberId(Integer examPaperId, AuthenticatedUser user) {

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을수 없습니다."));

        ExamPaperEntity examPaperEntity = epr.findById(examPaperId)
                .orElseThrow(()-> new EntityNotFoundException("시험지 정보를 찾을수 없습니다."));

        ItemLikeEntity selectItemLikeByMemberId = ilr.selectItemLikeByMemberId(memberEntity.getMemberId(), examPaperEntity.getExamPaperId(), ItemTypeEnum.EXAM);

        if (selectItemLikeByMemberId == null) {
            ItemLikeEntity itemLikeEntity = ItemLikeEntity.builder()
                    .memberId(memberEntity)
                    .itemId(examPaperEntity.getExamPaperId())
                    .itemType(ItemTypeEnum.EXAM)
                    .build();
            ilr.save(itemLikeEntity);
        } else {
            ilr.delete(selectItemLikeByMemberId);
        }

    }


}
