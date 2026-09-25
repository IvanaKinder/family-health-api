package com.familyhealth.api.mapper;

import com.familyhealth.api.generated.model.ReminderPage;
import com.familyhealth.api.generated.model.ReminderRequest;
import com.familyhealth.api.generated.model.ReminderResponse;
import com.familyhealth.api.model.Reminder;
import com.familyhealth.api.service.model.ReminderCommand;
import com.familyhealth.api.service.model.ReminderView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface ReminderMapper {

    ReminderCommand toCommand(ReminderRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "child", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Reminder toEntity(ReminderCommand command);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "child", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(ReminderCommand command, @MappingTarget Reminder reminder);

    ReminderView toView(Reminder reminder);

    ReminderResponse toResponse(ReminderView view);

    default ReminderPage toPage(Page<ReminderView> page) {
        return new ReminderPage()
                .content(page.getContent().stream().map(this::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages());
    }
}
