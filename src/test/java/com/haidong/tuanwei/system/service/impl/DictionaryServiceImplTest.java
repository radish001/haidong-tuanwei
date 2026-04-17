package com.haidong.tuanwei.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.haidong.tuanwei.job.dao.JobPostDao;
import com.haidong.tuanwei.system.dao.DictionaryDao;
import com.haidong.tuanwei.system.dao.MajorCatalogDao;
import com.haidong.tuanwei.system.dao.SchoolDao;
import com.haidong.tuanwei.system.dto.DictionaryItemForm;
import com.haidong.tuanwei.system.entity.DictItem;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DictionaryServiceImplTest {

    private DictionaryDao dictionaryDao;
    private MajorCatalogDao majorCatalogDao;
    private SchoolDao schoolDao;
    private JobPostDao jobPostDao;
    private DictionaryServiceImpl dictionaryService;

    @BeforeEach
    void setUp() {
        dictionaryDao = mock(DictionaryDao.class);
        majorCatalogDao = mock(MajorCatalogDao.class);
        schoolDao = mock(SchoolDao.class);
        jobPostDao = mock(JobPostDao.class);
        dictionaryService = new DictionaryServiceImpl(dictionaryDao, majorCatalogDao, schoolDao, jobPostDao);
    }

    @Test
    void createMajorCategoryWithValidEducationScopes() {
        DictionaryItemForm form = new DictionaryItemForm();
        form.setDictLabel("新专业类别");
        form.setDictValue("15");
        form.setEducationScopes("UNDERGRADUATE,GRADUATE");

        when(dictionaryDao.findByType("major_category")).thenReturn(List.of());

        // Should not throw any exception
        dictionaryService.create("major_category", form);
    }

    @Test
    void createMajorCategoryWithoutEducationScopesThrowsException() {
        DictionaryItemForm form = new DictionaryItemForm();
        form.setDictLabel("新专业类别");
        form.setDictValue("15");
        form.setEducationScopes(null);

        assertThatThrownBy(() -> dictionaryService.create("major_category", form))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("专业类别必须至少选择一项所属学历层次");
    }

    @Test
    void createMajorCategoryWithEmptyEducationScopesThrowsException() {
        DictionaryItemForm form = new DictionaryItemForm();
        form.setDictLabel("新专业类别");
        form.setDictValue("15");
        form.setEducationScopes("");

        assertThatThrownBy(() -> dictionaryService.create("major_category", form))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("专业类别必须至少选择一项所属学历层次");
    }

    @Test
    void createMajorCategoryWithInvalidEducationScopesThrowsException() {
        DictionaryItemForm form = new DictionaryItemForm();
        form.setDictLabel("新专业类别");
        form.setDictValue("15");
        form.setEducationScopes("INVALID_SCOPE");

        assertThatThrownBy(() -> dictionaryService.create("major_category", form))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("所属学历层次必须包含有效的选项");
    }

    @Test
    void updateMajorCategoryWithEducationScopes() {
        DictItem existing = new DictItem();
        existing.setId(1L);
        existing.setDictType("major_category");
        existing.setDictLabel("工学");
        existing.setDictValue("08");
        existing.setSortNo(8);
        existing.setEducationScopes("UNDERGRADUATE");

        when(dictionaryDao.findById(1L)).thenReturn(existing);

        DictionaryItemForm form = new DictionaryItemForm();
        form.setDictLabel("工学（新）");
        form.setDictValue("08");
        form.setEducationScopes("UNDERGRADUATE,JUNIOR_COLLEGE");

        // Should not throw any exception
        dictionaryService.update(1L, form);
    }

    @Test
    void createNonMajorCategoryDoesNotRequireEducationScopes() {
        DictionaryItemForm form = new DictionaryItemForm();
        form.setDictLabel("男");
        form.setDictValue("M");
        // gender doesn't require educationScopes

        when(dictionaryDao.findByType("gender")).thenReturn(List.of());

        // Should not throw any exception
        dictionaryService.create("gender", form);
    }

    @Test
    void getByTypeReturnsDictItemsWithEducationScopes() {
        DictItem item1 = new DictItem();
        item1.setId(1L);
        item1.setDictType("major_category");
        item1.setDictLabel("工学");
        item1.setDictValue("08");
        item1.setEducationScopes("UNDERGRADUATE");

        DictItem item2 = new DictItem();
        item2.setId(2L);
        item2.setDictType("major_category");
        item2.setDictLabel("医学");
        item2.setDictValue("10");
        item2.setEducationScopes("UNDERGRADUATE,JUNIOR_COLLEGE");

        when(dictionaryDao.findByType("major_category")).thenReturn(List.of(item1, item2));

        List<DictItem> result = dictionaryService.getByType("major_category");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEducationScopes()).isEqualTo("UNDERGRADUATE");
        assertThat(result.get(1).getEducationScopes()).isEqualTo("UNDERGRADUATE,JUNIOR_COLLEGE");
    }

    @Test
    void createMajorCategoryWithSingleEducationScope() {
        DictionaryItemForm form = new DictionaryItemForm();
        form.setDictLabel("新专业类别");
        form.setDictValue("15");
        form.setEducationScopes("JUNIOR_COLLEGE");

        when(dictionaryDao.findByType("major_category")).thenReturn(List.of());

        // Should not throw any exception for a single valid scope
        dictionaryService.create("major_category", form);
    }

    @Test
    void createMajorCategoryWithAllEducationScopes() {
        DictionaryItemForm form = new DictionaryItemForm();
        form.setDictLabel("新专业类别");
        form.setDictValue("15");
        form.setEducationScopes("JUNIOR_COLLEGE,UNDERGRADUATE,GRADUATE");

        when(dictionaryDao.findByType("major_category")).thenReturn(List.of());

        // Should not throw any exception when all scopes are valid
        dictionaryService.create("major_category", form);
    }
}
