package com.belong.dao;

import com.belong.model.Video_rec;
import com.belong.model.Video_recExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface Video_recMapper {
    int countByExample(Video_recExample example);

    int deleteByExample(Video_recExample example);

    int insert(Video_rec record);

    int insertSelective(Video_rec record);

    List<Video_rec> selectByExample(Video_recExample example);

    int updateByExampleSelective(@Param("record") Video_rec record, @Param("example") Video_recExample example);

    int updateByExample(@Param("record") Video_rec record, @Param("example") Video_recExample example);
}