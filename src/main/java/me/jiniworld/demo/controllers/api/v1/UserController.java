package me.jiniworld.demo.controllers.api.v1;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.jiniworld.demo.models.entities.User;
import me.jiniworld.demo.models.responses.BasicResponse;
import me.jiniworld.demo.models.responses.CommonResponse;
import me.jiniworld.demo.models.responses.ErrorResponse;
import me.jiniworld.demo.models.values.UserValue;
import me.jiniworld.demo.services.UserService;

@Tag(name = "user")
@RequestMapping(value = "${demo.api}/users")
@RequiredArgsConstructor
@RestController
public class UserController {
	
	private final UserService userService;
	
	@Operation(summary = "회원 가입")
	@PostMapping("")
	public ResponseEntity<? extends BasicResponse> save(@RequestBody UserValue value) {
		User user = userService.save(value);
		if(user == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("회원 가입 실패"));
		}
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/{id}")
	@ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", 
        		content = {@Content(array = @ArraySchema(schema = @Schema(implementation = User.class)))}),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 입니다.")
    })
	@Operation(summary = "회원 조회")
	public ResponseEntity<? extends BasicResponse> select(
			@Parameter(description = "user 의 id") @PathVariable("id") long id) {
		Optional<User> oUser = userService.findById(id);
		if(!oUser.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ErrorResponse("일치하는 회원 정보가 없습니다. 사용자 id를 확인해주세요."));
		}
		return ResponseEntity.ok().body(new CommonResponse<User>(oUser.get()));
	}
	
	@PatchMapping("/{id}")
	@ApiResponses({
        @ApiResponse(responseCode = "204", description = "수정 완료"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 입니다.")
    })
	@Operation(summary = "회원 수정")
	public ResponseEntity<? extends BasicResponse> patch(
			@Parameter(description = "user 의 id") @PathVariable("id") long id, @RequestBody UserValue value) {
		if(!userService.patch(id, value)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ErrorResponse("일치하는 회원 정보가 없습니다. 사용자 id를 확인해주세요."));
		}
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/{id}")
	@ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 완료"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 입니다.")
    })
	@Operation(summary = "회원 삭제")
	public ResponseEntity<? extends BasicResponse> delete(
			@Parameter(description = "user 의 id") @PathVariable("id") long id) {
		if(!userService.delete(id)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ErrorResponse("일치하는 회원 정보가 없습니다. 사용자 id를 확인해주세요."));
		}
		return ResponseEntity.noContent().build();
	}
	
}
