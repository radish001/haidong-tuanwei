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

    List<String> findEducationCodesByJobId(@Param("jobPostId") Long jobPostId);

    List<String> findMajorCodesByJobId(@Param("jobPostId") Long jobPostId);

    List<Long> findSchoolCategoryIdsByJobId(@Param("jobPostId") Long jobPostId);

    List<Long> findSchoolTagIdsByJobId(@Param("jobPostId") Long jobPostId);

    int deleteEducationRelations(@Param("jobPostId") Long jobPostId);

    int deleteMajorRelations(@Param("jobPostId") Long jobPostId);

    int deleteSchoolCategoryRelations(@Param("jobPostId") Long jobPostId);

    int deleteSchoolTagRelations(@Param("jobPostId") Long jobPostId);

    int insertEducationRelation(@Param("jobPostId") Long jobPostId,
            @Param("educationCode") String educationCode,
            @Param("createBy") Long createBy);

    int insertMajorRelation(@Param("jobPostId") Long jobPostId,
            @Param("majorCode") String majorCode,
            @Param("createBy") Long createBy);

    int insertSchoolCategoryRelation(@Param("jobPostId") Long jobPostId,
            @Param("categoryDictItemId") Long categoryDictItemId,
            @Param("createBy") Long createBy);

    int insertSchoolTagRelation(@Param("jobPostId") Long jobPostId,
            @Param("tagId") Long tagId,
            @Param("createBy") Long createBy);

    long countMajorUsage(@Param("majorCode") String majorCode);

    long countSchoolCategoryUsage(@Param("categoryDictItemId") Long categoryDictItemId);

    long countSchoolTagUsage(@Param("tagId") Long tagId);
}
