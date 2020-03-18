package kr.co.cntt.scc.app.admin.common;

import org.springframework.http.HttpStatus;

import lombok.Data;

public class ApiResultConsts {

public enum resultCode {
		
		// 200 성공
		//SUCCESS("s0000", "성공", HttpStatus.OK),
		SUCCESS("0000", "성공", HttpStatus.OK),
		
		
		//App admin
		//파라미터 없음
		ERROR_PARAM_NO_HEADER("0001", "사용자의 권한이 없습니다.", HttpStatus.BAD_REQUEST),
		//ERROR_PARAM_NO_BRANCHID("0001", "올바르지 않은 파라미터('branchId') 입니다.", HttpStatus.BAD_REQUEST),
		ERROR_PARAM_NO("0002", "올바르지 않은 파라미터가 전송되었습니다.", HttpStatus.BAD_REQUEST),
		ERROR_PARAM_NO_ID("0002", "올바르지 않은 파라미터('userId')가 전송되었습니다.", HttpStatus.BAD_REQUEST),
		ERROR_PARAM_NO_PASSWORD("0002", "올바르지 않은 파라미터('userPw')가 전송되었습니다.", HttpStatus.BAD_REQUEST),
		ERROR_PARAM_NO_AUTO("0002", "올바르지 않은 파라미터('auto')가 전송되었습니다.", HttpStatus.BAD_REQUEST),
		ERROR_PARAM_NO_UUID("0002", "올바르지 않은 파라미터('uuid')가 전송되었습니다.", HttpStatus.BAD_REQUEST),
		ERROR_PARAM_NO_OS("0002", "올바르지 않은 파라미터('os')가 전송되었습니다.", HttpStatus.BAD_REQUEST),
		ERROR_PARAM_NO_VERSION("0002", "올바르지 않은 파라미터('version')가 전송되었습니다.", HttpStatus.BAD_REQUEST),
		ERROR_PARAM_NO_DEVICE("0002", "올바르지 않은 파라미터('device')가 전송되었습니다.", HttpStatus.BAD_REQUEST),
		ERROR_PARAM_NO_PUSHID("0002", "올바르지 않은 파라미터('pushId')가 전송되었습니다.", HttpStatus.BAD_REQUEST),
		ERROR_PARAM_NO_SEARCHTYPE("0002", "올바르지 않은 파라미터('searchType')가 전송되었습니다.", HttpStatus.BAD_REQUEST),
		ERROR_PARAM_NO_MEMBERID("0002", "올바르지 않은 파라미터('memberId')가 전송되었습니다.", HttpStatus.BAD_REQUEST),
		ERROR_PARAM_NO_STARTDT("0002", "올바르지 않은 파라미터('startDt')가 전송되었습니다.", HttpStatus.BAD_REQUEST),
		ERROR_PARAM_NO_ENDDT("0002", "올바르지 않은 파라미터('endDt')가 전송되었습니다.", HttpStatus.BAD_REQUEST),
		ERROR_PARAM_NO_NOTICEID("0002", "올바르지 않은 파라미터('noticeId')가 전송되었습니다.", HttpStatus.BAD_REQUEST),
		
		ERROR_PARAM_NO_MAINBRANCH("0002", "자녀에게서 선택된 센터가 존재하지 않습니다. ", HttpStatus.BAD_REQUEST),
		ERROR_PARAM_NO_MAINBRANCHID("0002", "센터 선택 후 이용 가능합니다. ", HttpStatus.BAD_REQUEST),
		ERROR_PARAM_NO_PATTERN_PW("0002", "패스워드는 8~20자 영문 대 소문자, 숫자, 특수문자를 사용하세요", HttpStatus.BAD_REQUEST),
		ERROR_PARAM_NO_PATTERN_ID("0002", "아이디는 6~20자 영문 대 소문자, 숫자를 사용하세요", HttpStatus.BAD_REQUEST),
		
