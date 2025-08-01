package com.data.project_web_service.sercurity.config;

import com.data.project_web_service.model.entity.Role;
import com.data.project_web_service.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void initRoles() {
        for (Role.RoleName roleName : Role.RoleName.values()) {
            roleRepository.findRoleByRoleName(roleName)
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setRoleName(roleName);
                        return roleRepository.save(role);
                    });
        }
    }
}

