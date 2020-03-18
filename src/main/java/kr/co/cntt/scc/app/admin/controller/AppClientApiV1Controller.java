package kr.co.cntt.scc.app.admin.controller;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.imgscalr.Scalr;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


import kr.co.cntt.scc.Constants;
import kr.co.cntt.scc.app.admin.common.ApiResult;
import kr.co.cntt.scc.app.admin.common.ApiResultConsts;
import kr.co.cntt.scc.app.admin.model.settingSearch.ResponseVersion;
import kr.co.cntt.scc.app.admin.model.statusList.StatusListDeskInfo;
import kr.co.cntt.scc.app.admin.model.statusList.StatusListEntry;
import kr.co.cntt.scc.app.admin.model.statusList.TotalLearning;
import kr.co.cntt.scc.app.admin.model.AppClientReservation;
import kr.co.cntt.scc.app.admin.model.AppClientReservation.ReservationHistory;
import kr.co.cntt.scc.app.admin.model.AppClientStudy;
import kr.co.cntt.scc.app.admin.model.AppClientMain;
import kr.co.cntt.scc.app.admin.model.AppClientMember;
import kr.co.cntt.scc.app.admin.model.AppClientReport;
import kr.co.cntt.scc.app.admin.model.AppClientUserInfo;
import kr.co.cntt.scc.app.admin.model.register;
import kr.co.cntt.scc.app.admin.model.settingSearch;
import kr.co.cntt.scc.app.admin.model.smsCertify;
import kr.co.cntt.scc.app.admin.model.statusList;
import kr.co.cntt.scc.app.admin.model.notice.NoticeDetail;
import kr.co.cntt.scc.app.admin.model.forPassword;
import kr.co.cntt.scc.app.admin.model.notice;
import kr.co.cntt.scc.app.admin.model.register.RegisterPay;
import kr.co.cntt.scc.app.admin.model.register.RegisterReservation;
import kr.co.cntt.scc.app.admin.service.AppSmsCertify;
import kr.co.cntt.scc.app.student.service.AppClientMemberService;
import kr.co.cntt.scc.service.BranchMemberService;
import kr.co.cntt.scc.service.BranchPayService;
import kr.co.cntt.scc.service.SmsService;
import kr.co.cntt.scc.service.UserService;
import kr.co.cntt.scc.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

//@RequestMapping(value="/push/subscription", method=RequestMethod.POST, headers = {"content-type=application/json"})
@Controller
@Slf4j
@RequestMapping("/app/client/api/v1")
public class AppClientApiV1Controller {


	@Autowired
	private UserService userService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	private AppSmsCertify asc;

	@Autowired
	private SmsService smsService;
	
	@Autowired
	private AppClientMemberService appMemberService;
	
	@Autowired
	private BranchPayService branchPayService;

	@Autowired
	private BranchMemberService branchMemberService;

