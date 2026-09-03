<div dir="rtl" align="right">

# <span dir="ltr">SDK</span> جاوا درگاه سپ (<span dir="ltr">SEP</span>)

## معرفی

این کتابخانه یک <span dir="ltr">SDK</span> سبک برای اتصال به درگاه پرداخت اینترنتی سپ در پروژه‌های <span dir="ltr">
Spring Boot 3.5.7</span> است. هدف آن ساده‌سازی دریافت توکن، اعتبارسنجی زودهنگام (<span dir="ltr">fail-fast</span>) و
دریافت پاسخ‌های تایپ‌شده است.

این پروژه یک کتابخانه است و برنامه اجرایی ندارد؛ آن را در پروژه خود استفاده می‌کنید تا روی منطق کسب‌وکار تمرکز کنید.

---

## پیش‌نیازها

* <span dir="ltr">Java 21</span>
* <span dir="ltr">Spring Boot 3.5.7</span>

---

## نصب

### مرحله ۱: ساخت و نصب محلی

ابتدا کتابخانه را در مخزن محلی <span dir="ltr">Maven</span> نصب کنید:

<div dir="ltr" align="left">

```bash
mvn clean install
```

</div>

### مرحله ۲: افزودن به پروژه مصرف‌کننده

<div dir="ltr" align="left">

```xml

<dependency>
    <groupId>com.ernoxin</groupId>
    <artifactId>sep-java-sdk</artifactId>
    <version>1.1.0</version>
</dependency>
```

</div>

---

## پیکربندی

پیکربندی به‌صورت <span dir="ltr">fail-fast</span> انجام می‌شود: اگر مقدارهای اجباری ناقص باشند، برنامه در زمان بالا آمدن
متوقف می‌شود تا خطا به مرحله پرداخت نرسد.

### کلیدهای <span dir="ltr">application.properties</span>

| کلید                                                 | الزامی | پیش‌فرض                                          | توضیح                                                                                           |
|------------------------------------------------------|-------:|--------------------------------------------------|-------------------------------------------------------------------------------------------------|
| <span dir="ltr">`sep.enabled`</span>                 |    بله | <span dir="ltr">`false`</span>                   | برای ساخت beanهای Spring باید <span dir="ltr">`true`</span> باشد                                |
| <span dir="ltr">`sep.terminal-id`</span>             |    بله | -                                                | شماره ترمینال پذیرنده (فقط عدد)                                                                 |
| <span dir="ltr">`sep.callback-url`</span>            |    بله | -                                                | آدرس بازگشت پس از پرداخت، باید <span dir="ltr">http</span> یا <span dir="ltr">https</span> باشد |
| <span dir="ltr">`sep.base-url`</span>                |    خیر | <span dir="ltr">`https://sep.shaparak.ir`</span> | دامنه سرویس (در صورت دریافت آدرس تست از سپ تنظیم کنید)                                          |
| <span dir="ltr">`sep.timeout.connect`</span>         |    خیر | <span dir="ltr">`10s`</span>                     | مهلت اتصال                                                                                      |
| <span dir="ltr">`sep.timeout.read`</span>            |    خیر | <span dir="ltr">`30s`</span>                     | مهلت دریافت پاسخ                                                                                |
| <span dir="ltr">`sep.retry.enabled`</span>           |    خیر | <span dir="ltr">`false`</span>                   | فعال‌سازی تلاش مجدد در خطاهای شبکه                                                              |
| <span dir="ltr">`sep.retry.max-attempts`</span>      |    خیر | <span dir="ltr">`1`</span>                       | تعداد تلاش‌ها در صورت فعال بودن <span dir="ltr">`retry`</span>                                  |
| <span dir="ltr">`sep.retry.backoff`</span>           |    خیر | <span dir="ltr">`0ms`</span>                     | وقفه بین تلاش‌ها                                                                                |
| <span dir="ltr">`sep.http.user-agent`</span>         |    خیر | <span dir="ltr">`SepJavaSdk`</span>              | مقدار <span dir="ltr">User-Agent</span> در درخواست‌ها                                           |
| <span dir="ltr">`sep.min-token-expiry-in-min`</span> |    خیر | <span dir="ltr">`20`</span>                      | کمینه اعتبار توکن (دقیقه)                                                                       |
| <span dir="ltr">`sep.max-token-expiry-in-min`</span> |    خیر | <span dir="ltr">`3600`</span>                    | بیشینه اعتبار توکن (دقیقه)                                                                      |
| <span dir="ltr">`sep.max-settlement-items`</span>    |    خیر | <span dir="ltr">`9`</span>                       | حداکثر تعداد آیتم‌های تسهیم                                                                     |
| <span dir="ltr">`sep.max-hashed-card-count`</span>   |    خیر | <span dir="ltr">`10`</span>                      | حداکثر تعداد کارت‌های هش‌شده                                                                    |
| <span dir="ltr">`sep.max-res-num-length`</span>      |    خیر | <span dir="ltr">`50`</span>                      | حداکثر طول <span dir="ltr">ResNum</span> و پارامترهای اضافی                                     |

