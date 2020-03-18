package kr.co.cntt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import kr.co.cntt.scc.SCCWebApplication;
import kr.co.cntt.scc.service.SmsService;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SCCWebApplication.class)
@WebAppConfiguration
@ActiveProfiles("local")
public class SmsTest {
	
	@Autowired
	SmsService sms;
	
	@Test
	public void test() {
		for(int i=0; i<100; i++) {
			sms.sendSmsTest(i);
		}
	}

}
