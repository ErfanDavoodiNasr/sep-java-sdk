<div dir="rtl" align="right">

<h1><span dir="ltr">SDK</span> جاوا درگاه سپ (SEP)</h1>

<h2>معرفی</h2>
<p>این کتابخانه یک <span dir="ltr">SDK</span> سبک برای اتصال به درگاه پرداخت اینترنتی سپ در پروژه‌های <span dir="ltr">Spring Boot 3.5.7</span> است. هدف آن ساده‌سازی دریافت توکن، اعتبارسنجی زودهنگام و دریافت پاسخ‌های تایپ‌شده است.</p>
<p>این پروژه یک کتابخانه است و برنامه اجرایی ندارد؛ آن را در پروژه خود استفاده می‌کنید تا روی منطق کسب‌وکار تمرکز کنید.</p>

<h2>پیش‌نیازها</h2>
<ul>
  <li><span dir="ltr">Java 21</span></li>
  <li><span dir="ltr">Spring Boot 3.5.7</span></li>
</ul>

<h2>نصب</h2>

<h3>مرحله ۱: ساخت و نصب محلی</h3>
<p>ابتدا کتابخانه را در مخزن محلی <span dir="ltr">Maven</span> نصب کنید.</p>
<div dir="ltr" align="left">
<pre><code class="language-bash">mvn clean install</code></pre>
</div>

<h3>مرحله ۲: افزودن به پروژه مصرف‌کننده</h3>
<div dir="ltr" align="left">
<pre><code class="language-xml">&lt;dependency&gt;
  &lt;groupId&gt;com.ernoxin&lt;/groupId&gt;
  &lt;artifactId&gt;sep-java-sdk&lt;/artifactId&gt;
  &lt;version&gt;1.0.3&lt;/version&gt;
&lt;/dependency&gt;
</code></pre>
</div>

<h2>پیکربندی</h2>
<p>پیکربندی به‌صورت <span dir="ltr">fail-fast</span> انجام می‌شود: اگر مقدارهای اجباری ناقص باشند، برنامه در زمان بالا آمدن متوقف می‌شود تا خطا به مرحله پرداخت نرسد.</p>

<h3>کلیدهای <span dir="ltr">application.properties</span></h3>
<table>
  <thead>
    <tr>
      <th>کلید</th>
      <th>الزامی</th>
      <th>پیش‌فرض</th>
      <th>توضیح</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code dir="ltr">sep.terminal-id</code></td>
      <td>بله</td>
      <td>-</td>
      <td>شماره ترمینال پذیرنده (فقط عدد)</td>
    </tr>
    <tr>
      <td><code dir="ltr">sep.callback-url</code></td>
      <td>بله</td>
      <td>-</td>
      <td>آدرس بازگشت پس از پرداخت، باید <span dir="ltr">http</span> یا <span dir="ltr">https</span> باشد</td>
    </tr>
    <tr>
      <td><code dir="ltr">sep.base-url</code></td>
      <td>خیر</td>
      <td><code dir="ltr">https://sep.shaparak.ir</code></td>
      <td>دامنه سرویس (در صورت دریافت آدرس تست از سپ تنظیم کنید)</td>
    </tr>
    <tr>
      <td><code dir="ltr">sep.timeout.connect</code></td>
      <td>خیر</td>
      <td><code dir="ltr">10s</code></td>
      <td>مهلت اتصال</td>
    </tr>
    <tr>
      <td><code dir="ltr">sep.timeout.read</code></td>
      <td>خیر</td>
      <td><code dir="ltr">30s</code></td>
      <td>مهلت دریافت پاسخ</td>
    </tr>
    <tr>
      <td><code dir="ltr">sep.retry.enabled</code></td>
      <td>خیر</td>
      <td><code dir="ltr">false</code></td>
      <td>فعال‌سازی تلاش مجدد در خطاهای شبکه</td>
    </tr>
    <tr>
      <td><code dir="ltr">sep.retry.max-attempts</code></td>
      <td>خیر</td>
      <td><code dir="ltr">1</code></td>
      <td>تعداد تلاش‌ها در صورت فعال بودن <code dir="ltr">retry</code></td>
    </tr>
    <tr>
      <td><code dir="ltr">sep.retry.backoff</code></td>
      <td>خیر</td>
      <td><code dir="ltr">0ms</code></td>
      <td>وقفه بین تلاش‌ها</td>
    </tr>
    <tr>
      <td><code dir="ltr">sep.http.user-agent</code></td>
      <td>خیر</td>
      <td><code dir="ltr">SepJavaSdk</code></td>
      <td>مقدار <span dir="ltr">User-Agent</span> در درخواست‌ها</td>
    </tr>
    <tr>
      <td><code dir="ltr">sep.min-token-expiry-in-min</code></td>
      <td>خیر</td>
      <td><code dir="ltr">20</code></td>
      <td>کمینه اعتبار توکن (دقیقه)</td>
    </tr>
    <tr>
      <td><code dir="ltr">sep.max-token-expiry-in-min</code></td>
      <td>خیر</td>
      <td><code dir="ltr">3600</code></td>
      <td>بیشینه اعتبار توکن (دقیقه)</td>
    </tr>
    <tr>
      <td><code dir="ltr">sep.max-settlement-items</code></td>
      <td>خیر</td>
      <td><code dir="ltr">9</code></td>
      <td>حداکثر تعداد آیتم‌های تسهیم</td>
    </tr>
    <tr>
      <td><code dir="ltr">sep.max-hashed-card-count</code></td>
      <td>خیر</td>
      <td><code dir="ltr">10</code></td>
      <td>حداکثر تعداد کارت‌های هش‌شده</td>
    </tr>
    <tr>
      <td><code dir="ltr">sep.max-res-num-length</code></td>
      <td>خیر</td>
      <td><code dir="ltr">50</code></td>
      <td>حداکثر طول <span dir="ltr">ResNum</span> و پارامترهای اضافی</td>
    </tr>
  </tbody>
