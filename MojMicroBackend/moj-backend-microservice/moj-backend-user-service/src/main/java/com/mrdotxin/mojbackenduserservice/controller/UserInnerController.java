package com.mrdotxin.mojbackenduserservice.controller;

import com.mrdotxin.moj.backend.model.entity.User;
import com.mrdotxin.mojbackendserviceclient.service.UserFeignClient;
import com.mrdotxin.mojbackenduserservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {

    @Resource
    private UserService userService;

    @Override
    @GetMapping("/get/id")
    public User getById(@RequestParam("userId") Long id) {
        return userService.getById(id);
    }

    @Override
    @GetMapping("/get/ids")
    public List<User> listByIds(@RequestParam("idList") Collection<Long> ids) {
        return userService.listByIds(ids);
    }
}
