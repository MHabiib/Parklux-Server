package com.future.pms.service;

import com.future.pms.model.User;
import com.future.pms.repository.UserRepository;
import com.future.pms.service.impl.UserDetailServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class) public class UserDetailServiceImplTest {

    private static final String EMAIL = "android@mail.com";
    private static final User USER =
        User.builder().idUser("idUser").email("emailUser").password("passwordUser").role("roleUser")
            .build();

    @InjectMocks UserDetailServiceImpl userDetailServiceImpl;
    @Mock UserRepository userRepository;

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsernameAndUserIsNull() {
        Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(null);
        Mockito.when(userDetailServiceImpl.loadUserByUsername(EMAIL))
            .thenThrow(new UsernameNotFoundException("Invalid username or password."));

        UserDetails user = userDetailServiceImpl.loadUserByUsername(EMAIL);

        assertThat(user).isNull();

        Mockito.verify(userRepository).findByEmail(EMAIL);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test public void loadUserByUsernameAndUserIsNotNull() {
        Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(USER);

        UserDetails user = userDetailServiceImpl.loadUserByUsername(EMAIL);

        assertThat(user).isNotNull();

        Mockito.verify(userRepository).findByEmail(EMAIL);
        Mockito.verifyNoMoreInteractions(userRepository);
    }
}