</table>

<h3><span dir="ltr">timeout</span> و <span dir="ltr">retry</span></h3>
<ul>
  <li><span dir="ltr">sep.timeout.connect</span> زمان برقراری اتصال است و شامل <span dir="ltr">TCP/SSL</span> می‌شود.</li>
  <li><span dir="ltr">sep.timeout.read</span> زمان انتظار برای دریافت پاسخ پس از اتصال است.</li>
  <li><span dir="ltr">retry</span> فقط روی خطاهای شبکه/ارتباطی فعال می‌شود و روی خطاهای منطقی درگاه یا کدهای پاسخ اجرا نمی‌شود.</li>
  <li><span dir="ltr">max-attempts</span> تعداد کل تلاش‌ها و <span dir="ltr">backoff</span> فاصله بین تلاش‌ها را مشخص می‌کند.</li>
  <li>هشدار: فعال کردن <span dir="ltr">retry</span> برای دریافت توکن ممکن است باعث ایجاد چند توکن شود اگر درخواست اول در درگاه ثبت شده ولی پاسخ آن به شما نرسیده باشد.</li>
</ul>

<p>فرمت مدت‌زمان‌ها می‌تواند به صورت <code dir="ltr">500ms</code>، <code dir="ltr">2s</code> یا <code dir="ltr">1m</code> باشد.</p>

<h3>نمونه تنظیمات</h3>
<p>حداقل تنظیمات لازم:</p>
<div dir="ltr" align="left">
<pre><code class="language-properties">sep.terminal-id=0000
sep.callback-url=https://example.com/payment/callback</code></pre>
</div>

<h2>آموزش گام‌به‌گام پرداخت</h2>
<ol>
  <li>تزریق کلاینت</li>
  <li>دریافت توکن</li>
  <li>انتقال خریدار به درگاه</li>
  <li>بازگشت و وریفای تراکنش</li>
</ol>

<h3>گام ۱: تزریق کلاینت</h3>
<p>کلاینت به‌صورت خودکار ساخته می‌شود و کافی است آن را تزریق کنید.</p>
<div dir="ltr" align="left">
<pre><code class="language-java">import com.ernoxin.sepjavasdk.client.SepClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
private final SepClient client;
}
</code></pre>
</div>

<h3>گام ۲: دریافت توکن</h3>
<p>حداقل ورودی لازم شامل مبلغ و <span dir="ltr">ResNum</span> است. اگر <code dir="ltr">sep.callback-url</code> در تنظیمات تعریف شده باشد، ارسال آن در هر درخواست ضروری نیست.</p>
<div dir="ltr" align="left">
<pre><code class="language-java">TokenRequest request = TokenRequest.builder(12000, "ORDER-1001")
        .cellNumber("09120000000")
        .build();

TokenResult result = client.requestToken(request);
</code></pre>
</div>

<h3>گام ۳: انتقال خریدار به درگاه</h3>
<div dir="ltr" align="left">
<pre><code class="language-java">String redirectUrl = client.buildRedirectUrl(result.token());
</code></pre>
</div>
<p>در صورت نیاز به هدایت با <span dir="ltr">POST</span> می‌توانید از آدرس <span dir="ltr">/OnlinePG/SendToken</span> استفاده کنید و توکن را در فرم ارسال نمایید.</p>

