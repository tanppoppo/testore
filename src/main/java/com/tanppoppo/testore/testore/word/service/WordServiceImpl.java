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
import com.tanppoppo.testore.testore.word.dto.WordDTO;
import com.tanppoppo.testore.testore.word.entity.LearningRecordEntity;
import com.tanppoppo.testore.testore.word.entity.WordBookEntity;
import com.tanppoppo.testore.testore.word.entity.WordEntity;
import com.tanppoppo.testore.testore.word.repository.LearningRecordRepository;
import com.tanppoppo.testore.testore.word.repository.WordBookRepository;
import com.tanppoppo.testore.testore.word.repository.WordRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
    private final LearningRecordRepository lrr;


    /**
     * 특정 사용자가 소유한 단어장의 개수를 반환
     * @author dhkdtjs1541
     * @param ownerId 단어장 소유자의 ID
     * @return 소유한 단어장의 개수
     */
    @Override
    public Integer countWordBooksByOwnerId(Integer ownerId) {
        return wbr.countWordBooksByOwnerId(ownerId);
    }

    /**
     * 단어장 생성 정보 저장
     * @author MinCheolHa
     * @param wordBookDTO 단어장 정보를 입력합니다.
     * @param user 사용자 인증 정보를 가져옵니다.
     * @return WordBookId를 반환합니다.
     */
    @Transactional
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
     * @param wordDTO 단어 정보를 입력합니다.
     * @param wordBookId 단어장 Id를 가져옵니다.
     * @param userId 사용자 정보를 가져옵니다.
     */
    @Transactional
    @Override
    public Integer addWords(WordDTO wordDTO, Integer wordBookId, Integer userId) {

        WordBookEntity wordBookEntity = wbr.findById(wordBookId)
                .orElseThrow(()-> new EntityNotFoundException("단어장 정보를 찾을 수 없습니다."));

        MemberEntity memberEntity = mr.findById(userId)
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        if (!wordBookEntity.getOwnerId().equals(memberEntity.getMemberId())) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        // 이미 단어가 존재하는지 체크
        List<WordEntity> existedWordEntityList = wr.findByWordBookIdOrderByWordNumDesc(wordBookEntity);
        if (existedWordEntityList.isEmpty()) {
            wordDTO.setWordNum(1);
        } else {
            WordEntity firstWordEntity = existedWordEntityList.get(0);
            wordDTO.setWordNum(firstWordEntity.getWordNum()+1);
        }

        WordEntity wordEntity = WordEntity.builder()
                .wordNum(wordDTO.getWordNum())
                .text1(wordDTO.getText1())
                .text2(wordDTO.getText2())
                .text3(wordDTO.getText3())
                .wordBookId(wordBookEntity)
                .build();

        wr.save(wordEntity);

        return wordDTO.getWordNum() + 1;


    }

    @Override
    public Integer checkWordNum(int wordBookId, Integer userId) {

        WordBookEntity wordBookEntity = wbr.findById(wordBookId)
                .orElseThrow(()-> new EntityNotFoundException("단어장 정보를 찾을 수 없습니다."));

        MemberEntity memberEntity = mr.findById(userId)
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        if (!wordBookEntity.getOwnerId().equals(memberEntity.getMemberId())) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        // 이미 단어가 존재하는지 체크
        List<WordEntity> existedWordEntityList = wr.findByWordBookIdOrderByWordNumDesc(wordBookEntity);
        if (existedWordEntityList.isEmpty()) {
            return 1;
        } else {
            WordEntity firstWordEntity = existedWordEntityList.get(0);
            return firstWordEntity.getWordNum() + 1;
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

        List<WordBookEntity> wordBookEntityList = wbr.findByOwnerIdWithBookmarks(memberEntity.getMemberId(), ItemTypeEnum.WORD);

        List<WordBookDTO> items = new ArrayList<>();

        for (WordBookEntity entity : wordBookEntityList) {
            WordBookDTO wordBookDTO = WordBookDTO.builder()
                    .wordBookId(entity.getWordBookId())
                    .title(entity.getTitle())
                    .content(entity.getContent())
                    .imagePath(entity.getImagePath())
                    .wordItemCount(wbr.getWordItemCount(entity.getWordBookId()))
                    .likeCount(ilr.getLikeCount(entity.getWordBookId(), ItemTypeEnum.WORD))
                    .shareCount(lrr.countByWordBookId(entity))
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

        MemberEntity memberEntity = mr.findById(user.getId())
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
                .likeCount(ilr.getLikeCount(wordBookEntity.getWordBookId(), ItemTypeEnum.WORD))
                .shareCount(lrr.countByWordBookId(wordBookEntity))
                .build();

        Map<String, Object> detail = new HashMap<>();
        detail.put("wordBookDTO", wordBookDTO);
        detail.put("nickname", wordBookEntity.getCreatorId().getNickname());
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
    @Transactional
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
                    .memberId(entity.getMemberId().getMemberId())
                    .reviewId(entity.getReviewId())
                    .rating(entity.getRating())
                    .content(entity.getContent())
                    .createdDate(entity.getCreatedDate())
                    .updateDate(entity.getUpdateDate())
                    .nickname(entity.getMemberId().getNickname())
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
    @Transactional
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
     * @param reviewId 리뷰 키값을 가져옵니다.
     * @return reviewDTO를 반환합니다.
     */
    @Override
    public ReviewDTO selectUpdatedReviewInfo(AuthenticatedUser user, int reviewId) {

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        ReviewEntity reviewEntity = rr.findById(reviewId)
                .orElseThrow(()-> new EntityNotFoundException("리뷰 정보를 찾을 수 없습니다."));

        if (!reviewEntity.getMemberId().getMemberId().equals(memberEntity.getMemberId())){
            throw new AccessDeniedException("인증회원과 리뷰정보가 일치하지 않습니다.");
        }

        ReviewDTO reviewDTO = ReviewDTO.builder()
                .itemId(reviewEntity.getItemId())
                .reviewId(reviewEntity.getReviewId())
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
    @Transactional
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
    @Transactional
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
                    .likeCount(ilr.getLikeCount(entity.getWordBookId(), ItemTypeEnum.WORD))
                    .shareCount(lrr.countByWordBookId(entity))
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
        List<WordBookEntity> wordBookEntity = wbr.findPopularWordBooksThisWeek(monday, sunday, pageable, ItemTypeEnum.WORD);

        List<WordBookDTO> likedWordBook = new ArrayList<>();

        for (WordBookEntity entity : wordBookEntity) {
            WordBookDTO wordBookDTO = WordBookDTO.builder()
                    .wordBookId(entity.getWordBookId())
                    .title(entity.getTitle())
                    .content(entity.getContent())
                    .imagePath(entity.getImagePath())
                    .wordItemCount(wbr.getWordItemCount(entity.getWordBookId()))
                    .likeCount(ilr.getLikeCount(entity.getWordBookId(), ItemTypeEnum.WORD))
                    .shareCount(lrr.countByWordBookId(entity))
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
        List<WordBookEntity> wordBookEntity = wbr.findWordBooksOrderByLearningCountDesc(pageable);

        List<WordBookDTO> muchSharedWordBook = new ArrayList<>();

        for (WordBookEntity entity : wordBookEntity) {
            WordBookDTO wordBookDTO = WordBookDTO.builder()
                    .wordBookId(entity.getWordBookId())
                    .title(entity.getTitle())
                    .content(entity.getContent())
                    .imagePath(entity.getImagePath())
                    .wordItemCount(wbr.getWordItemCount(entity.getWordBookId()))
                    .likeCount(ilr.getLikeCount(entity.getWordBookId(), ItemTypeEnum.WORD))
                    .shareCount(lrr.countByWordBookId(entity))
                    .build();
            muchSharedWordBook.add(wordBookDTO);

        }

        return muchSharedWordBook;

    }

    /**
     * 단어장 찾기 기능
     * @author KIMGEON64
     * @param user user 객체를 가져 옵니다.
     * @param keyword 사용자 입력 값을 가져옵니다.
     * @return wordBookDTOS 시험 결과 dto 리스트를 반환합니다.
     */
    @Override
    public List<WordBookDTO> findWordBookByMemberId(AuthenticatedUser user, String keyword) {

        mr.findById(user.getId()).orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        List<WordBookEntity> wordBookEntities;
        Sort sort = Sort.by(Sort.Direction.DESC, "wordBookId");

        if (keyword == null) {
            wordBookEntities = wbr.findByPublicOption(true, sort);
        } else {
            wordBookEntities = wbr.findByPublicOptionAndTitleContaining(true, keyword, sort);
        }

        List<WordBookDTO> wordBookDTOS = new ArrayList<>();

        for (WordBookEntity entity : wordBookEntities) {

            WordBookEntity wordBookEntity = wbr.findById(entity.getWordBookId())
                    .orElseThrow(() -> new EntityNotFoundException("단어장 정보를 찾을 수 없습니다."));

            WordBookDTO wordBookDTO = WordBookDTO.builder()
                    .wordBookId(wordBookEntity.getWordBookId())
                    .title(wordBookEntity.getTitle())
                    .content(wordBookEntity.getContent())
                    .imagePath(wordBookEntity.getImagePath())
                    .wordItemCount(wbr.getWordItemCount(wordBookEntity.getWordBookId()))
                    .likeCount(ilr.getLikeCount(wordBookEntity.getWordBookId(), ItemTypeEnum.WORD))
                    .shareCount(lrr.countByWordBookId(entity))
                    .isBookmarked(br.getBookmarkState(user.getId(), wordBookEntity.getWordBookId(), ItemTypeEnum.WORD))
                    .build();
            wordBookDTOS.add(wordBookDTO);
        }
        return wordBookDTOS;

    }

    /**
     * 업데이트할 단어장 조회
     * @author KIMGEON64
     * @param wordbookId 단어장 키값을 가져옵니다.
     * @param user user 객체를 가져옵니다.
     * @return wordbookDTO를 반환합니다.
     */
    @Override
    public WordBookDTO selectUpdatedWordbookInfo(int wordbookId, AuthenticatedUser user) {
        
        WordBookEntity wordBookEntity = wbr.findById(wordbookId)
                .orElseThrow(()-> new EntityNotFoundException("단어장 정보를 찾을 수 없습니다."));

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        if (!memberEntity.getMemberId().equals(wordBookEntity.getOwnerId()) && !wordBookEntity.getPublicOption()){
            throw new AccessDeniedException("공개된 단어장이 아닙니다.");
        }

        WordBookDTO wordBookDTO = WordBookDTO.builder()
                .wordBookId(wordBookEntity.getWordBookId())
                .title(wordBookEntity.getTitle())
                .content(wordBookEntity.getContent())
                .imagePath(wordBookEntity.getImagePath())
                .build();

        return wordBookDTO;

    }

    /**
     * 단어장 표제 수정
     * @author KIMGEON64
     * @param wordBookDTO 단어장 표제값을 가져옵니다.
     * @param user user 객체를 가져옵니다.
     */
    @Transactional
    @Override
    public void updateWordBook(WordBookDTO wordBookDTO, AuthenticatedUser user) {

        WordBookEntity wordBookEntity = wbr.findById(wordBookDTO.getWordBookId())
                .orElseThrow(()-> new EntityNotFoundException("단어장 정보를 찾을 수 없습니다."));

        MemberEntity memberEntity = mr.findById(wordBookEntity.getCreatorId().getMemberId())
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        if (!user.getId().equals(wordBookEntity.getOwnerId())){
            if (!wordBookEntity.getPublicOption()) {
                throw new AccessDeniedException("공개된 단어장이 아닙니다.");
            }
            throw new AccessDeniedException("권한이 없습니다.");
        }

        wordBookEntity.setTitle(wordBookDTO.getTitle());
        wordBookEntity.setContent(wordBookDTO.getContent());

    }

    /**
     * 시험지 삭제
     * @author gyahury
     * @param wordBookId 시험지 id를 가져옵니다.
     * @param userId user id를 가져옵니다.
     */
    @Transactional
    @Override
    public void deleteWordBook(int wordBookId, Integer userId) {
        WordBookEntity wordBookEntity = wbr.findById(wordBookId)
                .orElseThrow(()-> new EntityNotFoundException("단어장 정보를 찾을 수 없습니다."));

        if (!userId.equals(wordBookEntity.getOwnerId())){
            if (!wordBookEntity.getPublicOption()) {
                throw new AccessDeniedException("공개된 단어장이 아닙니다.");
            }
            throw new AccessDeniedException("권한이 없습니다.");
        }

        wbr.delete(wordBookEntity);

    }

    /**
     * 단어 리스트 목록 반환
     * @author gyahury
     * @param wordBookId 단어장 id를 가져옵니다.
     * @param userId 유저 id를 가져옵니다.
     * @return 단어 리스트를 반환합니다.
     */
    @Override
    public List<WordDTO> selectWordList(int wordBookId, Integer userId) {

        WordBookEntity wordBookEntity = wbr.findById(wordBookId)
                .orElseThrow(()-> new EntityNotFoundException("단어장 정보를 찾을 수 없습니다."));

        MemberEntity memberEntity = mr.findById(wordBookEntity.getCreatorId().getMemberId())
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        if (!memberEntity.getMemberId().equals(wordBookEntity.getOwnerId()) && !wordBookEntity.getPublicOption()){
            throw new AccessDeniedException("공개된 단어장이 아닙니다.");
        }

        List<WordEntity> wordEntityList = wr.findAllByWordBookId(wordBookEntity);

        if (wordEntityList.isEmpty()) {
            throw new IllegalStateException("비어있는 단어장입니다.");
        }

        List<WordDTO> wordDTOList = new ArrayList<>();



        for (WordEntity entity : wordEntityList) {

            WordDTO wordDTO = WordDTO.builder()
                    .wordId(entity.getWordId())
                    .wordNum(entity.getWordNum())
                    .text1(entity.getText1())
                    .text2(entity.getText2())
                    .text3(entity.getText3())
                    .notes(entity.getNotes())
                    .checked(entity.getChecked())
                    .build();
            wordDTOList.add(wordDTO);
        }

        return wordDTOList;

    }

    /**
     * 학습 기록 저장
     * @author gyahury
     * @param userId userId를 가져옵니다.
     * @param wordbookId wordbookId를 가져옵니다.
     */
    @Transactional
    @Override
    public void addLearningRecord(Integer userId, int wordbookId) {

        MemberEntity memberEntity = mr.findById(userId)
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        WordBookEntity wordBookEntity = wbr.findById(wordbookId)
                .orElseThrow(()-> new EntityNotFoundException("단어장을 찾을 수 없습니다."));

        LearningRecordEntity learningRecodeEntity = LearningRecordEntity.builder()
                .wordBookId(wordBookEntity)
                .memberId(memberEntity)
                .build();

        lrr.save(learningRecodeEntity);
        
    }

    /**
     * 단어 세부 수정 LIST 목록 반환
     * @author KIMGEON64
     * @param user user 객체를 가져옵니다.
     * @param wordbookId 단어장 키값을 가져옵니다.
     * @return
     */
    @Override
    public List<WordDTO> getWordsForUpdate(AuthenticatedUser user, int wordbookId) {

        WordBookEntity wordBookEntity = wbr.findById(wordbookId)
                .orElseThrow(()-> new EntityNotFoundException("단어장 정보를 찾을 수 없습니다."));

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        if (!memberEntity.getMemberId().equals(wordBookEntity.getOwnerId())){
            if (!wordBookEntity.getPublicOption()) {
                throw new AccessDeniedException("공개된 단어장이 아닙니다.");
            }
            throw new AccessDeniedException("권한이 없습니다.");
        }

        if (!user.getId().equals(wordBookEntity.getOwnerId()) && !wordBookEntity.getPublicOption()){
            throw new AccessDeniedException("공개된 단어장이 아닙니다.");
        }

        List<WordEntity> wordEntityList = wr.findByWordBookIdOrderByWordNumDesc(wordBookEntity);

        List<WordDTO> wordDTOList = new ArrayList<>();

        for (WordEntity entity : wordEntityList) {

            WordDTO wordDTO = WordDTO.builder()
                    .wordId(entity.getWordId())
                    .wordNum(entity.getWordNum())
                    .text1(entity.getText1())
                    .text2(entity.getText2())
                    .text3(entity.getText3())
                    .notes(entity.getNotes())
                    .checked(entity.getChecked())
                    .build();
            wordDTOList.add(wordDTO);
        }
        return wordDTOList;

    }

    /**
     * 단어 세부 값을 반환
     * @author KIMGEON64
     * @param user user 객체를 가져옵니다.
     * @param wordNum 단어 고유 번호를 가져옵니다.
     * @return WordDTO 객체를 반환합니다.
     */
    @Override
    public WordDTO getWordDetails(AuthenticatedUser user, Integer wordNum) {

        mr.findById(user.getId()).orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        WordEntity wordEntity = wr.findById(wordNum)
                .orElseThrow(()-> new EntityNotFoundException("단어 정보를 찾을 수 없습니다."));

        WordDTO wordDTO = WordDTO.builder()
                .wordbookId(wordEntity.getWordBookId().getWordBookId())
                .wordId(wordEntity.getWordId())
                .wordNum(wordEntity.getWordNum())
                .text1(wordEntity.getText1())
                .text2(wordEntity.getText2())
                .text3(wordEntity.getText3())
                .notes(wordEntity.getNotes())
                .checked(wordEntity.getChecked())
                .build();

        return wordDTO;

    }

    /**
     * 단어 세부 수정 사항 저장
     * @author KIMGEON64
     * @param user user 객체를 가져옵니다.
     * @param wordbookId 단어장 키값을 가져옵니다.
     * @param wordDTO wordDTO 객체를 가져옵니다.
     */
    @Transactional
    @Override
    public void saveEditedWord(AuthenticatedUser user, int wordbookId, WordDTO wordDTO) {

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        WordBookEntity wordBookEntity = wbr.findById(wordbookId)
                .orElseThrow(()-> new EntityNotFoundException("단어장 정보를 찾을 수 없습니다."));

        WordEntity wordEntity = wr.findById(wordDTO.getWordNum())
                .orElseThrow(()-> new EntityNotFoundException("단어 정보를 찾을 수 없습니다."));

        if (!memberEntity.getMemberId().equals(wordBookEntity.getOwnerId())){
            if (!wordBookEntity.getPublicOption()) {
                throw new AccessDeniedException("공개된 단어장이 아닙니다.");
            }
            throw new AccessDeniedException("권한이 없습니다.");
        }

        if (!user.getId().equals(wordBookEntity.getOwnerId()) && !wordBookEntity.getPublicOption()){
            throw new AccessDeniedException("공개된 단어장이 아닙니다.");
        }

        wordEntity.setText1(wordDTO.getText1());
        wordEntity.setText2(wordDTO.getText2());
        wordEntity.setText3(wordDTO.getText3());
        wordEntity.setNotes(wordDTO.getNotes());
        wordEntity.setChecked(wordDTO.getChecked());

    }

    /**
     * 내가 좋아요한 단어장 조회
     * @author KIMGEON64
     * @param user user 객체를 가져 옵니다.
     * @param keyword 사용자 입력값은 가져 옵니다.
     * @return items dto 리스트를 반환합니다.
     */
    @Override
    public List<WordBookDTO> getLikedWordBook(AuthenticatedUser user, String keyword) {

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        List<WordBookEntity> wordBookEntityList;

        if (keyword == null) {
            wordBookEntityList = wbr.findLikedWordBooksByMemberId(memberEntity.getMemberId(), ItemTypeEnum.WORD);
        } else {
            wordBookEntityList = wbr.findWordBooksLikedByMemberAndKeyword(memberEntity.getMemberId(), ItemTypeEnum.WORD, keyword);
        }

        List<WordBookDTO> items = new ArrayList<>();

        for (WordBookEntity entity : wordBookEntityList) {

            WordBookEntity wordBookEntity = wbr.findById(entity.getWordBookId())
                    .orElseThrow(()-> new EntityNotFoundException("단어장 정보를 찾을 수 없습니다."));

            WordBookDTO wordBookDTO = WordBookDTO.builder()
                    .wordBookId(wordBookEntity.getWordBookId())
                    .title(wordBookEntity.getTitle())
                    .content(wordBookEntity.getContent())
                    .language(wordBookEntity.getLanguage())
                    .languageLevel(Integer.valueOf(wordBookEntity.getLanguageLevel()))
                    .membershipLevel(Integer.valueOf(wordBookEntity.getMembershipLevel()))
                    .creatorId(wordBookEntity.getCreatorId().getMemberId())
                    .ownerId(wordBookEntity.getOwnerId())
                    .imagePath(wordBookEntity.getImagePath())
                    .publicOption(wordBookEntity.getPublicOption())
                    .studiedDate(wordBookEntity.getStudiedDate())
                    .createdDate(wordBookEntity.getCreatedDate())
                    .updatedDate(wordBookEntity.getUpdatedDate())
                    .wordItemCount(wbr.getWordItemCount(wordBookEntity.getWordBookId()))
                    .likeCount(ilr.getLikeCount(wordBookEntity.getWordBookId(), ItemTypeEnum.WORD))
                    .shareCount(lrr.countByWordBookId(entity))
                    .isBookmarked(br.getBookmarkState(memberEntity.getMemberId(), wordBookEntity.getWordBookId(), ItemTypeEnum.EXAM))
                    .build();
            items.add(wordBookDTO);
        }

        return items;

    }

    /**
     * 내가 북마크한 단어장 조회
     * @author KIMGEON64
     * @param user user 객체를 가져 옵니다.
     * @param keyword 사용자 입력값은 가져 옵니다.
     * @return items dto 리스트를 반환합니다.
     */
    @Override
    public List<WordBookDTO> getBookmarkedWordBook(AuthenticatedUser user, String keyword) {

        MemberEntity memberEntity = mr.findById(user.getId())
                .orElseThrow(()-> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        List<WordBookEntity> wordBookEntityList;

        if (keyword == null) {
            wordBookEntityList = wbr.findBookmarkedWordBooksByMemberId(memberEntity.getMemberId(), ItemTypeEnum.WORD);
        } else {
            wordBookEntityList = wbr.findWordBooksBookmarkedByMemberAndKeyword(memberEntity.getMemberId(), ItemTypeEnum.WORD, keyword);
        }

        List<WordBookDTO> items = new ArrayList<>();

        for (WordBookEntity entity : wordBookEntityList) {

            WordBookEntity wordBookEntity = wbr.findById(entity.getWordBookId())
                    .orElseThrow(()-> new EntityNotFoundException("단어장 정보를 찾을 수 없습니다."));

            WordBookDTO wordBookDTO = WordBookDTO.builder()
                    .wordBookId(wordBookEntity.getWordBookId())
                    .title(wordBookEntity.getTitle())
                    .content(wordBookEntity.getContent())
                    .language(wordBookEntity.getLanguage())
                    .languageLevel(Integer.valueOf(wordBookEntity.getLanguageLevel()))
                    .membershipLevel(Integer.valueOf(wordBookEntity.getMembershipLevel()))
                    .creatorId(wordBookEntity.getCreatorId().getMemberId())
                    .ownerId(wordBookEntity.getOwnerId())
                    .imagePath(wordBookEntity.getImagePath())
                    .publicOption(wordBookEntity.getPublicOption())
                    .studiedDate(wordBookEntity.getStudiedDate())
                    .createdDate(wordBookEntity.getCreatedDate())
                    .updatedDate(wordBookEntity.getUpdatedDate())
                    .wordItemCount(wbr.getWordItemCount(wordBookEntity.getWordBookId()))
                    .likeCount(ilr.getLikeCount(wordBookEntity.getWordBookId(), ItemTypeEnum.WORD))
                    .shareCount(lrr.countByWordBookId(entity))
                    .isBookmarked(br.getBookmarkState(memberEntity.getMemberId(), wordBookEntity.getWordBookId(), ItemTypeEnum.WORD))
                    .build();
            items.add(wordBookDTO);
        }

        return items;

    }



}