### <span dir="ltr">timeout</span> و <span dir="ltr">retry</span>

* <span dir="ltr">`sep.timeout.connect`</span> زمان برقراری اتصال است و شامل <span dir="ltr">TCP/SSL</span> می‌شود.
* <span dir="ltr">`sep.timeout.read`</span> زمان انتظار برای دریافت پاسخ پس از اتصال است.
* <span dir="ltr">retry</span> فقط روی خطاهای شبکه/ارتباطی فعال می‌شود و روی خطاهای منطقی درگاه یا کدهای پاسخ اجرا
  نمی‌شود.
* <span dir="ltr">`max-attempts`</span> تعداد کل تلاش‌ها و <span dir="ltr">`backoff`</span> فاصله بین تلاش‌ها را مشخص
  می‌کند.
* **هشدار:** فعال کردن <span dir="ltr">retry</span> برای دریافت توکن ممکن است باعث ایجاد چند توکن شود اگر درخواست اول در
  درگاه ثبت شده ولی پاسخ آن به شما نرسیده باشد.

فرمت مدت‌زمان‌ها می‌تواند به صورت <span dir="ltr">`500ms`</span>، <span dir="ltr">`2s`</span> یا <span dir="ltr">
`1m`</span> باشد.

### نمونه تنظیمات

حداقل تنظیمات لازم:

<div dir="ltr" align="left">

```properties
sep.enabled=true
sep.terminal-id=0000
sep.callback-url=https://example.com/payment/callback
```

</div>

---

## آموزش گام‌به‌گام پرداخت

1. تزریق کلاینت
2. دریافت توکن
3. انتقال خریدار به درگاه
4. بازگشت و وریفای تراکنش

### گام ۱: تزریق کلاینت

کلاینت به‌صورت خودکار ساخته می‌شود و کافی است آن را تزریق کنید:

<div dir="ltr" align="left">

```java
import com.ernoxin.sepjavasdk.client.SepClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final SepClient client;

}
```

</div>

### گام ۲: دریافت توکن

حداقل ورودی لازم شامل مبلغ و <span dir="ltr">ResNum</span> است. اگر <span dir="ltr">`sep.callback-url`</span> در تنظیمات
تعریف شده باشد، ارسال آن در هر درخواست ضروری نیست.

<div dir="ltr" align="left">

```java
TokenRequest request = TokenRequest.builder(12000, "ORDER-1001")
        .cellNumber("09120000000")
        .build();

TokenResult result = client.requestToken(request);
```

</div>

### گام ۳: انتقال خریدار به درگاه

<div dir="ltr" align="left">

```java
String redirectUrl = client.buildRedirectUrl(result.token());
```

</div>

در صورت نیاز به هدایت با <span dir="ltr">POST</span> می‌توانید از آدرس <span dir="ltr">/OnlinePG/SendToken</span>
استفاده کنید و توکن را در فرم ارسال نمایید.

