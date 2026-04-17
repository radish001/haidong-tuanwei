package com.haidong.tuanwei.youth.service;

import com.haidong.tuanwei.youth.dto.YouthFormRequest;
import com.haidong.tuanwei.youth.dto.YouthImportResult;
import com.haidong.tuanwei.youth.dto.YouthSearchRequest;
import com.haidong.tuanwei.youth.entity.YouthInfo;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface YouthInfoService {

    List<YouthInfo> search(String youthType, YouthSearchRequest query);

    long count(String youthType, YouthSearchRequest query);

    YouthInfo getById(Long id);

    void create(String youthType, YouthFormRequest request, Long operatorId);

    void update(Long id, YouthFormRequest request, Long operatorId);

    void delete(Long id, Long operatorId);

    int deleteBatch(String youthType, List<Long> ids, Long operatorId);

    byte[] generateImportTemplate();

    YouthImportResult importFromExcel(String youthType, MultipartFile file, Long operatorId);

    byte[] generateFailedImportExcel(YouthImportResult result);

    byte[] exportExcel(String youthType, YouthSearchRequest query);
}
