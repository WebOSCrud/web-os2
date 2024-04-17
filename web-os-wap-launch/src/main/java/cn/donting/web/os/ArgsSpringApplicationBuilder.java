package cn.donting.web.os;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class ArgsSpringApplicationBuilder extends SpringApplicationBuilder {
    private String[] args;
    public ArgsSpringApplicationBuilder(String[] args) {
        this.args = args;
    }

    @Override
    public SpringApplication build() {
        return super.build(args);
    }

    public static void main(String[] args) {

    }
}
