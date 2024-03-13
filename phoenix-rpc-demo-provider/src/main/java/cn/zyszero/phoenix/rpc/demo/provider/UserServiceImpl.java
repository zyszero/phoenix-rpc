package cn.zyszero.phoenix.rpc.demo.provider;

import cn.zyszero.phoenix.rpc.core.annotation.PhoenixProvider;
import cn.zyszero.phoenix.rpc.demo.api.User;
import cn.zyszero.phoenix.rpc.demo.api.UserService;
import org.springframework.stereotype.Component;

@Component
@PhoenixProvider
public class UserServiceImpl implements UserService {
    @Override
    public User findById(Integer id) {
        return new User(id, "ZZ-" + System.currentTimeMillis());
    }

    @Override
    public int getId(int id) {
        return  id;
    }

    @Override
    public String getName(String name) {
        return name;
    }
}
