package com.activity.platform.dto;

import com.activity.platform.pojo.Activity;
import com.activity.platform.pojo.ActivityCharacter;
import lombok.Data;

import java.util.List;

@Data
public class ActivityCreateDTO {
    // 活动基本信息
    private String name;
    private String description;
    private String location;
    private String status;
    private String imageUrl;
    private String type;
    private String startTime;
    private String endTime;
    private String startToGetTime;
    private String endToGetTime;
    
    // 活动角色列表 - 使用与前端一致的字段名
    private List<CharacterDTO> characters;
    
    // 内部角色DTO类，匹配前端字段名
    @Data
    public static class CharacterDTO {
        private String characterName;
        private Integer volume;
    }
    
    // 转换为Activity实体
    public Activity toActivity() {
        Activity activity = new Activity();
        activity.setName(this.name);
        activity.setDescription(this.description);
        activity.setLocation(this.location);
        // 转换状态值
        activity.setStatus(convertStatus(this.status));
        activity.setImageUrl(this.imageUrl);
        activity.setType(this.type);
        // 时间字段需要转换，这里先保持为String，在Controller中处理
        return activity;
    }
    
    // 转换状态值
    private String convertStatus(String frontendStatus) {
        if (frontendStatus == null) return "报名中";
        
        switch (frontendStatus) {
            case "报名中":
                return "报名中";
            case "进行中":
                return "进行中";
            case "已结束":
                return "已结束";
            default:
                return "报名中";
        }
    }
    
    // 转换为ActivityCharacter列表
    public List<ActivityCharacter> toActivityCharacters() {
        if (characters == null) return null;
        
        return characters.stream().map(charDTO -> {
            ActivityCharacter character = new ActivityCharacter();
            // 注意：数据库字段是character_name，但POJO字段是name
            // 这里需要确保POJO的name字段被正确设置
            character.setName(charDTO.getCharacterName()); // 映射字段名
            character.setVolume(charDTO.getVolume());
            return character;
        }).toList();
    }
} 