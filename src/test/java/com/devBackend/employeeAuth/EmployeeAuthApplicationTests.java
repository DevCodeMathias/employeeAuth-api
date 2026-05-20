package com.devBackend.employeeAuth;

import com.devBackend.employeeAuth.domain.repository.IEmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
		"security.jwt.secret=test-secret-value-with-at-least-32-chars",
		"security.jwt.expiration-hours=2",
		"spring.autoconfigure.exclude=org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration,"
				+ "org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration,"
				+ "org.springframework.boot.data.mongodb.autoconfigure.DataMongoRepositoriesAutoConfiguration"
})
class EmployeeAuthApplicationTests {

	@MockitoBean
	private IEmployeeRepository employeeRepository;

	@Test
	void contextLoads() {
	}

}
