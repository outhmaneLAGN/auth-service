package com.eqdom.auth.dto;

import java.util.Set;

import com.eqdom.auth.entity.RoleName;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRolesRequest {

    @NotEmpty
    private Set<RoleName> roles;
}
