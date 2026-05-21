package com.devBackend.employeeAuth;

import com.devBackend.employeeAuth.domain.repository.IEmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = {
		"security.jwt.secret=test-secret-value-with-at-least-32-chars",
		"security.jwt.expiration-hours=24",
		"spring.autoconfigure.exclude=org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration,"
				+ "org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration,"
				+ "org.springframework.boot.data.mongodb.autoconfigure.DataMongoRepositoriesAutoConfiguration"
})
class EmployeeAuthApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private IEmployeeRepository employeeRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void returnsNotFoundErrorWhenRouteDoesNotExist() throws Exception {
		mockMvc.perform(get("/api/v1/unknown"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("ROUTE_NOT_FOUND"))
				.andExpect(jsonPath("$.message").value("Route not found"))
				.andExpect(jsonPath("$.path").value("/api/v1/unknown"));
	}

}
