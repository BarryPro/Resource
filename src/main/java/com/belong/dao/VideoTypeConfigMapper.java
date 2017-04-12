package com.belong.dao;

import com.belong.model.VideoTypeConfig;
import com.belong.model.VideoTypeConfigExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface VideoTypeConfigMapper {
    int countByExample(VideoTypeConfigExample example);

    int deleteByExample(VideoTypeConfigExample example);

    int insert(VideoTypeConfig record);

    int insertSelective(VideoTypeConfig record);

    List<VideoTypeConfig> selectByExample(VideoTypeConfigExample example);

    int updateByExampleSelective(@Param("record") VideoTypeConfig record, @Param("example") VideoTypeConfigExample example);

    int updateByExample(@Param("record") VideoTypeConfig record, @Param("example") VideoTypeConfigExample example);
}