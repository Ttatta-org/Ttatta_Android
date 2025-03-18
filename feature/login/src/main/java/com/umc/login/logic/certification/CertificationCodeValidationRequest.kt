package com.umc.login.logic.certification

sealed class CertificationCodeValidationRequest(
    open val email: String,
    open val code: String,
)

data class CertificationCodeValidationRequestForJoin(
    override val email: String,
    override val code: String,
): CertificationCodeValidationRequest(email = email, code = code)

data class CertificationCodeValidationRequestForFindingId(
    override val email: String,
    override val code: String,
): CertificationCodeValidationRequest(email = email, code = code)

data class CertificationCodeValidationRequestForFindingPassword(
    override val email: String,
    override val code: String,
): CertificationCodeValidationRequest(email = email, code = code)
