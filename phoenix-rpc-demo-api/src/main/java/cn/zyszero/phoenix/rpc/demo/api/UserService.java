package cn.zyszero.phoenix.rpc.demo.api;

public interface UserService {
    User findById(Integer id);

    int getId(int id);

    String getName(String name);
}
