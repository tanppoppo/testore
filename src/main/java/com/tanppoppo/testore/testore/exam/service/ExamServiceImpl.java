package com.tanppoppo.testore.testore.exam.service;

import com.tanppoppo.testore.testore.exam.repository.ExamItemRepository;
import com.tanppoppo.testore.testore.exam.repository.ExamPaperRepository;
import com.tanppoppo.testore.testore.exam.repository.ExamResultRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ExamServiceImpl implements ExamService {
    private final ExamPaperRepository epr;
    private final ExamItemRepository eir;
    private final ExamResultRepository err;
}
