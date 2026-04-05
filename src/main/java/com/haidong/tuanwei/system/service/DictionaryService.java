package com.haidong.tuanwei.system.service;

import com.haidong.tuanwei.system.dto.DictionaryItemForm;
import com.haidong.tuanwei.system.entity.DictItem;
import java.util.List;

public interface DictionaryService {

    List<DictItem> getByType(String dictType);

    List<DictItem> searchByType(String dictType, String keyword, int page, int pageSize);

    long countByType(String dictType, String keyword);

    List<DictItem> getAll();

    DictItem getById(Long id);

    void create(String dictType, DictionaryItemForm request);

    void update(Long id, DictionaryItemForm request);

    void delete(Long id);
}
