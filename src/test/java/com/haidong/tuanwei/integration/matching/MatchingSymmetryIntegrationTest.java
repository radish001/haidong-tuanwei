package com.haidong.tuanwei.integration.matching;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.haidong.tuanwei.integration.IntegrationTestBase;
import com.haidong.tuanwei.job.entity.JobPost;
import com.haidong.tuanwei.job.support.JobRequirementOptionSupport;
import com.haidong.tuanwei.youth.entity.YouthInfo;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

class MatchingSymmetryIntegrationTest extends IntegrationTestBase {

    @Test
    void matchingShouldBeSymmetricForSpecificMajorEducationAndSchoolCategory() throws Exception {
        YouthInfo youth = createCollegeYouth("双向基线学生", uniquePhone("181"), "10743", "080901", "BK", 1);
        JobPost job = createJob("双向基线岗位", List.of("BK"), List.of("080901"), List.of("100"), 1);

        assertThat(jobMatchesYouth(job.getId(), youth.getId())).isTrue();
        assertThat(youthMatchesJob(youth.getId(), job.getId())).isTrue();
    }

    @Test
    void matchingShouldBeSymmetricWhenJobMajorRequirementIsUnlimited() throws Exception {
        YouthInfo youth = createCollegeYouth("不限专业学生", uniquePhone("182"), "10743", "080901", "BK", 1);
        JobPost job = createJob("不限专业岗位",
                List.of("BK"),
                List.of(JobRequirementOptionSupport.UNLIMITED_OPTION_VALUE),
                List.of("100"),
                1);

        assertThat(jobMatchesYouth(job.getId(), youth.getId())).isTrue();
        assertThat(youthMatchesJob(youth.getId(), job.getId())).isTrue();
    }

    @Test
    void matchingShouldBeSymmetricWhenJobEducationRequirementIsUnlimited() throws Exception {
        YouthInfo youth = createCollegeYouth("不限学历学生", uniquePhone("183"), "10743", "080901", "BK", 1);
        JobPost job = createJob("不限学历岗位",
                List.of(JobRequirementOptionSupport.UNLIMITED_OPTION_VALUE),
                List.of("080901"),
                List.of("100"),
                1);

        assertThat(jobMatchesYouth(job.getId(), youth.getId())).isTrue();
        assertThat(youthMatchesJob(youth.getId(), job.getId())).isTrue();
    }

    @Test
    void matchingShouldBeSymmetricWhenJobMajorAndEducationRequirementsAreUnfilled() throws Exception {
        YouthInfo youth = createCollegeYouth("不填专业学历学生", uniquePhone("184"), "10743", "080901", "BK", 1);
        JobPost job = createJob("不填专业学历岗位", List.of(), List.of(), List.of("100"), 1);

        assertThat(jobMatchesYouth(job.getId(), youth.getId())).isTrue();
        assertThat(youthMatchesJob(youth.getId(), job.getId())).isTrue();
    }

    @Test
    void matchingShouldBeSymmetricWhenJobSchoolCategoryRequirementIsUnfilled() throws Exception {
        YouthInfo youth = createCollegeYouth("不填院校层次学生", uniquePhone("185"), "10743", "080901", "BK", 1);
        JobPost job = createJob("不填院校层次岗位", List.of("BK"), List.of("080901"), List.of(), 1);

        assertThat(jobMatchesYouth(job.getId(), youth.getId())).isTrue();
        assertThat(youthMatchesJob(youth.getId(), job.getId())).isTrue();
    }

    private YouthInfo createCollegeYouth(
            String name, String phone, String schoolCode, String majorCode, String educationLevel, int sortOrder) throws Exception {
        mockMvc.perform(post("/youth/college")
                        .session(adminSession)
                        .param("name", name)
                        .param("gender", "M")
                        .param("birthDate", "2001-09-01")
                        .param("ethnicity", "HAN")
                        .param("educationLevel", educationLevel)
                        .param("schoolCode", schoolCode)
                        .param("majorCode", majorCode)
                        .param("recruitmentYear", "2023")
                        .param("phone", phone)
                        .param("sortOrder", String.valueOf(sortOrder)))
                .andExpect(status().is3xxRedirection());

        MvcResult youthListResult = mockMvc.perform(get("/youth/college").session(adminSession))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        List<YouthInfo> youthRecords =
                (List<YouthInfo>) youthListResult.getModelAndView().getModel().get("records");
        return youthRecords.stream()
                .filter(item -> phone.equals(item.getPhone()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("未找到学生: " + phone));
    }

    private JobPost createJob(
            String jobName, List<String> educationRequirements, List<String> majorCodes,
            List<String> schoolCategoryIds, int sortOrder) throws Exception {
        org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request = post("/jobs")
                .session(adminSession)
                .param("enterpriseId", "1")
                .param("jobName", jobName)
                .param("sortOrder", String.valueOf(sortOrder));

        appendParams(request, "educationRequirements", educationRequirements);
        appendParams(request, "majorCodes", majorCodes);
        appendParams(request, "schoolCategoryIds", schoolCategoryIds);

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection());

        MvcResult listResult = mockMvc.perform(get("/jobs").session(adminSession))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        List<JobPost> jobs = (List<JobPost>) listResult.getModelAndView().getModel().get("records");
        return jobs.stream()
                .filter(job -> jobName.equals(job.getJobName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("未找到岗位: " + jobName));
    }

    private boolean jobMatchesYouth(Long jobId, Long youthId) throws Exception {
        MvcResult matchResult = mockMvc.perform(get("/jobs/" + jobId + "/matches")
                        .session(adminSession)
                        .param("pageSize", "100"))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        List<YouthInfo> matchedRecords =
                (List<YouthInfo>) matchResult.getModelAndView().getModel().get("records");
        return matchedRecords.stream().anyMatch(record -> youthId.equals(record.getId()));
    }

    private boolean youthMatchesJob(Long youthId, Long jobId) throws Exception {
        MvcResult matchResult = mockMvc.perform(get("/youth/college/" + youthId + "/matches")
                        .session(adminSession)
                        .param("pageSize", "100"))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        List<JobPost> matchedRecords =
                (List<JobPost>) matchResult.getModelAndView().getModel().get("records");
        return matchedRecords.stream().anyMatch(record -> jobId.equals(record.getId()));
    }

    private void appendParams(org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request,
            String name, List<String> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        request.param(name, values.toArray(String[]::new));
    }

    private String uniquePhone(String prefix) {
        String millis = String.valueOf(System.currentTimeMillis());
        String suffix = millis.substring(Math.max(0, millis.length() - (11 - prefix.length())));
        String phone = prefix + suffix;
        if (phone.length() > 11) {
            return phone.substring(0, 11);
        }
        if (phone.length() < 11) {
            char[] padding = new char[11 - phone.length()];
            Arrays.fill(padding, '0');
            return phone + new String(padding);
        }
        return phone;
    }
}
