package kr.co.cntt;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import kr.co.cntt.scc.SCCWebApplication;
import kr.co.cntt.scc.model.User;
import kr.co.cntt.scc.util.CombinedSqlParameterSource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SCCWebApplication.class)
@WebAppConfiguration
@ActiveProfiles("local")
public class SCCWebApplicationTests {

	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;

	@Test
	public void contextLoads() {
	}

	@Test
	public void bcryptPasswordEncodeTest() {
		
		User user = new User();
		user.setUserId("test2");
		user.setPassword("asdfasdf");
		user.setName("test2");
		user.setRole("user");
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);

		String encoded_password = encoder.encode(user.getPassword());
		System.out.println(encoded_password);
		assertTrue(encoder.matches("asdfasdf", encoded_password));
		
		String s = " INSERT INTO user ( " + " userId, name, password, role " + " ) VALUES ( "
				+ " :userId, :name, :encoded_password, :role " + " ) ";

		System.out.println(encoder.encode(user.getEncoded_password()));
		CombinedSqlParameterSource source = new CombinedSqlParameterSource(user);
		
		source.addValue("userId", user.getUserId());
		source.addValue("password", encoder.encode(user.getPassword()));
		// KeyHolder keyHolder = new GeneratedKeyHolder();
		// return jdbcTemplate.update(s.toString(), source, keyHolder, new
		// String[] {"id"});
		int result = jdbcTemplate.update(s, source);
		System.out.println(result);
	}

	@Test
	public void standardPasswordEncodeTest() {

		User user = new User();
		user.setUserId("test2");
		user.setPassword("asdfasdf");
		user.setName("test2");
		user.setRole("user");
		user.setEncoded_password("user");

		PasswordEncoder encoder = new StandardPasswordEncoder();

		String encoded_password = encoder.encode(user.getPassword());
		System.out.println(encoded_password);
		assertTrue(encoder.matches("asdfasdf", encoded_password));

		String s = " INSERT INTO user ( " + " userId, name, password, role " + " ) VALUES ( "
				+ " :userId, :name, :encoded_password, :role " + " ) ";

		System.out.println(encoder.encode(user.getEncoded_password()));
		CombinedSqlParameterSource source = new CombinedSqlParameterSource(user);

		source.addValue("userId", user.getUserId());
		source.addValue("password", encoder.encode(user.getPassword()));
		// KeyHolder keyHolder = new GeneratedKeyHolder();
		// return jdbcTemplate.update(s.toString(), source, keyHolder, new
		// String[] {"id"});
		int result = jdbcTemplate.update(s, source);
		System.out.println(result);
	}

}
