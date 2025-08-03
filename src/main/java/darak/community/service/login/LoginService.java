package darak.community.service.login;

import darak.community.service.login.request.LoginServiceRequest;

public interface LoginService {

    void authenticate(LoginServiceRequest request);

    void validateMemberPasswordExpiration(String loginId);
}