### گام ۴: بازگشت و وریفای تراکنش

فقط وقتی وضعیت تراکنش <span dir="ltr">OK</span> است باید وریفای انجام شود.

**نکتهٔ امنیتی مهم:** برای جلوگیری از <span dir="ltr">Spending Double</span> مقدار <span dir="ltr">RefNum</span> را در
پایگاه‌داده ذخیره کنید و روی آن محدودیت یکتا (<span dir="ltr">unique constraint</span>) بگذارید؛ اگر <span dir="ltr">
RefNum</span> قبلاً ثبت شده بود، پیش از وریفای پردازش را متوقف کنید و نتیجه را «تأیید شده/تکراری» اعلام نمایید.

<div dir="ltr" align="left">

```java
public VerifyResult handleCallback(SepClient client, Map<String, String> params) {
    SepCallback callback = client.parseCallback(params);
    if (!callback.isOk()) {
        throw new IllegalStateException("پرداخت ناموفق یا لغو شده است");
    }

    Order order = orderService.findByResNum(callback.resNum());
    VerifyResult result = client.verifyTransaction(new VerifyRequest(callback.refNum()));

    Long verifiedAmount = result.transactionDetail().originalAmount();
    // For discount terminals, compare AffectiveAmount instead of OriginalAmount
    if (verifiedAmount == null || verifiedAmount != order.amount()) {
        client.reverseTransaction(new ReverseRequest(callback.refNum()));
        throw new IllegalStateException("مبلغ وریفای با مبلغ سفارش برابر نیست");
    }
    return result;
}
```

**امنیت:** <span dir="ltr">parseCallback</span> فقط پارامترها را می‌خواند و اصالت پرداخت را ثابت نمی‌کند. برای وضعیت OK
حتماً verify کنید و مبلغ/<span dir="ltr">resNum</span> را با دیتابیس سفارش خودتان تطبیق دهید.

</div>

---

## مدل‌ها و اعتبارسنجی

همه متدها <span dir="ltr">terminalId</span> را از تنظیمات می‌خوانند و در ورودی‌ها دریافت نمی‌کنند. در صورت نامعتبر بودن
داده‌ها، خطای <span dir="ltr">SepValidationException</span> قبل از ارسال درخواست رخ می‌دهد.

### <span dir="ltr">TokenRequest</span>

| فیلد (SDK)                                  | نوع                                             |           الزامی | توضیح                                                                                                                     |
|---------------------------------------------|-------------------------------------------------|-----------------:|---------------------------------------------------------------------------------------------------------------------------|
| <span dir="ltr">`amount`</span>             | <span dir="ltr">long</span>                     |              بله | مبلغ تراکنش؛ باید مثبت باشد.                                                                                              |
| <span dir="ltr">`resNum`</span>             | <span dir="ltr">String</span>                   |              بله | شناسه یکتا برای هر تراکنش؛ حداکثر ۵۰ کاراکتر.                                                                             |
| <span dir="ltr">`redirectUrl`</span>        | <span dir="ltr">URI</span>                      | خیر (در درخواست) | اگر مقدار ندهید از <span dir="ltr">`sep.callback-url`</span> استفاده می‌شود؛ باید <span dir="ltr">http/https</span> باشد. |
| <span dir="ltr">`cellNumber`</span>         | <span dir="ltr">String</span>                   |              خیر | شماره موبایل خریدار برای بازیابی کارت‌های ذخیره‌شده.                                                                      |
| <span dir="ltr">`wage`</span>               | <span dir="ltr">Long</span>                     |              خیر | کارمزد تراکنش (در پرداخت‌های تسهیمی).                                                                                     |
| <span dir="ltr">`tokenExpiryInMin`</span>   | <span dir="ltr">Integer</span>                  |              خیر | اعتبار توکن به دقیقه (بین ۲۰ تا ۳۶۰۰؛ خارج از بازه خطای اعتبارسنجی می‌دهد).                                               |
| <span dir="ltr">`hashedCardNumbers`</span>  | <span dir="ltr">List<String></span>             |              خیر | لیست کارت‌های هش‌شده (حداکثر ۱۰ مورد). مقادیر با <span dir="ltr">`\|`</span> ارسال می‌شوند.                               |
| <span dir="ltr">`getMethod`</span>          | <span dir="ltr">Boolean</span>                  |              خیر | در صورت <span dir="ltr">true</span> بازگشت از درگاه با <span dir="ltr">GET</span> انجام می‌شود.                           |
| <span dir="ltr">`resNum1..4`</span>         | <span dir="ltr">String</span>                   |              خیر | اطلاعات اضافی برای گزارش‌گیری (هرکدام حداکثر ۵۰ کاراکتر).                                                                 |
| <span dir="ltr">`tranType`</span>           | <span dir="ltr">SepTranType</span>              |              خیر | برای تراکنش دولتی با شناسه مقدار <span dir="ltr">`GOVERNMENT`</span> ارسال شود.                                           |
| <span dir="ltr">`settlementIbanInfo`</span> | <span dir="ltr">List<SettlementIbanInfo></span> |              خیر | تسویه به چند حساب؛ حداکثر ۹ آیتم.                                                                                         |

