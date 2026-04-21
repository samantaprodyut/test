document.addEventListener("DOMContentLoaded", function () {

    // ==============================
    // CHECK RESULT
    // ==============================
    document.getElementById("checkResultBtn").addEventListener("click", function () {

        const regNo = document.getElementById("registrationNo").value.trim();
        const email = document.getElementById("email").value.trim();
        const dob = document.getElementById("dob").value;
        const captcha = document.getElementById("captchaInput").value.trim();

        if (!regNo || !email || !dob || !captcha) {
            alert("Please fill all fields");
            return;
        }

        document.getElementById("resultSection").innerHTML =
            '<div class="text-center mt-4"><div class="spinner-border"></div></div>';

        const url = `/api/result?regNo=${encodeURIComponent(regNo)}&email=${encodeURIComponent(email)}&dob=${encodeURIComponent(dob)}&captcha=${encodeURIComponent(captcha)}`;

        fetch(url, {
            method: "GET",
            credentials: "include" // ✅ IMPORTANT FOR SESSION
        })
        .then(res => res.json())
        .then(data => {

            // ==============================
            // CAPTCHA / ERROR HANDLING
            // ==============================
            if (data.success === false) {

                alert(data.message || "Invalid CAPTCHA");

                // refresh captcha
                document.getElementById("captchaImage").src =
                    "/captcha?ts=" + new Date().getTime();

                // clear input
                document.getElementById("captchaInput").value = "";

                // remove loader
                document.getElementById("resultSection").innerHTML = "";

                return; // ⛔ STOP
            }

            // ==============================
            // SHOW RESULT
            // ==============================
            document.getElementById("mainContainer").style.display = "none";
            document.getElementById("backButtonContainer").style.display = "block";

            let html = `
            <div class="letter-container">
                <div class="letter-title">
                    MBA 2025-27 Batch: Admission Offer Status
                </div>

                <p><b>Name:</b> ${data.fullName || '-'}</p>
                <p><b>CAT Registration Number:</b> ${data.candidate.registrationNo || '-'}</p>
                <p><b>Email:</b> ${data.candidate.email || '-'}</p>
                <p><b>Category:</b> ${data.candidate.category || '-'}</p>
				<p><b>Test:</b> ${data.candidate.paymentDeadline || '-'}</p>

                <br/>
                <p>Dear ${data.candidate.fullName || 'Candidate'},</p>
            `;

            switch (data.statusId) {

                case 1:
                    if (data.isPaid) {
                        html += `
                        <div class="alert alert-success text-center mt-3">
                            Payment Already Submitted. Your Payment is under verification!
                        </div>`;
                    } else {
                        html += `
                        <p><b>Congratulations!</b></p>

                        <div id="paymentForm">
                            <input class="form-control mb-2" id="trxId" placeholder="Transaction ID">
                            <input class="form-control mb-2" id="bankName" placeholder="Bank Name">
                            <input class="form-control mb-2" id="amount" value="${data.amountDue || 50000}">

                            <button class="btn btn-primary w-100" id="submitPaymentBtn">
                                Submit Payment
                            </button>
                        </div>`;
                    }
                    break;

                case 8:
                    html += `
                    <p>EOI Payment Required</p>

                    <div id="paymentForm">
                        <input class="form-control mb-2" id="trxId" placeholder="Transaction ID">
                        <input class="form-control mb-2" id="bankName" placeholder="Bank Name">
                        <input class="form-control mb-2" id="amount" value="10000">

                        <button class="btn btn-warning w-100" id="submitPaymentBtn">
                            Submit EOI Payment
                        </button>
                    </div>`;
                    break;

                default:
                    html += `<p>Entered Details are not available. Please enter proper details.</p>`;
            }

            html += `</div>`;

            document.getElementById("resultSection").innerHTML = html;
        })
        .catch(err => {
            console.error(err);
            document.getElementById("resultSection").innerHTML =
                '<div class="alert alert-danger">Error fetching result</div>';
        });
    });

    // ==============================
    // CAPTCHA REFRESH
    // ==============================
    document.getElementById("refreshCaptchaBtn").addEventListener("click", function () {
        document.getElementById("captchaImage").src =
            "/captcha?ts=" + new Date().getTime();
    });

});


// ==============================
// PAYMENT SUBMIT
// ==============================
document.addEventListener("click", function (e) {

    if (e.target && e.target.id === "submitPaymentBtn") {

        const trxId = document.getElementById("trxId").value.trim();
        const bank = document.getElementById("bankName").value.trim();
        const amount = document.getElementById("amount").value;
		const regNo = document.getElementById("registrationNo").value.trim();
		const email = document.getElementById("email").value.trim();
		const dob = document.getElementById("dob").value;
        if (!trxId || !bank || !amount) {
            alert("Please fill all payment details");
            return;
        }

        const btn = e.target;
        btn.disabled = true;
        btn.innerText = "Submitting...";

        fetch("/api/payment", {
            method: "POST",
            credentials: "include", // ✅ GOOD PRACTICE
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                transactionId: trxId,
                bankName: bank,
                amount: amount,
                registrationNo: regNo,
				email: email,
				dob: dob


            })
        })
        .then(res => res.json())
        .then(response => {

            if (response.success === true) {
                showOverlay("success", response.message || "Payment completed successfully");
            } else {
                showOverlay("failed", response.message || "Payment failed");
                btn.disabled = false;
                btn.innerText = "Submit Payment";
            }
        })
        .catch(err => {
            console.error(err);
            alert("Server error");
            btn.disabled = false;
            btn.innerText = "Submit Payment";
        });
    }
});


// ==============================
// OVERLAY FUNCTION
// ==============================
function showOverlay(status, message) {

    const overlay = document.getElementById("successOverlay");
    const title = document.getElementById("overlayTitle");
    const msg = document.getElementById("overlayMessage");
    const icon = document.getElementById("overlayIcon");

    overlay.classList.remove("overlay-success", "overlay-failed");

    if (status === "success") {
        title.innerText = "Payment Successful";
        icon.innerText = "✔";
        overlay.classList.add("overlay-success");
    } else {
        title.innerText = "Payment Failed";
        icon.innerText = "✖";
        overlay.classList.add("overlay-failed");
    }

    msg.innerText = message;

    overlay.classList.remove("overlay-hidden");
    overlay.classList.add("overlay-show");
}


// ==============================
// OK BUTTON → REDIRECT
// ==============================
document.addEventListener("click", function (e) {
    if (e.target && e.target.id === "okRedirectBtn") {
        window.location.href = "/";
    }
});


// ==============================
// BACK BUTTON
// ==============================
function showFormAgain() {
    document.getElementById("mainContainer").style.display = "block";
    document.getElementById("resultSection").innerHTML = "";
    document.getElementById("backButtonContainer").style.display = "none";
}