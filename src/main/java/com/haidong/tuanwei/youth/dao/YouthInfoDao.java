package com.haidong.tuanwei.youth.dao;

import com.haidong.tuanwei.youth.dto.YouthSearchRequest;
import com.haidong.tuanwei.youth.entity.YouthInfo;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface YouthInfoDao {

    List<YouthInfo> search(@Param("youthType") String youthType,
            @Param("query") YouthSearchRequest query);

    long count(@Param("youthType") String youthType,
            @Param("query") YouthSearchRequest query);

    YouthInfo findById(@Param("id") Long id);

    int insert(YouthInfo youthInfo);

    int update(YouthInfo youthInfo);

    int softDelete(@Param("id") Long id, @Param("updateBy") Long updateBy);

    int softDeleteBatch(@Param("youthType") String youthType,
            @Param("ids") List<Long> ids,
            @Param("updateBy") Long updateBy);

    int countDuplicate(@Param("youthType") String youthType,
            @Param("name") String name,
            @Param("phone") String phone);
}
