package Service;

import annotation.MyComponent;

@MyComponent
public class UserService {
    String name = "userService";
    public void login(){
        System.out.println(name);
    }
}
