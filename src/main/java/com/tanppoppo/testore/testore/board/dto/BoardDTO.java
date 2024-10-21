package com.tanppoppo.testore.testore.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 게시판 DTO 클래스
 * @author dhkdtjs1541
 * @version 0.1.0
 * @since 0.1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {

    private Integer boardId; // 게시물 ID
    private Integer memberId; // 작성자 ID(Member ID)
    private String boardType; // 게시물 타입 (enum)
    private String title; // 제목
    private String content; // 내용
    private Integer viewCount; // 조회수
    private LocalDateTime createdDate; // 작성일
    private LocalDateTime updatedDate; // 수정일

    private String nickname;
    private int commentsCount;
}
