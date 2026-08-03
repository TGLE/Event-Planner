package com.tgle.planner.authentication.presentation;

import com.tgle.planner.authentication.application.dto.*;
import com.tgle.planner.authentication.presentation.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthenticationMapper {

    RegisterCommand toCommand(RegisterRequest request);
    LoginCommand toCommand(LoginRequest request, String deviceId);
    VerifyOtpCommand toCommand(VerifyOtpRequest request);
    OtpCommand toCommand(OtpRequest request);
    RefreshAccessTokenCommand toRefreshCommand(String refreshToken, String deviceId);
    LogoutCommand toLogoutCommand(String refreshToken, String deviceId);
    PasswordResetCommand toCommand(PasswordResetRequest request, String resetToken);
}
