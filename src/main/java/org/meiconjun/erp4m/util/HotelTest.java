package org.meiconjun.erp4m.util;

import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.meiconjun.erp4m.dto.RetObj;

/**
 * @ClassName HotelTest
 * @Description TODO
 * @Author chunhui.lao
 * @Date 2022/5/28 16:15
 **/
@Slf4j
public class HotelTest {

    public static void main(String[] args) throws Exception {
        HashMap<String, String> baseHeader = new HashMap();
        baseHeader.put("X-Requested-With", "XMLHttpRequest");
        baseHeader.put("Accept", "application/json");
        baseHeader.put("Content-Type", "application/json;charset=UTF-8");
        baseHeader.put("Accept-Encoding", "gzip, deflate, br");
        baseHeader.put("Accept-Language", "zh-TW,zh-CN;q=0.9,zh;q=0.8,en;q=0.7");
        baseHeader.put("Host", "epassport.meituan.com");
        baseHeader.put("Connection", "keep-alive");
        baseHeader.put("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"101\", \"Google Chrome\";v=\"101\"");
        baseHeader.put("sec-ch-ua-platform", "\"Windows\"");
        baseHeader.put("sec-ch-ua-mobile", "?0");
        baseHeader.put("Sec-Fetch-Dest", "empty");
        baseHeader.put("Sec-Fetch-Site", "same-origin");
        baseHeader.put("Sec-Fetch-Mode", "cors");
        // TODO step1
        HashMap<String, String> header1 = new HashMap();
        header1.putAll(baseHeader);
        header1.put("Referer", "https://epassport.meituan.com/account/unitivelogin?service=hotel&loginsource=14&noSignup=true&bg_source=4&loginurl=https%3A%2F%2Feb.meituan.com%2Febk%2Flogin%2Flogin.html&continue=https%3A%2F%2Feb.meituan.com%2Fgw%2Faccount%2Fbiz%2Fsettoken%3Fredirect_uri%3Dhttps%253A%252F%252Feb.meituan.com%252Febk%252Flogin%252Fsettoken.html%253Fredirect%253Dhttps%25253A%25252F%25252Feb.meituan.com%25252Febooking%25252Fnew-workbench%25252Findex.html");
        header1.put("Origin", "https://epassport.meituan.com");
        header1.put("Cookie", "_lxsdk_cuid=18100265126c8-0218a6b5572b0b-17333270-144000-18100265126c8; uuid=fbbe821aebb9f0468f76.1653564921.1.0.0; _lxsdk=18100265126c8-0218a6b5572b0b-17333270-144000-18100265126c8; WEBDFPID=0w6953u8w01956wzy019051xwyu4992u8189z9v6z3u9795833uv1zvw-1653817513774-1653731111815IQCEWQAfd79fef3d01d5e9aadc18ccd4d0c95077963; logan_session_token=qvqdmcfhew6gp7rkqb79; _lxsdk_s=1810a0e37ff-bf5-7fb-27%7C%7C3");
//        header1.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.64 Safari/537.36");

        HashMap<String, String> condMap1 = new HashMap();
        condMap1.put("captchaCode", "");
        condMap1.put("error", "");
        condMap1.put("isFetching", "false");
        condMap1.put("login", "KMFYDJJD88");
        condMap1.put("loginType", "account");
        condMap1.put("part_key", "");
        condMap1.put("password", "Youngyun7iu");
        condMap1.put("rohrToken", "eJyFk2Fv2jAQQP+LJfhSj9hxnBCkaoIxCt2AdYVqU1dVIZiQBRKaOEBb9b/vbILLtk6TkPN8d352zuQZ5YM5aj2jxSqIUIsSQhz2gtFW5KiFaIM0XISRLCDlcuYxSqnjUg+j8DTmEUo4RrP8potat8wjmHFypwJfYX5LOXNx03XucIU2oO1g9UCzAZSgpZSbomVZYhMUxSbLZWMtYlkGaSPM1lYQhlmZSqtMYxlvxSqL4vR9IfJtHIrzZSbFqq5jRVbmEKFOPc2u4ygtN+cyL0V9Ft1XKedQWOarc71ljbVrdg9+Yna6ow4kMOrq47OxlOtVPcxSCQbxP0G0g6E6OdAsfoKxEFJmiUhrrJeLeZyLUN6XeVxj3YPN5srHlZH/7eTVsbg5GH9V6tMpgTGrifEezZX7TbsOZlkSp1E1TcXu3S7Lk5lIw2UVi9O52Ovd4K9hLu43mYXgZtcTdbM2ZdgmHEoP5GiygZgmCmQrIj6mvqfJA6KaGKaeilEfsq6riWPKfU0USPloE7KOrmu6QMyQbYj+QZ7J+scVPgHiR/PB5zuwBzGk1/quoabJKouvz0wN2W8Qq1ZAJ4DMm7tEtSzRLbvBg9GX6QRSP0pCoEPH6T8SsDA47TXuTCeT8ej1hc0yaCH2mmaq6v0Ti1QWZRvCxw/5Ar4hIHG56+2mHz9F3fawM/xg+e3+6Nuim1z72VUgO+XywrlafCZP/Lu3HLcvyWZM0rNgP9r3H+a8eLx4+NlbbUeX0wHpb6wieRzGvHt21Q/oPuTo5Rf9CTro");
        condMap1.put("success", "");
        condMap1.put("verifyRequestCode", "");
        condMap1.put("verifyResponseCode", "");
        condMap1.put("verifyType", "");

        String url1 = "https://epassport.meituan.com/api/account/login?service=hotel&bg_source=4&loginContinue=https:%2F%2Feb.meituan.com%2Fgw%2Faccount%2Fbiz%2Fsettoken%3Fredirect_uri%3Dhttps%253A%252F%252Feb.meituan.com%252Febk%252Flogin%252Fsettoken.html%253Fredirect%253Dhttps%25253A%25252F%25252Feb.meituan.com%25252Febooking%25252Fnew-workbench%25252Findex.html&loginType=account";

        RetObj reObj1 = SendRequestUtil.doPost(url1, header1, condMap1, 0);

        log.info(reObj1.toString());
    }
}
