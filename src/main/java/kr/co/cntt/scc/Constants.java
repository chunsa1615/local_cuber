package kr.co.cntt.scc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.cntt.scc.Constants.JobTypes;

/**
 * Constants 고정값 
 * 
 * Created by jslivane on 2016. 5. 3..
 */
public class Constants {
	
	
	public static final String TEST_BRANCHID = "033f8817-71a0-4feb-bf7b-f9f184da7317";
	
	public static final String BRAND_FULL = "STUDY CODI";
	public static final String BRAND_SHORT = "STUDY CODI";
//	public static final String BRAND_FULL = "CNT Study Cafe/Center";
//	public static final String BRAND_SHORT = "CNT Study";

    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_USER = "user";
    public static final String ROLE_MANAGER = "manager";
    public static final String ROLE_ENTRY = "entry";

    public static final int USE = 1;
    public static final int NOT_USE = 0;

    public static final int VISIBLE = 1;
    public static final int INVISIBLE = 0;

    public static final int NORMAL = 10;
    public static final int CANCEL = 0;
    
    public static final String SEPERATOR = ",";
    public static final String SPACED_SEPERATOR = ", ";


    public static final String SMS_CLIENT_ID = "cntstudy";
    public static final String SMS_SERVER_KEY = "NTMzOC0xNDc5NDM1MTQxNTY2LTJjZTFmNjIwLTJlNTctNGM5YS1hMWY2LTIwMmU1NzFjOWE3OQ==";
    public static final String SMS_API_VERSION = "1";


//    public static final String SMS_SEND_SERVER_URL = "http://api.openapi.io/ppurio/" + SMS_API_VERSION + "/message/sms/" + SMS_CLIENT_ID;
//    public static final String SMS_SAVE_SEND_NUMBER_SERVER_URL = "http://api.openapi.io/ppurio/" + SMS_API_VERSION + "/sendnumber/save/" + SMS_CLIENT_ID;

    // 서버 주소 변경 (2016-10-30)
    public static final String SMS_SEND_SERVER_URL = "http://api.apistore.co.kr/ppurio/" + SMS_API_VERSION + "/message/sms/" + SMS_CLIENT_ID;
    public static final String SMS_SAVE_SEND_NUMBER_SERVER_URL = "http://api.apistore.co.kr/ppurio/" + SMS_API_VERSION + "/sendnumber/save/" + SMS_CLIENT_ID;

    public static final String APP_REALM = "APP_REALM";

    
    public static final String SCHOOL_GRADE = "schoolGrade";
    

    /**
     * 이력 유형
     *
     * enum : https://bluepoet.me/2012/07/18/%EB%B2%88%EC%97%AD%EC%9E%90%EB%B0%94-enum%EC%9D%98-10%EA%B0%80%EC%A7%80-%EC%98%88%EC%A0%9C/
     *
     */
    public enum HistoryType {
        USER_CREATE(10), USER_UPDATE(15), USER_LOGIN(16), USER_LOGOUT(17), USER_DELETE(19),
        BRANCH_CREATE(20), BRANCH_UPDATE(25), BRANCH_DELETE(29),
        MEMBER_CREATE(30), MEMBER_ENTRY_CREATE(31), MEMBER_UPDATE(35), MEMBER_DELETE(39),
        PAY_CREATE(40), PAY_UPDATE(45), PAY_DELETE(49),
        ROOM_CREATE(50), ROOM_UPDATE(55), ROOM_DELETE(59),
        DESK_CREATE(60), DESK_UPDATE(65), DESK_DELETE(69),
        BRANCH_MANAGER_CREATE(70), BRANCH_MANAGER_UPDATE(75), BRANCH_MANAGER_DELETE(79),
        RESERVATION_CREATE(80), RESERVATION_UPDATE(85), RESERVATION_DELETE(89),
        ORDER_CREATE(90), ORDER_UPDATE(95), ORDER_DELETE(99),
        RENTAL_CREATE(100), RENTAL_UPDATE(105), RENTAL_DELETE(109), 
        COMMONCODE_CREATE(100);

