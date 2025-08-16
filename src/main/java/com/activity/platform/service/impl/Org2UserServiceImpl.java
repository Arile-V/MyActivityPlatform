package com.activity.platform.service.impl;

import com.activity.platform.dto.Result;
import com.activity.platform.dto.UserDTO;
import com.activity.platform.mapper.Org2UserMapper;
import com.activity.platform.pojo.Org;
import com.activity.platform.pojo.plus.Org2User;
import com.activity.platform.service.IOrg2UserService;
import com.activity.platform.util.SnowflakeIdWorker;
import com.activity.platform.util.UserHolder;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class Org2UserServiceImpl extends ServiceImpl<Org2UserMapper, Org2User> implements IOrg2UserService {
    @Resource
    SnowflakeIdWorker snowflakeIdWorker;
    @Override
    public Result userJoin(Long OrgId) {//用户选择归属某一个组织
        UserDTO userDto = UserHolder.getUser();
        Org2User pastOrg = query().eq("user_id",userDto.getId()).one();
        if(pastOrg != null){
            return Result.fail("只能归属一个组织");
        }
        pastOrg = new Org2User();
        pastOrg.setOrgId(OrgId);
        pastOrg.setUserId(userDto.getId());
        pastOrg.setId(snowflakeIdWorker.nextId());
        save(pastOrg);
        return Result.ok("成功归属");

    }

    @Override
    public Result checkUser(Long OrgId) {
        return Result.ok(query().eq("org_id",OrgId).list()
        );
    }//管理员查组织内人员

    @Override
    public Result selectUser(Long UserId) {
        return Result.ok(query().eq("user_id",UserId).one()
        );
    }//用户自查组织归属情况

    @Override
    public Result userQuit(Long OrgId) {
        remove(query().eq("org_id",OrgId)
                .eq("user_id",UserHolder.getUser().getId()));
        return Result.ok("成功退出");
    }

    @Override//管理员踢出用户
    public Result kickOutUser(Long OrgId, Long UserId) {
        remove(
                query()
                        .eq("org_id",OrgId)
                        .eq("user_id",UserId)
        );
        return Result.ok("成功踢出");
    }
}
