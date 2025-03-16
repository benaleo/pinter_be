package com.kasirpinter.pos.service.impl;

import com.kasirpinter.pos.entity.MsJobPosition;
import com.kasirpinter.pos.entity.RlUserShift;
import com.kasirpinter.pos.entity.Users;
import com.kasirpinter.pos.model.JobPositionModel;
import com.kasirpinter.pos.model.projection.JobPositionIndexProjection;
import com.kasirpinter.pos.model.search.ListOfFilterPagination;
import com.kasirpinter.pos.model.search.SavedKeywordAndPageable;
import com.kasirpinter.pos.repository.CompanyRepository;
import com.kasirpinter.pos.repository.MsJobPositionRepository;
import com.kasirpinter.pos.repository.RlUserShiftRepository;
import com.kasirpinter.pos.repository.UserRepository;
import com.kasirpinter.pos.response.PageCreateReturn;
import com.kasirpinter.pos.response.ResultPageResponseDTO;
import com.kasirpinter.pos.service.MsJobPositionService;
import com.kasirpinter.pos.util.ContextPrincipal;
import com.kasirpinter.pos.util.GlobalConverter;
import com.kasirpinter.pos.util.TreeGetEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MsJobPositionServiceImpl implements MsJobPositionService {

    private final UserRepository userRepository;
    private final MsJobPositionRepository jobPositionRepository;
    private final CompanyRepository companyRepository;
    private final RlUserShiftRepository relationUserShiftRepository;

    @Override
    public ResultPageResponseDTO<JobPositionModel.JobPositionIndexResponse> listIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword) {
        Users user = TreeGetEntity.parsingUserByProjection(ContextPrincipal.getSecureUserId(), userRepository);
        String companyId = ContextPrincipal.getRoleName().equals("SUPERADMIN") ? null : user.getCompany().getSecureId();

        ListOfFilterPagination filter = new ListOfFilterPagination(keyword);
        SavedKeywordAndPageable set = GlobalConverter.appsCreatePageable(pages, limit, sortBy, direction, keyword,
                filter);

        // First page result (get total count)
        Page<JobPositionIndexProjection> firstResult = jobPositionRepository.findDataByKeyword(set.keyword(), set.pageable(), companyId);

        // Use a correct Pageable for fetching the next page
        Pageable pageable = GlobalConverter.oldSetPageable(pages, limit, sortBy, direction, firstResult, null);
        Page<JobPositionIndexProjection> pageResult = jobPositionRepository.findDataByKeyword(set.keyword(), pageable, companyId);

        // Map the data to the DTOs
        List<JobPositionModel.JobPositionIndexResponse> dtos = pageResult.stream().map((c) -> {
            JobPositionModel.JobPositionIndexResponse dto = new JobPositionModel.JobPositionIndexResponse();
            dto.setName(c.getName());
            dto.setDescription(c.getDescription());
            dto.setCompany_name(c.getCompanyName());
            dto.setIsActive(c.getIsActive());

            GlobalConverter.CmsIDTimeStampResponseAndIdProjection(dto, c.getId(), c.getCreatedAt(), c.getUpdatedAt(),
                    c.getCreatedBy(), c.getUpdatedBy());
            return dto;
        }).collect(Collectors.toList());

        return PageCreateReturn.create(
                pageResult,
                dtos);
    }

    @Override
    public JobPositionModel.JobPositionDetailResponse findJobPositionBySecureId(String id) {
        MsJobPosition data = TreeGetEntity.parseMsJobPositionByProjection(id, jobPositionRepository);
        return parseDetail(data);
    }

    @Override
    public JobPositionModel.JobPositionDetailResponse saveData(JobPositionModel.@Valid JobPositionCreateRequest item) {
        MsJobPosition data = new MsJobPosition();
        data.setName(item.name());
        data.setDescription(item.description());
        data.setCompany(TreeGetEntity.parsingCompanyByProjection(item.company_id(), companyRepository));

        GlobalConverter.CmsAdminCreateAtBy(data, ContextPrincipal.getId());
        data = jobPositionRepository.save(data);
        return parseDetail(data);
    }

    @Override
    public JobPositionModel.JobPositionDetailResponse updateData(String id, JobPositionModel.@Valid JobPositionUpdateRequest item) {
        MsJobPosition data = TreeGetEntity.parseMsJobPositionByProjection(id, jobPositionRepository);
        data.setName(item.name());
        data.setDescription(item.description());
        data.setIsActive(item.isActive());

        GlobalConverter.CmsAdminUpdateAtBy(data, ContextPrincipal.getId());
        data = jobPositionRepository.save(data);
        return parseDetail(data);
    }

    @Override
    public void deleteData(String id) {
        MsJobPosition data = TreeGetEntity.parseMsJobPositionByProjection(id, jobPositionRepository);
        if (relationUserShiftRepository.existsByPosition(data)) {
            data.setIsDeleted(true);
            jobPositionRepository.save(data);
        } else {
            jobPositionRepository.delete(data);
        }
    }

    private JobPositionModel.JobPositionDetailResponse parseDetail(MsJobPosition data) {
        return new JobPositionModel.JobPositionDetailResponse(
                data.getName(),
                data.getDescription(),
                data.getCompany() != null ? data.getCompany().getName() : null,
                data.getCompany() != null ? data.getCompany().getSecureId() : null,
                data.getIsActive()
        );
    }

}
