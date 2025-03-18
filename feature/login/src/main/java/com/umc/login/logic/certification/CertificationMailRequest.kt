package com.umc.login.logic.certification

sealed class CertificationMailRequest(
    open val email: String,
)

data class CertificationMailRequestForJoin(
    override val email: String,
): CertificationMailRequest(email = email)

data class CertificationMailRequestForFindingId(
    override val email: String,
    val name: String,
): CertificationMailRequest(email = email)

data class CertificationMailRequestForFindingPassword(
    override val email: String,
    val id: String,
    val name: String,
): CertificationMailRequest(email = email)

