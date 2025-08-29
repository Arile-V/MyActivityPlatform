package com.activity.platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.activity.platform.dto.Result;
import com.activity.platform.dto.UserDTO;
import com.activity.platform.dto.UserRegisterConfirmDTO;
import com.activity.platform.dto.UserLoginDTO;
import com.activity.platform.mapper.UserMapper;
import com.activity.platform.pojo.User;
import com.activity.platform.service.IUserService;
import com.activity.platform.service.IJavaMailService;
import com.activity.platform.util.EmailCode;
import com.activity.platform.util.SnowflakeIdWorker;
import com.activity.platform.util.UserHolder;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.activity.platform.util.RedisString.LOGIN;
import static com.activity.platform.util.RedisString.USER_REGISTER;
import static com.activity.platform.util.RedisString.USER_REGISTER_EMAIL;
import static com.activity.platform.util.RedisString.USER_TOKEN;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private final StringRedisTemplate stringRedisTemplate;

    public UserServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Resource
    private IJavaMailService javaMailService;
    @Resource
    private SnowflakeIdWorker snowflakeIdWorker;

    @Override
    public Result sentCode(String loginString) throws MessagingException {

        // 生成随机验证码
        String code = EmailCode.randomCode();
        // 根据用户名查询用户
        User user = query().eq("username",loginString).one();
        // 如果用户不存在，则根据邮箱查询用户
        if(user==null){
            // 清理输入参数，去除双引号和其他不必要的字符
            if (loginString != null && !loginString.trim().isEmpty()) {
                String cleanEmail = loginString.replaceAll("[\"\"']", "").trim();
                log.info("清理后的邮箱: '{}' -> '{}'", loginString, cleanEmail);
                loginString = cleanEmail;
                user = query().eq("email",cleanEmail).one();
            }
            // 如果用户仍然不存在，则返回用户不存在的提示
            if(user == null){
                return Result.fail("用户不存在");
            }
        }
        // 如果验证码发送成功，则返回发送成功的提示
        if(Boolean.TRUE.equals(stringRedisTemplate.opsForValue()
                .setIfAbsent(LOGIN + loginString, code, 60 * 5, TimeUnit.SECONDS))){
            if(javaMailService.sendEmailCode(user.getEmail(),"登录验证码"+code+"请勿泄漏")){
                return Result.ok("发送成功");
            // 如果验证码发送失败，则返回请勿频繁发送验证码的提示
            }else{
                return Result.fail("请勿频繁发送验证码");
            }
        // 如果验证码发送失败，则返回请勿频繁发送验证码的提示
        }else{
            return Result.fail("请勿频繁发送验证码");
        }
    }

    @Override
    public Result login(String loginUser, String code) {
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN+loginUser);
        if (cacheCode == null){
            return Result.fail("请先获取验证码");
        }else if(!cacheCode.equals(code)){
            return Result.fail("验证码错误");
        }else{
            User user = query().eq("username",loginUser).one();
            if (user == null){
                user = query().eq("email",loginUser).one();
                if(user == null){
                    return Result.fail("用户不存在");
                }else{
                    return loginSuccess(loginUser, user);
                }
            }else{
                return loginSuccess(loginUser, user);
            }
        }
    }

    @Override
    public Result login(UserLoginDTO loginDTO) {
        String loginUser = loginDTO.getLoginUser();
        String code = loginDTO.getCode();
        
        // 如果验证码是123456，跳过验证码校验
        if ("123456".equals(code)) {
            User user = query().eq("username", loginUser).one();
            if (user == null) {
                user = query().eq("email", loginUser).one();
                if (user == null) {
                    return Result.fail("用户不存在");
                } else {
                    return loginSuccess(loginUser, user);
                }
            } else {
                return loginSuccess(loginUser, user);
            }
        }
        
        // 正常验证码校验流程
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN + loginUser);
        if (cacheCode == null) {
            return Result.fail("请先获取验证码");
        } else if (!cacheCode.equals(code)) {
            return Result.fail("验证码错误");
        } else {
            User user = query().eq("username", loginUser).one();
            if (user == null) {
                user = query().eq("email", loginUser).one();
                if (user == null) {
                    return Result.fail("用户不存在");
                } else {
                    return loginSuccess(loginUser, user);
                }
            } else {
                return loginSuccess(loginUser, user);
            }
        }
    }


    // 登录成功方法
    private Result loginSuccess(String loginUser, User user) {
        // 将User对象转换为UserDTO对象
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        // 删除Redis中存储的登录表单信息
        stringRedisTemplate.delete(LOGIN+loginUser);
        // 生成一个随机的token
        String token = UUID.randomUUID().toString();
        // 将UserDTO对象转换为JSON字符串，并存储到Redis中，设置过期时间为24小时
        stringRedisTemplate.opsForValue().set(USER_TOKEN + token, JSONUtil.toJsonStr(userDTO), 60*60*24, TimeUnit.SECONDS);
        // 返回登录成功的结果，包含生成的token
        return Result.ok(token);
    }

    @Override
    public Result sentRegisterCode(User user) throws MessagingException {
        //判断邮箱是否已被注册
        if(query().eq("email",user.getEmail()).one()!=null){
            return Result.fail("该邮箱已被注册");
        }else if(query().eq("username",user.getUsername()).one()!=null){
            return Result.fail("该用户名已被注册");
        }else if (query().eq("school_id",user.getSchoolId()).one()!=null){
            return Result.fail("该学号已被注册");
        }
        user.setId(snowflakeIdWorker.nextId());
        String code = EmailCode.randomCode();
        javaMailService.sendEmailCode(user.getEmail(),"注册验证码"+code+"请勿泄漏");
        while(Boolean.FALSE.equals(stringRedisTemplate.opsForValue()
                .setIfAbsent(USER_REGISTER + code, JSONUtil.toJsonStr(user), 60 * 5, TimeUnit.SECONDS))){
            code = EmailCode.randomCode(); //保证唯一性
        }
        stringRedisTemplate.opsForValue().set(USER_REGISTER_EMAIL+user.getEmail(), code, 60*5, TimeUnit.SECONDS);
        return Result.ok("发送成功");
    }

    @Override
    @Transactional
    public Result register(String email, String code) {
        //1.判断验证码是否正确
        String cacheEmailCode = stringRedisTemplate.opsForValue().get(USER_REGISTER_EMAIL+email);
        if(cacheEmailCode == null){
            return Result.fail("验证码已过期");
        }
        if(!cacheEmailCode.equals(code)){
            return Result.fail("验证码不正确");
        }
        //2.如果正确，将缓存当中的用户数据持久化到数据库当中
        User user = JSONUtil.toBean(stringRedisTemplate.opsForValue().get(USER_REGISTER+code),User.class);
        save(user);
        return Result.ok("注册成功");
    }

    @Override
    @Transactional
    public Result register(UserRegisterConfirmDTO registerDTO) {
        //1.判断验证码是否正确
        String cacheEmailCode = stringRedisTemplate.opsForValue().get(USER_REGISTER_EMAIL + registerDTO.getEmail());
        if (cacheEmailCode == null && !registerDTO.getCode().equals("123456")){
            return Result.fail("验证码已过期");
        }
        if(!registerDTO.getCode().equals("123456") && !cacheEmailCode.equals(registerDTO.getCode())){
            return Result.fail("验证码不正确");
        }
        
        //2.验证码正确，检查用户是否已存在
        User existingUser = query().eq("email", registerDTO.getEmail()).one();
        
        if (existingUser != null) {
            // 如果用户已存在，且验证码是123456，则更新用户信息
            if (registerDTO.getCode().equals("123456")) {
                existingUser.setUsername(registerDTO.getUsername());
                existingUser.setName(registerDTO.getName());
                existingUser.setSchoolId(registerDTO.getSchoolID());
                existingUser.setWorkingHours(0L);
                existingUser.setCreateTime(new java.sql.Timestamp(System.currentTimeMillis()));
                
                // 检查用户名和学号是否与其他用户冲突
                User usernameConflict = query().eq("username", registerDTO.getUsername()).ne("id", existingUser.getId()).one();
                if (usernameConflict != null) {
                    return Result.fail("该用户名已被其他用户使用");
                }
                
                User schoolIdConflict = query().eq("school_id", registerDTO.getSchoolID()).ne("id", existingUser.getId()).one();
                if (schoolIdConflict != null) {
                    return Result.fail("该学号已被其他用户使用");
                }
                
                // 更新用户信息
                updateById(existingUser);
                return Result.ok("用户信息更新成功");
            } else {
                return Result.fail("该邮箱已被注册");
            }
        } else {
            // 如果用户不存在，检查用户名和学号是否已被使用
            if(query().eq("username", registerDTO.getUsername()).one() != null){
                return Result.fail("该用户名已被注册");
            }
            if(query().eq("school_id", registerDTO.getSchoolID()).one() != null){
                return Result.fail("该学号已被注册");
            }
            
            //3.创建新用户
            User user = new User();
            user.setId(snowflakeIdWorker.nextId());
            user.setEmail(registerDTO.getEmail());
            user.setUsername(registerDTO.getUsername());
            user.setName(registerDTO.getName());
            user.setSchoolId(registerDTO.getSchoolID());
            user.setWorkingHours(0L); // 设置默认工作时长
            user.setCreateTime(new java.sql.Timestamp(System.currentTimeMillis()));
            
            //4.保存用户到数据库
            save(user);
        }
        
        //5.清理Redis缓存
        stringRedisTemplate.delete(USER_REGISTER_EMAIL + registerDTO.getEmail());
        
        return Result.ok("注册成功");
    }

    @Override
    public Result logout(String token) {
        // 删除用户token
        Boolean deleted = stringRedisTemplate.delete(USER_TOKEN + token);
        // 如果删除成功，返回登出成功
        if (Boolean.TRUE.equals(deleted)) {
            return Result.ok("登出成功");
        } else {
            // 如果删除失败，返回登录状态已经失效
            return Result.fail("登录状态已经失效");
        }
    }

    @Override
    public Result getAllUsers() {
        try {
            List<User> users = list();
            return Result.ok(users);
        } catch (Exception e) {
            log.error("查询用户列表失败: ", e);
            return Result.fail("查询用户列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result searchUsers(String email, String username, String schoolId) {
        try {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            
            if (email != null && !email.trim().isEmpty()) {
                queryWrapper.eq("email", email.trim());
            }
            if (username != null && !username.trim().isEmpty()) {
                queryWrapper.eq("username", username.trim());
            }
            if (schoolId != null && !schoolId.trim().isEmpty()) {
                queryWrapper.eq("school_id", schoolId.trim());
            }
            
            List<User> users = list(queryWrapper);
            return Result.ok(users);
        } catch (Exception e) {
            log.error("查询用户失败: ", e);
            return Result.fail("查询用户失败: " + e.getMessage());
        }
    }

    @Override
    public Result getUserInfo(String token) {
        try {
            // 从Redis中获取用户信息
            String userJson = stringRedisTemplate.opsForValue().get(USER_TOKEN + token);
            if (userJson == null) {
                return Result.ok();
                //return Result.fail("token已过期或无效");
            }
            
            // 解析用户信息
            UserDTO userDTO = JSONUtil.toBean(userJson, UserDTO.class);
            if (userDTO == null) {
                return Result.fail("用户信息解析失败");
            }
            
            // 从数据库获取最新的用户信息
            User user = getById(userDTO.getId());
            if (user == null) {
                return Result.fail("用户不存在");
            }
            
            // 返回用户信息
            return Result.ok(user);
        } catch (Exception e) {
            log.error("获取用户信息失败: ", e);
            return Result.fail("获取用户信息失败: " + e.getMessage());
        }
    }

}
