package com.tanppoppo.testore.testore.util;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 메시지 전달을 위한 전역 유틸 클래스
 * @author gyahury
 * @version 0.1.0
 * @since 0.1.0
 */
public class MessageUtil {

    /**
     * 플래시 토스트 메시지 전달
     * @author gyahury
     * @param redirectAttributes 리다이렉트 속성 객체를 전달합니다.
     * @param isSuccess 성공, 실패 여부를 전달합니다.
     * @param message 메시지를 전달합니다.
     */
    public static void setFlashToastMessage(RedirectAttributes redirectAttributes, boolean isSuccess, String message) {
        redirectAttributes.addFlashAttribute("toastShown", true);
        redirectAttributes.addFlashAttribute("toastMessage", message);
        redirectAttributes.addFlashAttribute("isSuccess", isSuccess);
    }

    /**
     * 모델 토스트 메시지 전달
     * @author gyahury
     * @param model 모델 객체를 전달합니다.
     * @param isSuccess 성공, 실패 여부를 전달합니다.
     * @param message 메시지를 전달합니다.
     */
    public static void setModelToastMessage(Model model, boolean isSuccess, String message) {
        model.addAttribute("toastShown", true);
        model.addAttribute("toastMessage", message);
        model.addAttribute("isSuccess", isSuccess);
    }

    /**
     * 플래시 모달 메시지 전달
     * @author gyahury
     * @param redirectAttributes 리다이렉트 속성 객체를 전달합니다.
     * @param message 모달 메시지를 전달합니다.
     * @param canCancel 모달 취소 버튼의 존재 여부를 전달합니다.
     * @param link 모달 링크를 전달합니다.
     */
    public static void setFlashModalMessage(RedirectAttributes redirectAttributes, String message, Boolean canCancel, String link) {
        redirectAttributes.addFlashAttribute("modalShown", true);
        redirectAttributes.addFlashAttribute("modalMessage", message);
        redirectAttributes.addFlashAttribute("canCancel", canCancel);
        redirectAttributes.addFlashAttribute("link", link);
    }

    /**
     * 모델 모달 메시지 전달
     * @author gyahury
     * @param model 모델 객체를 전달합니다.
     * @param message 모달 메시지를 전달합니다.
     * @param canCancel 모달 취소 버튼의 존재 여부를 전달합니다.
     * @param link 모달 링크를 전달합니다.
     */
    public static void setModelModalMessage(Model model, String message, Boolean canCancel, String link) {
        model.addAttribute("modalShown", true);
        model.addAttribute("modalMessage", message);
        model.addAttribute("canCancel", canCancel);
        model.addAttribute("link", link);
    }

}
