package com.codesoom.assignment.application;

import com.codesoom.assignment.role.application.port.out.RoleRepository;
import com.codesoom.assignment.role.domain.Role;
import com.codesoom.assignment.support.UserFixture;
import com.codesoom.assignment.user.application.UserService;
import com.codesoom.assignment.user.application.exception.UserEmailDuplicationException;
import com.codesoom.assignment.user.application.exception.UserNotFoundException;
import com.codesoom.assignment.user.application.port.out.UserRepository;
import com.codesoom.assignment.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.codesoom.assignment.support.IdFixture.ID_MAX;
import static com.codesoom.assignment.support.UserFixture.회원_1번;
import static com.codesoom.assignment.support.UserFixture.회원_2번;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DisplayName("UserService 유닛 테스트")
class UserServiceTest {
    private UserService userService;

    private final UserRepository userRepository = mock(UserRepository.class);

    private final RoleRepository roleRepository = mock(RoleRepository.class);

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, roleRepository, passwordEncoder);
    }


    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class createUser_메서드는 {

        @Nested
        @DisplayName("이미 등록된 이메일이 주어지면")
        class Context_with_already_exist_email {

            @BeforeEach
            void setUp() {
                given(userRepository.existsByEmail(회원_1번.이메일()))
                        .willReturn(true);
            }

            @Test
            @DisplayName("UserEmailDuplicationException 예외를 던진다")
            void it_returns_exception() {
                assertThatThrownBy(
                        () -> userService.createUser(회원_1번.등록_요청_데이터_생성())
                )
                        .isInstanceOf(UserEmailDuplicationException.class);

                verify(userRepository).existsByEmail(회원_1번.이메일());
                verify(userRepository, never()).save(any(User.class));
            }
        }

        @Nested
        @DisplayName("유효한 회원 정보가 주어지면")
        class Context_with_valid_user {

            @BeforeEach
            void setUp() {
                given(userRepository.existsByEmail(회원_1번.이메일()))
                        .willReturn(false);

                given(userRepository.save(any(User.class)))
                        .will(invocation -> {
                            User user = invocation.getArgument(0);

                            return User.builder()
                                    .id(회원_1번.아이디())
                                    .email(user.getEmail())
                                    .name(user.getName())
                                    .password(user.getPassword())
                                    .build();
                        });
            }

            @Test
            @DisplayName("회원을 저장하고 리턴한다")
            void it_returns_user() {
                User user = userService.createUser(회원_1번.등록_요청_데이터_생성());

                USER_이메일_이름_값_검증(user, 회원_1번);
                assertThat(passwordEncoder.matches(회원_1번.비밀번호(), user.getPassword())).isTrue();
                assertThat(user.isDeleted()).isFalse();

                verify(userRepository).existsByEmail(회원_1번.이메일());
                verify(userRepository).save(any(User.class));
                verify(roleRepository).save(any(Role.class));
            }
        }
    }


    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class updateUser_메서드는 {

        @Nested
        @DisplayName("찾을 수 없는 id가 주어지면")
        class Context_with_not_exist_id {
            private final Long 찾을_수_없는_id = 회원_2번.아이디();

            @BeforeEach
            void setUp() {
                given(userRepository.findByIdAndDeletedIsFalse(찾을_수_없는_id))
                        .willReturn(Optional.empty());
            }

            @Test
            @DisplayName("UserNotFoundException 예외를 던진다")
            void it_returns_exception() {
                assertThatThrownBy(
                        () -> userService.updateUser(
                                찾을_수_없는_id,
                                회원_2번.수정_요청_데이터_생성()
                        )
                )
                        .isInstanceOf(UserNotFoundException.class);

                verify(userRepository).findByIdAndDeletedIsFalse(찾을_수_없는_id);
            }
        }

        @Nested
        @DisplayName("삭제 된 회원의 id가 주어지면")
        class Context_with_deleted_id {
            private final Long 삭제된_회원_id = ID_MAX.value();

            @BeforeEach
            void setUp() {
                given(userRepository.findByIdAndDeletedIsFalse(삭제된_회원_id))
                        .willReturn(Optional.empty());
            }

            @Test
            @DisplayName("UserNotFoundException 예외를 던진다")
            void it_returns_exception() {
                assertThatThrownBy(
                        () -> userService.updateUser(
                                삭제된_회원_id,
                                회원_1번.수정_요청_데이터_생성()
                        )
                )
                        .isInstanceOf(UserNotFoundException.class);

                verify(userRepository).findByIdAndDeletedIsFalse(삭제된_회원_id);
            }
        }

        @Nested
        @DisplayName("찾을 수 있는 id가 주어지면")
        class Context_with_exist_id {
            private final Long 찾을_수_있는_id = 회원_1번.아이디();

            @BeforeEach
            void setUp() {
                given(userRepository.findByIdAndDeletedIsFalse(찾을_수_있는_id))
                        .willReturn(
                                Optional.of(회원_1번.회원_엔티티_생성(찾을_수_있는_id))
                        );
            }

            @Test
            @DisplayName("회원을 수정하고 리턴한다")
            void it_returns_user() {
                User user = userService.updateUser(
                        찾을_수_있는_id,
                        회원_2번.수정_요청_데이터_생성()
                );


                USER_이름_값_검증(user, 회원_2번);
                assertThat(passwordEncoder.matches(회원_2번.비밀번호(), user.getPassword())).isTrue();
                assertThat(user.isDeleted()).isFalse();

                verify(userRepository).findByIdAndDeletedIsFalse(찾을_수_있는_id);
            }
        }
    }


    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class deleteUser_메서드는 {

        @Nested
        @DisplayName("찾을 수 없는 id가 주어지면")
        class Context_with_not_exist_id {
            private final Long 찾을_수_없는_id = 회원_2번.아이디();

            @BeforeEach
            void setUp() {
                given(userRepository.findByIdAndDeletedIsFalse(찾을_수_없는_id))
                        .willReturn(Optional.empty());
            }

            @Test
            @DisplayName("UserNotFoundException 예외를 던진다")
            void it_returns_exception() {
                assertThatThrownBy(() -> userService.deleteUser(찾을_수_없는_id))
                        .isInstanceOf(UserNotFoundException.class);

                verify(userRepository).findByIdAndDeletedIsFalse(찾을_수_없는_id);
            }
        }

        @Nested
        @DisplayName("삭제 된 회원의 id가 주어지면")
        class Context_with_deleted_id {
            private final Long 삭제된_회원_id = ID_MAX.value();

            @BeforeEach
            void setUp() {
                given(userRepository.findByIdAndDeletedIsFalse(삭제된_회원_id))
                        .willReturn(Optional.empty());
            }

            @Test
            @DisplayName("UserNotFoundException 예외를 던진다")
            void it_returns_exception() {
                assertThatThrownBy(() -> userService.deleteUser(삭제된_회원_id))
                        .isInstanceOf(UserNotFoundException.class);

                verify(userRepository).findByIdAndDeletedIsFalse(삭제된_회원_id);
            }
        }

        @Nested
        @DisplayName("찾을 수 있는 id가 주어지면")
        class Context_with_exist_id {
            private final Long 찾을_수_있는_id = 회원_1번.아이디();

            @BeforeEach
            void setUp() {
                given(userRepository.findByIdAndDeletedIsFalse(찾을_수_있는_id))
                        .willReturn(
                                Optional.of(회원_1번.회원_엔티티_생성(찾을_수_있는_id))
                        );
            }

            @Test
            @DisplayName("회원을 삭제 상태로 수정 후 리턴한다")
            void it_returns_user() {
                User user = userService.deleteUser(찾을_수_있는_id);

                assertThat(user.isDeleted()).isTrue();

                verify(userRepository).findByIdAndDeletedIsFalse(찾을_수_있는_id);
            }
        }
    }


    private static void USER_이메일_이름_값_검증(User user, UserFixture userFixture) {
        USER_이메일_값_검증(user, userFixture);
        USER_이름_값_검증(user, userFixture);
    }

    private static void USER_이름_값_검증(User user, UserFixture userFixture) {
        assertThat(user.getName()).isEqualTo(userFixture.이름());
    }

    private static void USER_이메일_값_검증(User user, UserFixture USER_1) {
        assertThat(user.getEmail()).isEqualTo(USER_1.이메일());
    }
}
