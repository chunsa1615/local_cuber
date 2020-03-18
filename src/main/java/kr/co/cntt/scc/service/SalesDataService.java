package kr.co.cntt.scc.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.cntt.scc.model.SalesData;

@Service
@Transactional
public class SalesDataService {

	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;
	
	
	public List<SalesData> select_account_data(String from_date, String to_date, String branchId) {

		String s = " SELECT "
				+ " cate_sort, cate_nm, sub_cate_cd ,sum(Jan) 'Jan', sum(Feb) Feb, sum(Mar) 'Mar' ,sum(Apr) 'Apr', sum(May) May, "
				+ " sum(Jun) 'Jun',sum(Jul) 'Jul', sum(Aug) Aug, sum(Sep) 'Sep',sum(Oct) 'Oct', sum(Nov) Nov, sum(`Dec`) `Dec` "
				+ " from (select case when cate_cd = '901' then 0 " + " when cate_cd = '101' then 1 "
				+ " when cate_cd = '201' then 2 " + " when cate_cd = '301' then 3 " + " when cate_cd = '401' then 4 "
				+ " when cate_cd = '501' then 5 " + " when cate_cd = '0' then 6 " + " END as cate_sort, "
				+ " case when cate_cd = '901' then '매출' " + " when cate_cd = '101' then '관리비' "
				+ " when cate_cd = '201' then '운영비' " + " when cate_cd = '301' then '인건비' "
				+ " when cate_cd = '401' then '로얄티' " + " when cate_cd = '501' then '임대료' "
				+ " when cate_cd = '0' then '기타' " + " END as cate_nm " + " 	, sub_cate_cd "
				+ " ,IFNULL(case when mon =  1 then amt end,0) as 'Jan', "
				+ " IFNULL(case when mon =  2 then amt end,0) as 'Feb', "
				+ " IFNULL(case when mon =  3 then amt end,0) as 'Mar', "
				+ " IFNULL(case when mon =  4 then amt end,0) as 'Apr', "
				+ " IFNULL(case when mon =  5 then amt end,0) as 'May', "
				+ " IFNULL(case when mon =  6 then amt end,0) as 'Jun', "
				+ " IFNULL(case when mon =  7 then amt end,0) as 'Jul', "
				+ " IFNULL(case when mon =  8 then amt end,0) as 'Aug', "
				+ " IFNULL(case when mon =  9 then amt end,0) as 'Sep', "
				+ " IFNULL(case when mon = 10 then amt end,0) as 'Oct', "
				+ " IFNULL(case when mon = 11 then amt end,0) as 'Nov', "
				+ " IFNULL(case when mon = 12 then amt end,0) as `Dec` "
				+ " from ( select  b.cate_cd, b.sub_cate_cd, month(IFNULL(salesDt,now())) mon, "
				+ " sum(IFNULL(case when payStateType='0' then IFNULL(amount,0)*-1 else amount end,0)) as amt "
				+ " from statistics_sales_day a " + " right outer join code_detail b on a.`group` = b.cate_cd "
				+ " and a.`option` = b.sub_cate_cd " + " and a.branchid = :branchId "
				+ " and a.salesDt between :from_date and :to_date   " 
				+ " and a.`group`  <> '0' "
				+ " and a.payType in (1,2) " 
				+ " group by b.cate_cd, b.sub_cate_cd, month(IFNULL(salesDt,now())) ) a "
				+ " ) a "
				+ " group by  cate_sort, cate_nm, sub_cate_cd " + " order by cate_sort ";

		Map<String, Object> args = new HashMap<String, Object>();

		args.put("from_date", from_date);
		args.put("to_date", to_date);
		args.put("branchId", branchId);

		return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(SalesData.class));
	}

	
	
	 public int salesData_scheduler() {
		
		
	String s = 	" insert into statistics_sales_day (branchId, salesDt, `group`, `option`, payInOutType, payType, payStateType, amount, useYn,div_mons, insertDt, updateDt) "
				+ " select bp.branchId, IFNULL(bp.paydt,substring(bp.insertDt,1,10)) as SaleDt, '901' as `group`, IFNULL(brm.roomType,'99') as `option` , '20' as  payInOutType, bp.payType, bp.payStateType, sum(bp.payAmount), '1' as useyn , IFNULL(datediff(br.deskEndDt, br.deskStartDt) div 29,0) as div_mons "
				+ " , now(), null "
				+ " from  branch bch "
				+ " inner join branch_pay bp "
				+ " on bch.branchId = bp.branchId " 
				+ " and bch.useyn = 1 "
				+ " and bp.useyn = 1 "
				//+ " and bp.branchId <> '033f8817-71a0-4feb-bf7b-f9f184da7317' "
				+ " left outer join  (select br.* "
				+ " from branch_reservation  br "
				+ "					, branch bch "
				+ "					where br.branchId = bch.branchId "
				+ "					and bch.useyn = 1 "
				+ "					and br.useyn = 1 "
				+ "				  ) br  "
				+ " on bp.branchId = br.branchId "
				+ " and bp.reservationId = br.reservationId " 
				+ " left outer join branch_desk bd "
				+ " on br.deskId = bd.deskId "
				+ " left outer join  branch_room brm "
				+ " on brm.roomId = bd.roomId "  
				+ " where 1=1  "
				+ " and br.reservationStatus 	!= 90 "
				+ " and concat(bp.payDt,' ',bp.payTm) between date_add(sysdate(), interval -22 hour) and date_add(sysdate(), interval -3 hour) "
				+ " and bp.payStateType = 10 "
				+ " and bp.payInOutType = 20 "
				+ " and bp.payType in (1, 2) "
				+ " and ( bp.payAmount > 0  or bp.payAmount < 0) "
				+ " group by br.branchId, bp.paydt , brm.roomType , bp.payType, bp.payStateType, datediff(br.deskEndDt, br.deskStartDt) "
				+ " union all "
				+ " select  branchId, expenseDt as SaleDt , expenseGroup as `group`, expenseOption as `option` , '10' as payInOutType, payType,  '10' as payStateType, sum(expenseAmount) as Amount, '1' as useyn , 0 "
				+ " , now(), null from branch_expense be"
				+ " where 1=1 " 
				//+ " and concat(bp.payDt,' ',bp.payTm) between date_add(sysdate(), interval -22 hour) and date_add(sysdate(), interval -3 hour) "
				+ " and concat(be.expenseDt,' ',be.expenseTm) between date_add(sysdate(), interval -22 hour) and date_add(sysdate(), interval -3 hour) "
				+ " and  be.useYn = :useYn "
				+ " group by be.branchId, be.expenseDt, be.expenseGroup, be.expenseOption, be.payType  ";
	
				Map<String, Object> args = new HashMap<>();
				args.put("useYn", "1");
			
				int result = jdbcTemplate.update(s, args);
				
				return result;
		
	}

		public List<SalesData> select_branch_info(String branchId) {

			String s = " SELECT " 	 
					+ " b.name, b.openDt, sum(bd.deskmax) as seat_count, left(sysdate(), 4) as cur_year " 
					+ " FROM branch b JOIN branch_desk bd ON b.branchId = bd.branchId "
					+ " WHERE b.branchId = :branchId "
					+ " AND (bd.roomId != null or bd.roomId != '') "
					+ " AND bd.useYn=1 AND b.name NOT LIKE '%test%' "
					+ " GROUP BY bd.branchId ";

			Map<String, Object> args = new HashMap<String, Object>();

			args.put("branchId", branchId);

			return jdbcTemplate.query(s, args, new BeanPropertyRowMapper<>(SalesData.class));
		}
	 
}
