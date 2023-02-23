package com.micro.mall;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MallDemoApplicationTests {
	private Logger logger = LoggerFactory.getLogger(MallDemoApplicationTests.class);
	@Test
	public void contextLoads() {
	}

	@Test
	public void testLogStash() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		logger.info("MallDemoApplicationTests");
	}

}
