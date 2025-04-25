package remitly.swift;

import org.springframework.boot.SpringApplication;

public class TestSwiftApplication {

	public static void main(String[] args) {
		SpringApplication.from(SwiftApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
