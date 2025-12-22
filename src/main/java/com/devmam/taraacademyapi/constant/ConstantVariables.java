package com.devmam.taraacademyapi.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConstantVariables {
    public static String SIGNER_KEY;

    public static String CLAUDE_API_KEY;

    public static String CLAUDE_API_URL;

    public static String CLAUDE_API_VERSION;

    public static String CLAUDE_API_MODEL;

    @Value("${constant.key.signer-key}")
    public void setSignerKey(String signerKey) {
        SIGNER_KEY = signerKey;
    }


    //AI
    @Value("${claude.api.key}")
    public void setClaudeApiKey(String claudeApiKey) {
        CLAUDE_API_KEY = claudeApiKey;
    }


    @Value("${claude.api.url:https://api.anthropic.com/v1/messages}")
    private void setClaudeApiUrl(String claudeApiUrl) {
        CLAUDE_API_URL = claudeApiUrl;
    }

    @Value("${claude.api.version:2023-06-01}")
    private void setClaudeApiVersion(String claudeApiVersion) {
        CLAUDE_API_VERSION = claudeApiVersion;
    }

    @Value("${claude.model:claude-sonnet-4-20250514}")
    private void setClaudeApiModel(String claudeApiModel) {
        CLAUDE_API_MODEL = claudeApiModel;
    }
}
