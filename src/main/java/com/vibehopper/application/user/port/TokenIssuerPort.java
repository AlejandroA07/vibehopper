package com.vibehopper.application.user.port; import com.vibehopper.domain.user.AppUser; public interface TokenIssuerPort { String issueToken(AppUser user); }
