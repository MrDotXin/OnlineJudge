package com.mrdotxin.mojbackendquestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mrdotxin.moj.backend.common.common.ErrorCode;
import com.mrdotxin.moj.backend.common.constant.CommonConstant;
import com.mrdotxin.moj.backend.common.exception.BusinessException;
import com.mrdotxin.moj.backend.common.utils.SqlUtils;
import com.mrdotxin.moj.backend.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.mrdotxin.moj.backend.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.mrdotxin.moj.backend.model.entity.Question;
import com.mrdotxin.moj.backend.model.entity.QuestionSubmit;
import com.mrdotxin.moj.backend.model.entity.User;
import com.mrdotxin.moj.backend.model.enums.JudgeInfoMessageEnum;
import com.mrdotxin.moj.backend.model.enums.QuestionSubmitLanguageEnum;
import com.mrdotxin.moj.backend.model.vo.QuestionSubmitVO;
import com.mrdotxin.mojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.mrdotxin.mojbackendquestionservice.message.JudgeRequestMessageProducer;
import com.mrdotxin.mojbackendquestionservice.service.QuestionService;
import com.mrdotxin.mojbackendquestionservice.service.QuestionSubmitService;
import com.mrdotxin.mojbackendserviceclient.service.UserFeignClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService {
    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userService;

    @Resource
    private JudgeRequestMessageProducer judgeRequestMessageProducer;
    /**
     * 提交
     *
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 判断实体是否存在，根据类别获取实体
        long questionId = questionSubmitAddRequest.getQuestionId();
        
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        String language = questionSubmitAddRequest.getLanguage();
        if (QuestionSubmitLanguageEnum.getEnumByValue(language) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        
        // 是否已提交
        long userId = loginUser.getId();
        // 每个用户串行提交
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(questionSubmitAddRequest.getLanguage());
        // 设置初始状态
        questionSubmit.setStatus(JudgeInfoMessageEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");

        // 次数限制
        QueryWrapper<QuestionSubmit> countQueryWrapper = new QueryWrapper<QuestionSubmit>();
        countQueryWrapper.eq("userId", userId);
        Long count = this.baseMapper.selectCount(countQueryWrapper);
        long maximumSavingSubmit = 50L;

        if (count == maximumSavingSubmit) {
            // 超过固定最大次数，开始强制删除
            QueryWrapper<QuestionSubmit> oldestSubmissionQuery = new QueryWrapper<QuestionSubmit>();
            oldestSubmissionQuery.orderBy(true, true, "createTime");

            QuestionSubmit oldestSubmit = this.getBaseMapper().selectOne(oldestSubmissionQuery);
            if (oldestSubmit.getStatus().equals(JudgeInfoMessageEnum.RUNNING.getValue())) {
                // 直接拒绝任务, 说明提交太过频繁
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "提交太频繁了!");
            } else { // 删除这个提交
                Long id = oldestSubmit.getId();
                this.getBaseMapper().deleteById(id);
            }
        }

        boolean result = this.save(questionSubmit);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        Long questionSubmitId = questionSubmit.getId();
        // 异步执行判题服务
        judgeRequestMessageProducer.sendJudgeRequestMessage(String.valueOf(questionSubmitId));

        return questionSubmitId;
    }

    /**
     * 获取查询包装类 用户可能根据哪些字段查询, 根据前端的查询字段, 得到mybatis支持的
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }

        String language = questionSubmitQueryRequest.getLanguage();
        String status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortFiled = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 如果字段不为空就拼接查询
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(JudgeInfoMessageEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);

        queryWrapper.eq("isDelete", false);
        
        // 设置排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortFiled), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
            sortFiled);

        return queryWrapper;
    }

    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);

        if (!loginUser.getId().equals(questionSubmit.getUserId()) && !userService.isAdmin(loginUser)) {
            questionSubmitVO.setCode("仅用户自己以及管理员可查看");
        }

        return questionSubmitVO;
    }

    /**
     * 
     * 将原始信息脱敏成前端可以显示的信息
     *  
     */
    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, HttpServletRequest request) {
        // 就算数据表为空也不允许未登录用户查
        User loginUser = userService.getLoginUser(request);

        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }

        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(questionSubmit -> {
            return getQuestionSubmitVO(questionSubmit, loginUser);
        }).collect(Collectors.toList());
        
        questionSubmitVOPage.setRecords(questionSubmitVOList);

        return questionSubmitVOPage;
    }
}
