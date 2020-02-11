package com.future.pms.service;

import com.future.pms.config.MongoTokenStore;
import com.future.pms.model.User;
import com.future.pms.repository.CustomerRepository;
import com.future.pms.repository.ParkingZoneRepository;
import com.future.pms.repository.UserRepository;
import com.future.pms.service.impl.UserServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static com.future.pms.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class) public class UserServiceImplTest {
    private static final User USER =
        User.builder().idUser("idUser").email(null).password("passwordUser").role("roleUser")
            .build();
    private static final User USER2 =
        User.builder().idUser("idUser").email("email2").password("passwordUser").role("roleUser")
            .build();
    private static final String EMAIL = "email";
    private static final String ID = "id";
    private static final List<User> LIST_OF_USERS = Collections.singletonList(USER);
    private static final Page<User> PAGE_OF_USERS = new PageImpl<>(LIST_OF_USERS);
    private static final PageRequest PAGE_REQUEST =
        new PageRequest(0, 10, new Sort(Sort.Direction.ASC, "name"));

    @InjectMocks UserServiceImpl userServiceImpl;
    @Mock OAuth2Authentication oAuth2Authentication;
    @Mock UserRepository userRepository;
    @Mock CustomerRepository customerRepository;
    @Mock ParkingZoneRepository parkingZoneRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AuthorizationServerTokenServices authorizationServerTokenServices;
    @Mock ConsumerTokenServices consumerTokenServices;
    @Mock MongoTokenStore mongoTokenStore;
    @Mock private Principal principal;

    @Test public void loadAll() {
        Mockito.when(userRepository.findAll()).thenReturn(LIST_OF_USERS);

        ResponseEntity responseEntity = userServiceImpl.loadAll();

        assertThat(responseEntity).isNotNull();

        Mockito.verify(userRepository).findAll();
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test public void loadAllPager() {
        Mockito.when(userRepository
            .findAllByRoleAndEmailIsNot(SUPER_ADMIN, PAGE_REQUEST, principal.getName()))
            .thenReturn(PAGE_OF_USERS);

        ResponseEntity responseEntity = userServiceImpl.loadAll(0, principal);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(userRepository)
            .findAllByRoleAndEmailIsNot(SUPER_ADMIN, PAGE_REQUEST, principal.getName());
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test public void createUserEmailNotNull() {
        Mockito.when(userRepository.findByEmail(USER.getEmail())).thenReturn(USER);

        ResponseEntity responseEntity = userServiceImpl.createUser(USER);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(userRepository).findByEmail(USER.getEmail());
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test public void createUserEmailNotNullAndRoleIsAdmin() {
        USER.setRole(ADMIN);
        Mockito.when(userRepository.findByEmail(USER.getEmail())).thenReturn(null);

        ResponseEntity responseEntity = userServiceImpl.createUser(USER);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(userRepository).findByEmail(USER.getEmail());
        Mockito.verify(userRepository).save(USER);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test public void createUserEmailNotNullAndRoleIsCustomer() {
        USER.setRole(CUSTOMER);
        Mockito.when(userRepository.findByEmail(USER.getEmail())).thenReturn(null);

        ResponseEntity responseEntity = userServiceImpl.createUser(USER);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(userRepository).findByEmail(USER.getEmail());
        Mockito.verify(userRepository).save(USER);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test public void createUserEmailNotNullAndRoleIsSuperAdmin() {
        USER.setRole(SUPER_ADMIN);
        Mockito.when(userRepository.findByEmail(USER.getEmail())).thenReturn(null);

        ResponseEntity responseEntity = userServiceImpl.createUser(USER);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(userRepository).findByEmail(USER.getEmail());
        Mockito.verify(userRepository).save(USER);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test public void createUserEmailNotNullAndRoleIsElse() {
        USER.setRole(null);
        Mockito.when(userRepository.findByEmail(USER.getEmail())).thenReturn(null);

        ResponseEntity responseEntity = userServiceImpl.createUser(USER);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(userRepository).findByEmail(USER.getEmail());
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test public void updateUserEmailAlreadyRegistered() {
        USER.setEmail(EMAIL);
        UsernamePasswordAuthenticationToken principal =
            new UsernamePasswordAuthenticationToken("email2", "password");

        Mockito.when(userRepository.findByEmail(USER.getEmail())).thenReturn(USER);

        ResponseEntity responseEntity = userServiceImpl.updateUser(USER, principal);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(userRepository, Mockito.times(2)).findByEmail(USER.getEmail());
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test public void updateUserEmailSuccess() {
        USER.setEmail(EMAIL);
        UsernamePasswordAuthenticationToken principal =
            new UsernamePasswordAuthenticationToken(EMAIL, "password");

        Mockito.when(userRepository.findByEmail(USER.getEmail())).thenReturn(USER);

        ResponseEntity responseEntity = userServiceImpl.updateUser(USER, principal);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(userRepository, Mockito.times(3)).findByEmail(USER.getEmail());
        Mockito.verify(userRepository).save(USER);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test public void updateUserFromList() {
        USER.setPassword("pass");
        Mockito.when(userRepository.findByIdUser(ID)).thenReturn(USER);

        ResponseEntity responseEntity = userServiceImpl.updateUserFromList(ID, USER);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(userRepository).findByIdUser(ID);
        Mockito.verify(userRepository).findByEmail(USER.getEmail());
        Mockito.verify(userRepository).save(USER);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test public void updateUserFromListEmailNotNull() {
        USER.setEmail(EMAIL);
        Mockito.when(userRepository.findByIdUser(ID)).thenReturn(USER2);
        Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(USER2);

        ResponseEntity responseEntity = userServiceImpl.updateUserFromList(ID, USER);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(userRepository).findByIdUser(ID);
        Mockito.verify(userRepository).findByEmail(USER.getEmail());
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test public void updateUserFromListEmailNotNullExist() {
        USER.setEmail(EMAIL);
        Mockito.when(userRepository.findByIdUser(ID)).thenReturn(USER);
        Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(USER2);

        ResponseEntity responseEntity = userServiceImpl.updateUserFromList(ID, USER);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(userRepository).findByIdUser(ID);
        Mockito.verify(userRepository).findByEmail(USER.getEmail());
        Mockito.verify(userRepository).save(USER);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test public void getUserSA() {
        Mockito.when(userRepository.findByIdUser(ID)).thenReturn(USER);

        ResponseEntity responseEntity = userServiceImpl.getUserSA(ID);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(userRepository, Mockito.times(2)).findByIdUser(ID);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test public void getUserSANull() {
        Mockito.when(userRepository.findByIdUser(ID)).thenReturn(null);

        ResponseEntity responseEntity = userServiceImpl.getUserSA(ID);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(userRepository).findByIdUser(ID);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test public void deleteSuperAdmin() {
        UsernamePasswordAuthenticationToken principal =
            new UsernamePasswordAuthenticationToken("email", "password");

        Mockito.when(userRepository.findByIdUser(ID)).thenReturn(USER2);

        String responseEntity = userServiceImpl.deleteSuperAdmin(ID, principal);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(userRepository).findByIdUser(ID);
        Mockito.verify(userRepository).delete(USER2);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test public void deleteSuperAdminNull() {
        Mockito.when(userRepository.findByIdUser(ID)).thenReturn(null);

        String responseEntity = userServiceImpl.deleteSuperAdmin(ID, principal);

        assertThat(responseEntity).isNotNull();

        Mockito.verify(userRepository).findByIdUser(ID);
        Mockito.verifyNoMoreInteractions(userRepository);
    }
}
