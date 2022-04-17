package Controller;

import Service.UserService;
import annotation.AfterBeanInited;
import annotation.MyComponent;
import annotation.MyInject;

@MyComponent
public class UserController {
    @MyInject
     UserService userService;

    @AfterBeanInited
    public void doLogin() {
        userService.login();
    }
}
