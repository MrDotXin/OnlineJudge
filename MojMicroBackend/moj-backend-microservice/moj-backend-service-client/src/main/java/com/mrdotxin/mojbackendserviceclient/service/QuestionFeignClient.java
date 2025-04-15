package com.mrdotxin.mojbackendserviceclient.service;

import com.mrdotxin.moj.backend.model.entity.Question;
import com.mrdotxin.moj.backend.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@FeignClient(name = "moj-backend-question-service", path = "/api/question/inner")
public interface    QuestionFeignClient {

    @GetMapping("/get/id")
    Question getQuestionById(@RequestParam("id") long id);


    @GetMapping("/question_submit/get/id")
    QuestionSubmit getQuestionSubmitById(@RequestParam("id") long id);

    @PostMapping("/question_submit/update")
    boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit);
}
