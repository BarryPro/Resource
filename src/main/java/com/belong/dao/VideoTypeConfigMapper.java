package com.belong.dao;

import com.belong.model.VideoTypeConfig;
import com.belong.model.VideoTypeConfigExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface VideoTypeConfigMapper {
    int countByExample(VideoTypeConfigExample example);

    int deleteByExample(VideoTypeConfigExample example);

    int insert(Map map);

    int insertSelective(VideoTypeConfig record);

    List<VideoTypeConfig> selectByExample(VideoTypeConfigExample example);

    int updateByExampleSelective(@Param("record") VideoTypeConfig record, @Param("example") VideoTypeConfigExample example);

    int updateByExample(@Param("record") VideoTypeConfig record, @Param("example") VideoTypeConfigExample example);
}
