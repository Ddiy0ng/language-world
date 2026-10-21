package com.languageworld.be.global.auth.entity;

import com.languageworld.be.global.auth.enumGroup.UserRoleCode;

import java.util.UUID;

public record CustomPrincipal (UUID uuid, UserRoleCode userRole) {}
