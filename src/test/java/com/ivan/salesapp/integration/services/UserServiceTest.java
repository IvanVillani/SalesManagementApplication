package com.ivan.salesapp.integration.services;

import com.ivan.salesapp.domain.entities.Role;
import com.ivan.salesapp.domain.entities.User;
import com.ivan.salesapp.domain.models.service.RoleServiceModel;
import com.ivan.salesapp.domain.models.service.UserServiceModel;
import com.ivan.salesapp.enums.UserRole;
import com.ivan.salesapp.exceptions.InvalidUserException;
import com.ivan.salesapp.exceptions.UserNotFoundException;
import com.ivan.salesapp.repository.UserRepository;
import com.ivan.salesapp.services.IDiscountService;
import com.ivan.salesapp.services.IRoleService;
import com.ivan.salesapp.services.IUserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {
    @Autowired
    IUserService iUserService;

    @MockBean
    private UserRepository mockUserRepository;

    @MockBean
    private IRoleService mockIRoleService;

    @MockBean
    private IDiscountService mockIDiscountService;

    @MockBean
    private BCryptPasswordEncoder encoder;

    private List<User> users;

    @Before
    public void setupTest() {
        users = new ArrayList<>();
        when(mockUserRepository.findAll())
                .thenReturn(users);

        when(encoder.encode("password"))
                .thenReturn("password");

        when(mockIRoleService.findAllRoles())
                .thenReturn(Set.of(new RoleServiceModel() {{
                    setAuthority("ROOT");
                }}));
    }

    @Test
    public void registerUser_whenFirst_giveAllRoles() {
        UserServiceModel result = iUserService.registerUser(new UserServiceModel() {{
            setUsername("username");
            setPassword("password");
            setEmail("email");
        }});

        verify(mockUserRepository)
                .saveAndFlush(any());

        assertEquals("password", result.getPassword());

        List<String> roles = result.getAuthorities()
                .stream()
                .map(RoleServiceModel::getAuthority)
                .collect(toList());

        assertTrue(roles.contains("ROOT"));

    }

    @Test(expected = UsernameNotFoundException.class)
    public void findUserByUsername_whenNoUsers_throwException() throws UsernameNotFoundException {
        users.clear();
        iUserService.findUserByUsername("testUsername");
    }

    @Test
    public void findUserById_whenUserPresent_returnUser() {
        users.add(new User() {{
            setId("testId");
        }});
        UserServiceModel result = iUserService.findUserById("testId");

        assertNotNull(result);
    }

    @Test
    public void editUser_whenUserValid_returnEditedUser() throws InvalidUserException {
        when(mockUserRepository.findByUsername(any()))
                .thenReturn(java.util.Optional.of(new User() {{
                    setPassword("test-pass");
                }}));

        when(encoder.matches(any(), any()))
                .thenReturn(true);

        UserServiceModel result = iUserService
                .editUserProfile(new UserServiceModel() {{
                    setPassword("password");
                }}, "test-pass");

        verify(mockUserRepository)
                .saveAndFlush(any());

        assertEquals("password", result.getPassword());
    }

    @Test(expected = InvalidUserException.class)
    public void editUser_whenUserNotValid_throwException() throws InvalidUserException {
        when(mockUserRepository.findByUsername(any()))
                .thenReturn(java.util.Optional.of(new User() {{
                    setPassword("test-pass");
                }}));

        iUserService.editUserProfile(new UserServiceModel() {{
            setPassword("password");
        }}, "test-pass-wrong");
    }

    @Test
    public void deleteUser_whenUserPresent_deleteUser() throws UserNotFoundException {
        when(mockUserRepository.findById(any()))
                .thenReturn(java.util.Optional.of(new User()));

        iUserService.deleteUserById("test-id");

        verify(mockUserRepository)
                .delete(any());
    }

    @Test(expected = UserNotFoundException.class)
    public void deleteUser_whenUserNotFound_throwException() throws UserNotFoundException {
        iUserService.deleteUserById("test-id");
    }

    @Test
    public void setUserRole_whenUserPresent_setNewRole() throws UserNotFoundException {
        User testUser = new User() {{
            setAuthorities(
                    new LinkedHashSet<>() {{
                        add(new Role() {{
                            setAuthority(UserRole.CLIENT.toString());
                        }});
                    }}
            );
        }};
        users.add(testUser);

        when(mockUserRepository.findById(any()))
                .thenReturn(java.util.Optional.of(testUser));

        when(mockIRoleService.findByAuthority(any()))
                .thenReturn(new RoleServiceModel(){{ setAuthority(UserRole.RESELLER.toString()); }});

        UserServiceModel result = iUserService.setUserRole("test-id", "reseller");

        verify(mockUserRepository)
                .saveAndFlush(any());

        assertEquals(result.getAuthorities().size(), 1);
        assertEquals(UserRole.RESELLER.toString(),
                result.getAuthorities()
                        .stream()
                        .findFirst()
                        .map(RoleServiceModel::getAuthority)
                        .orElseThrow());
    }

    @Test(expected = UserNotFoundException.class)
    public void setUserRole_whenUserNotFound_throwException() throws UserNotFoundException {
        UserServiceModel result = iUserService.setUserRole("test-id", "reseller");
    }
}
