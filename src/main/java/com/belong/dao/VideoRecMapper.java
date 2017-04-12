package com.belong.dao;

import com.belong.model.VideoRec;
import com.belong.model.VideoRecExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface VideoRecMapper {
    int countByExample(VideoRecExample example);

    int deleteByExample(VideoRecExample example);

    int insert(VideoRec record);

    int insertSelective(VideoRec record);

    List<VideoRec> selectByExample(VideoRecExample example);

    int updateByExampleSelective(@Param("record") VideoRec record, @Param("example") VideoRecExample example);

    int updateByExample(@Param("record") VideoRec record, @Param("example") VideoRecExample example);
}