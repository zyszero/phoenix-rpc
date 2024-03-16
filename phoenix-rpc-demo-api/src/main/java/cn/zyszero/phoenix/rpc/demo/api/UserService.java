package cn.zyszero.phoenix.rpc.demo.api;

public interface UserService {
    /**
     *
     * @param id
     * @return
     */
    User findById(int id);

    /**
     *
     * @param id
     * @param name
     * @return
     */
    User findById(int id, String name);

    int getId(int id);

    String getName(String name);


    String getName(int id);
}