	@RequestMapping(value = "/activate", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<?> validateUnknownUser(
										@RequestParam(value = "authType", required = false) Integer authType,
										@RequestParam(value = "tel", required = false) String tel,
										@RequestParam(value = "authNum", required = false) String authNum,
										@RequestParam(value = "name", required = false) String name,
										@RequestParam(value = "id", required = false) String id) {
		
		ModelMapper modelMapper = new ModelMapper();
		smsCertify.Response result_data = null;
		ApiResultConsts.resultCode result_code = null;
		ApiResult.header header = modelMapper.map("", ApiResult.header.class);
		ApiResult apiResult = null;

		if (tel == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

		}

		if (authType == null) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

		}

			// http://localhost:8888/app/client/api/v1/activate?tel=01050086019&authType=10
			result_data = modelMapper.map("", smsCertify.Response.class);
			String description = "";
			String sendAuthNum = "";
			
			
			if(authNum == null || authNum.equals("")) {
				
				Random random = new Random();
				sendAuthNum = String.valueOf(random.nextInt(1000000) + 100000);
	
				if (sendAuthNum.length() > 6) {
					int no_authNum = Integer.parseInt(sendAuthNum) - 100000;
					sendAuthNum = String.valueOf(no_authNum);
				} else {
				}
			
			} else {
				
			}
			
				if(authType == 10 ) {
					 description = "가입-본인인증";
				} else if(authType == 20) {
					
					//학생 테이블에서 검색
					int result = userService.selectUserStudent(tel, name, id);
					
						if( result > 0 ) {
							// 학생 계정이 존재함
							result_data.setMemberYn(true);
							result_data.setTel(tel);
						} else {
							//학부모 테이블에서 검색
							result = userService.selectUserParents(tel, name, id);
							
							if( result > 0 ) {
								// 계정이 존재함
								result_data.setMemberYn(true);
								result_data.setTel(tel);
							} else {
								// 부모 계정이 존재하지 않음
								result_data.setMemberYn(false);
								result_data.setAuthYn(false);
								result_data.setTel(tel);
								
								result_code = ApiResultConsts.resultCode.SUCCESS;
								header.setResult_code(result_code.getCode());
								header.setResult_message(result_code.getMessage());
								apiResult = new ApiResult(result_data, header);

								return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());								
							}
							
						}
					
							if(authNum != null && result_data.getMemberYn() == true ) {
							
								// 인증번호 확인
								List<String> smscertify = asc.chkAuthNum(tel, authNum);
			
								if (smscertify.size() > 0) {
									result_data.setMemberYn(true);	
									result_data.setAuthYn(true);
									
								} else {
									result_data.setMemberYn(true);
									// 폰 번호 변경 후 인증요청 시 or 인증번호 오류
									result_data.setAuthYn(false);
								}
									 
							} else {
								result_data.setMemberYn(true);	
								result_data.setAuthYn(false);
								
							}
						
						// authType 20 and 계정이 존재할 때의 조건
						 description = "비밀번호 찾기-본인인증";
						 result_data.setTel(tel);
						 
				} else {
					
				}

				
			if( authType == 10 || (authType == 20 && result_data.getMemberYn() == true &&  ( authNum == null || authNum.equals("")) )) {
				
					if(authNum == null || authNum.equals("")){
				
						int result = asc.insertSmsDB(tel, sendAuthNum, description, authType);
						
						if(result == 0 ) {
							
							// 커넥션 에러  ( DB INSERT ERROR )
							result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
							header.setResult_code(result_code.getCode());
							header.setResult_message(result_code.getMessage());
							result_data.setMemberYn(false);
							apiResult = new ApiResult(result_data, header);
		
							return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
							
						} else {
								
							String msg = String.format("[%s] 인증번호는 [ %s ]입니다.", "CNT 스터디센터", sendAuthNum);
							
							smsService.sendSms("", "", "CNT 스터디센터", "1800-0109", "고객", tel, msg);
							// 인증번호 발송 성공
							if(authType == 10) {
								result_data.setMemberYn(false);
							} else {
								result_data.setMemberYn(true);
							}
							//result_data.setMemberYn(false);
							//result_data.setAuthYn(true);
							result_data.setAuthYn(false);
							result_data.setTel(tel);
						}
					} else { 
						// 인증번호 확인
						List<String> smscertify = asc.chkAuthNum(tel, authNum);
		
						if (smscertify.size() > 0) {
							if (smscertify.get(0).equals("1")) {
								result_data.setMemberYn(false);	
								result_data.setAuthYn(true);
							} else {
								result_data.setMemberYn(false);
								result_data.setAuthYn(false);
							}
							
						} else {
							result_data.setMemberYn(false);
							// 폰 번호 변경 후 인증요청 시 or 인증번호 오류
							result_data.setAuthYn(false);
						}
							result_data.setTel(tel);
					}
				} else {
					
					
				}
			result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

		}


	//V
	@RequestMapping(value = "/checkId", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<?> doCheckId(@RequestParam(value = "id", required = false) String userId) {

		ModelMapper modelMapper = new ModelMapper();

		AppClientMember.DupResponse result_data = null;
		result_data = modelMapper.map("", AppClientMember.DupResponse.class);
		ApiResultConsts.resultCode result_code = null;
		ApiResult.header header = modelMapper.map("", ApiResult.header.class);
		ApiResult apiResult = null;

		if (userId == null || userId.equals("")) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
			
		} else {
			
			Pattern p = Pattern.compile("^[a-zA-Z]{1}[a-zA-Z0-9_].{4,18}$");
			Matcher m = p.matcher(userId);
			if (m.find()) {
				
			} else {
				result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_PATTERN_ID;
		        
		        header.setResult_code(result_code.getCode());
		        header.setResult_message(result_code.getMessage());
		        result_data.setIdDupYn(false);
		        apiResult = new ApiResult(result_data, header);

				return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
			}

		}

		List<AppClientMember> appClientMember = userService.duplicateChkStudent(userId);

		if (appClientMember.get(0).getResult_count() == 0) {

			appClientMember = userService.duplicateChkParents(userId);

			if (appClientMember.get(0).getResult_count() == 0) {

				result_data.setIdDupYn(true);
				result_code = ApiResultConsts.resultCode.SUCCESS;

			} else {

				result_data.setIdDupYn(false);
				result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			}

		} else {
			// 통신은 성공했지만 아이디 중복
			result_code = ApiResultConsts.resultCode.SUCCESS;
			result_data.setIdDupYn(false);
		}

		header = modelMapper.map("", ApiResult.header.class);
		header.setResult_code(result_code.getCode());
		header.setResult_message(result_code.getMessage());

		apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

	}
	
	//V
	@RequestMapping(value = "/findId", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<?> doFindId(
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "tel", required = false) String tel,
			@RequestParam(value = "role", required = false) Integer role

	) {

		tel = tel.toString().replace("-", "");

		ModelMapper modelMapper = new ModelMapper();

		AppClientMember.IdResponse result_data = null;
		ApiResultConsts.resultCode result_code = null;
		ApiResult.header header = modelMapper.map("", ApiResult.header.class);
		ApiResult apiResult = null;
 
		if (name == null || name.equals("")) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

		}

		if (tel == null || tel.equals("")) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}

		if (role == null) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}

		AppClientMember acm = null;

		if (role == 10) {

			acm = userService.findIdInStudent(name, tel, role);

			if (acm == null) {
				
				// 통신은 성공했지만 role=10 (학생) 계정이 없을 시
				result_data = modelMapper.map("", AppClientMember.IdResponse.class);
				result_data.setIdResult("false");
				result_code = ApiResultConsts.resultCode.SUCCESS;

				header = modelMapper.map("", ApiResult.header.class);
				header.setResult_code(result_code.getCode());
				header.setResult_message(result_code.getMessage());

				apiResult = new ApiResult(result_data, header);

				return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

			} else {
			}

		} else if (role == 20) {

			acm = userService.findIdParents(name, tel, role);

			if (acm == null) {
				//통신은 성공했지만 role=20 (부모) 계정이 없을 시
				result_data = modelMapper.map("", AppClientMember.IdResponse.class);
				result_data.setIdResult("false");
				result_code = ApiResultConsts.resultCode.SUCCESS;

				header = modelMapper.map("", ApiResult.header.class);
				header.setResult_code(result_code.getCode());
				header.setResult_message(result_code.getMessage());

				apiResult = new ApiResult(result_data, header);

				return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

			} else {
			}

		} else {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

		}

		String userId = acm.getId() == null ? null : acm.getId();

		result_data = modelMapper.map("", AppClientMember.IdResponse.class);

		int idSize = userId.length() / 2;

		result_data.setIdResult(userId.replaceAll("(?<=.{" + idSize + "}).", "*"));
		result_code = ApiResultConsts.resultCode.SUCCESS;

		header = modelMapper.map("", ApiResult.header.class);
		header.setResult_code(result_code.getCode());
		header.setResult_message(result_code.getMessage());

		apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

	}
	
	@RequestMapping(value = "/findPw", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<?> doFindPw(
									@RequestParam(value = "name", required = false) String name,
									@RequestParam(value = "role", required = false) Integer role,
									@RequestParam(value = "id", required = false) String userId,
									@RequestParam(value = "tel", required = false) String tel,
									@RequestParam(value = "newPw", required = false) String newPw
									) {

		ModelMapper modelMapper = new ModelMapper();


		AppClientMember acm = new AppClientMember();
		AppClientMember.PwResponse result_data = new AppClientMember.PwResponse();
		ApiResultConsts.resultCode result_code = null;
		ApiResult.header header = modelMapper.map("", ApiResult.header.class);
		ApiResult apiResult = null;

		if (name == null || name.equals("")) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

		}

		if (userId == null || userId.equals("")) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}

		if (tel == null || tel.equals("")) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		
		if (role == null || tel.equals("")) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		
		forPassword forPassword = new forPassword();

		if (role == 10) {

			int change_result = 0;

			// 학생 null 체크
			acm = userService.findStudentPw(name, userId, tel, role);

			// 비밀번호 변경 전 회원 유무 체크
			if (newPw == null || newPw.equals("")) {

				// 학생 null - TRUE
				if (acm.getResult_count() == 0) {
					
					// 통신은 성공했지만 존재하지 않는 회원일 경우
					result_data = modelMapper.map("", AppClientMember.PwResponse.class);
					result_code = ApiResultConsts.resultCode.SUCCESS;
					result_data.setPwResult(false);

				} else {

					result_data = modelMapper.map("", AppClientMember.PwResponse.class);
					result_data.setPwResult(true);
					result_code = ApiResultConsts.resultCode.SUCCESS;
				}

			} else {
					
				Pattern p = Pattern.compile("([a-zA-Z0-9].*[!,@,#,$,%,^,&,*,?,_,~])$|([!,@,#,$,%,^,&,*,?,_,~].*[a-zA-Z0-9])$");
				Matcher m = p.matcher(newPw);
				if (m.find() && (newPw.length() > 7 && newPw.length() < 21)) {

				} else {
					result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_PATTERN_PW;
			        
			        header.setResult_code(result_code.getCode());
			        header.setResult_message(result_code.getMessage());
			        result_data.setPwResult(false);
			        apiResult = new ApiResult(result_data, header);

					return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
				}
				
				// 학생 null - TRUE
				if (acm.getResult_count() == 0) {

					// 통신은 성공했지만 존재하지 않는 회원일 경우
					result_data = modelMapper.map("", AppClientMember.PwResponse.class);
					result_code = ApiResultConsts.resultCode.SUCCESS;
					result_data.setPwResult(false);

				} else {

					result_data = modelMapper.map("", AppClientMember.PwResponse.class);
					result_data.setPwResult(true);

					forPassword.setName(name);
					forPassword.setUserId(userId);
					forPassword.setTel(tel);
					forPassword.setNewPw(newPw);
					forPassword.setRole(role);

					change_result = userService.changeStudentPw(forPassword);

					if (change_result == 0) {
						result_code = ApiResultConsts.resultCode.ERROR_SERVER;
					} else {
						result_code = ApiResultConsts.resultCode.SUCCESS;
					}

				}
			}

			header = modelMapper.map("", ApiResult.header.class);
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());

			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

		} else if (role == 20) {

			int change_result = 0;

			// 부모 null 체크
			acm = userService.findParentsPw(name, userId, tel, role);

			// 비밀번호 변경 전 회원 유무 체크
			if (newPw == null || newPw.equals("")) {

				// 학생 null - TRUE
				if (acm.getResult_count() == 0) {

					result_data = modelMapper.map("", AppClientMember.PwResponse.class);
					result_data.setPwResult(false);
					result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

				} else {

					result_data = modelMapper.map("", AppClientMember.PwResponse.class);
					result_data.setPwResult(true);
					result_code = ApiResultConsts.resultCode.SUCCESS;
				}

			} else {
				
				Pattern p = Pattern.compile("([a-zA-Z0-9].*[!,@,#,$,%,^,&,*,?,_,~])$|([!,@,#,$,%,^,&,*,?,_,~].*[a-zA-Z0-9])$");
				Matcher m = p.matcher(newPw);
				if (m.find() && (newPw.length() > 7 && newPw.length() < 21)) {

				} else {
					result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_PATTERN_PW;
			        
			        header.setResult_code(result_code.getCode());
			        header.setResult_message(result_code.getMessage());
			        result_data.setPwResult(false);
			        apiResult = new ApiResult(result_data, header);

					return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
				}
				
				// 부모 null - TRUE
				if (acm.getResult_count() == 0) {

					result_data = modelMapper.map("", AppClientMember.PwResponse.class);
					result_data.setPwResult(false);
					result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

				} else {
					result_data = modelMapper.map("", AppClientMember.PwResponse.class);

					forPassword.setName(name);
					forPassword.setUserId(userId);
					forPassword.setTel(tel);
					forPassword.setNewPw(newPw);
					forPassword.setRole(role);

					change_result = userService.changeParentsPw(forPassword);

					if (change_result == 0) {
						result_code = ApiResultConsts.resultCode.ERROR_SERVER;
						result_data.setPwResult(false);
					} else {

						result_code = ApiResultConsts.resultCode.SUCCESS;
						result_data.setPwResult(true);
					}

				}
			}

			header = modelMapper.map("", ApiResult.header.class);
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());

			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

		} else {

			// role값 안 넘어왔거나 10/20이 아닐 경우
			result_data = modelMapper.map("", AppClientMember.PwResponse.class);
			result_data.setPwResult(false);
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header = modelMapper.map("", ApiResult.header.class);
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());

			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}

	}

	@RequestMapping(value = "/login", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<?> doLogin(
			@RequestParam(value = "userId", required = false) String userId,
			@RequestParam(value = "userPw", required = false) String userPw,
			@RequestParam(value = "auto", defaultValue = "TRUE", required = false) Boolean auto,
			@RequestParam(value = "uuid", required = false) String uuid,
			@RequestParam(value = "os", required = false) String os,
			@RequestParam(value = "version", required = false) String version,
			@RequestParam(value = "device", required = false) String device,
			@RequestParam(value = "pushId", required = false) String pushId,
			@RequestParam(value = "loginType", required = false) String loginType) {

		ModelMapper modelMapper = new ModelMapper();

		AppClientMember.LoginResponse result_data = null;
		ApiResultConsts.resultCode result_code = null;
		ApiResult.header header = modelMapper.map("", ApiResult.header.class);
		ApiResult apiResult = null;

		if (userId == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		if (userPw == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		if (auto == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		if (uuid == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		if (os == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		if (version == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		if (device == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		if (pushId == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}

		List<AppClientMember> appClientMember = userService.findAppUser_student(userId);

		if (appClientMember.get(0).getNo() == null) {

			appClientMember = userService.findAppUser_parents(userId);

			if (appClientMember.get(0).getNo() == null) {

				result_code = ApiResultConsts.resultCode.ERROR_LOGIN_NOT_MATCH;
				result_data = null;
				header.setResult_code(result_code.getCode());
				header.setResult_message(result_code.getMessage());
				apiResult = new ApiResult(result_data, header);

				return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
				
			} else {

			}

		} else {
		}

		// if (userPw.equals(appClientMember.get(0).getUserPw())) {
		if (passwordEncoder.matches(userPw, appClientMember.get(0).getUserPw()) == true) {

			int result = userService.insertAppStudent(appClientMember.get(0).getNo(), userId, auto, uuid, os, version,
					device, pushId);

			if (result == 0) {

				result_code = ApiResultConsts.resultCode.ERROR_SERVER;

				result_data = modelMapper.map("", AppClientMember.LoginResponse.class);
				result_data.setNo(appClientMember.get(0).getNo());
				result_data.setRole(appClientMember.get(0).getRole());

				header.setResult_code(result_code.getCode());
				header.setResult_message(result_code.getMessage());
				apiResult = new ApiResult(result_data, header);

				return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

			} else {

				userService.insertAppClientLogHistory(appClientMember.get(0).getNo(),  "APP_LOGIN");

				result_data = modelMapper.map("", AppClientMember.LoginResponse.class);
				result_data.setNo(appClientMember.get(0).getNo());
				result_data.setRole(appClientMember.get(0).getRole());
				
				if(appClientMember.get(0).getRole() == 10) {
					result_data.setMainChildNo("");
				} else { 
					result_data.setMainChildNo(appClientMember.get(0).getMainChildNo());
				}
				
				result_code = ApiResultConsts.resultCode.SUCCESS;

				header.setResult_code(result_code.getCode());
				header.setResult_message(result_code.getMessage());
				apiResult = new ApiResult(result_data, header);

				return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
			}

		} else {

			result_data = modelMapper.map("", AppClientMember.LoginResponse.class);
			result_code = ApiResultConsts.resultCode.ERROR_LOGIN_NOT_MATCH;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

		}

	}

	@RequestMapping(value = "/logout", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<?> doLogout(
									@RequestHeader(value = "no", required = false) String no,
									@RequestHeader(value = "role", required = false) Integer role ) {

		// 결과
		ModelMapper modelMapper = new ModelMapper();

		settingSearch.ResponseLogout result_data = modelMapper.map("", settingSearch.ResponseLogout.class);
		ApiResultConsts.resultCode result_code = null;
		ApiResult.header header = modelMapper.map("", ApiResult.header.class);
		ApiResult apiResult = null;

		if (no == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(null, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}

		if (role == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(null, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}

		List<AppClientMember> appClientMember = userService.selectStudentUserNo(no, role);

		if (appClientMember.get(0).getResult_count() == 0) {

			appClientMember = userService.selectParentsUserNo(no, role);

			if (appClientMember.get(0).getResult_count() == 0) {

				result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

				header.setResult_code(result_code.getCode());
				header.setResult_message(result_code.getMessage());

				apiResult = new ApiResult(result_data, header);

				return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

			} else {
			}

		} else {
		}

		int result = userService.insertAppClientLogHistory(no, "APP_LOGOUT");

		if (result == 0) {
			result_data.setSuccessYn(false);
		} else {
			result_data.setSuccessYn(true);
		}

		result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
		header.setResult_code(result_code.getCode());
		header.setResult_message(result_code.getMessage());
		apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}

	@RequestMapping(value = "/mypage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<?> getMyPage(
			@RequestHeader(value = "no", required = false) String no,
			@RequestHeader(value = "role", required = false) Integer role
	) {

		ModelMapper modelMapper = new ModelMapper();

		AppClientUserInfo result_data = modelMapper.map("", AppClientUserInfo.class);
		ApiResultConsts.resultCode result_code = null;
		ApiResult.header header = modelMapper.map("", ApiResult.header.class);
		ApiResult apiResult = null;

		if (no == null || no.equals("")) {
			result_code = ApiResultConsts.resultCode.SUCCESS;
			
				result_data.setNo(null);
				result_data.setRole(null);
				result_data.setId(null);
				result_data.setName(null);
				result_data.setGender(null);
				result_data.setBranchNm(null);
				result_data.setImgUrl(null);
				result_data.setTel(null);
				result_data.setSmsYes(null);
				
				result_data.setEnterexitYes(null);
				result_data.setAddress1(null);
				result_data.setAddress2(null);
				result_data.setPostCode(null);
				result_data.setLocker(null);
				result_data.setJob(null);
				result_data.setInterest(null);
				result_data.setEmail(null);
				result_data.setBirthDt(null);
			
			if(role == 20) {
				result_data.setTelParent(null);
			} else {
				
			}
				
			if(role == 20) {
				result_data.setMainChildNo(null);
				result_data.setChildList(null);
			} else {
				
			}
			
			
			
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			
			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
			
			
		}

		if (role == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(null, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

		}


		if (role == 10) {

			AppClientUserInfo.StudentsUserInfo appClientUserInfo = null;
			
			appClientUserInfo = userService.selectUserinfoStudent(no, role);

			if (appClientUserInfo == null) {
				result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			} else {

				result_code = ApiResultConsts.resultCode.SUCCESS;
				appClientUserInfo.setBirthDt(DateUtil.getCurrentDateStringAppAdminParse(appClientUserInfo.getBirthDt()));
			}
				header.setResult_code(result_code.getCode());
				header.setResult_message(result_code.getMessage());
				apiResult = new ApiResult(appClientUserInfo, header);
	
				return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
			
		} else if (role == 20) {

			AppClientUserInfo.ParentsUserInfo appClientUserInfo = null;
			
			appClientUserInfo = userService.selectUserinfoParents(no, role);

			if (appClientUserInfo == null) {

				result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			} else {

				List<AppClientUserInfo.StudentList> childList = userService.selectStudentList(appClientUserInfo.getNo());

				appClientUserInfo.setChildList(childList);
				appClientUserInfo.setBirthDt(DateUtil.getCurrentDateStringAppAdminParse(appClientUserInfo.getBirthDt()));
				
				result_code = ApiResultConsts.resultCode.SUCCESS;
				header.setResult_code(result_code.getCode());
				header.setResult_message(result_code.getMessage());
				apiResult = new ApiResult(appClientUserInfo, header);

				return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

			}

		} else {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
		}

		header.setResult_code(result_code.getCode());
		header.setResult_message(result_code.getMessage());
		apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

	} 

	@RequestMapping(value = "/mypageProfile", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<?> profileImageUpload(
			@RequestHeader(value = "no", required = false) String no,
			@RequestHeader(value = "role", required = false) Integer role,
			//@RequestParam(value = "no", required = false) String no,
			//@RequestParam(value = "role", required = false) Integer role,
			@RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
			HttpServletRequest request
			) {

		MultipartFile uploadFile = profileImage;
		
		
		ModelMapper modelMapper = new ModelMapper();
		AppClientUserInfo.UpdateResult result_data = null;
		ApiResultConsts.resultCode result_code = null;
		ApiResult.header header = modelMapper.map("", ApiResult.header.class);
		ApiResult apiResult = null;

		if (no == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

		}

		if (role == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

		}
		
		if (uploadFile.getSize() > 3072000) {
			//용량 3MB 이상 접근거부
			//application.properties에서는 버퍼 5MB 설정
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

		}
		
		if (uploadFile.getSize() > 0) {
			try {
				
				String uuid = UUID.randomUUID().toString().replace("-", "");
				byte[] bytes = profileImage.getBytes();

				// 서버 내 이미지 저장 경로
				String path = System.getProperty("user.home");
				String finalPath = File.separator+ "tmp" + File.separator + "member" + File.separator + "images" + File.separator;
				Path imgPath = Paths.get(finalPath + uuid + "_" + profileImage.getOriginalFilename());
				String strImgPath = imgPath.toString();
				// 디렉토리 생성
				File imgFolder = new File(finalPath);
				
				if (!imgFolder.exists()) {
					imgFolder.mkdirs();
    			} else {
    				
    			}
								
				// 이미지 생성
				Files.write(imgPath, bytes);
				BufferedImage bimg = ImageIO.read(new File(imgPath.toString()));
				BufferedImage scaled = Scalr.resize(bimg, 252, 252);
				
				int pos = uploadFile.getOriginalFilename().lastIndexOf( "." );
				String ext = uploadFile.getOriginalFilename().substring( pos + 1 );

				ImageIO.write(scaled, ext , new File(imgPath.toString()));
				// App에서 요청하는 이미지 경로
				String serverPath = request.getScheme() + "://" + request.getServerName() + ":" 
								+ request.getServerPort() + request.getContextPath();
				String dbPath = serverPath + "/" + "files" + "/";
				String fauxPath = dbPath + uuid + "_" + profileImage.getOriginalFilename();
				Integer pathLength = (dbPath.length())+1;
				List<AppClientUserInfo> appClientUserInfo = new ArrayList<>();
				
				int result = 0;
				
				if (role == 10) {
						//기존 이미지 경로 SELECT(role = 10)
						appClientUserInfo = userService.selectOldStudentImg(no, pathLength);
						result = userService.updateStudentImage(no, fauxPath);
				} else if (role == 20) {
						//기존 이미지 경로 SELECT(role = 20)					
						appClientUserInfo = userService.selectOldParentsImg(no, pathLength);
						result = userService.updateParentsImage(no, fauxPath);
				} else {
				}
					
					if(result == 0 ) {
						// 기존이미지 update 실패 시
						result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

						header.setResult_code(result_code.getCode());
						header.setResult_message(result_code.getMessage());
						apiResult = new ApiResult(result_data, header);

						return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
					}
				
				String oldImg = appClientUserInfo.get(0).getImgUrl();
				
				// 이미지 삭제 (기존 이미지)
				if (oldImg != null) {
					Files.delete(Paths.get(finalPath + oldImg));
				} else {
				}

				if (result == 0) {

					result_data = modelMapper.map("", AppClientUserInfo.UpdateResult.class);
					result_data.setUpdResult(false);
					result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

					header.setResult_code(result_code.getCode());
					header.setResult_message(result_code.getMessage());
					apiResult = new ApiResult(result_data, header);

					return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

				} else {

				}

			} catch (IOException e) {
				e.printStackTrace();

				result_data = modelMapper.map("", AppClientUserInfo.UpdateResult.class);
				result_data.setUpdResult(false);
				result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

				header.setResult_code(result_code.getCode());
				header.setResult_message(result_code.getMessage());
				apiResult = new ApiResult(result_data, header);

				return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
			}

		} else {
		}

		result_data = modelMapper.map("", AppClientUserInfo.UpdateResult.class);
		result_data.setUpdResult(true);
		result_code = ApiResultConsts.resultCode.SUCCESS;

		header.setResult_code(result_code.getCode());
		header.setResult_message(result_code.getMessage());
		apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}
	
	
	@RequestMapping(value = "/version", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<?> getVersionGET(
									@RequestParam(value = "os") String paramOs) {
		// 안드로이드(A)/아이폰(I)

		// 결과
		ModelMapper modelMapper = new ModelMapper();

		ApiResultConsts.resultCode result_code = null;
		ApiResult.header header = modelMapper.map("", ApiResult.header.class);
		ApiResult apiResult = null;

		if (paramOs == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_OS;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(null, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}

		Integer type = 20;
		List<ResponseVersion> responseVersion = userService.selectVersion(paramOs, type);
		settingSearch.ResponseVersion result_data = modelMapper.map("", settingSearch.ResponseVersion.class);
		result_data.setVersion(responseVersion.get(0).getVersion());
		result_data.setUpdateYn(responseVersion.get(0).isUpdateYn());

		result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;

		header = modelMapper.map("", ApiResult.header.class);
		header.setResult_code(result_code.getCode());
		header.setResult_message(result_code.getMessage());

		apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

	}

	@RequestMapping(value = "/mypageReservation", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<?> getseatReservationList(
			@RequestHeader(value = "no", required = false) String no,
			@RequestHeader(value = "role", required = false) Integer role,
			@RequestParam(value = "type", required = false) Integer type) {

		// 결과
		ModelMapper modelMapper = new ModelMapper();

		AppClientReservation result_data = modelMapper.map("", AppClientReservation.class);
		ApiResultConsts.resultCode result_code = null;
		ApiResult.header header = modelMapper.map("", ApiResult.header.class);
		ApiResult apiResult = null;

		if (no == null || no.equals("")) {
			
			result_code = ApiResultConsts.resultCode.SUCCESS;

			result_data.setReservationHistory(null);
			
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		if (role == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(null, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}

		if (type == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(null, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}

		List<ReservationHistory> reservationHistory = new ArrayList<ReservationHistory>();

		 if (role == 10) { 
			 	reservationHistory = userService.selectSeatReservationList(no, role, type);	
		 } else if(role == 20){
			 	//학부모
				reservationHistory = userService.selectParentsSeatReservationList(no, role, type);
		 } else { 
			 	// role 값 잘못 입력
			 	result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

				header.setResult_code(result_code.getCode());
				header.setResult_message(result_code.getMessage());
				apiResult = new ApiResult(null, header);

				return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		 }
		 
		result_code = ApiResultConsts.resultCode.SUCCESS;
			
		if (reservationHistory.size() == 0) {
			result_data.setReservationHistory(null);
		} else {
			result_data.setReservationHistory(reservationHistory);
		}
		
		header = modelMapper.map("", ApiResult.header.class);
		header.setResult_code(result_code.getCode());
		header.setResult_message(result_code.getMessage());

		apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

	}

	@RequestMapping(value = "/mypageRegisterList", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<?> getUserRegisterListGET(
			@RequestHeader(value = "no", required = false) String no,
			@RequestHeader(value = "role", required = false) Integer role,
			@RequestParam(value = "pagingNum", defaultValue = "1", required = false) Integer pagingNum) {

		// 결과
		ModelMapper modelMapper = new ModelMapper();

		register.Response result_data = null;
		result_data = modelMapper.map("", register.Response.class);
		ApiResultConsts.resultCode result_code = null;
		ApiResult.header header = modelMapper.map("", ApiResult.header.class);
		ApiResult apiResult = null;

		if (no == null || no.equals("")) {
			result_code = ApiResultConsts.resultCode.SUCCESS;

			result_data.setTotalCount(0);
			result_data.setPagingNum(0);
			result_data.setRegisterPayList(null);
			
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		
		if(role == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		
		
		if(role == 10 ) {
			
		} else if(role == 20) {
			
			List<AppClientMember> childNo = userService.getMainChildNo(no, role);
			
			if(childNo.size() > 0) {
					if(childNo.get(0).getMainChildNo() == null || childNo.get(0).getMainChildNo().equals("")) {
						// 주자녀가 없을 경우
						//result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
		
						//header.setResult_code(result_code.getCode());
						//header.setResult_message(result_code.getMessage());
						//apiResult = new ApiResult(result_data, header);
	
						//return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
						
					} else {
						no = childNo.get(0).getMainChildNo();
						role = 10;
					}					
			}
			
		} else {
			// role 오류
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		
		Integer paramPagingNum = (pagingNum - 1) * 10; // 2일 경우 10, 3일 경우 20
		
		// branchId, memberId 매칭
		String paramBranchId = "";
		String paramMemberId = "";

		List<AppClientMember> userInfo = userService.selectBranchId(no, role);
		
		if (userInfo.size() == 0) {
			//회원번호는 있지만 등록내역이 없을 떄
			result_code = ApiResultConsts.resultCode.SUCCESS;

			result_data.setPagingNum(1);
			result_data.setTotalCount(0);
			result_data.setRegisterPayList(null);
			
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

		} else {
			
			List<AppClientMember> branchMatch = userService.selectBranchMatch(no, userInfo.get(0).getMainBranchId());
			
				if(branchMatch.size() > 0) {
					
					 paramBranchId = branchMatch.get(0).getBranchId();
					 paramMemberId = branchMatch.get(0).getMemberId();
					 
				} else {
					// app,codi 매칭 branchId, memberId 없을 때
					result_code = ApiResultConsts.resultCode.SUCCESS;

					result_data.setPagingNum(1);
					result_data.setTotalCount(0);
					result_data.setRegisterPayList(null);
					
					header.setResult_code(result_code.getCode());
					header.setResult_message(result_code.getMessage());
					apiResult = new ApiResult(result_data, header);

					return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
				}
		}
		
		
		
		if (paramBranchId != null || paramMemberId != null) {
			

			List<RegisterReservation> registerReservationList = branchPayService.selectAppRegisterPayList(paramBranchId,
					paramMemberId, paramPagingNum);
			List<RegisterReservation> registerReservationTotal = branchPayService.selectAppRegisterPayList(paramBranchId, paramMemberId, null);
			
			
			List<RegisterReservation> registerReservationListTemp = new ArrayList<RegisterReservation>();
			List<RegisterPay> registerPayList = new ArrayList<RegisterPay>();
			RegisterPay registerPay = new RegisterPay();

			String orderIdTemp = "";

			for (RegisterReservation rr : registerReservationList) {
				Object payType = Constants.PayType.getMap().get(Integer.parseInt(rr.getPayType()));
				if (payType == null) {
					rr.setPayType(null);
					// registerReservationListTemp.get(0).setPayType("선택안함");
				} else {
					rr.setPayType(payType.toString());
					// registerReservationListTemp.get(0).setPayType(payType.toString());
				}
				Object payStateType = Constants.PayStateType.getMap().get(Integer.parseInt(rr.getPayStateType()));
				if (payStateType == null) {
					rr.setPayStateType(null);
					// registerReservationListTemp.get(0).setPayType("선택안함");
				} else {
					rr.setPayStateType(payStateType.toString());
					// registerReservationListTemp.get(0).setPayType(payType.toString());
				}
				Object reservationStatus = Constants.ReservationStatusType.getMap()
						.get(Integer.parseInt(rr.getReservationStatus()));
				if (reservationStatus == null) {
					rr.setReservationStatus(null);
				} else {
					rr.setReservationStatus(reservationStatus.toString());
				}
				// System.out.println("==========================================="+rr.getPayDt());
				rr.setPayDt(DateUtil.getCurrentDateStringAppAdminParse(rr.getPayDt()));
				rr.setDeskStartDt(DateUtil.getCurrentDateStringAppAdminParse(rr.getDeskStartDt()));
				rr.setDeskEndDt(DateUtil.getCurrentDateStringAppAdminParse(rr.getDeskEndDt()));
				
				if (orderIdTemp.equals(rr.getOrderId())) {

					registerReservationListTemp.add(rr);
					registerPay.setOrderId(rr.getOrderId());
					registerPay.setPayDt(rr.getPayDt());
					registerPay.setName(rr.getName());
					registerPay.setPayType(rr.getPayType());
					registerPay.setPayAmount(rr.getPayAmount());
					registerPay.setRoomName(rr.getRoomName());
					registerPay.setDeskName(rr.getDeskName());
					registerPay.setPayStateType(rr.getPayStateType());
					registerPay.setRegisterReservationList(registerReservationListTemp);

					if (registerReservationList.indexOf(rr) == registerReservationList.size() - 1) {
						registerPayList.add(registerPay);
						registerReservationListTemp = new ArrayList<RegisterReservation>();
						registerPay = new RegisterPay();
					}

				} else {
					if (registerReservationList.indexOf(rr) == 0) {

					} else {
						registerPayList.add(registerPay);
						registerReservationListTemp = new ArrayList<RegisterReservation>();
						registerPay = new RegisterPay();
					}

					registerReservationListTemp.add(rr);
					registerPay.setOrderId(rr.getOrderId());
					registerPay.setPayDt(rr.getPayDt());
					registerPay.setName(rr.getName());
					registerPay.setPayType(rr.getPayType());
					registerPay.setPayAmount(rr.getPayAmount());
					registerPay.setRoomName(rr.getRoomName());
					registerPay.setDeskName(rr.getDeskName());
					registerPay.setPayStateType(rr.getPayStateType());
					registerPay.setRegisterReservationList(registerReservationListTemp);
					
				}

				orderIdTemp = rr.getOrderId();
				if (registerReservationList.indexOf(rr) == registerReservationList.size() - 1) {
					registerPayList.add(registerPay);
				}

			}

			result_data.setTotalCount(registerReservationTotal.size());
			result_data.setPagingNum(pagingNum);
			result_data.setRegisterPayList(registerPayList);

		} else {

			result_data.setTotalCount(0);
			result_data.setPagingNum(pagingNum);
			result_data.setRegisterPayList(null);

		}

		result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;

		header = modelMapper.map("", ApiResult.header.class);
		header.setResult_code(result_code.getCode());
		header.setResult_message(result_code.getMessage());

		apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

	}

	@RequestMapping(value = "/mypageUpdatePw", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<?> changePw(
									@RequestHeader(value = "no", required = false) String no,
									@RequestHeader(value = "role", required = false) Integer role,
									@RequestParam(value = "userPw", required = false) String userPw,
									@RequestParam(value = "newPw", required = false) String newPw) {

		ModelMapper modelMapper = new ModelMapper();

		AppClientMember.PwChResponse result_data = null;
		result_data = modelMapper.map("", AppClientMember.PwChResponse.class);
		ApiResultConsts.resultCode result_code = null;
		ApiResult.header header = modelMapper.map("", ApiResult.header.class);
		ApiResult apiResult = null;

		if (no == null) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		if (userPw == null) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		if (role == null) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		if (newPw == null) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		
		Pattern p = Pattern.compile("([a-zA-Z0-9].*[!,@,#,$,%,^,&,*,?,_,~])$|([!,@,#,$,%,^,&,*,?,_,~].*[a-zA-Z0-9])$");
		Matcher m = p.matcher(newPw);
		if (m.find() && (newPw.length() > 7 && newPw.length() < 21)) {

		} else {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_PATTERN_PW;
	        
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        result_data.setPwChYn(false);
	        apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		
		List<AppClientMember> appClientMember = userService.validChk(no, role, userPw);

		if (appClientMember.size() == 0) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			result_data.setPwChYn(false);

		} else {


			
			if (passwordEncoder.matches(userPw, appClientMember.get(0).getUserPw()) == true) {

				int result = userService.changePw(no, role, newPw);

				if (result == 0) {
					result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
					result_data.setPwChYn(false);
				} else {
					result_code = ApiResultConsts.resultCode.SUCCESS;
					result_data.setPwChYn(true);
				}

			} else {
				// 기존의 비밀번호가 일치하지 않을 때
				result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
				result_data.setPwChYn(false);
			}

		}

		result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;

		header = modelMapper.map("", ApiResult.header.class);
		header.setResult_code(result_code.getCode());
		header.setResult_message(result_code.getMessage());

		apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

	}
	
	
	@RequestMapping(value = "/settingSearch", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<?> selectSettingSearch(
							@RequestHeader(value = "no", required = false) String no,
							@RequestHeader(value = "role", required = false) Integer role ) {

		ModelMapper modelMapper = new ModelMapper();

		AppClientUserInfo.SettingInfo result_data = null;
		result_data = modelMapper.map("", AppClientUserInfo.SettingInfo.class);
		ApiResultConsts.resultCode result_code = null;
		ApiResult.header header = modelMapper.map("", ApiResult.header.class);
		ApiResult apiResult = null;

		if (no == null) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
	
		if (role == null) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}

				result_data = userService.selectSetInfo(no);
			
				if(result_data == null) {
					// 검색된 사용자 설정 정보가 없을 때
					result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
					header.setResult_code(result_code.getCode());
					header.setResult_message(result_code.getMessage());
					
					result_data = null;
					apiResult = new ApiResult(result_data, header);

					return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
					
				} else { 
					
					result_code = ApiResultConsts.resultCode.SUCCESS;
					header = modelMapper.map("", ApiResult.header.class);
					header.setResult_code(result_code.getCode());
					header.setResult_message(result_code.getMessage());
					
					
					apiResult = new ApiResult(result_data, header);

					return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
				}
				
	}
	

	@RequestMapping(value = "/setting", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<?> setSetting(
							@RequestHeader(value = "no", required = false) String no,
							@RequestHeader(value = "role", required = false) Integer role,
							@RequestParam(value = "auto", required = false) Integer auto,
							@RequestParam(value = "pushYn", required = false) Integer pushYn
			) {

		ModelMapper modelMapper = new ModelMapper();

		AppClientUserInfo.AfterSet result_data = null;
		result_data = modelMapper.map("", AppClientUserInfo.AfterSet.class);
		ApiResultConsts.resultCode result_code = null;
		ApiResult.header header = modelMapper.map("", ApiResult.header.class);
		ApiResult apiResult = null;

		if (no == null) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
	
		if (role == null) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		
		if (auto == null) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		
		if (pushYn == null) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
			
		
			int result = userService.updateSetting(no, role, auto, pushYn); 
			
			if(result == 0 ) {
				// 설정 변경 실패 
				result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
				header.setResult_code(result_code.getCode());
				header.setResult_message(result_code.getMessage());
				
				result_data = null;
				apiResult = new ApiResult(result_data, header);

				return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
				
			} else {
				
				result_data = userService.selectAfterSetInfo(no);
				
				result_code = ApiResultConsts.resultCode.SUCCESS;
				header.setResult_code(result_code.getCode());
				header.setResult_message(result_code.getMessage());
				
				apiResult = new ApiResult(result_data, header);

				return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
			}
			
	}
	
	@RequestMapping(value = "/studySet", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<?> doStudySet(
			@RequestHeader(value = "no", required = false) String no,
			@RequestHeader(value = "role", required = false) Integer role,
			@RequestParam(value = "type", required = false) Integer type,
			@RequestParam(value = "startDt", required = false) String startDt,
			@RequestParam(value = "endDt", required = false) String endDt,
			@RequestParam(value = "goalTime", required = false) Integer goalTime,
			@RequestParam(value = "learningTime", required = false) String learningTime) {

		ModelMapper modelMapper = new ModelMapper();

		AppClientStudy result_data = null;
		result_data = modelMapper.map("", AppClientStudy.class);

		ApiResultConsts.resultCode result_code = null;
		ApiResult.header header = modelMapper.map("", ApiResult.header.class);
		ApiResult apiResult = null;

		if (no == null) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		if (role == null) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}

		if (type == null) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

		} else {
	
		}

		
		// branchId, memberId 매칭
		String paramBranchId = "";
		String paramMemberId = "";

		List<AppClientMember> userInfo = userService.selectBranchId(no, role);
		
		//if (userInfo.size() == 0) {
		if (userInfo.get(0).getMainBranchId() == null || userInfo.get(0).getMainBranchId().equals("")) {
			// 사용자 정보가 없을 때
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_MAINBRANCHID;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

		} else {
			
			List<AppClientMember> branchMatch = userService.selectBranchMatch(no, userInfo.get(0).getMainBranchId());
			
				if(branchMatch.size() > 0) {
					
					 paramBranchId = branchMatch.get(0).getBranchId();
					 paramMemberId = branchMatch.get(0).getMemberId();
					 
				} else {
					// app,codi 매칭 branchId, memberId 없을 때
					result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

					header.setResult_code(result_code.getCode());
					header.setResult_message(result_code.getMessage());
					apiResult = new ApiResult(result_data, header);

					return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
				}
		}

		//테스트 데이터
		//paramBranchId="525b4464-2805-4af6-9103-9d30e2120e50";
		//paramMemberId="fb8faf1e-fd3a-4224-84a7-b6a2186be42f";
		
		String mainBranchId = paramBranchId;
		String memberId = paramMemberId;
		
		//학습계획
		if (type == 10 && goalTime != null) {

			if (startDt == null || endDt == null) {

				result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
				
				result_data.setSuccesYn(false);
				header.setResult_code(result_code.getCode());
				header.setResult_message(result_code.getMessage());
				apiResult = new ApiResult(result_data, header);
				
				return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
			}
			
			if(goalTime == 30) {
				 
			} else {
				goalTime = (goalTime)*60;
			}
			
			try {

				SimpleDateFormat dateFormatDt = new SimpleDateFormat("yyyy-MM-dd");

				Date startStudy = dateFormatDt.parse(startDt);
				Date endStudy = dateFormatDt.parse(endDt);

				long diffTime = (long) (endStudy.getTime() - startStudy.getTime());
				if(diffTime < 0 ) {
					diffTime = diffTime * -1;
				} else { 
					
				}
				long countDays = ( diffTime / (24 * 60 * 60 * 1000)) + 1;
				long goalTotal = countDays * goalTime;

				int progResult = userService.presentProgressYn(no, role, mainBranchId, memberId);

				if (progResult == 0) {

					int result = userService.insertStudySet(mainBranchId, memberId, no, role, startDt, endDt,
							goalTotal);

					if (result == 0) {

						result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
						result_data.setSuccesYn(false);
						header.setResult_code(result_code.getCode());
						header.setResult_message(result_code.getMessage());
						apiResult = new ApiResult(result_data, header);

						return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

					} else {
					}

				} else {
					
					int result = userService.updateStudySet(no, role, startDt, endDt, goalTotal, mainBranchId, memberId); 
					
					if (result == 0) {

						result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
						result_data.setSuccesYn(false);
						header.setResult_code(result_code.getCode());
						header.setResult_message(result_code.getMessage());
						
						result_data.setSuccesYn(false);
						apiResult = new ApiResult(result_data, header);

						return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

					} else {
						
						//List<AppClientStudy.StudyPlan> studyPlan = null;
						
						AppClientStudy.StudyPlan studyPlan = new AppClientStudy.StudyPlan();
						
						studyPlan = userService.recentStudySet(mainBranchId, no, role, startDt, endDt);

						if(studyPlan == null) {
							result_data.setSuccesYn(false);
							result_data.setStudyPlan(null);
							result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
						} else { 
							result_data.setSuccesYn(true);
							result_data.setStudyPlan(studyPlan);
							result_code = ApiResultConsts.resultCode.SUCCESS;
						}
						
						result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
		
						header.setResult_code(result_code.getCode());
						header.setResult_message(result_code.getMessage());
						//apiResult = new ApiResult(studyPlan, header);
						apiResult = new ApiResult(result_data, header);
		
						return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
					}

				}


				AppClientStudy.StudyPlan studyPlan = new AppClientStudy.StudyPlan();
				studyPlan = userService.recentStudySet(mainBranchId, no, role, startDt, endDt);

				if(studyPlan == null) {
					result_data.setSuccesYn(false);
					result_data.setStudyPlan(null);
					result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
				} else { 
					result_data.setSuccesYn(true);
					result_data.setStudyPlan(studyPlan);
					result_code = ApiResultConsts.resultCode.SUCCESS;
				}
				
					result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS
							: ApiResultConsts.resultCode.ERROR;
	
					header.setResult_code(result_code.getCode());
					header.setResult_message(result_code.getMessage());
					apiResult = new ApiResult(result_data, header);
	
					return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

			} catch (ParseException e) {
				e.printStackTrace();
			}

		} else if (type == 20 && learningTime != null) {
			
			
			AppClientStudy.learnUpd resultRes = null;
			resultRes = modelMapper.map("", AppClientStudy.learnUpd.class);
			
				AppClientStudy.StudyPlan studyPlan = new AppClientStudy.StudyPlan();
				
				
			// 학습계획 업데이트
			int result = userService.updateLearningTime( mainBranchId, memberId, no, role, learningTime);

			if (result == 0) {
				result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
				resultRes.setSuccesYn(false);
			} else {
				result_code = ApiResultConsts.resultCode.SUCCESS;
				resultRes.setSuccesYn(true);
				
					userService.updateStatus(mainBranchId, memberId, no, role);
					
					studyPlan = userService.selectSuccessChk( mainBranchId, memberId, no, role);
					
						if(studyPlan == null) {
							result_data.setSuccesYn(true);
							result_data.setStudyPlan(null);
						} else { 
							result_data.setSuccesYn(true);
							result_data.setStudyPlan(studyPlan);
						}
						
					result_data = new AppClientStudy();
					
					header.setResult_code(result_code.getCode());
					header.setResult_message(result_code.getMessage());
					
					studyPlan.setNo(no);
					studyPlan.setRole(role);
					//studyPlan.setSuccessYn(true);
					
					result_data.setSuccesYn(true);
					result_data.setStudyPlan(studyPlan);
					
					apiResult = new ApiResult(result_data, header);
		
					return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
						
			}
				header.setResult_code(result_code.getCode());
				header.setResult_message(result_code.getMessage());
				
				apiResult = new ApiResult(resultRes, header);
	
				return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
				
		} else {
				
		}
		
			result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
			
			header = modelMapper.map("", ApiResult.header.class);
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
	
			result_data.setSuccesYn(false);
	
			apiResult = new ApiResult(result_data, header);
	
			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

	}

	@RequestMapping(value = "/studyGet", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody ResponseEntity<?> doStudyGet(
								@RequestHeader(value = "no", required = false) String no,
								@RequestHeader(value = "role", required = false) Integer role,
								@RequestParam(value = "pagingNum", defaultValue = "1", required = true) Integer pagingNum) {

		ModelMapper modelMapper = new ModelMapper();

		AppClientStudy.GoalStatus result_data = null;
		result_data = modelMapper.map("", AppClientStudy.GoalStatus.class);

		ApiResultConsts.resultCode result_code = null;
		ApiResult.header header = modelMapper.map("", ApiResult.header.class);
		ApiResult apiResult = null;

		if (no == null) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		if (role == null) {

			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}

		Integer paramPagingNum;
		if (pagingNum == null) {
			paramPagingNum = 0;
		} else {
			paramPagingNum = (pagingNum - 1) * 10; // 2일 경우 10, 3일 경우 20
		}
		
		// branchId, memberId 매칭
		String paramBranchId = "";
		String paramMemberId = "";
		String paramBranchNm = "";
		
		List<AppClientMember> userInfo = new ArrayList<>(); 
		
		if(role == 10) {
				userInfo = userService.selectBranchId(no, role);
		} else if(role == 20) {
			
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
			
		} else {
			
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
			
		}
		
	/*	if (userInfo.size() == 0) {
			// 사용자 정보가 없을 때
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

		} else {*/
			
		//if (userInfo.size() == 0) {
		if (userInfo.get(0).getMainBranchId() == null || userInfo.get(0).getMainBranchId().equals("")) {
			// 사용자 정보가 없을 때
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_MAINBRANCHID;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

		} else {
		
			List<AppClientMember> branchMatch = userService.selectBranchMatch(no, userInfo.get(0).getMainBranchId());
			
				if(branchMatch.size() > 0) {
					
					 paramBranchId = branchMatch.get(0).getBranchId();
					 paramMemberId = branchMatch.get(0).getMemberId();
					 paramBranchNm = branchMatch.get(0).getBranchNm();
					 
				} else {
					// app,codi 매칭 branchId, memberId 없을 때
					/*result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

					header.setResult_code(result_code.getCode());
					header.setResult_message(result_code.getMessage());
					apiResult = new ApiResult(result_data, header);

					return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());*/
					
					result_data.setStartDt(null);
					result_data.setEndDt(null);
					result_data.setGoalTime(null);
					result_data.setStudyStatus(null);
					result_data.setLearningTime(null);
					result_data.setTimeRate(null);
					result_data.setRemainTime(null);
					result_data.setStudyStatus(null);
					result_data.setStudyPlanList(null);
					result_data.setNo(no);
					result_data.setName(userInfo.get(0).getName());
					result_data.setRole(role);
					result_data.setBranchNm(paramBranchNm);
					
					result_code = ApiResultConsts.resultCode.SUCCESS ;

					header = modelMapper.map("", ApiResult.header.class);
					header.setResult_code(result_code.getCode());
					header.setResult_message(result_code.getMessage());

					// result_data.setSuccesYn(false);
					apiResult = new ApiResult(result_data, header);

					return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
					
				}
		}

		//테스트 데이터
		//paramBranchId="525b4464-2805-4af6-9103-9d30e2120e50";
		//paramMemberId="fb8faf1e-fd3a-4224-84a7-b6a2186be42f";
		
		String mainBranchId = paramBranchId;
		String memberId = paramMemberId;
		
			Integer latestYn = 0;
			AppClientStudy.GoalStatus studyGet = userService.selectStudyGet( mainBranchId, memberId, no, latestYn);
			
			if(studyGet == null) {
				result_data = new AppClientStudy.GoalStatus(); 
				
					// 진행 중인 학습계획이 없을 시 가장 최근 기록 조회
					latestYn = 1;
					studyGet = userService.selectStudyGet( mainBranchId, memberId, no, latestYn);
					
						if(studyGet == null) { 
							
						} else {
							
								Integer goalTime = studyGet.getUnConvGtime();
								Integer learningTime = studyGet.getUnConvlTime();
								DecimalFormat form = new DecimalFormat("###.##");
								double goalRate = (double) ((double) learningTime/ (double) goalTime)*100;
								String convGoalRate = form.format(goalRate);
								double studyRate = Double.parseDouble(convGoalRate);
								
								Integer remains = studyGet.getDiffHours();
								String remainTimes = "";
							
							if(remains <= 0 ) {
								studyRate = 100;
								convGoalRate = "100";
								remainTimes = "00:00:00";
							} else {
								remainTimes = studyGet.getConvDiff();
							}
								result_data.setTimeRate(convGoalRate);
								result_data.setRemainTime(remainTimes);
								result_data.setStudyStatus(studyGet.getStudyStatus());
						}
				
					// 아~~무 학습계획이 없을 때
					result_code = ApiResultConsts.resultCode.SUCCESS;
					
					/*result_data.setNo(no);
					result_data.setName(userInfo.get(0).getName());
					result_data.setRole(role);*/
					result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
	
					header = modelMapper.map("", ApiResult.header.class);
					header.setResult_code(result_code.getCode());
					header.setResult_message(result_code.getMessage());
					
			} else {
				
					Integer goalTime = studyGet.getUnConvGtime();
					Integer learningTime = studyGet.getUnConvlTime();
					DecimalFormat form = new DecimalFormat("###.##");
					double goalRate = (double) ((double) learningTime/ (double) goalTime)*100;
					String convGoalRate = form.format(goalRate);
					double studyRate = Double.parseDouble(convGoalRate);
					
					Integer remains = studyGet.getDiffHours();
					String remainTimes = "";
				
				if(remains <= 0 ) {
					studyRate = 100;
					convGoalRate = "100";
					remainTimes = "00:00:00";
				} else {
					remainTimes = studyGet.getConvDiff();
				}
					
					result_data.setStartDt(studyGet.getStartDt());
					result_data.setEndDt(studyGet.getEndDt());
					result_data.setGoalTime(studyGet.getGoalTime());
					result_data.setStudyStatus(studyGet.getStudyStatus());
					//result_data.setLearningTime(String.valueOf(learningTime));
					result_data.setLearningTime(studyGet.getLearningTime());
					result_data.setTimeRate(convGoalRate);
					result_data.setRemainTime(remainTimes);
					result_data.setStudyStatus(studyGet.getStudyStatus());
		
			}	
		
			
			List<AppClientStudy.StudyList> studyList = userService.selectStudyPlanList(mainBranchId, memberId, no, role, paramPagingNum);
			
				if(studyList.size() == 0) {
					//학습계획 history가 없을 때
					result_data.setStudyPlanList(null);
				} else {
					result_data.setStudyPlanList(studyList);
				}
			
		result_data.setNo(no);
		result_data.setName(userInfo.get(0).getName());
		result_data.setRole(role);
		result_data.setBranchNm(paramBranchNm);
		
		result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;

		header = modelMapper.map("", ApiResult.header.class);
		header.setResult_code(result_code.getCode());
		header.setResult_message(result_code.getMessage());

		// result_data.setSuccesYn(false);
		apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

	}

	// 입퇴실현황 목록
	// http://localhost:8888/app/client/api/v1/reportingEnter?no=1&role=10&pagingNum=1
	@RequestMapping(value = "/reportingEnter", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<?> getReportingEnter(
			@RequestHeader(value = "no", required = false) String no,
			@RequestHeader(value = "role", required = false) Integer role,
			@RequestParam(value = "pagingNum", defaultValue = "1", required = false) Integer pagingNum) {
		// 결과
		ModelMapper modelMapper = new ModelMapper();

		statusList.Response result_data = null;
		ApiResultConsts.resultCode result_code = null;
		ApiResult.header header = modelMapper.map("", ApiResult.header.class);
		ApiResult apiResult = null;
		
		StatusListDeskInfo statusListDeskInfo = new StatusListDeskInfo();
		List<StatusListEntry> totalLearningTm = new ArrayList<>();
		List<StatusListEntry> statusListEntryList = new ArrayList<>();
		
		
		if (no == null || no.equals("")) {
			
			result_code = ApiResultConsts.resultCode.SUCCESS;
			
			result_data = modelMapper.map("", statusList.Response.class);
			
			result_data.setTotalCount(0);
			result_data.setPagingNum(0);
			result_data.setTotalLearningTm(0);
			result_data.setStatusListDeskInfo(null);
			result_data.setStatusListEntryList(null);
			
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		if (role == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		
		// role = 20 (학부모)로 접근 시
		if(role == 10 ) {
			
		} else if(role == 20) {
			
			List<AppClientMember> childNo = userService.getMainChildNo(no, role);
			
			if(childNo.size() > 0) {
			
					if(childNo.get(0).getMainChildNo() == null || childNo.get(0).getMainChildNo().equals("")) {
						// 주자녀가 없을 경우
						result_code = ApiResultConsts.resultCode.SUCCESS;
						
						result_data = modelMapper.map("", statusList.Response.class);

						result_data.setTotalLearningTm(0);
						result_data.setPagingNum(0);
						result_data.setTotalCount(0);
						result_data.setStatusListDeskInfo(null);
						result_data.setStatusListEntryList(null);
						
						header.setResult_code(result_code.getCode());
						header.setResult_message(result_code.getMessage());
						apiResult = new ApiResult(result_data, header);
	
						return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
						
					} else {
						no = childNo.get(0).getMainChildNo();
						role = 10;
					}
			}		
					
			
		} else {
			/*// role 오류
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());*/
			
			result_code = ApiResultConsts.resultCode.SUCCESS;
			
			result_data = modelMapper.map("", statusList.Response.class);

			result_data.setTotalLearningTm(0);
			result_data.setPagingNum(0);
			result_data.setTotalCount(0);
			result_data.setStatusListDeskInfo(null);
			result_data.setStatusListEntryList(null);
			
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
			
		}
		
		Integer paramPagingNum;
		if (pagingNum == null) {
			paramPagingNum = 0;
		} else {
			paramPagingNum = (pagingNum - 1) * 10; // 2일 경우 10, 3일 경우 20
		}

		// branchId, memberId 매칭
		String paramBranchId = "";
		String paramMemberId = "";
		String paramBranchNm = "";

		List<AppClientMember> userInfo = userService.selectBranchId(no, role);
		
		if (userInfo.get(0).getMainBranchId() == null) {
			// 주센터가 없을 경우
			result_code = ApiResultConsts.resultCode.SUCCESS;
			
			result_data = modelMapper.map("", statusList.Response.class);
			
			result_data.setTotalLearningTm(0);
			result_data.setPagingNum(0);
			result_data.setTotalCount(0);
			result_data.setStatusListDeskInfo(null);
			result_data.setStatusListEntryList(null);
			
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
			
		} else {
			
			List<AppClientMember> branchMatch = userService.selectBranchMatch(no, userInfo.get(0).getMainBranchId());
			
				if(branchMatch.size() > 0) {
					
					 paramBranchId = branchMatch.get(0).getBranchId();
					 paramMemberId = branchMatch.get(0).getMemberId();
					 paramBranchNm = branchMatch.get(0).getBranchNm();
					 
				} else {
					// 주센터가 없을 경우
					result_code = ApiResultConsts.resultCode.SUCCESS;
					
					result_data = modelMapper.map("", statusList.Response.class);
					
					result_data.setTotalLearningTm(0);
					result_data.setPagingNum(0);
					result_data.setTotalCount(0);
					result_data.setStatusListDeskInfo(null);
					result_data.setStatusListEntryList(null);
					
					header.setResult_code(result_code.getCode());
					header.setResult_message(result_code.getMessage());
					apiResult = new ApiResult(result_data, header);

					return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
				}
		}

		//테스트 데이터
		//paramBranchId="525b4464-2805-4af6-9103-9d30e2120e50";
		//paramMemberId="fb8faf1e-fd3a-4224-84a7-b6a2186be42f";
		//String mainBranchId = paramBranchId;
		//String memberId = paramMemberId;
		
		//paramBranchId="95060e0d-f234-4021-9cf0-7b78db725447";
		//paramMemberId="eafc58ec-a74e-4b3c-b4d8-44618a7d0b87";

		 statusListDeskInfo = branchMemberService.reportingAppStatusListDeskInfo(paramBranchId,
				paramMemberId);

		
		if(statusListDeskInfo == null ) {
			
			//회원정보는 있는데 입퇴실 기록이 없을 경우
			result_code = ApiResultConsts.resultCode.SUCCESS;
			
			result_data = modelMapper.map("", statusList.Response.class);
			
			result_data.setTotalLearningTm(0);
			result_data.setPagingNum(0);
			result_data.setTotalCount(0);
			result_data.setStatusListDeskInfo(null);
			result_data.setStatusListEntryList(null);
			
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
			
		} 
		
		/*if (statusListDeskInfo.getGender().equals("1")) {
			statusListDeskInfo.setGender("남");
		} else {
			statusListDeskInfo.setGender("여");
		}*/
		
		
		
		Object jobType = Constants.JobTypes.getMap().get(Integer.parseInt(statusListDeskInfo.getSchool()));
	        if (jobType == null) {
	        	statusListDeskInfo.setSchool(null);
	        } else {
	        	statusListDeskInfo.setSchool(jobType.toString());
	        }
		
		Object entryType = Constants.EntryType.getMap().get(statusListDeskInfo.getEntryType());
		if (entryType == null) {
			statusListDeskInfo.setEntryType("기타");
		} else {
			statusListDeskInfo.setEntryType(entryType.toString());
		}

		totalLearningTm = branchMemberService.selectStatusListEntryList(paramBranchId,
				paramMemberId, null);
		statusListEntryList = branchMemberService.MainSelectStatusListEntryList(paramBranchId,
				paramMemberId, paramPagingNum);

		for (StatusListEntry se : statusListEntryList) {

			Object entryListType = Constants.EntryType.getMap().get(se.getEntryType());
			if (entryListType == null) {
				se.setEntryType("기타");
			} else {
				se.setEntryDt(DateUtil.getCurrentDateStringAppAdminParse(se.getEntryDt()));
				se.setEntryType(entryListType.toString());
			}

		}

		TotalLearning totalLearning = new TotalLearning();
		int diff = 0;
		for (StatusListEntry sle : totalLearningTm) {
			//////// total 시간 계산
			if (sle.getEntryType().equals("1")) {
				// dateIn.
				totalLearning.setEntryDt(sle.getEntryDtOg());
				totalLearning.setEntryType("1");

			} else if (sle.getEntryType().equals("2")) {
				if (totalLearning.getEntryType().equals("2")) {

				} else if (totalLearning.getEntryType().equals("1")) {
					Calendar startCal = Calendar.getInstance();
					Calendar endCal = Calendar.getInstance();

					startCal.setTime(totalLearning.getEntryDt());
					endCal.setTime(sle.getEntryDtOg());

					long diffMillis = endCal.getTimeInMillis() - startCal.getTimeInMillis();
					diff += (int) (diffMillis / (1000 * 60));

					totalLearning = new TotalLearning();
					totalLearning.setEntryDt(sle.getEntryDtOg());
					totalLearning.setEntryType("2");
				}
			}
		}

		result_data = modelMapper.map("", statusList.Response.class);

		// result_data.setTotalCount(1);
		result_data.setTotalLearningTm(diff);
		result_data.setPagingNum(pagingNum);
		result_data.setTotalCount(totalLearningTm.size());
		result_data.setStatusListDeskInfo(statusListDeskInfo);
		result_data.setStatusListEntryList(statusListEntryList);

		result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;

		header = modelMapper.map("", ApiResult.header.class);
		header.setResult_code(result_code.getCode());
		header.setResult_message(result_code.getMessage());

		apiResult = new ApiResult(result_data, header);

		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

	}

	// /main
	// 메인페이지
	@RequestMapping(value = "/main", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<?> getMain(
			@RequestHeader(value = "no", required = true) String no,
			@RequestHeader(value = "role", required = true) Integer role ) {
		
		// 결과
		ModelMapper modelMapper = new ModelMapper();

		AppClientMain result_data = null;
		ApiResultConsts.resultCode result_code = null;
		ApiResult.header header = modelMapper.map("", ApiResult.header.class);
		ApiResult apiResult = null;

		if (no == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		if (role == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}

		List<AppClientMember> parentsList = new ArrayList<>();
		result_data = new AppClientMain();
		
		
		if(role == 10 ) {
			
		} else if(role == 20) {
			
			parentsList = userService.getMainChildNo(no, role);
			
			if (parentsList.size() > 0) {
				result_data.setParentsName(parentsList.get(0).getName());
				
				if (parentsList.get(0).getMainChildNo() == null || parentsList.get(0).getMainChildNo().equals("")) {
					no = null;
				} else {
					no = parentsList.get(0).getMainChildNo();
					role = 10;
				}
			}
					
			
		} else {
			// role 오류
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}

		List<AppClientMain.NoticeList> centerNotices = null;
		AppClientMain.CenterInfo centerInfo = new AppClientMain.CenterInfo();
		AppClientMain.MainRank mainRank = new AppClientMain.MainRank();
		
		// branchId, memberId 매칭
		String paramBranchId = "";
		String paramMemberId = "";
		String paramBranchNm = "";
		
		List<AppClientMember> userInfo = userService.selectBranchId(no, 10);
		
		if (userInfo.size() == 0) {
			if (role == 10) {
				// 사용자 정보가 없을 때
				result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
	
				header.setResult_code(result_code.getCode());
				header.setResult_message(result_code.getMessage());
				apiResult = new ApiResult(result_data, header);
	
				return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
			}		

		} else {
			
				result_data.setName(userInfo.get(0).getName());
				result_data.setImgUrl(userInfo.get(0).getImgUrl());
				
			if (userInfo.get(0).getMainBranchId() == null) {
				//null 값 insert
				//result_data.setEntryInfo(null);
				//result_data.setMainRank(null);
				//result_data.
				
				
			} else {
				List<AppClientMember> branchMatch = userService.selectBranchMatch(no, userInfo.get(0).getMainBranchId());
						result_data.setBranchName(branchMatch.get(0).getBranchNm());
				if(branchMatch.size() > 0) {
					
					 paramBranchId = branchMatch.get(0).getBranchId();
					 paramMemberId = branchMatch.get(0).getMemberId();
					 paramBranchNm = branchMatch.get(0).getBranchNm();
					 
					// 메인 통계데이터
						AppClientReport reportEntInfo = userService.selectReportInfo(paramBranchId, paramMemberId);
						
						if(reportEntInfo == null ){
							//조회된 통계가 없을 때
//							result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;
//
//							header.setResult_code(result_code.getCode());
//							header.setResult_message(result_code.getMessage());
//							apiResult = new ApiResult(result_data, header);
//
//							return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
														
							result_data.setAttendanceRate("0");
							result_data.setCenterInfo(null);
					
							
							
						} else {
							
							String deskStartDt = reportEntInfo.getDeskStartDt();
							String deskEndDt = reportEntInfo.getDeskEndDt();
							Integer regPeriod = reportEntInfo.getRegPeriod();
							
							result_data.setName(reportEntInfo.getName());
							
							// 출석률 뽑기 
							String attendRate = userService.selectAttendanceRate(paramBranchId, paramMemberId, regPeriod, deskStartDt, deskEndDt);
							
							if(attendRate == null) {
								
								// 출석률 조회가 안 될 때
								/*result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

								header.setResult_code(result_code.getCode());
								header.setResult_message(result_code.getMessage());
								apiResult = new ApiResult(result_data, header);

								return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());*/
								result_data.setAttendanceRate(null);
							} else {
								result_data.setAttendanceRate(attendRate);
							}
								
								AppClientMain appClientMain = new AppClientMain();								
								

										//appClientMain.setNo(no);
										//appClientMain.setRole(role);
										//appClientMain.setName(reportEntInfo.getName());
										//appClientMain.setBranchName(reportEntInfo.getBranchNm());										
										//appClientMain.setAttendanceRate(attendRate);
										
										//result_data.setNo(no);
										//result_data.setRole(role);
										//result_data.setName(reportEntInfo.getName());
										result_data.setBranchName(reportEntInfo.getBranchNm());										
										
								
										
								// 센터정보 받아오는 객체 생성
								centerInfo.setRoomName(reportEntInfo.getRoomName());
								centerInfo.setSeatName(reportEntInfo.getSeatNo());
								centerInfo.setBranchName(reportEntInfo.getBranchNm());
								
								//appClientMain.setCenterInfo(centerInfo); 
								result_data.setCenterInfo(centerInfo);
								
								// 입퇴실정보 (최근 1건)
								String entryInfo = userService.getEntryInfo(paramBranchId, paramMemberId);
									
									if(StringUtils.isEmpty(entryInfo) ) {
										// 입퇴실 정보가 없을 때
										//appClientMain.setEntryInfo(null);
										result_data.setEntryInfo(null);
									} else {
										//appClientMain.setEntryInfo(entryInfo);
										result_data.setEntryInfo(entryInfo);
									}
																
									
								//랭킹 뽑기	
								String cntrRanking = userService.selectCenterRank(paramBranchId, paramMemberId);
								
									if((StringUtils.isEmpty(cntrRanking))) {
										mainRank.setMyRank(null);
									} else {
										mainRank.setMyRank(cntrRanking);
									}
									
								String cntrTotal = userService.selectCntrTotalMember(paramBranchId, paramMemberId);	
								
									if((StringUtils.isEmpty(cntrTotal))) {
										mainRank.setTotalMember(null);
									} else {
										mainRank.setTotalMember(cntrTotal);
									}
								
										//appClientMain.setMainRank(mainRank);
										result_data.setMainRank(mainRank);
								
						}
					 
				} else {
					// app,codi 매칭 branchId, memberId 없을 때
					result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

					header.setResult_code(result_code.getCode());
					header.setResult_message(result_code.getMessage());
					apiResult = new ApiResult(result_data, header);

					return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
					
				}
				
			}
		}

		
		//공지사항
		centerNotices = userService.selectNotice(paramBranchId);
		
			if(centerNotices == null) {
				
				result_data.setNoticeList(null);
				
			} else {
				result_data.setNoticeList(centerNotices);
			}
		
			if(role == 10) {
				Object jobType = Constants.JobTypes.getMap().get(userInfo.get(0).getJob());
		        if (jobType == null) {
		        	result_data.setJob(null);
		        
		        } else {
		        	result_data.setJob(jobType.toString());
		        }
			} else {
				result_data.setJob(null);
			}
					
			
			result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
			header = modelMapper.map("", ApiResult.header.class);
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);
	
			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

	}

	
	@RequestMapping(value = "/reporting", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<?> getReportMain(
			@RequestHeader(value = "no", required = true) String no,
			@RequestHeader(value = "role", required = true) Integer role ) {
		
		// 결과
		ModelMapper modelMapper = new ModelMapper();
		
		AppClientReport result_data = null;
		ApiResultConsts.resultCode result_code = null;
		ApiResult.header header = modelMapper.map("", ApiResult.header.class);
		ApiResult apiResult = null;

		if (no == null || no.equals("")) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		if (role == null) {
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		
		AppClientReport reportEntInfo = new AppClientReport();
		
		if(role == 10 ) {
			
		} else if(role == 20) {
			
			List<AppClientMember> childNo = userService.getMainChildNo(no, role);
			
			if (childNo.size() > 0) {
					if(childNo.get(0).getMainChildNo() == null || childNo.get(0).getMainChildNo().equals("")) {
						// 주자녀가 없을 경우
						result_code = ApiResultConsts.resultCode.SUCCESS;
						
						reportEntInfo.setCntrRank(null);
						reportEntInfo.setTotalRank(null);
						reportEntInfo.setEntryInfo(null);
						reportEntInfo.setReservationDt(null);
						reportEntInfo.setAttendanceRate(null);
						reportEntInfo.setImgUrl(null);
						
						header.setResult_code(result_code.getCode());
						header.setResult_message(result_code.getMessage());
						apiResult = new ApiResult(reportEntInfo, header);
	
						return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
						
					} else {
						no = childNo.get(0).getMainChildNo();
						role = 10;
					}
			}
			
		} else {
			// role 오류
			result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(result_data, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		}
		
		// branchId, memberId 매칭
		String paramBranchId = "";
		String paramMemberId = "";
		String paramBranchNm = "";
		String branchNm = "";
		
		List<AppClientMember> userInfo = userService.selectBranchId(no, role);
		
		if (userInfo.size() == 0) {
			
			String usrName = userService.selectGetName(no, role);
			// 사용자 정보가 없을 때
			result_code = ApiResultConsts.resultCode.SUCCESS;

			reportEntInfo.setName(usrName);
			reportEntInfo.setNo(no);
			reportEntInfo.setRole(role);
			reportEntInfo.setBranchNm(branchNm);
			reportEntInfo.setImgUrl(null);
			reportEntInfo.setCntrRank(null);
			reportEntInfo.setTotalRank(null);
			reportEntInfo.setEntryInfo(null);
			reportEntInfo.setReservationDt(null);
			reportEntInfo.setAttendanceRate(null);
			
			
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
			apiResult = new ApiResult(reportEntInfo, header);

			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
		

		} else {
			
			List<AppClientMember> branchMatch = userService.selectBranchMatch(no, userInfo.get(0).getMainBranchId());
				/*	if(branchMatch.size() == 0 ) {
						branchNm = branchMatch.get(0).getBranchNm();
					} else {
						branchNm = null;
					}*/
						
				if(branchMatch.size() > 0) {
					
					 paramBranchId = branchMatch.get(0).getBranchId();
					 paramMemberId = branchMatch.get(0).getMemberId();
					 paramBranchNm = branchMatch.get(0).getBranchNm();
					 
				} else {
					// app,codi 매칭 branchId, memberId 없을 때
					result_code = ApiResultConsts.resultCode.SUCCESS;
					
					String usrName = userService.selectGetName(no, role);
					
					if(usrName == null) {
						usrName = "";
					} else {
						
					}
					
					reportEntInfo.setName(usrName);
					reportEntInfo.setNo(no);
					reportEntInfo.setRole(role);
					reportEntInfo.setBranchNm(branchNm);
					reportEntInfo.setCntrRank(null);
					reportEntInfo.setTotalRank(null);
					reportEntInfo.setEntryInfo(null);
					reportEntInfo.setReservationDt(null);
					reportEntInfo.setAttendanceRate(null);
					reportEntInfo.setImgUrl(null);
					
					header.setResult_code(result_code.getCode());
					header.setResult_message(result_code.getMessage());
					//apiResult = new ApiResult(result_data, header);
					apiResult = new ApiResult(reportEntInfo, header);

					return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
				}
		}
		
			reportEntInfo = userService.selectReportInfo(paramBranchId, paramMemberId);
			
			if(reportEntInfo == null){
				//조회된 통계가 없을 때
				
				reportEntInfo = new AppClientReport();
				
				String usrName = userService.selectGetName(no, role);
				
					if(usrName == null) {
						usrName = "";
					} else {
						
					}
					
				reportEntInfo.setName(usrName);
				reportEntInfo.setNo(no);
				reportEntInfo.setRole(role);
				reportEntInfo.setBranchNm(branchNm);
				reportEntInfo.setImgUrl(userInfo.get(0).getImgUrl());
				
			} else {
				
					String reservationDt = reportEntInfo.getReservationDt();
					String deskStartDt = reportEntInfo.getDeskStartDt();
					String deskEndDt = reportEntInfo.getDeskEndDt();
					Integer regPeriod = reportEntInfo.getRegPeriod();
					
					String reportingEnter = userService.selectAttendanceRate(paramBranchId, paramMemberId, regPeriod, deskStartDt, deskEndDt);
				
				if(reportingEnter == null) {
					
					// codi 데이터 조회결과가 없을 때
					String usrName = userService.selectGetName(no, role);
					// 사용자 정보가 없을 때
					result_code = ApiResultConsts.resultCode.SUCCESS;

					reportEntInfo.setName(usrName);
					reportEntInfo.setNo(no);
					reportEntInfo.setRole(role);
					reportEntInfo.setBranchNm(branchNm);
					reportEntInfo.setCntrRank(null);
					reportEntInfo.setTotalRank(null);
					reportEntInfo.setEntryInfo(null);
					reportEntInfo.setReservationDt(null);
					reportEntInfo.setAttendanceRate(null);
					reportEntInfo.setImgUrl(null);
					
					header.setResult_code(result_code.getCode());
					header.setResult_message(result_code.getMessage());
					apiResult = new ApiResult(reportEntInfo, header);

					return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
					
				} else {
					
					reportEntInfo.setReservationDt(reservationDt);
					reportEntInfo.setAttendanceRate(reportingEnter);
					reportEntInfo.setNo(no);
				}
				
				//센터랭킹
				String cntrRanking = userService.selectCenterRank(paramBranchId, paramMemberId);
				
					if((StringUtils.isEmpty(cntrRanking))) {
						reportEntInfo.setCntrRank(null);
					} else {
						reportEntInfo.setCntrRank(cntrRanking);
					}
				
				String countryRanking = userService.selectCountryRank(paramBranchId, paramMemberId);
					
					if((StringUtils.isEmpty(countryRanking))) {
						reportEntInfo.setTotalRank(null);
					} else {
						reportEntInfo.setTotalRank(countryRanking);
					}
				
				// 입퇴실정보 (최근 1건)
				String entryInfo = userService.getEntryInfo(paramBranchId, paramMemberId);
					
					if(StringUtils.isEmpty(entryInfo) ) {
						// 입퇴실 정보가 없을 때
						reportEntInfo.setEntryInfo(null);
					} else {
						reportEntInfo.setEntryInfo(entryInfo);
					}
				
			}
			
			reportEntInfo.setRole(10);
			
			result_code = ApiResultConsts.resultCode.SUCCESS;
			
			header = modelMapper.map("", ApiResult.header.class);
			header.setResult_code(result_code.getCode());
			header.setResult_message(result_code.getMessage());
	
			apiResult = new ApiResult(reportEntInfo, header);
	
			return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	}
	
	 @RequestMapping(value = "/noticeList", method = { RequestMethod.GET, RequestMethod.POST })
	    public @ResponseBody ResponseEntity<?> getNoticeListGET(
	    							@RequestHeader(value="no", required=false) String no, 
	    							@RequestHeader(value="role", required=false) Integer role) {        
	    	
	    	// 결과
	        ModelMapper modelMapper = new ModelMapper();
	                       
	        notice.NoticeDetailResponse result_data = null;
	    	ApiResultConsts.resultCode result_code = null;
	    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
	    	ApiResult apiResult = null;
	    	
	    	if (no == null) {
				result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
		        
		        header.setResult_code(result_code.getCode());
		        header.setResult_message(result_code.getMessage());
		        apiResult = new ApiResult(result_data, header);

	    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	    	}
	    	if (role == null) {
				result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
		        
		        header.setResult_code(result_code.getCode());
		        header.setResult_message(result_code.getMessage());
		        apiResult = new ApiResult(result_data, header);

	    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	    	}
	    	
	    	// branchId, memberId 매칭
			String paramBranchId = "";
			String paramMemberId = "";

			result_data = modelMapper.map("", notice.NoticeDetailResponse.class);
			
			if(role == 10) {
				
				List<AppClientMember> userInfo = userService.selectBranchId(no, role);
			
			if (userInfo.size() == 0) {
				
				
				// 사용자 정보가 없을 때
				List<NoticeDetail> noticeList = userService.getNoticeList2(paramBranchId);
		    	if (noticeList.size() > 0) {
		        	for (NoticeDetail n : noticeList) {
		        		n.setNoticeDt((DateUtil.getCurrentDateStringAppAdminParse(n.getNoticeDt()))); 
		        	}
		        }
				
		    	result_data.setNoticeList(noticeList);

			} else {
				
				List<AppClientMember> branchMatch = userService.selectBranchMatch(no, userInfo.get(0).getMainBranchId());
				
					if(branchMatch.size() > 0) {
						
						 paramBranchId = branchMatch.get(0).getBranchId();
						 paramMemberId = branchMatch.get(0).getMemberId();
						 
					} else {
						
						// app,codi 매칭 branchId, memberId 없을 때
						List<NoticeDetail> noticeList = userService.getNoticeList2(paramBranchId);
				    	if (noticeList.size() > 0) {
				        	for (NoticeDetail n : noticeList) {
				        		n.setNoticeDt((DateUtil.getCurrentDateStringAppAdminParse(n.getNoticeDt()))); 
				        	}
				        }
						
				    	result_data.setNoticeList(noticeList);
				    	
					}
			}
			
	 		} else if(role == 20) {
				
			}
			
			//테스트 데이터
			//paramBranchId="525b4464-2805-4af6-9103-9d30e2120e50";
			//paramMemberId="fb8faf1e-fd3a-4224-84a7-b6a2186be42f";
			//String mainBranchId = paramBranchId;
			//String memberId = paramMemberId;
			
			//paramBranchId="033f8817-71a0-4feb-bf7b-f9f184da7317";
			//paramMemberId="7474d422-6d9a-4afb-8722-61a7e10bb927";
	    	
	        //List<NoticeDetail> noticeList = new ArrayList();
	    	List<NoticeDetail> noticeList = userService.getNoticeList2(paramBranchId);
	    	if (noticeList.size() > 0) {
	        	for (NoticeDetail n : noticeList) {
	        		n.setNoticeDt((DateUtil.getCurrentDateStringAppAdminParse(n.getNoticeDt()))); 
	        	}
	        }
	        
			
			result_data.setNoticeList(noticeList);
	        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
	        
	        header = modelMapper.map("", ApiResult.header.class);
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());
	        
	        apiResult = new ApiResult(result_data, header);
	        
	        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

	    }   
	    
	    @RequestMapping(value = "/noticeDetail", method = { RequestMethod.GET, RequestMethod.POST })
	    public @ResponseBody ResponseEntity<?> getNoticeDetailGET(
	    					@RequestHeader(value="no", required=false) String no, 
	    					@RequestHeader(value="role", required=false) Integer role,
	    					@RequestParam(value="noticeId", required=false) String paramNoticeId) {
	    	//paramUserId = userIdTest;
	    	//paramBranchId = branchIdTest;
	    	
	    	// 결과
	        ModelMapper modelMapper = new ModelMapper();

	        notice.Response result_data = null;
	    	ApiResultConsts.resultCode result_code = null;
	    	ApiResult.header header = modelMapper.map("", ApiResult.header.class);
	    	ApiResult apiResult = null;
	    	
	    	if (no == null) {
				result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
		        
		        header.setResult_code(result_code.getCode());
		        header.setResult_message(result_code.getMessage());
		        apiResult = new ApiResult(result_data, header);

	    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	    	}
	    	if (role == null) {
				result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_HEADER;
		        
		        header.setResult_code(result_code.getCode());
		        header.setResult_message(result_code.getMessage());
		        apiResult = new ApiResult(result_data, header);

	    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	    	}
	    	if (paramNoticeId== null) {
				result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO_NOTICEID;
		        
		        header.setResult_code(result_code.getCode());
		        header.setResult_message(result_code.getMessage());
		        apiResult = new ApiResult(result_data, header);

	    		return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	    	}    	
	    	
	    	
	    	// branchId, memberId 매칭
			String paramBranchId = "";
			String paramMemberId = "";
			result_data = modelMapper.map("", notice.Response.class); 
			
			//테스트 데이터
			//paramBranchId="525b4464-2805-4af6-9103-9d30e2120e50";
			//paramMemberId="fb8faf1e-fd3a-4224-84a7-b6a2186be42f";
			//String mainBranchId = paramBranchId;
			//String memberId = paramMemberId;
			//paramBranchId="033f8817-71a0-4feb-bf7b-f9f184da7317";
			//paramMemberId="7474d422-6d9a-4afb-8722-61a7e10bb927";
			
	    	 List<notice.Response> noticeDetailList = userService.getNoticeDetail(paramBranchId, paramNoticeId);
			
	    	 if(noticeDetailList.size() == 0) {
	    		 // error param no
				result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

				header.setResult_code(result_code.getCode());
				header.setResult_message(result_code.getMessage());
				apiResult = new ApiResult(result_data, header);

				return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	    		 
	    	 } else {
	    		 
	    		 if(noticeDetailList.get(0).getBranchId().equals("ALL")) {
	    			 
	    			 result_data.setTitle(noticeDetailList.get(0).getTitle());
	    	    	 result_data.setNoticeDt(DateUtil.getCurrentDateStringAppAdminParse(noticeDetailList.get(0).getNoticeDt()));
	    	    	 result_data.setContent(noticeDetailList.get(0).getContent());
	    			 
	    		 } else {
	    			 
	    			 List<AppClientMember> userInfo = userService.selectBranchId(no, role);
	    				
	    				if (userInfo.size() == 0) {
	    					// 사용자 정보가 없을 때
	    					result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

	    					header.setResult_code(result_code.getCode());
	    					header.setResult_message(result_code.getMessage());
	    					apiResult = new ApiResult(result_data, header);

	    					return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

	    				} else {
	    					
	    					List<AppClientMember> branchMatch = userService.selectBranchMatch(no, userInfo.get(0).getMainBranchId());
	    					
	    						if(branchMatch.size() > 0) {
	    							
	    							 paramBranchId = branchMatch.get(0).getBranchId();
	    							 paramMemberId = branchMatch.get(0).getMemberId();
	    							 
	    							 noticeDetailList = userService.getNoticeDetail(paramBranchId, paramNoticeId);
	    							 
	    							 if (noticeDetailList.size() == 0) {
	    								 result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

	 	    							header.setResult_code(result_code.getCode());
	 	    							header.setResult_message(result_code.getMessage());
	 	    							apiResult = new ApiResult(result_data, header);

	 	    							return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	 	    							
	    							 } else {
	    								 
	    								 result_data.setTitle(noticeDetailList.get(0).getTitle());
	    				    	    	 result_data.setNoticeDt(DateUtil.getCurrentDateStringAppAdminParse(noticeDetailList.get(0).getNoticeDt()));
	    				    	    	 result_data.setContent(noticeDetailList.get(0).getContent());
	    							 }
	    							
	    							 
	    						} else {
	    							// app,codi 매칭 branchId, memberId 없을 때
	    							result_code = ApiResultConsts.resultCode.ERROR_PARAM_NO;

	    							header.setResult_code(result_code.getCode());
	    							header.setResult_message(result_code.getMessage());
	    							apiResult = new ApiResult(result_data, header);

	    							return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());
	    						}
	    				}

	    		 }
	    		 
	    	 }
	    	
	    	//result_data = modelMapper.map("", notice.Response.class);
			
	        //result_data.setNoticeDt("2017.06.19");
	        //result_data.setTitle("7월 25일 씨엔티 스터디 카페 창업 설명회를 개최합니다.");
	        //result_data.setContent("7월 25일 씨엔티 스터디 카페 창업 설명회를 개최합니다. <br> 많은 참여 바랍니다.");
	        
	        result_code = (result_data != null) ? ApiResultConsts.resultCode.SUCCESS : ApiResultConsts.resultCode.ERROR;
	        
	        header = modelMapper.map("", ApiResult.header.class);
	        header.setResult_code(result_code.getCode());
	        header.setResult_message(result_code.getMessage());

	        apiResult = new ApiResult(result_data, header);
	        
	        return APIController.apiResult(apiResult, result_code.getCode(), result_code.getMessage());

	    }
	    
	    
}