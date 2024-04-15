package cn.zyszero.phoenix.rpc.demo.provider;

import cn.zyszero.phoenix.rpc.core.annotation.PhoenixProvider;
import cn.zyszero.phoenix.rpc.demo.api.User;
import cn.zyszero.phoenix.rpc.demo.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@PhoenixProvider
public class UserServiceImpl implements UserService {

    @Autowired
    private Environment environment;

    @Override
    public User findById(int id) {
        return new User(id, "ZZ-"
                + environment.getProperty("server.port")
                + "-" + System.currentTimeMillis());
    }

    @Override
    public User findById(int id, String name) {
        return new User(id, "ZZ-V10-"
                + environment.getProperty("server.port")
                + System.currentTimeMillis() + "-" + name);
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
        return "ZZ-"
                + environment.getProperty("server.port")
                + "-" + id;
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
        User[] users = userList.toArray(new User[0]);
        System.out.println(" ==> userList.toArray()[] = ");
        Arrays.stream(users).forEach(System.out::println);
        userList.add(new User(2024, "ZZ2024"));
        return userList;
    }

    @Override
    public Map<String, User> getMap(Map<String, User> userMap) {
//        userMap.values().forEach(x -> System.out.println(x.getClass()));
//        User[] users = userMap.values().toArray(new User[userMap.size()]);
//        System.out.println(" ==> userMap.values().toArray()[] = ");
//        Arrays.stream(users).forEach(System.out::println);
//        userMap.put("A2024", new User(2024, "ZZ2024"));
        return userMap;
    }

    @Override
    public Boolean getFlag(boolean flag) {
        return flag;
    }

    @Override
    public User[] findUsers(User[] users) {
        return users;
    }

    @Override
    public User findById(long id) {
        return new User(Long.valueOf(id).intValue(), "ZZ");
    }

    @Override
    public User ex(boolean flag) {
        if (flag) {
            throw new RuntimeException("just throw an exception");
        }
        return new User(100, "ZZ100");
    }


    String timeoutPorts = "8081,8094";

    @Override
    public User find(int timeout) {
        String port = environment.getProperty("server.port");
        if (Arrays.asList(timeoutPorts.split(",")).contains(port)) {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return findById(100);
    }


    @Override
    public void setTimeoutPorts(String timeoutPorts) {
        this.timeoutPorts = timeoutPorts;
    }
}
