package cn.xr.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("cn.xr")
@EntityScan({"cn.xr.test","cn.xr.model.truth"})
public class TestSomeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestSomeApplication.class, args);
    }

}