<h3>گام ۴: بازگشت و وریفای تراکنش</h3>
<p>فقط وقتی وضعیت تراکنش <span dir="ltr">OK</span> است باید وریفای انجام شود.</p>
<p><strong>نکتهٔ امنیتی مهم:</strong> برای جلوگیری از <span dir="ltr">Spending Double</span> مقدار <span dir="ltr">RefNum</span> را در پایگاه‌داده ذخیره کنید و روی آن محدودیت یکتا (unique constraint) بگذارید؛ اگر <span dir="ltr">RefNum</span> قبلاً ثبت شده بود، پیش از وریفای پردازش را متوقف کنید و نتیجه را «تأیید شده/تکراری» اعلام نمایید.</p>
<div dir="ltr" align="left">
<pre><code class="language-java">public VerifyResult handleCallback(SepClient client, Map&lt;String, String&gt; params) {
    SepCallback callback = client.parseCallback(params);
    if (!callback.isOk()) {
        throw new IllegalStateException("پرداخت ناموفق یا لغو شده است");
    }

    Order order = orderService.findByResNum(callback.resNum());
    VerifyResult result = client.verifyTransaction(new VerifyRequest(callback.refNum()));

    long verifiedAmount = result.transactionDetail().originalAmount();
    // For discount terminals, compare AffectiveAmount instead of OriginalAmount
    if (verifiedAmount != order.amount()) {
        client.reverseTransaction(new ReverseRequest(callback.refNum()));
        throw new IllegalStateException("مبلغ وریفای با مبلغ سفارش برابر نیست");
    }
    return result;
}
</code></pre>
</div>

<h2>مدل‌ها و اعتبارسنجی</h2>
<p>همه متدها <span dir="ltr">terminalId</span> را از تنظیمات می‌خوانند و در ورودی‌ها دریافت نمی‌کنند. در صورت نامعتبر بودن داده‌ها، خطای <span dir="ltr">SepValidationException</span> قبل از ارسال درخواست رخ می‌دهد.</p>

<h3><span dir="ltr">TokenRequest</span></h3>
<table>
  <thead>
    <tr>
      <th>فیلد (SDK)</th>
      <th>نوع</th>
      <th>الزامی</th>
      <th>توضیح</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code dir="ltr">amount</code></td>
      <td>long</td>
      <td>بله</td>
      <td>مبلغ تراکنش؛ باید مثبت باشد.</td>
    </tr>
    <tr>
      <td><code dir="ltr">resNum</code></td>
      <td>String</td>
      <td>بله</td>
      <td>شناسه یکتا برای هر تراکنش؛ حداکثر ۵۰ کاراکتر.</td>
    </tr>
    <tr>
      <td><code dir="ltr">redirectUrl</code></td>
      <td>URI</td>
      <td>خیر (در درخواست)</td>
      <td>اگر مقدار ندهید از <code dir="ltr">sep.callback-url</code> استفاده می‌شود؛ باید <span dir="ltr">http/https</span> باشد.</td>
    </tr>
    <tr>
      <td><code dir="ltr">cellNumber</code></td>
      <td>String</td>
      <td>خیر</td>
      <td>شماره موبایل خریدار برای بازیابی کارت‌های ذخیره‌شده.</td>
    </tr>
    <tr>
      <td><code dir="ltr">wage</code></td>
      <td>Long</td>
      <td>خیر</td>
      <td>کارمزد تراکنش (در پرداخت‌های تسهیمی).</td>
    </tr>
    <tr>
      <td><code dir="ltr">tokenExpiryInMin</code></td>
      <td>Integer</td>
      <td>خیر</td>
      <td>اعتبار توکن به دقیقه (بین ۲۰ تا ۳۶۰۰؛ مقادیر خارج از بازه به کمینه/بیشینه تبدیل می‌شود).</td>
    </tr>
    <tr>
      <td><code dir="ltr">hashedCardNumbers</code></td>
      <td>List&lt;String&gt;</td>
      <td>خیر</td>
      <td>لیست کارت‌های هش‌شده (حداکثر ۱۰ مورد). مقادیر با <span dir="ltr">|</span> ارسال می‌شوند.</td>
    </tr>
    <tr>
      <td><code dir="ltr">getMethod</code></td>
      <td>Boolean</td>
      <td>خیر</td>
      <td>در صورت <span dir="ltr">true</span> بازگشت از درگاه با <span dir="ltr">GET</span> انجام می‌شود.</td>
    </tr>
    <tr>
      <td><code dir="ltr">resNum1..4</code></td>
      <td>String</td>
      <td>خیر</td>
      <td>اطلاعات اضافی برای گزارش‌گیری (هرکدام حداکثر ۵۰ کاراکتر).</td>
    </tr>
    <tr>
      <td><code dir="ltr">tranType</code></td>
      <td><span dir="ltr">SepTranType</span></td>
      <td>خیر</td>
      <td>برای تراکنش دولتی با شناسه مقدار <code dir="ltr">GOVERNMENT</code> ارسال شود.</td>
    </tr>
    <tr>
      <td><code dir="ltr">settlementIbanInfo</code></td>
      <td>List&lt;SettlementIbanInfo&gt;</td>
      <td>خیر</td>
      <td>تسویه به چند حساب؛ حداکثر ۹ آیتم.</td>
    </tr>
  </tbody>