### <span dir="ltr">SettlementIbanInfo</span>

| فیلد (SDK)                          | نوع                           | الزامی | توضیح                                                                                      |
|-------------------------------------|-------------------------------|-------:|--------------------------------------------------------------------------------------------|
| <span dir="ltr">`iban`</span>       | <span dir="ltr">String</span> |    بله | شماره شبا ۲۶ کاراکتری که با <span dir="ltr">IR</span> شروع می‌شود.                         |
| <span dir="ltr">`amount`</span>     | <span dir="ltr">long</span>   |    بله | مبلغ تسهیم؛ باید مثبت باشد.                                                                |
| <span dir="ltr">`purchaseId`</span> | <span dir="ltr">String</span> |    بله | شناسه تسهیم. در ترکیب تسویه دولتی و عادی، برای موارد عادی مقدار ۳۰ کاراکتر صفر ارسال کنید. |

---

## سناریوهای رایج

### توکن با آدرس بازگشت اختصاصی

<div dir="ltr" align="left">

```java
TokenRequest request = TokenRequest.builder(12000, "INV-1002")
        .redirectUrl(URI.create("https://example.com/payment/callback"))
        .build();

TokenResult result = client.requestToken(request);
```

</div>

### پرداخت چند حسابی

<div dir="ltr" align="left">

```java
List<SettlementIbanInfo> settlements = List.of(
        new SettlementIbanInfo("IR111111111111111111111111", 7000, "12345678901234567890123456789"),
        new SettlementIbanInfo("IR222222222222222222222222", 4000, "12345678901234567890123456789")
);

TokenRequest request = TokenRequest.builder(12000, "ORDER-2001")
        .settlementIbanInfo(settlements)
        .build();

TokenResult result = client.requestToken(request);
```

</div>

### تراکنش دولتی با شناسه

<div dir="ltr" align="left">

```java
TokenRequest request = TokenRequest.builder(12000, "ORDER-2002")
        .tranType(SepTranType.GOVERNMENT)
        .settlementIbanInfo(List.of(
                new SettlementIbanInfo("IR111111111111111111111111", 12000, "12345678901234567890123456789")
        ))
        .build();

TokenResult result = client.requestToken(request);
```

</div>

### الزام کارت مشخص

<div dir="ltr" align="left">

```java
TokenRequest request = TokenRequest.builder(12000, "ORDER-2003")
        .hashedCardNumbers(List.of("BA0206CADCAB97F06AAE34AD3FDF1168"))
        .build();

TokenResult result = client.requestToken(request);
```

</div>

---

## تمام متدهای کلاینت