		ERROR_LOGIN_NOT_MATCH("0003", "로그인 정보가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),		
		ERROR_PARENTS_SERVICE_NOT_MATCH("0003", "학부모 또는 학생 정보가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),		
		ERROR_PARENTS_SERVICE_DUPL("0003", "이미 학부모 안심서비스에 가입하셨습니다.", HttpStatus.BAD_REQUEST),
		
		//내용이 없음
		ERROR_NO_CONTENT("0004", "조회되는 데이터가 없습니다.", HttpStatus.BAD_REQUEST),
		
		//0005 서버에러
		ERROR_SERVER("0005", "서버에러", HttpStatus.BAD_REQUEST),
		
		//App_student
		ERROR_ID_DUPL("0002", "이미 등록된 정보가 존재합니다.", HttpStatus.BAD_REQUEST),
						
		
		//App_student
		ERROR_ID_DUPL2("0002", "이미 등록된 ID가 존재합니다.", HttpStatus.BAD_REQUEST),
		
		ERROR_SEAT_DUPL("0002", "무료체험은 센터별 1인 1회에 한해서만 가능합니다.", HttpStatus.BAD_REQUEST),
		
		
		ERROR_BRANCH_NOT_MATCH("e40001", "branchId가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
		//ERROR_NO_CONTENT("e40001", "조회되는 데이터가 없습니다.", HttpStatus.BAD_REQUEST),
		//-----------------------------------------------
		// 4xx 요청 관련 오류
		//-----------------------------------------------
		
		// 400 잘못된 요청
		ERROR("e40000", "에러", HttpStatus.BAD_REQUEST),
		ERROR_MISSING_TOKEN("e40001", "토큰값 누락", HttpStatus.BAD_REQUEST),
		ERROR_MISSING_PARAMETER_DEFAULT("e40002", "기본 파라미터 누락", HttpStatus.BAD_REQUEST),
		ERROR_MISSING_PARAMETER_MUST("e40003", "필수 파라미터 누락", HttpStatus.BAD_REQUEST),
		ERROR_API_NOT_USE("e40004", "사용이 중지된 API 호출", HttpStatus.BAD_REQUEST),
		ERROR_MAX_TOKEN_COUNT_OVER("e40005", "최대 토큰 보유 갯수 초과.", HttpStatus.BAD_REQUEST),
		ERROR_TOKEN_OVER_EXPIRE_DATE("e40006", "토큰 유효기간 초과.", HttpStatus.BAD_REQUEST),
		ERROR_NOT_VALIDATE_ORDER_AMT("e40007", "주문금액 불일치.", HttpStatus.BAD_REQUEST),		
		ERROR_NOT_UPDATE("e40008", "데이터가 변경되지 않았습니다.", HttpStatus.BAD_REQUEST),
		ERROR_NOT_INSERT("e40009", "데이터가 저장되지 않았습니다.", HttpStatus.BAD_REQUEST),		
		ERROR_NOT_REMOVE("e40010", "데이터가 삭제되지 않았습니다.", HttpStatus.BAD_REQUEST),
		ERROR_NO_DATA("e40011", "데이터가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
		ERROR_NOT_AVAILABLE_PARAMETER("e40012", "유효하지 않은 파라미터 값 입니다.", HttpStatus.BAD_REQUEST),
		
		// 401 권한없음 : 인증 관련 오류
		ERROR_PARAMETER_AUTH("e40101", "파라미터 유효성 인증 실패", HttpStatus.UNAUTHORIZED),
		ERROR_DECODE_PARAMETER("e40102", "파라미터 암호화 인증 실패", HttpStatus.UNAUTHORIZED),
		ERROR_NOT_VALIDATE_AUTHNUM("e40103", "유효하지 않은 인증 번호 입니다.", HttpStatus.UNAUTHORIZED),		
		ERROR_NOT_ACCEPT_ACCOUNT("e40104", "계정 인증 실패.", HttpStatus.UNAUTHORIZED),
		ERROR_TOKEN_NOT_ACCEPT_ROLE("e40105", "권한 인증 실패.", HttpStatus.UNAUTHORIZED),
		
		// 404 찾을 수 없음
		ERROR_NOT_FOUND("e40401", "잘못된 URL이거나 현재 서비스 준비중입니다.", HttpStatus.NOT_FOUND),
		
		// 405 지원하지 않음
		ERROR_METHOD_NOT_ALLOWED("e40501", "허용하지 않는 주소입니다.", HttpStatus.METHOD_NOT_ALLOWED),
		
		// 500 서버 오류
		ERROR_REVERSE_API_CALL("e99996", "시스템 에러(외부 요청)", HttpStatus.INTERNAL_SERVER_ERROR), // 외부 API 호출 실패로 인핸 시스템 에러
		ERROR_MISSING_PROCESS("e99997", "시스템 에러", HttpStatus.INTERNAL_SERVER_ERROR), // 비정상적인 흐름, 디버깅시 활용
		ERROR_ENCODE("e99997", "인코딩 에러", HttpStatus.INTERNAL_SERVER_ERROR),
		ERROR_DECODE("e99998", "디코딩 에러", HttpStatus.INTERNAL_SERVER_ERROR),
		ERROR_SYSTEM("e99999", "시스템 에러", HttpStatus.INTERNAL_SERVER_ERROR),
		
		
		// e쿠폰 관련 오류
		ERROR_ECOUPON_INVALID("ec1000", "유효하지 않은 쿠폰입니다.", HttpStatus.OK),
		ERROR_ECOUPON_USED("ec2000", "이미 사용된 쿠폰입니다.", HttpStatus.OK),
		ERROR_ECOUPON_NOT_SALES("ec3000", "카카오톡 주문하기에 등록되지 않은 제품입니다.", HttpStatus.OK),

		
		
		
		ERROR_NO_TEL("ac000011", "핸드폰 번호 없음", HttpStatus.BAD_REQUEST),
		ERROR_NO_NAME("ac000012", "본인 이름 없음", HttpStatus.BAD_REQUEST),

		FIND_ID_SUCESS("ac100001", "아이디 찾기 완료", HttpStatus.OK),
		FIND_ID_FAILURE("ac100011", "일치하는 ID 없음", HttpStatus.BAD_REQUEST),
	
		// 비밀번호 찾기 성공/실패
		ERROR_NO_ID("ac100021", "존재하지 않는 ID", HttpStatus.BAD_REQUEST),
		ERROR_NO_newPw("ac100022", "신규 비밀번호 없음", HttpStatus.BAD_REQUEST),
		FIND_PW_FAILURE("ac100023", "일치하는 정보 없음", HttpStatus.BAD_REQUEST),
		NETWORK_MALFUNCTION("ac100024", "네트워크 오류", HttpStatus.BAD_REQUEST),
		ROLE_ERROR("ac100025", "고객유형 값 오류", HttpStatus.BAD_REQUEST);
	
	
		private String code;
		private String message;
		private HttpStatus httpsCode;
		
		resultCode(String code, String message, HttpStatus httpsCode){
		
			this.code = code;
			this.message = message;
			this.httpsCode = httpsCode;

		}

		public String getCode() {
			return code;
		}

		public String getMessage() {
			return message;
		}
		public HttpStatus getHttpsCode() {
			return httpsCode;
		}
	}

}