</table>

<h3><span dir="ltr">SettlementIbanInfo</span></h3>
<table>
  <thead>
    <tr>
      <th>فیلد (SDK)</th>
      <th>نوع</th>
      <th>الزامی</th>
      <th>توضیح</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code dir="ltr">iban</code></td>
      <td>String</td>
      <td>بله</td>
      <td>شماره شبا ۲۶ کاراکتری که با <span dir="ltr">IR</span> شروع می‌شود.</td>
    </tr>
    <tr>
      <td><code dir="ltr">amount</code></td>
      <td>long</td>
      <td>بله</td>
      <td>مبلغ تسهیم؛ باید مثبت باشد.</td>
    </tr>
    <tr>
      <td><code dir="ltr">purchaseId</code></td>
      <td>String</td>
      <td>بله</td>
      <td>شناسه تسهیم. در ترکیب تسویه دولتی و عادی، برای موارد عادی مقدار ۳۰ کاراکتر صفر ارسال کنید.</td>
    </tr>
  </tbody>
</table>

<h2>سناریوهای رایج</h2>

<h3>توکن با آدرس بازگشت اختصاصی</h3>
<div dir="ltr" align="left">
<pre><code class="language-java">TokenRequest request = TokenRequest.builder(12000, "INV-1002")
        .redirectUrl(URI.create("https://example.com/payment/callback"))
        .build();

TokenResult result = client.requestToken(request);
</code></pre>
</div>

<h3>پرداخت چند حسابی</h3>
<div dir="ltr" align="left">
<pre><code class="language-java">List&lt;SettlementIbanInfo&gt; settlements = List.of(
        new SettlementIbanInfo("IR111111111111111111111111", 7000, "12345678901234567890123456789"),
        new SettlementIbanInfo("IR222222222222222222222222", 4000, "12345678901234567890123456789")
);

TokenRequest request = TokenRequest.builder(12000, "ORDER-2001")
.settlementIbanInfo(settlements)
.build();

TokenResult result = client.requestToken(request);
</code></pre>
</div>

<h3>تراکنش دولتی با شناسه</h3>
<div dir="ltr" align="left">
<pre><code class="language-java">TokenRequest request = TokenRequest.builder(12000, "ORDER-2002")
        .tranType(SepTranType.GOVERNMENT)
        .settlementIbanInfo(List.of(
                new SettlementIbanInfo("IR111111111111111111111111", 12000, "12345678901234567890123456789")
        ))
        .build();

TokenResult result = client.requestToken(request);
</code></pre>
</div>

<h3>الزام کارت مشخص</h3>
<div dir="ltr" align="left">
<pre><code class="language-java">TokenRequest request = TokenRequest.builder(12000, "ORDER-2003")
        .hashedCardNumbers(List.of("BA0206CADCAB97F06AAE34AD3FDF1168"))
        .build();

TokenResult result = client.requestToken(request);
</code></pre>
</div>

