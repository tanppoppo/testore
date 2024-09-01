package com.tanppoppo.testore.testore.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member")
public class MemberEntity {
    @Id
    @Column(name = "member_id", length = 20)
    String memberId;

    @Column(name = "member_password", nullable = false, length = 100)
    String memberPassword;

    @Column(name = "member_name", nullable = false, length = 20)
    String memberName;

    @Column(name = "phone", nullable = false, length = 20)
    String phone;
}
