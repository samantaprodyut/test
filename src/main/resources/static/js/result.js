document.addEventListener("DOMContentLoaded", () => {
    // result button
    document.getElementById("checkResultBtn")
        .addEventListener("click", checkResult);

    // captcha refresh button
    document.getElementById("refreshCaptchaBtn")
        .addEventListener("click", refreshCaptcha);
});

// refresh typed captcha image
function refreshCaptcha() {
    const img = document.getElementById("captchaImage");
    img.src = "/captcha?ts=" + new Date().getTime();

    document.getElementById("captchaInput").value = "";
}

// Check candidate result
function checkResult() {
    const regNo = document.getElementById("registrationNo").value;
    const email = document.getElementById("email").value;
    const dob = document.getElementById("dob").value;
	const captchaToken = grecaptcha.getResponse();
	const captcha = document.getElementById("captchaInput").value;



    if (!regNo || !email || !dob) {
        alert("Please fill all fields");
        return;
    }

	if (!captcha || !captchaToken) {
	       alert("Please complete captcha verification");
	       return;
	   }
	
    // Hide login form
    document.getElementById("checkResultForm").style.display = "none";

	fetch(`/api/result?regNo=${encodeURIComponent(regNo)}
	&email=${encodeURIComponent(email)}
	&dob=${encodeURIComponent(dob)}
	&captcha=${encodeURIComponent(captcha)}
	&captchaToken=${encodeURIComponent(captchaToken)}`.replace(/\s+/g, ''))
        .then(res => res.json())
        .then(data => {
            console.log("Backend response:", data);

            let html = '';

            if (data.alreadyPaid) {
                // User has already submitted payment
                html = `<div class="alert alert-success">
                            Dear ${data.fullName}, your payment details have been submitted and are under verification.
                        </div>`;
            } else {
                // Normal flow
                html = `<div class="alert alert-info">
                            Dear ${data.fullName}, ${data.message}
                        </div>`;

                if (data.showPaymentForm) {
                    html += `
                        <button class="btn btn-success mb-3" id="payNowBtn">Pay Now</button>
                        <div id="paymentForm" style="margin-top:15px;">
                            <form id="paymentDetailsForm">
                                <input type="text" class="form-control mb-3" id="trxId" placeholder="Transaction ID" required>
                                <input type="text" class="form-control mb-3" id="bankName" placeholder="Bank Name" required>
                                <input type="number" class="form-control mb-3" id="amount" placeholder="Amount" value="${data.amountDue}" required>
                                <button type="button" class="btn btn-primary w-100" id="submitPaymentBtn">Submit Payment</button>
                            </form>
                        </div>
                    `;
                }
            }

            document.getElementById("resultSection").innerHTML = html;

            if (!data.alreadyPaid && data.showPaymentForm) {
                document.getElementById("payNowBtn").addEventListener("click", () => redirectToPayment(regNo));
                document.getElementById("submitPaymentBtn").addEventListener("click", () => submitPayment(regNo));
            }
        })
        .catch(err => {
            console.error(err);
            document.getElementById("resultSection").innerHTML = `<div class="alert alert-danger">Error fetching result</div>`;
        });
}

// Open external payment URL
function redirectToPayment(regNo) {
    const paymentUrl = `https://externalgateway.com/pay?regNo=${regNo}`;
    window.open(paymentUrl, "_blank");
}


// Submit manual payment to backend
function submitPayment(regNo) {
    const trxId = document.getElementById("trxId").value;
    const bankName = document.getElementById("bankName").value;
    const amount = document.getElementById("amount").value;

    if (!trxId || !bankName || !amount) {
        alert("Please fill all payment details");
        return;
    }

    fetch('/api/payment', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({regNo, trxId, bankName, amount})
    })
    .then(res => res.json())
    .then(data => {
        // Show payment confirmation
        document.getElementById("resultSection").innerHTML = `
            <div class="alert alert-success">Dear ${data.fullName}, ${data.message}</div>
        `;
    })
    .catch(err => {
        alert("Payment submission failed");
        console.error(err);
    });
}