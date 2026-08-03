package com.tgle.planner.profile.presentation;

import com.tgle.planner.profile.application.dto.ChangeEmailCommand;
import com.tgle.planner.profile.application.dto.ChangePasswordCommand;
import com.tgle.planner.profile.application.dto.VerifyEmailChangeCommand;
import com.tgle.planner.profile.presentation.dto.ChangeEmailRequest;
import com.tgle.planner.profile.presentation.dto.ChangePasswordRequest;
import com.tgle.planner.profile.presentation.dto.VerifyEmailChangeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfileMapper {

    ChangePasswordCommand toCommand(ChangePasswordRequest request, Long userId);
    ChangeEmailCommand toCommand(ChangeEmailRequest request, Long userId);
    VerifyEmailChangeCommand toCommand(VerifyEmailChangeRequest request, Long userId);
}
