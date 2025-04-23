package com.xalts.expense_tracker.controller;

import com.xalts.expense_tracker.dto.LoginRequest;
import com.xalts.expense_tracker.dto.RegisterRequest;
import com.xalts.expense_tracker.facade.ExpenseTrackerFacade;
import com.xalts.expense_tracker.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final ExpenseTrackerFacade expenseTrackerFacade;

    public AuthController(ExpenseTrackerFacade expenseTrackerFacade) {
        this.expenseTrackerFacade = expenseTrackerFacade;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        User user = expenseTrackerFacade.registerUser(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = expenseTrackerFacade.loginUser(request);
        return ResponseEntity.ok(token);
    }
}