| متد                                         | ورودی                                                              | خروجی                                | نکته مهم                                                                   |
|---------------------------------------------|--------------------------------------------------------------------|--------------------------------------|----------------------------------------------------------------------------|
| <span dir="ltr">`requestToken`</span>       | <span dir="ltr">TokenRequest</span>                                | <span dir="ltr">TokenResult</span>   | ایجاد توکن پرداخت                                                          |
| <span dir="ltr">`buildRedirectUrl`</span>   | <span dir="ltr">token</span>                                       | <span dir="ltr">String</span>        | آدرس نهایی بر اساس <span dir="ltr">/OnlinePG/SendToken</span> ساخته می‌شود |
| <span dir="ltr">`parseCallback`</span>      | <span dir="ltr">Map</span> یا <span dir="ltr">MultiValueMap</span> | <span dir="ltr">SepCallback</span>   | خروجی شامل وضعیت و پارامترهای کلیدی بازگشت است                             |
| <span dir="ltr">`verifyTransaction`</span>  | <span dir="ltr">VerifyRequest</span>                               | <span dir="ltr">VerifyResult</span>  | تایید تراکنش با <span dir="ltr">RefNum</span>                              |
| <span dir="ltr">`reverseTransaction`</span> | <span dir="ltr">ReverseRequest</span>                              | <span dir="ltr">ReverseResult</span> | برگشت تراکنش (تا ۵۰ دقیقه پس از پرداخت)                                    |

---

## کدهای خطا

### توکن و وضعیت تراکنش

| کد | توضیح                            |
|---:|----------------------------------|
|  1 | انصراف کاربر                     |
|  2 | پرداخت موفق                      |
|  3 | پرداخت ناموفق                    |
|  4 | عدم پاسخ کاربر در زمان تعیین شده |
|  5 | پارامترهای نامعتبر               |
|  8 | آی‌پی پذیرنده نامعتبر            |
| 10 | توکن یافت نشد                    |
| 11 | توکن الزامی است                  |
| 12 | ترمینال یافت نشد                 |
| 21 | محدودیت‌های تسهیم رعایت نشده است |

### کدهای سرویس‌های <span dir="ltr">Verify</span> و <span dir="ltr">Reverse</span>

|   کد | توضیح                                    |
|-----:|------------------------------------------|
|    0 | موفق                                     |
|    2 | درخواست تکراری                           |
|   -2 | تراکنش یافت نشد                          |
|   -6 | بیش از ۳۰ دقیقه از زمان تراکنش گذشته است |
| -104 | ترمینال غیرفعال است                      |
| -105 | ترمینال در سیستم موجود نیست              |
| -106 | آی‌پی درخواست نامعتبر است                |
|    5 | تراکنش برگشت خورده است                   |

---

## <span dir="ltr">Exception</span>های <span dir="ltr">SDK</span>

* <span dir="ltr">`SepValidationException`</span>: ورودی با قوانین معتبر نیست و قبل از ارسال به درگاه خطا گرفته می‌شود
* <span dir="ltr">`SepApiException`</span>: درگاه پاسخ خطا داده است و شامل کد و پیام درگاه است
* <span dir="ltr">`SepTransportException`</span>: خطای شبکه یا تایم‌اوت در ارتباط با درگاه
* <span dir="ltr">`SepCallbackException`</span>: پارامترهای بازگشتی ناقص یا نامعتبر هستند

---

## پرسش‌های پرتکرار

* چرا دریافت توکن خطای اعتبارسنجی می‌دهد؟ بررسی کنید <span dir="ltr">terminal-id</span> و <span dir="ltr">
  callback-url</span> مقدار دارند و <span dir="ltr">ResNum</span> خالی نیست.
* چرا وریفای خطای <span dir="ltr">-2</span> می‌دهد؟ مقدار <span dir="ltr">RefNum</span> نامعتبر است یا تراکنش یافت نشده
  است.
* چرا ریورس خطای <span dir="ltr">-106</span> می‌دهد؟ آی‌پی سرور در پنل ثبت نشده است.
* چرا پرداخت موفق بوده اما باز هم خطا دریافت می‌کنم؟ ابتدا نتیجه <span dir="ltr">verify</span> را بررسی کنید و مقدار
  مبلغ را با فاکتور تطبیق دهید.

</div>
