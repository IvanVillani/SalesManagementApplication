package com.ivan.salesapp.integration.services;

import com.ivan.salesapp.domain.models.service.RoleServiceModel;
import com.ivan.salesapp.enums.UserRole;
import com.ivan.salesapp.services.IRoleService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RoleServiceTest {
    @Autowired
    IRoleService iRoleService;

    @Before
    public void setupTest() {
        iRoleService.seedRolesInDB();
    }

    @Test
    public void assertSeededRoles_afterSeed_returnTrue() {
        Set<RoleServiceModel> result = iRoleService.findAllRoles();
        assertEquals(4, result.size());
    }

    @Test
    public void findRoleByAuthority_whenAuthorityClient_returnClientRole() {
        RoleServiceModel result = iRoleService.findByAuthority(UserRole.CLIENT.toString());

        assertNotNull(result);
        assertEquals(UserRole.CLIENT.toString(), result.getAuthority());
    }

    @Test
    public void findRoleByAuthority_whenAuthorityReseller_returnResellerRole() {
        RoleServiceModel result = iRoleService.findByAuthority(UserRole.RESELLER.toString());

        assertNotNull(result);
        assertEquals(UserRole.RESELLER.toString(), result.getAuthority());
    }

    @Test
    public void findRoleByAuthority_whenAuthorityAdmin_returnAdminRole() {
        RoleServiceModel result = iRoleService.findByAuthority(UserRole.ADMIN.toString());

        assertNotNull(result);
        assertEquals(UserRole.ADMIN.toString(), result.getAuthority());
    }

    @Test
    public void findRoleByAuthority_whenAuthorityRoot_returnRootRole() {
        RoleServiceModel result = iRoleService.findByAuthority(UserRole.ROOT.toString());

        assertNotNull(result);
        assertEquals(UserRole.ROOT.toString(), result.getAuthority());
    }
}
