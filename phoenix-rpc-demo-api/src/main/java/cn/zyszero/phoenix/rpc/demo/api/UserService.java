package cn.zyszero.phoenix.rpc.demo.api;

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
}
