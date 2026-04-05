package com.haidong.tuanwei.job.dao;

import com.haidong.tuanwei.job.dto.JobSearchRequest;
import com.haidong.tuanwei.job.entity.JobPost;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface JobPostDao {

    List<JobPost> search(@Param("query") JobSearchRequest query);

    long count(@Param("query") JobSearchRequest query);

    JobPost findById(@Param("id") Long id);

    int insert(JobPost jobPost);

    int update(JobPost jobPost);

    int softDelete(@Param("id") Long id, @Param("updateBy") Long updateBy);

    int softDeleteBatch(@Param("ids") List<Long> ids, @Param("updateBy") Long updateBy);
}
