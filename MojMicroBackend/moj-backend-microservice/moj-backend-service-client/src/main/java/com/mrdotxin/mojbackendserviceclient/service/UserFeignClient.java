package com.mrdotxin.mojbackendserviceclient.service;

import com.mrdotxin.moj.backend.common.common.ErrorCode;
import com.mrdotxin.moj.backend.common.exception.BusinessException;
import com.mrdotxin.moj.backend.model.entity.User;
import com.mrdotxin.moj.backend.model.enums.UserRoleEnum;
import com.mrdotxin.moj.backend.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

import static com.mrdotxin.moj.backend.common.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务
 *
 * 
 * 
 */
@FeignClient(name = "moj-backend-user-service", path = "/api/user/inner")
public interface UserFeignClient  {

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    default User getLoginUser(HttpServletRequest request) {
       // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;

        System.out.println(currentUser);
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        currentUser.setUserName(currentUser.getUserAccount());
        return currentUser;
    }

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    default  boolean isAdmin(User user) {
         return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    default  UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 获取用户
     * @param id
     * @return
     */
    @GetMapping("/get/id")
    User getById(@RequestParam("userId") Long id);

    /**
     * 批量获取用户信息
     * @param ids
     * @return
     */
    @GetMapping("/get/ids")
    List<User> listByIds(@RequestParam("idList") Collection<Long> ids);
}
