package com.caixy.backend.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.backend.common.ErrorCode;
import com.caixy.backend.constant.CommonConstant;
import com.caixy.backend.constant.UserConstant;
import com.caixy.backend.exception.BusinessException;
import com.caixy.backend.mapper.UserMapper;
import com.caixy.backend.model.dto.user.UserLoginRequest;
import com.caixy.backend.model.dto.user.UserQueryRequest;
import com.caixy.backend.model.dto.user.UserRegisterRequest;
import com.caixy.backend.model.entity.User;
import com.caixy.backend.model.enums.UserRoleEnum;
import com.caixy.backend.model.vo.LoginUserVO;
import com.caixy.backend.model.vo.UserVO;
import com.caixy.backend.service.UserService;
import com.caixy.backend.utils.EncryptionUtils;
import com.caixy.backend.utils.RedisOperatorService;
import com.caixy.backend.utils.RegexUtils;
import com.caixy.backend.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService
{

    /**
     * 盐值
     */
    private static final String SALT = "caixy";

    /**
     * 校验添加用户的信息
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/7 18:48
     */
    @Override
    public void validateUserInfo(User registerRequest)
    {
        final String userAccount = registerRequest.getUserAccount();
        final String userPassword = registerRequest.getUserPassword();

        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (RegexUtils.validatePassword(userPassword))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短或不符合规范");
        }
//        if (registerRequest.getUserEmail() != null && !RegexUtils.isEmail(registerRequest.getUserEmail()))
//        {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式不正确");
//        }

//        if (userSex == null || userSex < 0 || userSex > 2)
//        {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户性别不合法");
//        }
    }


    @Override
    public long userRegister(UserRegisterRequest registerRequest)
    {
        User user = new User();
        BeanUtils.copyProperties(registerRequest, user);
        validateUserInfo(user);
        return this.makeRegister(user);
    }

    @Override
    public User userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request)
    {
        // 0. 提取参数
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
//        String captcha = userLoginRequest.getCaptcha().trim();
//        String captchaId = userLoginRequest.getCaptchaId();
        // 1. 校验
        // 1.1 检查参数是否完整
        if (StringUtils.isAnyBlank(userAccount, userPassword))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }

        // 1.2 校验验证码

        // 2. 根据账号查询用户是否存在
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        User user = baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null)
        {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        if (!EncryptionUtils.matches(userPassword, user.getUserPassword()))
        {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        User userVo = new User();
        BeanUtils.copyProperties(user, userVo);
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, userVo);
        // 登录成功
        return userVo;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request)
    {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null)
        {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUserPermitNull(HttpServletRequest request)
    {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null)
        {
            return null;
        }
        return currentUser;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request)
    {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user)
    {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request)
    {
        if (request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE) == null)
        {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    /**
     * 获取用户登录脱密信息
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/7 18:17
     */
    @Override
    public LoginUserVO getLoginUserVO(User user)
    {
        if (user == null)
        {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user)
    {
        if (user == null)
        {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList)
    {
        if (CollUtil.isEmpty(userList))
        {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest)
    {
        if (userQueryRequest == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getMpOpenId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "unionId", unionId);
        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mpOpenId", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    /**
     * 注册用户
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/7 18:15
     */
    @Override
    public Long makeRegister(User user)
    {
        // 线程单机锁，保证接口幂等性
        synchronized (user.getUserAccount().intern())
        {
            // 检查账户是否重复
            checkUserAccount(user.getUserAccount());

            // 加密密码并设置
            String encryptPassword = EncryptionUtils.encodePassword(user.getUserPassword());
            user.setUserPassword(encryptPassword);

            // 插入数据
            boolean saveResult = this.save(user);
            if (!saveResult)
            {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }
    // 私有方法，用于检查账户是否重复
    private void checkUserAccount(String userAccount)
    {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.count(queryWrapper);
        if (count > 0)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
    }
}
