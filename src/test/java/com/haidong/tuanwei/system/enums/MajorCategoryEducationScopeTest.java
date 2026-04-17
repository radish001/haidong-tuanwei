package com.haidong.tuanwei.system.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MajorCategoryEducationScopeTest {

    @Test
    void enumValuesExist() {
        assertThat(MajorCategoryEducationScope.values())
                .hasSize(3)
                .extracting(MajorCategoryEducationScope::getLabel)
                .containsExactly("专科专业", "本科专业", "研究生专业");
    }

    @Test
    void enumConstantsExist() {
        assertThat(MajorCategoryEducationScope.JUNIOR_COLLEGE).isNotNull();
        assertThat(MajorCategoryEducationScope.UNDERGRADUATE).isNotNull();
        assertThat(MajorCategoryEducationScope.GRADUATE).isNotNull();
    }

    @Test
    void fromValueReturnsCorrectEnum() {
        assertThat(MajorCategoryEducationScope.fromValue("JUNIOR_COLLEGE"))
                .isEqualTo(MajorCategoryEducationScope.JUNIOR_COLLEGE);
        assertThat(MajorCategoryEducationScope.fromValue("UNDERGRADUATE"))
                .isEqualTo(MajorCategoryEducationScope.UNDERGRADUATE);
        assertThat(MajorCategoryEducationScope.fromValue("GRADUATE"))
                .isEqualTo(MajorCategoryEducationScope.GRADUATE);
    }

    @Test
    void fromValueReturnsNullForInvalidValue() {
        assertThat(MajorCategoryEducationScope.fromValue("INVALID")).isNull();
        assertThat(MajorCategoryEducationScope.fromValue("")).isNull();
        assertThat(MajorCategoryEducationScope.fromValue(null)).isNull();
    }

    @Test
    void toLabelDisplayConvertsSingleScope() {
        assertThat(MajorCategoryEducationScope.toLabelDisplay("UNDERGRADUATE"))
                .isEqualTo("本科专业");
        assertThat(MajorCategoryEducationScope.toLabelDisplay("JUNIOR_COLLEGE"))
                .isEqualTo("专科专业");
        assertThat(MajorCategoryEducationScope.toLabelDisplay("GRADUATE"))
                .isEqualTo("研究生专业");
    }

    @Test
    void toLabelDisplayConvertsMultipleScopes() {
        assertThat(MajorCategoryEducationScope.toLabelDisplay("UNDERGRADUATE,GRADUATE"))
                .isEqualTo("本科专业、研究生专业");
        assertThat(MajorCategoryEducationScope.toLabelDisplay("JUNIOR_COLLEGE,UNDERGRADUATE,GRADUATE"))
                .isEqualTo("专科专业、本科专业、研究生专业");
    }

    @Test
    void toLabelDisplayHandlesEmptyInput() {
        assertThat(MajorCategoryEducationScope.toLabelDisplay(null)).isEqualTo("");
        assertThat(MajorCategoryEducationScope.toLabelDisplay("")).isEqualTo("");
    }

    @Test
    void toLabelDisplayHandlesInvalidScopes() {
        assertThat(MajorCategoryEducationScope.toLabelDisplay("INVALID")).isEqualTo("");
        assertThat(MajorCategoryEducationScope.toLabelDisplay("INVALID,UNDERGRADUATE"))
                .isEqualTo("本科专业");
    }

    @Test
    void toLabelDisplayHandlesWhitespace() {
        assertThat(MajorCategoryEducationScope.toLabelDisplay(" UNDERGRADUATE , GRADUATE "))
                .isEqualTo("本科专业、研究生专业");
    }

    @Test
    void getLabelReturnsCorrectValue() {
        assertThat(MajorCategoryEducationScope.JUNIOR_COLLEGE.getLabel()).isEqualTo("专科专业");
        assertThat(MajorCategoryEducationScope.UNDERGRADUATE.getLabel()).isEqualTo("本科专业");
        assertThat(MajorCategoryEducationScope.GRADUATE.getLabel()).isEqualTo("研究生专业");
    }

    @Test
    void getValueReturnsCorrectValue() {
        assertThat(MajorCategoryEducationScope.JUNIOR_COLLEGE.getValue()).isEqualTo("JUNIOR_COLLEGE");
        assertThat(MajorCategoryEducationScope.UNDERGRADUATE.getValue()).isEqualTo("UNDERGRADUATE");
        assertThat(MajorCategoryEducationScope.GRADUATE.getValue()).isEqualTo("GRADUATE");
    }
}
