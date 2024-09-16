package com.tanppoppo.testore.testore.exam.service;

import com.tanppoppo.testore.testore.exam.dto.ExamPaperDTO;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;

import java.util.List;

public interface ExamService {

    int examCreate(ExamPaperDTO examPaperDTO, AuthenticatedUser user);

    List<ExamPaperDTO> getListItems();
}
