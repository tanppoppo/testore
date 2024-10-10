package com.tanppoppo.testore.testore.word.service;

import com.tanppoppo.testore.testore.common.util.ItemTypeEnum;
import com.tanppoppo.testore.testore.member.dto.ReviewDTO;
import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import com.tanppoppo.testore.testore.member.entity.ReviewEntity;
import com.tanppoppo.testore.testore.member.repository.BookmarkRepository;
import com.tanppoppo.testore.testore.member.repository.ItemLikeRepository;
import com.tanppoppo.testore.testore.member.repository.MemberRepository;
import com.tanppoppo.testore.testore.member.repository.ReviewRepository;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;
import com.tanppoppo.testore.testore.word.dto.WordBookDTO;
import com.tanppoppo.testore.testore.word.entity.WordBookEntity;
import com.tanppoppo.testore.testore.word.entity.WordEntity;
import com.tanppoppo.testore.testore.word.repository.WordBookRepository;
import com.tanppoppo.testore.testore.word.repository.WordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WordServiceImpl implements WordService {

    private final WordBookRepository wbr;
    private final WordRepository wr;
    private final MemberRepository mr;
    private final ReviewRepository rr;
    private final ItemLikeRepository ilr;
    private final BookmarkRepository br;


    /**
     * 특정 사용자가 소유한 단어장의 개수를 반환
     * @author dhkdtjs1541
     * @param ownerId 단어장 소유자의 ID
     * @return 소유한 단어장의 개수
     */
    @Override
    public Integer countWordBooksByOwnerId(Integer ownerId) {
        return wbr.countByOwnerId(ownerId);
    }

    /**
     * 단어장 생성 정보 저장
     * @author MinCheolHa
     * @param wordBookDTO 단어장 정보를 입력합니다.
     * @param user 사용자 인증 정보를 가져옵니다.
     * @return WordBookId를 반환합니다.
     */
    @Override
    public int createWordBook(WordBookDTO wordBookDTO, AuthenticatedUser user) {

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(()->new EntityNotFoundException("회원정보를 찾을 수 없습니다."));

        WordBookEntity wordBookEntity = WordBookEntity.builder()
                .title(wordBookDTO.getTitle())
                .content(wordBookDTO.getContent())
                .creatorId(memberEntity)
                .imagePath(wordBookDTO.getImagePath())
                .ownerId(memberEntity.getMemberId())
                .build();

        wbr.save(wordBookEntity);

        return wordBookEntity.getWordBookId();
    }

    /**
     * 단어 저장
     * @author MinCheolHa
     * @param words 단어 정보를 입력합니다.
     * @param wordBookId 단어장 Id를 가져옵니다.
     * @param userId 사용자 정보를 가져옵니다.
     */
    @Override
    public void addWords(Map<String, String[]> words, Integer wordBookId, Integer userId) {

        WordBookEntity wordBookEntity = wbr.findById(wordBookId)
                .orElseThrow(()-> new EntityNotFoundException("단어장 정보를 찾을 수 없습니다."));

        int wordNum = 1;

        while (words.containsKey("text_1_" + wordNum)) {

            String[] text1 = words.get("text_1_" + wordNum);
            String[] text2 = words.get("text_2_" + wordNum);
            String[] text3 = words.get("text_3_" + wordNum);

            if (text1 == null || text2 == null || text3 == null) {
                throw new IllegalArgumentException("단어, 뜻, 설명의 입력이 올바르지 않습니다.");
            }
            WordEntity wordEntity = new WordEntity();
            wordEntity.setWordBookId(wordBookEntity);
            wordEntity.setWordNum(wordNum);
            wordEntity.setText1(text1[0]);
            wordEntity.setText2(text2[0]);
            wordEntity.setText3(text3[0]);

            wr.save(wordEntity);

            wordNum++;

        }

    }

    /**
     * 단어장 메인 페이지 이동
     * @author MinCheolHa
     * @param user 인증된 회원 정보를 가져옵니다.
     * @return List형식의 items를 반환합니다.
     */
    @Override
    public List<WordBookDTO> getListItems(AuthenticatedUser user) {

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        Sort sort = Sort.by(Sort.Direction.DESC, "wordBookId");
        List<WordBookEntity> wordBookEntityList = wbr.findByOwnerIdWithBookmarks(memberEntity.getMemberId(), sort);

        List<WordBookDTO> items = new ArrayList<>();

        for (WordBookEntity entity : wordBookEntityList) {
            WordBookDTO wordBookDTO = WordBookDTO.builder()
                    .wordBookId(entity.getWordBookId())
                    .title(entity.getTitle())
                    .content(entity.getContent())
                    .imagePath(entity.getImagePath())
                    .wordItemCount(wbr.getWordItemCount(entity.getWordBookId()))
                    .likeCount(ilr.getLikeCount(entity.getWordBookId()))
                    .shareCount(wbr.getShareCount(entity.getCreatorId().getMemberId()))
                    .isBookmarked(br.getBookmarkState(user.getId(), entity.getWordBookId(), ItemTypeEnum.WORD))
                    .build();
            items.add(wordBookDTO);
        }

        return items;
    }

    /**
     * 단어장 상세 정보 조회
     * @author MinCheolHa
     * @param wordBookId wordBookId 시험지 키값을 가져옵니다.
     * @return Map 객체 detail 을 반환합니다.
     */
    @Override
    public Map<String, Object> selectWordBookDetail(int wordBookId, AuthenticatedUser user) {

        WordBookEntity wordBookEntity = wbr.findById(wordBookId)
                .orElseThrow(()-> new EntityNotFoundException("단어장 정보를 찾을 수 없습니다."));

        MemberEntity memberEntity = mr.findById(wordBookEntity.getCreatorId().getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("회원 정보를 찾을수 없습니다."));

        if (!user.getId().equals(wordBookEntity.getOwnerId()) && !wordBookEntity.getPublicOption()){
            throw new AccessDeniedException("공개된 단어장이 아닙니다.");
        }

        WordBookDTO wordBookDTO = WordBookDTO.builder()
                .wordBookId(wordBookEntity.getWordBookId())
                .creatorId(wordBookEntity.getCreatorId().getMemberId())
                .imagePath(wordBookEntity.getImagePath())
                .title(wordBookEntity.getTitle())
                .createdDate(wordBookEntity.getCreatedDate())
                .content(wordBookEntity.getContent())
                .ownerId(wordBookEntity.getOwnerId())
                .publicOption(wordBookEntity.getPublicOption())
                .wordItemCount(wbr.getWordItemCount(wordBookEntity.getWordBookId()))
                .likeCount(ilr.getLikeCount(wordBookEntity.getWordBookId()))
                .shareCount(wbr.getShareCount(wordBookEntity.getCreatorId().getMemberId()))
                .build();

        Map<String, Object> detail = new HashMap<>();
        detail.put("wordBookDTO", wordBookDTO);
        detail.put("nickName", wordBookEntity.getCreatorId().getNickname());
        detail.put("reviewCount", rr.getReviewCount(wordBookId));
        detail.put("likeState", ilr.getLikeState(memberEntity.getMemberId(), wordBookId, ItemTypeEnum.WORD));
        detail.put("bookmarkState", br.getBookmarkState(memberEntity.getMemberId(), wordBookId, ItemTypeEnum.WORD));
        return detail;

    }

    /**
     * 공개 여부 변경
     * @param wordBookId 단어장 아이디를 가져옵니다.
     * @param user       인증된 회원정보를 가져옵니다.
     */
    @Override
    public void updatePublicOptionByMemberId(Integer wordBookId, AuthenticatedUser user) {

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("회원 정보를 찾을수 없습니다."));

        WordBookEntity wordBookEntity = wbr.findById(wordBookId)
                .orElseThrow(() -> new EntityNotFoundException("단어장 정보를 찾을수 없습니다."));

        if (!memberEntity.getMemberId().equals(wordBookEntity.getOwnerId())) {
            throw new AccessDeniedException("소유 권한이 없습니다.");
        }

        if (wordBookEntity.getPublicOption()) {
            wordBookEntity.setPublicOption(false);
        } else {
            wordBookEntity.setPublicOption(true);
        }

        wbr.save(wordBookEntity);
    }

    /**
     * 업데이트할 단어장 정보 조회
     * @param wordBookId 단어장 id를 가져옵니다.
     * @param userId user id를 가져옵니다.
     * @return examResultDTO를 반환합니다.
     */
    @Override
    public WordBookDTO selectUpdatedBookInfo (int wordBookId, Integer userId) {

        WordBookEntity wordBookEntity = wbr.findById(wordBookId)
                .orElseThrow(()-> new EntityNotFoundException("단어장 정보를 찾을 수 없습니다."));

        if (!userId.equals(wordBookEntity.getOwnerId()) && !wordBookEntity.getPublicOption()) {
            throw new AccessDeniedException("공개된 단어장이 아닙니다,");
        }

        WordBookDTO wordBookDTO = WordBookDTO.builder()
                .build();

        return wordBookDTO;
    }
    /**
     * 리뷰 목록 반환
     * @author MinCheolHa
     * @param user user 객체를 가져옵니다.
     * @param wordBookId 단어장 키값을 가져옵니다.
     * @return Map 객체 result를 반환합니다.
     */
    @Override
    public Map<String,Object> getListReviews(AuthenticatedUser user, int wordBookId) {

        wbr.findById(wordBookId).orElseThrow(()-> new EntityNotFoundException("단어장 정보를 찾을 수 없습니다."));

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        Sort sort = Sort.by(Sort.Direction.DESC, "reviewId");
        List<ReviewEntity> reviewEntityList = rr.findByItemIdAndItemType(wordBookId, ItemTypeEnum.WORD, sort);

        List<ReviewDTO> reviewDTOList = new ArrayList<>();

        for (ReviewEntity entity : reviewEntityList) {
            ReviewDTO reviewDTO = ReviewDTO.builder()
                    .rating(entity.getRating())
                    .content(entity.getContent())
                    .createdDate(entity.getCreatedDate())
                    .updateDate(entity.getUpdateDate())
                    .nickname(memberEntity.getNickname())
                    .build();
            reviewDTOList.add(reviewDTO);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("reviewDTOList", reviewDTOList);

        return result;

    }

    /**
     * 시험지 리뷰 생성 정보 저장
     * @author MinCheolHa
     * @param user user 객체를 가져옵니다.
     * @param wordBookId 단어장 키값을 가져옵니다.
     */
    @Override
    public void createReview(AuthenticatedUser user, int wordBookId, ReviewDTO reviewDTO) {

        wbr.findById(wordBookId).orElseThrow(()-> new EntityNotFoundException("시험지 정보가 존재하지 않습니다."));

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(()-> new EntityNotFoundException("회원 정보가 존재하지 않습니다."));

        ReviewEntity reviewEntity = ReviewEntity.builder()
                .rating(reviewDTO.getRating())
                .content(reviewDTO.getContent())
                .itemId(wordBookId)
                .memberId(memberEntity)
                .itemType(ItemTypeEnum.WORD)
                .build();
        rr.save(reviewEntity);

    }

    /**
     * 리뷰 수정 페이지 이동
     * @author MinCheolHa
     * @param user user 객체를 가져옵니다.
     * @param wordBookId 단어장 키값을 가져옵니다.
     * @param reviewId 리뷰 키값을 가져옵니다.
     * @return reviewDTO를 반환합니다.
     */
    @Override
    public ReviewDTO selectUpdatedReviewInfo(AuthenticatedUser user, int wordBookId, int reviewId) {

        wbr.findById(wordBookId).orElseThrow(()-> new EntityNotFoundException("단어장 정보를 찾을 수 없습니다."));

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        ReviewEntity reviewEntity = rr.findById(reviewId)
                .orElseThrow(()-> new EntityNotFoundException("리뷰 정보를 찾을 수 없습니다."));

        if (!reviewEntity.getMemberId().getMemberId().equals(memberEntity.getMemberId())){
            throw new AccessDeniedException("인증회원과 리뷰정보가 일치하지 않습니다.");
        }

        ReviewDTO reviewDTO = ReviewDTO.builder()
                .rating(reviewEntity.getRating())
                .content(reviewEntity.getContent())
                .build();

        return reviewDTO;

    }

    /**
     * 리뷰 수정
     * @author MinCheolHa
     * @param user user 객체를 가져옵니다.
     * @param reviewId 리뷰 키값을 가져옵니다.
     */
    @Override
    public void updateReview(AuthenticatedUser user, int reviewId, ReviewDTO reviewDTO) {

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        ReviewEntity reviewEntity = rr.findById(reviewId)
                .orElseThrow(()-> new EntityNotFoundException("리뷰 정보를 찾을 수 없습니다."));

        if (!reviewEntity.getMemberId().getMemberId().equals(memberEntity.getMemberId())){
            throw new AccessDeniedException("인증회원과 리뷰정보가 일치하지 않습니다.");
        }

        reviewEntity.setRating(reviewDTO.getRating());
        reviewEntity.setContent(reviewDTO.getContent());

    }

    /**
     * 리뷰 삭제
     * @author MinCheolHa
     * @param user user 객체를 가져옵니다.
     * @param reviewId 리뷰 키값을 가져옵니다.
     */
    @Override
    public void deleteReview(AuthenticatedUser user, int reviewId) {

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        ReviewEntity reviewEntity = rr.findById(reviewId)
                .orElseThrow(()-> new EntityNotFoundException("리뷰 정보를 찾을 수 없습니다."));

        if (!reviewEntity.getMemberId().getMemberId().equals(memberEntity.getMemberId())){
            throw new AccessDeniedException("인증회원과 리뷰정보가 일치하지 않습니다.");
        }

        rr.delete(reviewEntity);

    }


    /**
     * 추천 단어장 반환
     * @author MinCheolHa
     * @param user user 객체를 가져 옵니다.
     * @return WordBookDTO 형식의 recommendWord 를 반환 합니다.
     */
    @Override
    public List<WordBookDTO> recommendedWordBook(AuthenticatedUser user) {
        mr.findById(user.getId()).orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(0, 3);
        List<WordBookEntity> wordBookEntity = wbr.findRandomWordBooks(pageable);

        List<WordBookDTO> recommendedWordBook = new ArrayList<>();

        for (WordBookEntity entity : wordBookEntity) {
            WordBookDTO wordBookDTO = WordBookDTO.builder()
                    .wordBookId(entity.getWordBookId())
                    .title(entity.getTitle())
                    .content(entity.getContent())
                    .imagePath(entity.getImagePath())
                    .wordItemCount(wbr.getWordItemCount(entity.getWordBookId()))
                    .likeCount(ilr.getLikeCount(entity.getWordBookId()))
                    .shareCount(wbr.getShareCount(entity.getCreatorId().getMemberId()))
                    .build();
            recommendedWordBook.add(wordBookDTO);

        }

        return recommendedWordBook;
    }

    /**
     * 이번주 인기 단어장 반환
     * @author MinCheolHa
     * @param user user 객체를 가져 옵니다.
     * @return WordBookDTO 형식의 popularityWord 를 반환 합니다.
     */
    @Override
    public List<WordBookDTO> likedWordBook(AuthenticatedUser user) {

        mr.findById(user.getId()).orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monday = now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
        LocalDateTime sunday = now.with(DayOfWeek.SUNDAY).toLocalDate().atTime(23, 59, 59);

        Pageable pageable = PageRequest.of(0, 3);
        List<WordBookEntity> wordBookEntity = wbr.findPopularWordsThisWeek(monday, sunday, pageable);

        List<WordBookDTO> likedWordBook = new ArrayList<>();

        for (WordBookEntity entity : wordBookEntity) {
            WordBookDTO wordBookDTO = WordBookDTO.builder()
                    .wordBookId(entity.getWordBookId())
                    .title(entity.getTitle())
                    .content(entity.getContent())
                    .imagePath(entity.getImagePath())
                    .wordItemCount(wbr.getWordItemCount(entity.getWordBookId()))
                    .likeCount(ilr.getLikeCount(entity.getWordBookId()))
                    .shareCount(wbr.getShareCount(entity.getCreatorId().getMemberId()))
                    .build();
            likedWordBook.add(wordBookDTO);

        }

        return likedWordBook;

    }

    /**
     * 많이 공유된 단어장 반환
     * @author MinCheolHa
     * @param user user 객체를 가져 옵니다.
     * @return WordBookDTO 형식의 muchSharedWord 를 반환 합니다.
     */
    @Override
    public List<WordBookDTO> muchSharedWordBook(AuthenticatedUser user) {

        mr.findById(user.getId()).orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(0, 3);
        List<WordBookEntity> wordBookEntity = wbr.findSortedWordBooksByShareCount(pageable);

        List<WordBookDTO> muchSharedWordBook = new ArrayList<>();

        for (WordBookEntity entity : wordBookEntity) {
            WordBookDTO wordBookDTO = WordBookDTO.builder()
                    .wordBookId(entity.getWordBookId())
                    .title(entity.getTitle())
                    .content(entity.getContent())
                    .imagePath(entity.getImagePath())
                    .wordItemCount(wbr.getWordItemCount(entity.getWordBookId()))
                    .likeCount(ilr.getLikeCount(entity.getWordBookId()))
                    .shareCount(wbr.getShareCount(entity.getCreatorId().getMemberId()))
                    .build();
            muchSharedWordBook.add(wordBookDTO);

        }

        return muchSharedWordBook;

    }


}
