package com.tanppoppo.testore.testore.word.service;

import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import com.tanppoppo.testore.testore.member.repository.MemberRepository;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;
import com.tanppoppo.testore.testore.word.dto.WordBookDTO;
import com.tanppoppo.testore.testore.word.entity.WordBookEntity;
import com.tanppoppo.testore.testore.word.entity.WordEntity;
import com.tanppoppo.testore.testore.word.repository.WordBookRepository;
import com.tanppoppo.testore.testore.word.repository.WordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class WordServiceImpl implements WordService {

    private final WordBookRepository wbr;
    private final WordRepository wr;
    private final MemberRepository mr;


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
     * 단어장 메인 리스트 조회
     * @author MinCheolHa
     * @param user 인증된 회원 정보를 가져옵니다.
     * @return List형식의 items를 반환합니다.
     */
    @Override
    public List<WordBookDTO> getListItems(AuthenticatedUser user) {

        Sort sort = Sort.by(Sort.Direction.DESC, "wordBookId");
        List<WordBookEntity> wordBookEntityList = wbr.findByOwnerId(user.getId(), sort);
        List<WordBookDTO> items = new ArrayList<>();

        for (WordBookEntity entity : wordBookEntityList) {
            WordBookDTO wordBookDTO = WordBookDTO.builder()
                    .wordBookId(entity.getWordBookId())
                    .title(entity.getTitle())
                    .content(entity.getContent())
                    .imagePath(entity.getImagePath())
                    .wordItemCount(wbr.getWordItemCount(entity.getWordBookId()))
                    .likeCount(wbr.getLikeCount(entity.getWordBookId()))
                    .shareCount(wbr.getShareCount(entity.getCreatorId().getMemberId()))
                    .build();
            items.add(wordBookDTO);
        }

        return items;
    }

    /**
     * 단어장 상세 정보 조회
     * @author MinCheolHa
     * @param wordbookId wordBookId 시험지 키값을 가져옵니다.
     * @return Map 객체 detail 을 반환합니다.
     */
    @Override
    public Map<String, Object> selectWordBookDetail(int wordbookId, AuthenticatedUser user) {

        WordBookEntity wordBookEntity = wbr.findById(wordbookId)
                .orElseThrow(()-> new EntityNotFoundException("단어장 정보를 찾을 수 없습니다."));

        MemberEntity memberEntity = mr.findById(wordBookEntity.getCreatorId().getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("회원 정보를 찾을수 없습니다."));

        WordBookDTO wordBookDTO = WordBookDTO.builder()
                .creatorId(wordBookEntity.getCreatorId().getMemberId())
                .imagePath(wordBookEntity.getImagePath())
                .title(wordBookEntity.getTitle())
                .createdDate(wordBookEntity.getCreatedDate())
                .content(wordBookEntity.getContent())
                .ownerId(wordBookEntity.getOwnerId())
                .publicOption(wordBookEntity.getPublicOption())
                .wordItemCount(wbr.getWordItemCount(wordBookEntity.getWordBookId()))
                .likeCount(wbr.getLikeCount(wordBookEntity.getWordBookId()))
                .shareCount(wbr.getShareCount(wordBookEntity.getCreatorId().getMemberId()))
                .build();

        Map<String, Object> detail = new HashMap<>();
        detail.put("wordBookDTO", wordBookDTO);
        detail.put("nickName", wordBookEntity.getCreatorId().getNickname());
        detail.put("reviewCount", wbr.getReviewCount(wordbookId));
//        detail.put("likeState", wbr.getLikeState(memberEntity.getMemberId(), wordbookId));
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



}
