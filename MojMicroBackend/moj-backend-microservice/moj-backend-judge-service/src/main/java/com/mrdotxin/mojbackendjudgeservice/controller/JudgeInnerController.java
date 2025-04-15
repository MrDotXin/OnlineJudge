package com.mrdotxin.mojbackendjudgeservice.controller;

import com.mrdotxin.moj.backend.model.entity.QuestionSubmit;
import com.mrdotxin.moj.backend.model.vo.QuestionSubmitVO;
import com.mrdotxin.mojbackendjudgeservice.service.JudgeService;
import com.mrdotxin.mojbackendserviceclient.service.JudgeFeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/inner")
public class JudgeInnerController {

}