        private int value;

        HistoryType(int value) {
            this.value = value;

        }

        public int getValue() {
            return value;
        }

    }
    
    
    /**
     * 지점 유형
     *
     */
    public enum BranchType {
    	CENTER(10, "센터"), CAFE(20, "카페");
    	
    	private int value;
    	private String text;
    	
    	BranchType(int value, String text) {
    		this.value = value;
    		this.text = text;
    	}

		public int getValue() {
			return value;
		}

		public String getText() {
			return text;
		}

        public static Map<Integer, Object> getMap() {
            Map<Integer, Object> map = new HashMap<>();

            for(BranchType t: BranchType.values()) {
                map.put(t.getValue(), t.getText());
            }
            return map;
        }

        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();

            for(BranchType branchType: BranchType.values()) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", branchType.getValue());
                map.put("text", branchType.getText());

                list.add(map);
            }
            return list;
        }
    	
    }

    /**
     * 광고/공지사항 유형
     *
     */
    public enum AdOrNoticeType {
        AD(10, "광고"), NOTICE(20, "공지사항");

        private int value;
        private String text;

        AdOrNoticeType(int value, String text) {
            this.value = value;
            this.text = text;

        }

        public int getValue() {
            return value;
        }

        public String getText() {
                return text;
            }

        public static Map<Integer, Object> getMap() {
            Map<Integer, Object> map = new HashMap<>();

            for(DeskType t: DeskType.values()) {
                map.put(t.getValue(), t.getText());

            }

            return map;

        }

        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();

            for(DeskType deskType: DeskType.values()) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", deskType.getValue());
                map.put("text", deskType.getText());

                list.add(map);

            }

            return list;

        }

    }



    /**
     * 결제 유형
     *
     */
    public enum PayType {
        CASH(1, "현금"), CREDIT(2, "신용카드"), RECEIVABLE(3, "미수금"), HEADOFFICE_EXPENSE(4, "본사지출"), ACCOUNT_TRANSFER(5, "계좌이체");

        private int value;
        private String text;

        PayType(int value, String text) {
            this.value = value;
            this.text = text;

        }

        public int getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        public static Map<Integer, Object> getMap() {
            Map<Integer, Object> map = new HashMap<>();

            for(PayType t: PayType.values()) {
                map.put(t.getValue(), t.getText());

            }

            return map;

        }

        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();

            for(PayType payType: PayType.values()) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", payType.getValue());
                map.put("text", payType.getText());

                list.add(map);

            }

            return list;

        }

    }
    
    /**
     * 결제 상태 유형
     *
     */
    public enum PayStateType {
    	CANCEL(0, "취소"), NORMAL(10, "정상"); 

        private int value;
        private String text;

        PayStateType(int value, String text) {
            this.value = value;
            this.text = text;

        }

        public int getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        public static Map<Integer, Object> getMap() {
            Map<Integer, Object> map = new HashMap<>();

            for(PayStateType t: PayStateType.values()) {
                map.put(t.getValue(), t.getText());

            }

            return map;

        }

        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();

            for(PayStateType PayStateType: PayStateType.values()) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", PayStateType.getValue());
                map.put("text", PayStateType.getText());

                list.add(map);

            }

            return list;

        }

    }
   
    /**
     * 결제 수입/지출 유형
     *
     */
    public enum PayInOutType {
    	EXPENSE(10, "지출"), INCOME(20, "매출");

        private int value;
        private String text;

        PayInOutType(int value, String text) {
            this.value = value;
            this.text = text; 

        }

        public int getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        public static Map<Integer, Object> getMap() {
            Map<Integer, Object> map = new HashMap<>();

            for(PayInOutType t: PayInOutType.values()) {
                map.put(t.getValue(), t.getText());

            }

            return map;

        }

        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();

            for(PayInOutType PayInOutType: PayInOutType.values()) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", PayInOutType.getValue());
                map.put("text", PayInOutType.getText());

                list.add(map);

            }

            return list;

        }

    }
    
    /**
     * 결제 수입/지출 유형
     *
     */
    public enum ExpenseOption {
    	// 001 ~ 999 METC = MANAGEMENT + ETC, OETC = OPERATING + ETC    	
    	ELECTRIC(101, "전기"), LPG(102, "가스"), WATER(103, "수도"), METC(109, "기타"),
    	REPARE(201, "수리비"), SUPPLIES(202, "소모품"),  CLEAN(203, "청소비"), OETC(209,"기타"),
    	PRIME_M(301, "책임총무"), MANAGER1(302, "총무1"), MANAGER2(303, "총무2"), MANAGER3(304, "총무3"), 
    	MANAGER4(305, "총무4"), LETC(309, "기타"),ROYAL(401, "로열티"), RETC(409, "기타"), RETC2(0, "기타");
    	
    	private int value;
    	private String text;
    	
    	ExpenseOption(int value, String text) {
    		this.value = value;
    		this.text = text;
    	}
    	
    	public int getValue() { return value; }
    	public String getText() { return text; }
    	
    	public static Map<Integer, Object> getMap() {
            Map<Integer, Object> map = new HashMap<>();

            for(ExpenseOption t: ExpenseOption.values()) {
                map.put(t.getValue(), t.getText());

            }

            return map;

        }

        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();

            for(ExpenseOption ExpenseOption: ExpenseOption.values()) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", ExpenseOption.getValue());
                map.put("text", ExpenseOption.getText());

                list.add(map);

            }

            return list;

        }
    }
    
    /**
     * 결제 수입/지출 유형
     *
     */
    public enum ExpenseGroup {
    	// 001 ~ 999 
    	
    	MANAGEMENT(101, "관리비", new ExpenseOption[]{
    			ExpenseOption.ELECTRIC, ExpenseOption.LPG, ExpenseOption.WATER, ExpenseOption.METC
    	}),
    	OPERATING(201, "운영비", new ExpenseOption[]{
    			ExpenseOption.REPARE, ExpenseOption.SUPPLIES, ExpenseOption.CLEAN, ExpenseOption.OETC 
    	}),
    	LABOR(301, "인건비", new ExpenseOption[]{
    			ExpenseOption.PRIME_M, ExpenseOption.MANAGER1, ExpenseOption.MANAGER2, ExpenseOption.MANAGER3, ExpenseOption.MANAGER4, ExpenseOption.LETC
    	}),
    	ROYAL(401, "로얄티", new ExpenseOption[]{
    			ExpenseOption.ROYAL, ExpenseOption.RETC 
    	}),
    	EMPTY(0, "기타", new ExpenseOption[] {});
    	    	
    	private int value;
    	private String text;
    	private ExpenseOption[] containPayment;
    	
    	ExpenseGroup(int value, String text, ExpenseOption[] containPayment) {
    		this.value = value;
    		this.text = text;
    		this.containPayment = containPayment;
    	}
    	
//    	public static ExpenseGroup findGroup(ExpenseOption searchTarget) {
//    		return Arrays.stream(ExpenseGroup.values())
//    				.filter(group -> hasExpenseOption(group, searchTarget))
//    				.findAny()
//    				.orElse(ExpenseGroup.EMPTY);
//    	}
//    	
//    	private static boolean hasExpenseOption(ExpenseGroup from, ExpenseOption searchTarget) {
//    		return Arrays.stream(from.containPayment)
//    				.anyMatch(containPay -> containPay == searchTarget);
//    	}
    	
    	public int getValue() { return value; }
    	public String getText() { return text; }

    	public static Map<Integer, Object> getMap() {
            Map<Integer, Object> map = new HashMap<>();

            for(ExpenseGroup t: ExpenseGroup.values()) {
                map.put(t.getValue(), t.getText());

            }

            return map;

        }

        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();

            for(ExpenseGroup ExpenseGroup: ExpenseGroup.values()) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", ExpenseGroup.getValue());
                map.put("text", ExpenseGroup.getText());

                list.add(map);

            }

            return list;

        }
        
    }    
    
    // 대여 상태
    public enum RentalStateType {
    	RETURN(0, "반납"), RENT(10, "대여중"), PARTRETURN(5, "일부반납"); 

        private int value;
        private String text;

        RentalStateType(int value, String text) {
            this.value = value;
            this.text = text;

        }

        public int getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        public static Map<Integer, Object> getMap() {
            Map<Integer, Object> map = new HashMap<>();

            for(RentalStateType t: RentalStateType.values()) {
                map.put(t.getValue(), t.getText());

            }

            return map;

        }

        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();

            for(RentalStateType RentalStateType: RentalStateType.values()) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", RentalStateType.getValue());
                map.put("text", RentalStateType.getText());

                list.add(map);

            }

            return list;

        }

    }
    /**
     * 시스템 사용자 유형
     *
     */
    public enum UserRoleType {
        ADMIN("admin", "시스템관리자"), USER("user", "센터장"), MANAGER("manager", "매니저"), ENTRY("entry", "태블릿");

        private String value;
        private String text;

        UserRoleType(String value, String text) {
            this.value = value;
            this.text = text;

        }

        public String getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

    }
    

    /**
     * 출입 유형
     *
     */
    public enum EntryType {
        IN("1", "입실"), OUT("2", "퇴실"), OUTING("3", "외출"), RETURN("4", "복귀");

        private String value;
        private String text;

        EntryType(String value, String text) {
            this.value = value;
            this.text = text;

        }

        public String getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        public static Map<String, Object> getMap() {
            Map<String, Object> map = new HashMap<>();

            for(EntryType t: EntryType.values()) {
                map.put(t.getValue(), t.getText());

            }

            return map;

        }
        
	   
    }
    public static EntryType getEntryType(String value) {
    	for(EntryType e: EntryType.values()) {
    		if(e.getValue().equals(value)) return e;
    	}
    	
    	return null;
    }
    /**
     * 주문상태 유형
     *
     * enum : https://bluepoet.me/2012/07/18/%EB%B2%88%EC%97%AD%EC%9E%90%EB%B0%94-enum%EC%9D%98-10%EA%B0%80%EC%A7%80-%EC%98%88%EC%A0%9C/
     *
     */
    public enum OrderStatusType {
        READY(10, "주문대기"), CONFIRMED(20, "주문확정"), CANCELED(90, "주문취소");

        private int value;
        private String text;

        OrderStatusType(int value, String text) {
            this.value = value;
            this.text = text;

        }

        public int getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        public static Map<Integer, Object> getMap() {
            Map<Integer, Object> map = new HashMap<>();

            for(OrderStatusType t: OrderStatusType.values()) {
                map.put(t.getValue(), t.getText());

            }

            return map;

        }

    }

    /**
     * 등록상태 유형
     *
     * enum : https://bluepoet.me/2012/07/18/%EB%B2%88%EC%97%AD%EC%9E%90%EB%B0%94-enum%EC%9D%98-10%EA%B0%80%EC%A7%80-%EC%98%88%EC%A0%9C/
     *
     */
    public enum ReservationStatusType {
        READY(10, "등록대기"), CONFIRMED(20, "등록"), CHANGED(50, "등록수정"), CANCELED(90, "등록취소");

        private int value;
        private String text;

        ReservationStatusType(int value, String text) {
            this.value = value;
            this.text = text;

        }

        public int getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        public static Map<Integer, Object> getMap() {
            Map<Integer, Object> map = new HashMap<>();

            for(ReservationStatusType t: ReservationStatusType.values()) {
                map.put(t.getValue(), t.getText());

            }

            return map;

        }

    }
    
    /**
     * 좌석 유형
     *
     */
    public enum DeskType {
        NORMAL(10, "일반석"), FREE(20, "자유석");

        private int value;
        private String text;

        DeskType(int value, String text) {
            this.value = value;
            this.text = text;

        }

        public int getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        public static Map<Integer, Object> getMap() {
            Map<Integer, Object> map = new HashMap<>();

            for(DeskType t: DeskType.values()) {
                map.put(t.getValue(), t.getText());

            }

            return map;

        }

        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();

            for(DeskType deskType: DeskType.values()) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", deskType.getValue());
                map.put("text", deskType.getText());

                list.add(map);

            }

            return list;

        }

    }
    
    
    /**
     * 열람실 유형
     *
     */
    public enum RoomType {
        MULTI(10, "멀티스페이스(다인석)"), SINGLE(20, "싱글스페이스(1인석)"), PRIVATE(30, "프라이빗큐브(개인실)");//, PREMIUM(40, "Premium Zone");

        private int value;
        private String text;

        RoomType(int value, String text) {
            this.value = value;
            this.text = text;

        }

        public int getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        public static Map<Integer, Object> getMap() {
            Map<Integer, Object> map = new HashMap<>();

            for(RoomType t: RoomType.values()) {
                map.put(t.getValue(), t.getText());

            }

            return map;

        }

        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();

            for(RoomType roomType: RoomType.values()) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", roomType.getValue());
                map.put("text", roomType.getText());

                list.add(map);

            }

            return list;

        }

    }

    /**
     * 성별 유형
     *
     */
    public enum GenderType {
        MALE(10, "남자"), FEMALE(20, "여자");

        private int value;
        private String text;

        GenderType(int value, String text) {
            this.value = value;
            this.text = text;

        }

        public int getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        public static Map<Integer, Object> getMap() {
            Map<Integer, Object> map = new HashMap<>();

            for(GenderType t: GenderType.values()) {
                map.put(t.getValue(), t.getText());

            }

            return map;

        }

        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();

            for(GenderType genderType: GenderType.values()) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", genderType.getValue());
                map.put("text", genderType.getText());

                list.add(map);

            }

            return list;

        }

    }
    
    
    /**
     * 루트 유형
     *
     */
    public enum cmpRouteType {
        POSTER(10, "전단지"), FRIEND(20, "지인소개"), SIGN(30, "간판 및 현수막"), ONLINE(40, "온라인"), CENTER(50, "타 센터 이용"), ETC(60, "기타");

        private int value;
        private String text;

        cmpRouteType(int value, String text) {
            this.value = value;
            this.text = text;

        }

        public int getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        public static Map<Integer, Object> getMap() {
            Map<Integer, Object> map = new HashMap<>();

            for(cmpRouteType t: cmpRouteType.values()) {
                map.put(t.getValue(), t.getText());

            }

            return map;

        }

        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();

            for(cmpRouteType cmpRouteType: cmpRouteType.values()) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", cmpRouteType.getValue());
                map.put("text", cmpRouteType.getText());

                list.add(map);

            }

            return list;

        }

    }
    
    
    public enum SalesGroup {
    	
    	SALES(901, "매출", new RoomType[]{
    			RoomType.MULTI, RoomType.SINGLE, RoomType.PRIVATE
    	}),
    	EMPTY(0, "기타", new RoomType[] {});
    	    	
    	private int value;
    	private String text;
    	private RoomType[] containPayment;
    	
    	SalesGroup(int value, String text, RoomType[] containPayment) {
    		this.value = value;
    		this.text = text;
    		this.containPayment = containPayment;
    	}
    	
//    	public static SalesGroup findGroup(RoomType searchTarget) {
//    		return Arrays.stream(SalesGroup.values())
//    				.filter(group -> hasRoomtype(group, searchTarget))
//    				.findAny()
//    				.orElse(SalesGroup.EMPTY);
//    	}
    	
//    	private static boolean hasRoomtype(SalesGroup from, RoomType searchTarget) {
//    		return Arrays.stream(from.containPayment)
//    				.anyMatch(containPay -> containPay == searchTarget);
//    	}
    	
    	public int getValue() { return value; }
    	public String getText() { return text; }

    	public static Map<Integer, Object> getMap() {
            Map<Integer, Object> map = new HashMap<>();

            for(SalesGroup t: SalesGroup.values()) {
                map.put(t.getValue(), t.getText());

            }

            return map;

        }

        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();

            for(SalesGroup SalesGroup: SalesGroup.values()) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", SalesGroup.getValue());
                map.put("text", SalesGroup.getText());

                list.add(map);

            }

            return list;

        }
        
    }    
    
    
    
    /**
     * 시험 유형
     *
     */
    public enum ExamType {
        GENERAL(10, "공무원(행정직)"), TECHNICAL(20, "공무원(기술직)"), FIVEGRADE(30, "5급공채(고시)"),
        TAX(40, "세무직"), POLICE(50, "경찰직"), FIRE(60, "소방직"), CIVIL(70, "임용고시"), GOVERNMENT(80, "공기업"), 
        CERTIFICATE(90, "자격증"), TRANSFER(100, "편입"), GRADUATE(110, "대학원"), ETC(990, "기타");

        private int value;
        private String text;

        ExamType(int value, String text) {
            this.value = value;
            this.text = text;

        }

        public int getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        public static Map<Integer, Object> getMap() {
            Map<Integer, Object> map = new HashMap<>();

            for(ExamType t: ExamType.values()) {
                map.put(t.getValue(), t.getText());

            }

            return map;

        }

        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();

            for(ExamType examType: ExamType.values()) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", examType.getValue());
                map.put("text", examType.getText());

                list.add(map);

            }

            return list;

        }

    }
    
    
    
    /**
     * 일반/보호자 유형
     *
     */
    public enum GuestTypes {
    	 STANDARD(10, "일반"), PARENTS(20, "보호자");

        private int value;
        private String text;

        GuestTypes(int value, String text) {
            this.value = value;
            this.text = text;

        }

        public int getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        public static Map<Integer, Object> getMap() {
            Map<Integer, Object> map = new HashMap<>();

            for(GuestTypes t: GuestTypes.values()) {
                map.put(t.getValue(), t.getText());

            }

            return map;

        }

        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();

            for(GuestTypes examType: GuestTypes.values()) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", examType.getValue());
                map.put("text", examType.getText());

                list.add(map);

            }

            return list;

        }

    }
    
    
    
    /**
     * 직무분야 유형
     *
     */
    public enum JobTypes {
   	 MIDDLE(10, "중학생"), HIGH(20, "고등학생"), EXAM(30, "고시/수험생"), UNIVERSITY(40, "대학/대학원생"), JOB(50, "취업 준비생"), CIVIL(60, "공무원"), 
   	ENGINEER(70, "엔지니어/프로그래머"), DESIGNER(80, "디자이너"), MEDIA(90, "언론/방송인"), PROFESSOR(100, "교수/강사"), AUTHOR(110, "통/번역/작가"), 
   	AD(120, "광고/마케팅"), SALES(130, "영업"), MD(140, "유통/MD"), HR(150, "인사/총무"), FINANCE(160, "재무/회계"), PLANNING(170, "경영/컨설팅/기획"), 
   	PRIVATE(180, "개인사업자"), MEDICAL(190, "의료/법조"), 
   	ETC(990, "기타"); 

       private int value;
       private String text;

       JobTypes(int value, String text) {
           this.value = value;
           this.text = text;

       }

       public int getValue() {
           return value;
       }

       public String getText() {
           return text;
       }

       public static Map<Integer, Object> getMap() {
           Map<Integer, Object> map = new HashMap<>();

           for(JobTypes t: JobTypes.values()) {
               map.put(t.getValue(), t.getText());

           }

           return map;

       }

       public static List<Map<String, Object>> getList() {
           List<Map<String, Object>> list = new ArrayList<>();

           for(JobTypes jobType: JobTypes.values()) {
               Map<String, Object> map = new HashMap<>();
               map.put("value", jobType.getValue());
               map.put("text", jobType.getText());

               list.add(map);

           }

           return list;

       }

   }
    

    /**
     * 흥미분야 유형
     *
     */
    public enum InterestTypes {
    	MOBILE(10, "IT/통신/모바일"), GAME(20, "게임"), PROGRAMMING(30, "프로그래밍"), PLANNING(40, "기획/마케팅"), AD(50, "광고/홍보"), 
    	INTERIOR(60, "건축/인테리어"), MD(70, "유통/MD"), STARTUP(80, "프랜차이즈/창업"), SALES(90, "영업"), CIVIL(100, "행정/외무/사법고시"), 
    	EXAM(110, "수능/입시/유학"), ENGLISH(120, "영어"), 

    	ETC(990, "기타"); 

       private int value;
       private String text;

       InterestTypes(int value, String text) {
           this.value = value;
           this.text = text;

       }

       public int getValue() {
           return value;
       }

       public String getText() {
           return text;
       }

       public static Map<Integer, Object> getMap() {
           Map<Integer, Object> map = new HashMap<>();

           for(InterestTypes t: InterestTypes.values()) {
               map.put(t.getValue(), t.getText());

           }

           return map;

       }

       public static List<Map<String, Object>> getList() {
           List<Map<String, Object>> list = new ArrayList<>();

           for(InterestTypes interestType: InterestTypes.values()) {
               Map<String, Object> map = new HashMap<>();
               map.put("value", interestType.getValue());
               map.put("text", interestType.getText());

               list.add(map);

           }

           return list;

       }

   }
    
    

    /**
     * 학부모 안심 서비스 상태 유형
     *
     */
    public enum StatusType {
    	READY(10, "대기"), CONFIRMED(20, "확정"), CANCELED(90, "취소"), AUTOCANCEL(100, "자동취소");

        private int value;
        private String text;

        StatusType(int value, String text) {
            this.value = value;
            this.text = text;

        }

        public int getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        public static Map<Integer, Object> getMap() {
            Map<Integer, Object> map = new HashMap<>();

            for(StatusType t: StatusType.values()) {
                map.put(t.getValue(), t.getText());

            }

            return map;

        }

        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();

            for(StatusType statusType: StatusType.values()) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", statusType.getValue());
                map.put("text", statusType.getText());

                list.add(map);

            }

            return list;

        }

    }
    
    
    
    /**
     * 전국 코드
     *
     */
    public enum AreaTypes {
    	ALL(10, "전국"), SEOUL(20, "서울"), BUSAN(30, "부산"), DAEGU(40, "대구"), INCHEON(50, "인천"), GWANGJU(60, "광주"), 
    	DAEJEON(70, "대전"), ULSAN(80, "울산"), SEJONG(90, "세종"), GYEONGGI(100, "경기"), GANGWON(110, "강원"), CHUNGBUK(120, "충북"),
    	CHUNGNAM(130, "충남"), JEONBUK(140, "전북"), JEONNAM(150, "전남"), GYEONGBUK(160, "경북"), GYUEONGNAM(170, "경남"), JEJU(180, "제주");

        private int value;
        private String text;

        AreaTypes(int value, String text) {
            this.value = value;
            this.text = text;

        }

        public int getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        public static Map<Integer, Object> getMap() {
            Map<Integer, Object> map = new HashMap<>();

            for(AreaTypes t: AreaTypes.values()) {
                map.put(t.getValue(), t.getText());

            }

            return map;

        }

        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();

            for(AreaTypes areaType: AreaTypes.values()) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", areaType.getValue());
                map.put("text", areaType.getText());

                list.add(map);

            }

            return list;

        }

    }
    
    
    
    /*
     * if(status == 10)방문완료status == 20){ }}<strong>예약완료(status == 30)예약요청 (status == 40)예약취소
     * 
     */
    public enum SeatStatusType {
    	REQUEST(10, "예약요청"), RESERVATION(20, "예약완료"), CANCEL(90, "예약취소"), AUTOCANCEL(100, "자동취소");

        private int value;
        private String text;

        SeatStatusType(int value, String text) {
            this.value = value;
            this.text = text;

        }

        public int getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        public static Map<Integer, Object> getMap() {
            Map<Integer, Object> map = new HashMap<>();

            for(SeatStatusType t: SeatStatusType.values()) {
                map.put(t.getValue(), t.getText());

            }

            return map;

        }

        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();

            for(SeatStatusType seatStatusType: SeatStatusType.values()) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", seatStatusType.getValue());
                map.put("text", seatStatusType.getText());

                list.add(map);

            }

            return list;

        }

    }
    
    
}
