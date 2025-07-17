package com.loopers.domain.user;
import com.loopers.interfaces.api.user.UserV1RequestDto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User registerMember(UserV1RequestDto.Register request) {
        checkDuplicateAccount(request.account());

        return userRepository.save(
                User.register(request.toCommand())
        );
    }

    private void checkDuplicateAccount(String account) {
        if (userRepository.findBy(account).isPresent()) {
            throw new CoreException(ErrorType.BAD_REQUEST,"이미 존재하는 아이디입니다: " + account);
        }
    }
}
