package com.tanppoppo.testore.testore.exam.repository;

import com.tanppoppo.testore.testore.common.util.ExamStatusEnum;
import com.tanppoppo.testore.testore.exam.entity.ExamResultEntity;
import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResultEntity, Integer> {
    List<ExamResultEntity> findByMemberIdAndStatus(MemberEntity memberEntity, ExamStatusEnum status);

}
