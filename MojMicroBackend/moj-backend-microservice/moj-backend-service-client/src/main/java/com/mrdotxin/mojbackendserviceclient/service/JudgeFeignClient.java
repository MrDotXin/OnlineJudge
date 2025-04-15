package com.mrdotxin.mojbackendserviceclient.service;

import com.mrdotxin.moj.backend.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "moj-backend-judge-service", path = "/api/judge/inner")
public interface JudgeFeignClient {
    /*  
     * 判题服务
     */

    @PostMapping("/execute")
    QuestionSubmit doJudge(@RequestParam("questionSubmitId") long questionSubmitId);
}
