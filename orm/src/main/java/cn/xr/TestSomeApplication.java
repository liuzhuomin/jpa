package cn.xr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@ComponentScan("cn.xr")
//@EntityScan({"cn.xr.test", "cn.xr.model.truth"})
@Slf4j
public class TestSomeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestSomeApplication.class, args);
    }



}

