package com.ivan.salesapp.services;

import com.ivan.salesapp.domain.entities.Role;
import com.ivan.salesapp.domain.models.service.RoleServiceModel;
import com.ivan.salesapp.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@Service
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public RoleService(RoleRepository roleRepository, ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void seedRolesInDB() {
        if (this.roleRepository.count() == 0){
            this.roleRepository.saveAndFlush(new Role("ROLE_USER"));
            this.roleRepository.saveAndFlush(new Role("ROLE_MODERATOR"));
            this.roleRepository.saveAndFlush(new Role("ROLE_ADMIN"));
            this.roleRepository.saveAndFlush(new Role("ROLE_ROOT"));
        }
    }

//    @Override
//    public void assignUserRoles(UserServiceModel  userServiceModel, long numberOfUsers) {
//        if (numberOfUsers == 0){
//            userServiceModel
//                    .setAuthorities(this.roleRepository
//                    .findAll()
//                    .stream()
//                    .map(r -> this.modelMapper.map(r, RoleServiceModel.class))
//                    .collect(Collectors.toSet()));
//        }
//    }

    @Override
    public Set<RoleServiceModel> findAllRoles() {
        return this.roleRepository.findAll()
                .stream()
                .map(r -> this.modelMapper.map(r, RoleServiceModel.class))
                .collect(toSet());
    }

    @Override
    public RoleServiceModel findByAuthority(String authority) {
        return this.modelMapper.map(this.roleRepository.findByAuthority(authority), RoleServiceModel.class);
    }
}
