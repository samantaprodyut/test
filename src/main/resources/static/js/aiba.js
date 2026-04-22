document.addEventListener("DOMContentLoaded", function () {

    // ==============================
    // CHECK RESULT
    // ==============================
    document.getElementById("checkResultBtn").addEventListener("click", function () {

        const regNo = document.getElementById("registrationNo").value.trim();
        const email = document.getElementById("email").value.trim();
        const dob = document.getElementById("dob").value;
        const captcha = document.getElementById("captchaInput").value.trim();
		const type = 'AIBA';

        if (!regNo || !email || !dob || !captcha) {
            alert("Please fill all fields");
            return;
        }

        document.getElementById("resultSection").innerHTML =
            '<div class="text-center mt-4"><div class="spinner-border"></div></div>';

		const url = `/api/result?regNo=${encodeURIComponent(regNo)}&email=${encodeURIComponent(email)}&dob=${encodeURIComponent(dob)}&captcha=${encodeURIComponent(captcha)}&type=${encodeURIComponent(type)}`;
      
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
			<div class="card shadow p-4 letter-container">

			    <div class="text-center mb-3">
			        <h5>AIBA 2025-27 Batch: Admission Offer Status</h5>
			    </div>

				<div class="mb-3">
				    <h6><b>CANDIDATE DETAILS</b></h6>

				    <table class="table table-bordered table-sm">
				        <tr>
				            <th style="width:25%">Name</th>
				            <td style="width:25%">${data.candidate.fullName || '-'}</td>
				            <th style="width:25%">Category</th>
				            <td style="width:25%">${data.candidate.category || '-'}</td>
				        </tr>
				        <tr>
				            <th>CAT Registration No.</th>
				            <td>${data.candidate.registrationNo || '-'}</td>
				            <th>PwD</th>
				            <td>${data.candidate.pwd || '-'}</td>
				        </tr>
				        <tr>
				            <th>Email</th>
				            <td>${data.candidate.email || '-'}</td>
				            <th>Deadline</th>
				            <td>${data.candidate.paymentDeadline || '9th May 2025'}</td>
				        </tr>
				    </table>
				</div>

			    <hr/>

				<p><b>Dear ${data.candidate.fullName || 'Candidate'},</b></p>
			`;

			let content = '';

			switch (data.candidate.status.id) {

			    case 1:
			        if (data.isPaid) {
			            content = `
			            <div class="alert alert-success text-center">
			                Offer Acceptance Fee Submitted. Payment under verification.
			            </div>`;
			        } 					else {
										html += `
										<div>
										    <p><b>Congratulations!</b></p>

										    <p>
										        With reference to your application for admission to <b>IIM Jammu</b>, 
										        we are pleased to inform you that you have been selected for admission 
										        to the <b>Master of Business Administration (MBA) 2025–27</b> batch, 
										        commencing from <b>July 2025</b>.
										    </p>

										    <p>
										        You are required to pay the <b>Offer Acceptance Amount of ₹50,000</b> 
										        through the online portal on or before 
										        <b>5:30 PM, May 2025</b>, failing which the offer will stand withdrawn.
										        This amount will be adjusted against your first-term fees.
										    </p>

										    <p>
										        If you wish to withdraw before <b>18 June 2025 (5:30 PM)</b>, 
										        the amount will be refunded after deducting ₹1,000 as processing charges. 
										        No refund will be admissible after this date.
										    </p>

										    <p>
										        Your admission is <b>provisional</b> and subject to verification of 
										        original documents (academic transcripts and work experience). 
										        Any discrepancy may lead to cancellation of admission.
										    </p>

										    <hr>

										    <h6><b>Steps to Complete Offer Acceptance</b></h6>
										    <ul>
										        <li>Click on the payment link (J&K Bank portal).</li>
										        <li>Select <b>"Offer Acceptance Deposit"</b>.</li>
										        <li>Enter details as per CAT Application Form.</li>
										        <li>Complete payment and save the receipt.</li>
										        <li>Enter receipt number in the portal and submit.</li>
										        <li>Download, print, sign, and send the Offer Acceptance Form to Admission Office.</li>
										    </ul>

										    <div class="alert alert-info mt-3">
										        For queries, contact: <b>admissions@iimj.ac.in</b><br>
										        Website: <b>www.iimj.ac.in</b>
										    </div>

										    <hr>

										    <h6><b>Submit Payment Details</b></h6>
										    <div id="paymentForm">
										        <input class="form-control mb-2" id="trxId" placeholder="Transaction / Receipt ID">
										        <input class="form-control mb-2" id="bankName" placeholder="Bank Name">
										        <input class="form-control mb-2" id="amount" value="50000" readonly>

										        <button class="btn btn-primary w-100" id="submitPaymentBtn">
										            Submit Payment
										        </button>
										    </div>
										</div>
										`;
							        }
							        break;

									case 2:
									    content = `
									    <p>
									        You have been <b>waitlisted</b> for the MBA 2025 programme at <b>IIM Jammu</b>.
									        The Institute may release another merit list after the offer acceptance deadline
									        of the current list.
									    </p>

									    <p>
									        <b>Note:</b> The next merit list may or may not be released. Being on the waitlist
									        does not guarantee an admission offer. Movement from the waitlist depends on
									        seat availability and the number of candidates declining their provisional offers.
									    </p>
									    `;
									    break;

			    case 3:
			        content = `
			        <div class="alert alert-success">
			            Your admission is confirmed. Fee received successfully.
			        </div>`;
			        break;

			    case 4:
			        content = `
			        <div class="alert alert-danger">
			            Your admission offer has expired.
			        </div>`;
			        break;

			    case 5:
			        content = `
			        <div class="alert alert-warning">
			            You were absent for the PI Process.
			        </div>`;
			        break;

			    case 6:
			        content = `
			        <div class="alert alert-danger">
			            You are not selected in CAP/SAP.
			        </div>`;
			        break;

			    case 7:
			        content = `
			        <div class="alert alert-info">
			            You did not opt for IIM Jammu MBA Programme.
			        </div>`;
			        break;

			    case 8:
			        if (data.isPaid) {
			            content = `
			            <div class="alert alert-success">
			                EOI Payment Submitted. Await further updates.
			            </div>`;
			        } else {
			            content = `
			            <p>You are in the <b>advanced waitlist</b>.</p>
			            <p>Please pay <b>₹10,000</b> as EOI fee.</p>

			            <div id="paymentForm">
			                <input class="form-control mb-2" id="trxId" placeholder="Transaction ID">
			                <input class="form-control mb-2" id="bankName" placeholder="Bank Name">
			                <input class="form-control mb-2" id="amount" value="10000" readonly>

			                <button class="btn btn-warning w-100" id="submitPaymentBtn">
			                    Submit EOI Payment
			                </button>
			            </div>`;
			        }
			        break;

			    case 9:
			        content = `
			        <div class="alert alert-success">
			            EOI fee received. You are in advanced waitlist.
			        </div>`;
			        break;

			    default:
			        content = `
			        <div class="alert alert-secondary">
			            Status not available. Please contact admissions.
			        </div>`;
			}

			// append status content
			html += content;

			// ✅ footer INSIDE same box
			html += `
			    <hr/>

			    <p><b>Admissions Office,</b><br/>IIM Jammu</p>

			    <p style="font-size:12px; color:gray;">
			        Disclaimer: This electronically generated information does not have any legal sanctity.
			        In case of discrepancy, final records will prevail.
			    </p>

			</div>
			`;

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
				dob: dob,
				type:"AIBA"

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