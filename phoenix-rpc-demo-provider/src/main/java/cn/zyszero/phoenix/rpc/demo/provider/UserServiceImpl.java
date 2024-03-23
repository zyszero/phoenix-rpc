package cn.zyszero.phoenix.rpc.demo.provider;

import cn.zyszero.phoenix.rpc.core.annotation.PhoenixProvider;
import cn.zyszero.phoenix.rpc.demo.api.User;
import cn.zyszero.phoenix.rpc.demo.api.UserService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@PhoenixProvider
public class UserServiceImpl implements UserService {
    @Override
    public User findById(int id) {
        return new User(id, "ZZ-" + System.currentTimeMillis());
    }

    @Override
    public User findById(int id, String name) {
        return new User(id, "ZZ-" + System.currentTimeMillis() + "-" + name);
    }

    @Override
    public long getId(long id) {
        return id;
    }

    @Override
    public long getId(float id) {
        return Float.valueOf(id).longValue();
    }

    @Override
    public long getId(User user) {
        return user.getId();
    }

    @Override
    public String getName(String name) {
        return name;
    }

    @Override
    public String getName(int id) {
        return "KK-" + id;
    }

    @Override
    public int[] getIds() {
        return new int[]{100, 200, 300};
    }

    @Override
    public long[] getLongIds() {
        return new long[]{1, 2, 3};
    }

    @Override
    public int[] getIds(int[] ids) {
        return ids;
    }

    @Override
    public List<User> getList(List<User> userList) {
        return userList;
    }

    @Override
    public Map<String, User> getMap(Map<String, User> userMap) {
        return userMap;
    }

    @Override
    public Boolean getFlag(boolean flag) {
        return flag;
    }
}
