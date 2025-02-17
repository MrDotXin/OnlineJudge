package com.MrDotXin.moj.controller;

import com.MrDotXin.moj.common.BaseResponse;
import com.MrDotXin.moj.common.ErrorCode;
import com.MrDotXin.moj.common.ResultUtils;
import com.MrDotXin.moj.exception.BusinessException;
import com.MrDotXin.moj.model.dto.postthumb.PostThumbAddRequest;
import com.MrDotXin.moj.model.entity.User;
import com.MrDotXin.moj.service.PostThumbService;
import com.MrDotXin.moj.service.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 帖子点赞接口
 *
 * 
 * 
 */
@RestController
@RequestMapping("/post_thumb")
public class PostThumbController {

    @Resource
    private PostThumbService postThumbService;

    @Resource
    private UserService userService;

    /**
     * 点赞 / 取消点赞
     *
     * @param postThumbAddRequest
     * @param request
     * @return resultNum 本次点赞变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> doThumb(@RequestBody PostThumbAddRequest postThumbAddRequest,
            HttpServletRequest request) {
        if (postThumbAddRequest == null || postThumbAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userService.getLoginUser(request);
        long postId = postThumbAddRequest.getPostId();
        int result = postThumbService.doPostThumb(postId, loginUser);
        return ResultUtils.success(result);
    }

}
