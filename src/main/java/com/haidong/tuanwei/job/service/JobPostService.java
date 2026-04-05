package com.haidong.tuanwei.job.service;

import com.haidong.tuanwei.job.dto.JobFormRequest;
import com.haidong.tuanwei.job.dto.JobSearchRequest;
import com.haidong.tuanwei.job.entity.JobPost;
import java.util.List;

public interface JobPostService {

    List<JobPost> search(JobSearchRequest query);

    long count(JobSearchRequest query);

    JobPost getById(Long id);

    void create(JobFormRequest request, Long operatorId);

    void update(Long id, JobFormRequest request, Long operatorId);

    void delete(Long id, Long operatorId);

    int deleteBatch(List<Long> ids, Long operatorId);
}
