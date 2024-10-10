package com.tanppoppo.testore.testore.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    private Integer commentId; // 댓글 ID
    private Integer boardId; // 게시물 ID(Board ID)
    private Integer memberId; // 작성자 ID(Member ID)
    private String content; // 내용
    private LocalDateTime createdDate; // 작성일
    private LocalDateTime updatedDate; // 수정일
    private String nickname;
}