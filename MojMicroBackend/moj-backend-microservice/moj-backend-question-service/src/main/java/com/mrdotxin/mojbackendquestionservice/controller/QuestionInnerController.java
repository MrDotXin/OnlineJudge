package com.mrdotxin.mojbackendquestionservice.controller;

import com.mrdotxin.moj.backend.model.entity.Question;
import com.mrdotxin.moj.backend.model.entity.QuestionSubmit;
import com.mrdotxin.mojbackendquestionservice.service.QuestionService;
import com.mrdotxin.mojbackendquestionservice.service.QuestionSubmitService;
import com.mrdotxin.mojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Override
    @GetMapping("/get/id")
    public Question getQuestionById(@RequestParam("id") long id) {
        return questionService.getById(id);
    }

    @Override
    public @GetMapping("/question_submit/get/id")
    QuestionSubmit getQuestionSubmitById(@RequestParam("id") long id) {
        return questionSubmitService.getById(id);
    }

    @Override
    public @PostMapping("/question_submit/update")
    boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit) {
        return questionSubmitService.updateById(questionSubmit);
    }


}
