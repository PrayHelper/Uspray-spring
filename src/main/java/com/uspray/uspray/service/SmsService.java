package com.uspray.uspray.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uspray.uspray.DTO.sms.CertDto;
import com.uspray.uspray.DTO.sms.MessageDto;
import com.uspray.uspray.DTO.sms.SmsRequestDto;
import com.uspray.uspray.DTO.sms.SmsResponseDto;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsService {
    private final String smsConfirmNum = createSmsKey();
    private final RedisTemplate redisTemplate;

    @Value("${naver-cloud-sms.accessKey}")
    private String accessKey;

    @Value("${naver-cloud-sms.secretKey}")
    private String secretKey;

    @Value("${naver-cloud-sms.serviceId}")
    private String serviceId;

    @Value("${naver-cloud-sms.senderPhone}")
    private String phone;
    public SmsResponseDto sendSms(MessageDto messageDto) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {

        // 현재시간
        String time = Long.toString(System.currentTimeMillis());
        // 헤더세팅
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time);
        headers.set("x-ncp-iam-access-key", accessKey);
        headers.set("x-ncp-apigw-signature-v2", getSignature(time)); // signature 서명

        List<MessageDto> messages = new ArrayList<>();
        messages.add(messageDto);
        // api 요청 양식에 맞춰 세팅
        SmsRequestDto request = SmsRequestDto.builder()
            .type("SMS")
            .contentType("COMM")
            .countryCode("82")
            .from(phone)
            .content("[uspray] 인증번호 [" + smsConfirmNum + "]를 입력해주세요")
            .messages(messages)
            .build();

        //request를 json형태로 body로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(request);
        // body와 header을 합친다
        HttpEntity<String> httpBody = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        //restTemplate를 통해 외부 api와 통신
        SmsResponseDto smsResponseDto = restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/"+ serviceId +"/messages"), httpBody, SmsResponseDto.class);

        //redis에 저장 (3분 / key는 requestId)
        redisTemplate.opsForValue().set(smsResponseDto.getRequestId(), smsConfirmNum, 3L, TimeUnit.MINUTES);

        return smsResponseDto;
    }
    // 전달하고자 하는 데이터를 암호화해주는 작업
    public String getSignature(String time) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/"+ this.serviceId+"/messages";
        String accessKey = this.accessKey;
        String secretKey = this.secretKey;

        String message = method
            + space
            + url
            + newLine
            + time
            + newLine
            + accessKey;

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes(UTF_8));

        return Base64.encodeBase64String(rawHmac);
    }
    // 5자리의 난수를 조합을 통해 인증코드 만들기
    public static String createSmsKey() {
        StringBuilder key = new StringBuilder();
        Random rnd = new Random();

        for (int i = 0; i < 6; i++) { // 인증코드 6자리
            key.append((rnd.nextInt(10)));
        }
        return key.toString();
    }

    public Boolean getCertification(CertDto certDto) {
        String verificationCode = (String) redisTemplate.opsForValue().get(certDto.getRequestId());
//        exception 정의한 pr 머지된 후 에러처리
//        if(Objects.equals(verificationCode, certDto.getSmsConfirmNum())) throw new MissMatchCertificationCodeException();
        return true;
    }
}
