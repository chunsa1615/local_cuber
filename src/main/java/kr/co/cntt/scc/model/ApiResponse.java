package kr.co.cntt.scc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 응답 (공통)
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {

    String errorMessage = "";

}
