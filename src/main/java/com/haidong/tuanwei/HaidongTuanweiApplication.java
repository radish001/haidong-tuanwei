package com.haidong.tuanwei;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.haidong.tuanwei.auth.dao", "com.haidong.tuanwei.dashboard.dao", "com.haidong.tuanwei.system.dao", "com.haidong.tuanwei.youth.dao", "com.haidong.tuanwei.analytics.dao", "com.haidong.tuanwei.enterprise.dao", "com.haidong.tuanwei.job.dao", "com.haidong.tuanwei.policy.dao"})
public class HaidongTuanweiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HaidongTuanweiApplication.class, args);
	}

}
