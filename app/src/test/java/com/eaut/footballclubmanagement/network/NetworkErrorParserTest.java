package com.eaut.footballclubmanagement.network;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NetworkErrorParserTest {

    @Test
    public void messageFromJson_readsStructuredBackendErrorMessage() {
        String json = "{\"error\":{\"code\":\"INVALID_CREDENTIALS\","
                + "\"message\":\"  Tên đăng nhập hoặc mật khẩu không đúng.  \"}}";

        assertEquals(
                "Tên đăng nhập hoặc mật khẩu không đúng.",
                NetworkErrorParser.messageFromJson(json, "Lỗi máy chủ")
        );
    }

    @Test
    public void messageFromJson_readsTopLevelMessageForSuccessfulStylePayloads() {
        assertEquals(
                "Phiên đăng nhập đã hết hạn.",
                NetworkErrorParser.messageFromJson(
                        "{\"message\":\" Phiên đăng nhập đã hết hạn. \"}",
                        "Lỗi máy chủ"
                )
        );
    }

    @Test
    public void messageFromJson_prefersStructuredErrorOverTopLevelMessage() {
        assertEquals(
                "Chi tiết chính xác",
                NetworkErrorParser.messageFromJson(
                        "{\"error\":{\"message\":\"Chi tiết chính xác\"},\"message\":\"Chung chung\"}",
                        "Lỗi máy chủ"
                )
        );
    }

    @Test
    public void messageFromJson_fallsBackForMalformedUnexpectedOrBlankBodies() {
        String fallback = "Không thể kết nối máy chủ";
        String[] invalidBodies = {
                null,
                "",
                "not-json",
                "[]",
                "42",
                "{\"error\":{\"message\":\"   \"}}",
                "{\"message\":null}",
                "{}"
        };

        for (String body : invalidBodies) {
            assertEquals(body, fallback, NetworkErrorParser.messageFromJson(body, fallback));
        }
    }
}
