package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.entity.vo.response.WeatherVO;
import com.example.service.WeatherService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

@Slf4j
@Service
public class WeatherServiceImpl implements WeatherService {

    @Resource
    RestTemplate rest;

    @Resource
    StringRedisTemplate template;

    @Value("${spring.weather.key}")
    String key;

    @Value("${spring.weather.apihost}")
    String apiHost;

    @Override
    public WeatherVO fetchWeather(double longitude, double latitude) {
        WeatherVO result = fetchFromCache(longitude, latitude);
        if (result != null)
            return result;
        // 如果所有方法都失败，返回默认天气数据
        return getDefaultWeather();
    }

    private WeatherVO fetchFromCache(double longitude, double latitude) {
        try {
            JSONObject geo = this.decompressStingToJson(rest.getForObject(
                    "https://" + apiHost + "/geo/v2/city/lookup?location=" + longitude + "," + latitude + "&key=" + key,
                    byte[].class));
            if (geo == null) {
                log.warn("Failed to get geo location for {},{}", longitude, latitude);
                return null;
            }
            JSONObject location = geo.getJSONArray("location").getJSONObject(0);
            // 处理十六进制格式的location id
            String idStr = location.getString("id");
            int id;
            try {
                // 先尝试十进制解析
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException e1) {
                try {
                    // 如果十进制解析失败，尝试十六进制解析
                    id = Integer.parseInt(idStr, 16);
                } catch (NumberFormatException e2) {
                    // 如果两种格式都解析失败，记录错误并返回null
                    log.error("Failed to parse location id: {}", idStr, e2);
                    return null;
                }
            }
            String cacheKey = Const.FORUM_WEATHER_CACHE + id;
            try {
                String cache = template.opsForValue().get(cacheKey);
                if (cache != null) {
                    log.info("Using cached weather data for location id: {}", id);
                    return JSONObject.parseObject(cache).to(WeatherVO.class);
                }
            } catch (Exception e) {
                log.warn("Failed to get weather data from cache: {}", e.getMessage());
                // Redis不可用时继续从API获取数据
            }
            WeatherVO vo = this.fetchFromAPI(id, location);
            if (vo != null) {
                try {
                    template.opsForValue().set(cacheKey, JSONObject.from(vo).toJSONString(), 1, TimeUnit.HOURS);
                } catch (Exception e) {
                    log.warn("Failed to cache weather data: {}", e.getMessage());
                    // Redis不可用时仍然返回API数据
                }
            }
            return vo;
        } catch (Exception e) {
            log.error("Error in fetchFromCache: {}", e.getMessage(), e);
            return null;
        }
    }

    private WeatherVO fetchFromAPI(int id, JSONObject location) {
        try {
            WeatherVO vo = new WeatherVO();
            vo.setLocation(location);

            // 获取当前天气
            boolean nowSuccess = false;
            try {
                JSONObject now = this.decompressStingToJson(rest.getForObject(
                        "https://" + apiHost + "/v7/weather/now?location=" + id + "&key=" + key, byte[].class));
                if (now != null) {
                    vo.setNow(now.getJSONObject("now"));
                    nowSuccess = true;
                } else {
                    log.warn("Failed to get current weather for location id: {}", id);
                }
            } catch (Exception e) {
                log.warn("Error getting current weather: {}", e.getMessage());
            }

            // 如果当前天气获取失败，设置默认值
            if (!nowSuccess) {
                JSONObject defaultNow = new JSONObject();
                defaultNow.put("temp", "20");
                defaultNow.put("text", "晴");
                defaultNow.put("icon", "100");
                vo.setNow(defaultNow);
            }

            // 获取小时预报
            boolean hourlySuccess = false;
            try {
                JSONObject hourly = this.decompressStingToJson(rest.getForObject(
                        "https://" + apiHost + "/v7/weather/24h?location=" + id + "&key=" + key, byte[].class));
                if (hourly != null) {
                    vo.setHourly(new JSONArray(hourly.getJSONArray("hourly").stream().limit(5).toList()));
                    hourlySuccess = true;
                } else {
                    log.warn("Failed to get hourly weather for location id: {}", id);
                }
            } catch (Exception e) {
                log.warn("Error getting hourly weather: {}", e.getMessage());
            }

            // 如果小时预报获取失败，设置默认值
            if (!hourlySuccess) {
                JSONArray defaultHourly = new JSONArray();
                for (int i = 0; i < 5; i++) {
                    JSONObject hour = new JSONObject();
                    hour.put("fxTime", System.currentTimeMillis() + i * 3600000);
                    hour.put("temp", "20");
                    hour.put("icon", "100");
                    defaultHourly.add(hour);
                }
                vo.setHourly(defaultHourly);
            }

            return vo;
        } catch (Exception e) {
            log.error("Error in fetchFromAPI: {}", e.getMessage(), e);
            // 即使发生异常，也返回包含默认值的天气数据
            WeatherVO vo = new WeatherVO();
            vo.setLocation(location);

            // 设置默认当前天气
            JSONObject defaultNow = new JSONObject();
            defaultNow.put("temp", "20");
            defaultNow.put("text", "晴");
            defaultNow.put("icon", "100");
            vo.setNow(defaultNow);

            // 设置默认小时预报
            JSONArray defaultHourly = new JSONArray();
            for (int i = 0; i < 5; i++) {
                JSONObject hour = new JSONObject();
                hour.put("fxTime", System.currentTimeMillis() + i * 3600000);
                hour.put("temp", "20");
                hour.put("icon", "100");
                defaultHourly.add(hour);
            }
            vo.setHourly(defaultHourly);

            return vo;
        }
    }

    private JSONObject decompressStingToJson(byte[] data) {
        if (data == null)
            return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(data));
            byte[] buffer = new byte[1024];
            int read;
            while ((read = gzip.read(buffer)) != -1)
                stream.write(buffer, 0, read);
            gzip.close();
            stream.close();
            return JSONObject.parseObject(stream.toString());
        } catch (IOException e) {
            log.warn("Error decompressing data: {}", e.getMessage());
            return null;
        }
    }

    private WeatherVO getDefaultWeather() {
        log.info("Returning default weather data");
        WeatherVO vo = new WeatherVO();

        // 设置默认位置信息
        JSONObject location = new JSONObject();
        location.put("country", "中国");
        location.put("adm1", "北京市");
        location.put("adm2", "北京");
        location.put("name", "城区");
        vo.setLocation(location);

        // 设置默认当前天气
        JSONObject now = new JSONObject();
        now.put("temp", "20");
        now.put("text", "晴");
        now.put("icon", "100");
        vo.setNow(now);

        // 设置默认小时预报
        JSONArray hourly = new JSONArray();
        for (int i = 0; i < 5; i++) {
            JSONObject hour = new JSONObject();
            hour.put("fxTime", System.currentTimeMillis() + i * 3600000);
            hour.put("temp", "20");
            hour.put("icon", "100");
            hourly.add(hour);
        }
        vo.setHourly(hourly);

        return vo;
    }

}
