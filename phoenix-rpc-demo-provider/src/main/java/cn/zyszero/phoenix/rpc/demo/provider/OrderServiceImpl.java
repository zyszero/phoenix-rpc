package cn.zyszero.phoenix.rpc.demo.provider;

import cn.zyszero.phoenix.rpc.core.annotation.PhoenixProvider;
import cn.zyszero.phoenix.rpc.demo.api.Order;
import cn.zyszero.phoenix.rpc.demo.api.OrderService;
import org.springframework.stereotype.Component;

@Component
@PhoenixProvider
public class OrderServiceImpl implements OrderService {
    @Override
    public Order findById(Integer id) {
        return new Order(id.longValue(), 100.0f);
    }
}
