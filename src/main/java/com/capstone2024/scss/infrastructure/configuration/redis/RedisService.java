package com.capstone2024.scss.infrastructure.configuration.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Lưu dữ liệu vào Redis
    public void saveData(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // Lưu dữ liệu với thời gian tồn tại
    public void saveDataWithExpiration(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    // Lấy dữ liệu từ Redis
    public Object getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // Xóa dữ liệu từ Redis
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    // Kiểm tra key có tồn tại không
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    // Đặt thời gian tồn tại cho một key
    public boolean setExpiration(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    // Lấy thời gian tồn tại còn lại của key
    public Long getRemainingTTL(String key) {
        return redisTemplate.getExpire(key);
    }

    // Lấy tất cả các khóa có prefix nhất định
    public Set<String> getKeysByPrefix(String prefix) {
        return redisTemplate.keys(prefix + "*");
    }

    // Xóa tất cả các khóa có prefix nhất định
    public void deleteKeysByPrefix(String prefix) {
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    // Cập nhật dữ liệu nếu tồn tại, nếu không thì không làm gì
    public void updateDataIfExists(String key, Object newValue) {
        if (exists(key)) {
            redisTemplate.opsForValue().set(key, newValue);
        }
    }

    // Thực hiện tăng giá trị của khóa (áp dụng cho dữ liệu kiểu số)
    public Long incrementValue(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    // Khóa Redis với cơ chế Lock, trả về true nếu khóa thành công
    public boolean acquireLock(String lockKey, String lockValue, long timeout, TimeUnit unit) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, timeout, unit);
        return Boolean.TRUE.equals(result);
    }

    // Giải phóng khóa Redis
    public void releaseLock(String lockKey, String lockValue) {
        if (lockValue.equals(getData(lockKey))) {
            deleteData(lockKey);
        }
    }
}