<h2>تمام متدهای کلاینت</h2>
<table>
  <thead>
    <tr>
      <th>متد</th>
      <th>ورودی</th>
      <th>خروجی</th>
      <th>نکته مهم</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code dir="ltr">requestToken</code></td>
      <td><code dir="ltr">TokenRequest</code></td>
      <td><code dir="ltr">TokenResult</code></td>
      <td>ایجاد توکن پرداخت</td>
    </tr>
    <tr>
      <td><code dir="ltr">buildRedirectUrl</code></td>
      <td><code dir="ltr">token</code></td>
      <td><code dir="ltr">String</code></td>
      <td>آدرس نهایی بر اساس <span dir="ltr">/OnlinePG/SendToken</span> ساخته می‌شود</td>
    </tr>
    <tr>
      <td><code dir="ltr">parseCallback</code></td>
      <td><code dir="ltr">Map</code> یا <code dir="ltr">MultiValueMap</code></td>
      <td><code dir="ltr">SepCallback</code></td>
      <td>خروجی شامل وضعیت و پارامترهای کلیدی بازگشت است</td>
    </tr>
    <tr>
      <td><code dir="ltr">verifyTransaction</code></td>
      <td><code dir="ltr">VerifyRequest</code></td>
      <td><code dir="ltr">VerifyResult</code></td>
      <td>تایید تراکنش با <span dir="ltr">RefNum</span></td>
    </tr>
    <tr>
      <td><code dir="ltr">reverseTransaction</code></td>
      <td><code dir="ltr">ReverseRequest</code></td>
      <td><code dir="ltr">ReverseResult</code></td>
      <td>برگشت تراکنش (تا ۵۰ دقیقه پس از پرداخت)</td>
    </tr>
  </tbody>
</table>

<h2>کدهای خطا</h2>

<h3>توکن و وضعیت تراکنش</h3>
<table>
  <thead>
    <tr>
      <th>کد</th>
      <th>توضیح</th>
    </tr>
  </thead>
  <tbody>
    <tr><td>1</td><td>انصراف کاربر</td></tr>
    <tr><td>2</td><td>پرداخت موفق</td></tr>
    <tr><td>3</td><td>پرداخت ناموفق</td></tr>
    <tr><td>4</td><td>عدم پاسخ کاربر در زمان تعیین شده</td></tr>
    <tr><td>5</td><td>پارامترهای نامعتبر</td></tr>
    <tr><td>8</td><td>آی‌پی پذیرنده نامعتبر</td></tr>
    <tr><td>10</td><td>توکن یافت نشد</td></tr>
    <tr><td>11</td><td>توکن الزامی است</td></tr>
    <tr><td>12</td><td>ترمینال یافت نشد</td></tr>
    <tr><td>21</td><td>محدودیت‌های تسهیم رعایت نشده است</td></tr>
  </tbody>
</table>

<h3>کدهای سرویس‌های Verify و Reverse</h3>
<table>
  <thead>
    <tr>
      <th>کد</th>
      <th>توضیح</th>
    </tr>
  </thead>
  <tbody>
    <tr><td>0</td><td>موفق</td></tr>
    <tr><td>2</td><td>درخواست تکراری</td></tr>
    <tr><td>-2</td><td>تراکنش یافت نشد</td></tr>
    <tr><td>-6</td><td>بیش از ۳۰ دقیقه از زمان تراکنش گذشته است</td></tr>
    <tr><td>-104</td><td>ترمینال غیرفعال است</td></tr>
    <tr><td>-105</td><td>ترمینال در سیستم موجود نیست</td></tr>
    <tr><td>-106</td><td>آی‌پی درخواست نامعتبر است</td></tr>
    <tr><td>5</td><td>تراکنش برگشت خورده است</td></tr>
  </tbody>
</table>

<h2>Exceptionهای SDK</h2>
<ul>
  <li><code dir="ltr">SepValidationException</code>: ورودی با قوانین معتبر نیست و قبل از ارسال به درگاه خطا گرفته می‌شود</li>
  <li><code dir="ltr">SepApiException</code>: درگاه پاسخ خطا داده است و شامل کد و پیام درگاه است</li>
  <li><code dir="ltr">SepTransportException</code>: خطای شبکه یا تایم‌اوت در ارتباط با درگاه</li>
  <li><code dir="ltr">SepCallbackException</code>: پارامترهای بازگشتی ناقص یا نامعتبر هستند</li>
</ul>

<h2>پرسش‌های پرتکرار</h2>
<ul>
  <li>چرا دریافت توکن خطای اعتبارسنجی می‌دهد؟ بررسی کنید <span dir="ltr">terminal-id</span> و <span dir="ltr">callback-url</span> مقدار دارند و <span dir="ltr">ResNum</span> خالی نیست.</li>
  <li>چرا وریفای خطای <span dir="ltr">-2</span> می‌دهد؟ مقدار <span dir="ltr">RefNum</span> نامعتبر است یا تراکنش یافت نشده است.</li>
  <li>چرا ریورس خطای <span dir="ltr">-106</span> می‌دهد؟ آی‌پی سرور در پنل ثبت نشده است.</li>
  <li>چرا پرداخت موفق بوده اما باز هم خطا دریافت می‌کنم؟ ابتدا نتیجه <span dir="ltr">verify</span> را بررسی کنید و مقدار مبلغ را با فاکتور تطبیق دهید.</li>
</ul>

</div>
