package com.rag.service.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constant {
    public static final String SESSION_ALREADY_EXIST_MSG="Session name already exists for this user";
    public static final String SESSION_NOT_FOUND_MSG="Session not found for user";
    public static final String UNAUTHORISED_SESSION_USER_MSG="User is not authorized to add messages to this session";
    public static final String RATE_LIMIT_EXCEED_MSG="Rate limit exceeded";
    public static final String USER_NOT_FOUND="User not found with id: ";
    public static final String EMAIL_ALREADY_EXIST="Email already exists";
}
