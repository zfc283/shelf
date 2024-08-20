package com.project.shelf.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.shelf.entity.User;
import com.project.shelf.entity.UserExample;
import com.project.shelf.exception.BusinessException;
import com.project.shelf.exception.BusinessExceptionCode;
import com.project.shelf.req.UserLoginReq;
import com.project.shelf.req.UserQueryReq;
import com.project.shelf.req.UserResetPasswordReq;
import com.project.shelf.req.UserSaveReq;
import com.project.shelf.resp.PageResp;
import com.project.shelf.resp.UserLoginResp;
import com.project.shelf.resp.UserQueryResp;
import com.project.shelf.util.CopyUtil;
import com.project.shelf.util.SnowFlake;
import com.project.shelf.mapper.UserMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
@Slf4j
public class UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq req) {
        PageHelper.startPage(req.getCurrent(), req.getPageSize());

        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();     // Criteria 类似 Where 条件
        if (!ObjectUtils.isEmpty(req.getLoginName())) {
            criteria.andLoginNameLike("%" + req.getLoginName() + "%");
        }
        List<User> userList =  userMapper.selectByExample(userExample);

        PageInfo<User> pageInfo = new PageInfo<>(userList);
        List<UserQueryResp> list =  CopyUtil.copyList(userList, UserQueryResp.class);

        PageResp<UserQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    private User getUserByLoginName(String loginName) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andLoginNameEqualTo(loginName);
        List<User> userList = userMapper.selectByExample(userExample);
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        }
        return userList.get(0);
    }

    public void insert(UserSaveReq req) {
        User user = CopyUtil.copy(req, User.class);
        if (!ObjectUtils.isEmpty(getUserByLoginName(user.getLoginName()))) {
            throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
        } else {
            Long id = snowFlake.nextId();
            user.setId(id);
            userMapper.insert(user);
        }
    }

    public void update(UserSaveReq req) {
        User user = CopyUtil.copy(req, User.class);
        // 后端校验, 不允许用户修改 loginName, password
        user.setLoginName(null);
        user.setPassword(null);
        userMapper.updateByPrimaryKeySelective(user);      // updateByPrimaryKeySelective 只有当 user 成员有值时才更新数据库字段
    }

    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }

    public void resetPassword(UserResetPasswordReq req) {
        User user = CopyUtil.copy(req, User.class);
        userMapper.updateByPrimaryKeySelective(user);
    }

    public UserLoginResp login(UserLoginReq req) {
        User user = getUserByLoginName(req.getLoginName());
        if (ObjectUtils.isEmpty(user)) {
            log.info("User name does not exist, {}", req.getLoginName());
            throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
        }
        String password = user.getPassword();
        if (!password.equals(req.getPassword())) {
            log.info("Passwords do not match, input password: {}, correct password: {}", req.getPassword(), password);
            throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
        }
        UserLoginResp userLoginResp = CopyUtil.copy(user, UserLoginResp.class);
        return userLoginResp;
    }
}
