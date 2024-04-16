package cn.zyszero.phoenix.rpc.demo.api;

import java.util.List;
import java.util.Map;

public interface UserService {
    /**
     * @param id
     * @return
     */
    User findById(int id);

    /**
     * @param id
     * @param name
     * @return
     */
    User findById(int id, String name);

    long getId(long id);

    long getId(float id);

    long getId(User user);

    String getName(String name);

    String getName(int id);

    int[] getIds();

    long[] getLongIds();

    int[] getIds(int[] ids);

    List<User> getList(List<User> userList);

    Map<String, User> getMap(Map<String, User> userMap);

    Boolean getFlag(boolean flag);

    User[] findUsers(User[] users);

    User findById(long id);

    User ex(boolean flag);

    User find(int timeout);

    void setTimeoutPorts(String ports);


    String echoParameter(String key);
}
