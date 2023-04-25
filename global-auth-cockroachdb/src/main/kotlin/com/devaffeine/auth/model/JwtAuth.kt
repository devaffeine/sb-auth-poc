package com.devaffeine.auth.model

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

class JwtAuth(val token: String) : UsernamePasswordAuthenticationToken(token, token)
