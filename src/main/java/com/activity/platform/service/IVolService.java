package com.activity.platform.service;

import com.activity.platform.dto.Result;
import com.activity.platform.pojo.Activity;
import com.activity.platform.pojo.plus.Vol;
import com.baomidou.mybatisplus.extension.service.IService;

//高并发
/*
设计思路：
    结合activity_character表（活动角色名额）
    新活动默认无角色，创建角色和名额后同步加载进缓存
    构建志愿者订单的时候先在缓存当中预扣，然后通过异步操作调用函数写库
    获取活动时间结束的时候，通过异步操作调用函数释放缓存中剩余名额
    退出活动的时候要重新写缓存和写库，这个要同步操作
* */
public interface IVolService extends IService<Vol>{
    Result get(Long characterId);

}
