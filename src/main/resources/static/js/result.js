document.addEventListener("DOMContentLoaded", function() {

    // ==============================
    // CHECK RESULT
    // ==============================
    document.getElementById("checkResultBtn").addEventListener("click", function() {

        const regNo = document.getElementById("registrationNo").value.trim();
        const email = document.getElementById("email").value.trim();
        const dob = document.getElementById("dob").value;
        const captcha = document.getElementById("captchaInput").value.trim();
        const type = 'MBA';

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
			        <h5>MBA 2026-28 Batch: Admission Offer Status</h5>
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
							<td>${data.candidate.pwd  ? 'Yes' : 'No'}</td>
				        </tr>
				        <tr>
				            <th>Registered Email ID</th>
				            <td>${data.candidate.email || '-'}</td>
				            <th>Status</th>
				            <td>${data.candidate.status.name || '-'}</td>
				        </tr>
				    </table>
				</div>

			    <hr/>

				<p><b>Dear ${data.candidate.fullName || 'Candidate'},</b></p>
			`;

                let content = '';

                switch (data.candidate.status.id) {

                    case 1:
                        //   if (data.isPaid) {
                        //        content = `
                        //         <div class="alert alert-success text-center">
                        //           Offer Acceptance Fee Submitted. Payment under verification.
                        //     </div>`;
                        // } 					else {
                        //   }
                        html += `
						<p><b>Congratulations!</b></p>

						    <p>
						    With reference to your application for admission to IIM Jammu, we are pleased to
						    inform you that you have been selected for admission in the first year of the Master
						    of Business Administration (MBA) 2026-28 of IIM Jammu commencing from July
						    2026.
						    </p>

						    <p>
						    You are required to pay the Offer Acceptance Amount of INR 50,000/- through online
						    portal latest by 5:30 PM by May’ 2026, failing which the offer will stand
						    withdrawn. This amount will be adjusted against the fee payable by you for the first
						    term of the programme.
						    </p>

						    <p>
						    If you subsequently wish to withdraw from the offer within the withdrawal date (dd/mm/yyyy) specified by
						    the Institute, the Offer Acceptance Amount will be refunded after deducting Rs. 1,000
						    as processing charges (the refund of offer fee will be made to the same account source through which the payment was made via transaction reversal process only after the admission cycle for 2026 is over, tentatively by September 2026). The candidate can withdraw the offer by 5:30 PM on 19th June 2026. If a candidate applies for withdrawal after the date specified by the Institute, no
						    refund will be admissible.
						    </p>

						    <p>
						    Your admission will be valid subject to your fulfilling the eligibility conditions stipulated
						    in the CAT-2025 Bulletin and any other conditions conveyed to you through our
						    website communication or other means. You have been offered provisional
						    admission on the basis of the information provided by you, which is subject to
						    verification of original educational transcripts, work experience certificates and category certificate (if applicable) at the
						    time of registration. If at any stage, any information provided by you is found incorrect
						    or false, your admission will be cancelled with immediate effect.
						    </p>

						    <p>
						    You need to read the following instructions carefully and follow the steps accordingly
						    to complete the acceptance process.
						    </p>

						    <ul>
						        <li>
						        Visit the link to make the payment of the Offer Acceptance Amount of INR 50,000/- (please do not close the IIM Jammu login page):
						        <br>
						        <a href="/payment-link" target="_blank"><b>Click for Payment</b></a>
						       
						        </li>

						        <li>
						        The page is an exclusive page of J&K Bank for payment of IIM Jammu fee.
						        </li>

						        <li>
						        You will have to select “Offer Acceptance Deposit” from the scroll down and enter
						        the captcha and press the Continue button to get to the next page (The Payment Page).
						        </li>

						        <li>
						        Provide the information as provided on the CAT Application Form on the payment
						        page followed by the submit button.
						        </li>

						        <li>
						        Follow the payment instructions on the page and complete the payment process.
						        You need to save the receipt generated for your records. It will contain a receipt
						        number which would be required to complete the offer acceptance process.
						        </li>

						        <li>
						        Post the payment, please go back to IIM Jammu login window and provide the
						        receipt number and click on the submit button.
						        </li>

						        <li>
						        For additional queries/ withdrawal of the acceptance, please write to admissions@iimj.ac.in
						        </li>
						    </ul>

						    <p>
						    For update information, you may visit our website http://www.iimj.ac.in/.
						    </p>

						    <p>
						    We look forward to having you with us at IIM Jammu.
						    </p>
					       `;
                        break;

                    case 2:
                        html += ` 
						<div>
						    <p><b>Greetings from IIM Jammu!</b></p>

						    <p>
						        With reference to your provisional admission offer for MBA and subsequent payment of the Offer Acceptance fee
						        for MBA Rs. 50,000/-, the registration will take place in the month of July 2026. You are, therefore, advised to
						        follow the instruction given below:
						    </p>

						    <ol>
						        <li>
						            First Term Fees Payment: First Term Fee for MBA is Rs. _____________ plus caution deposit of Rs. 20,000. You
						            are required to deposit the balance amount Rs. XXXXXXX (after adjusting acceptance fee of Rs. 50,000/-) as
						            Term 1 Fee on or before May 21st, 2026, 05:30 PM.
						        </li>

						        <li>
						            Online Payment: Check the payment link at the admission Portal and proceed for the payment (Click on the
						            link named: MBA Term-I Fees and Caution Deposit -AY 2025-27).
						        </li>

						        <li>
						            Automatic Cancellation: If you fail to make online payment before 5:30 pm, 21st May 2026, your provisional
						            admission offer shall automatically stand cancelled.
						        </li>

						        <li>
						            Registration: Registration for the Term - 1 will take place in July 2026 . The list of documents/certificate
						            required at the time of registration will be shared after the payment of Term-1 fee (Keep the original copy
						            with you for physical verification).
						        </li>

						        <li>
						            Loan facility: List of Banks, who provide educational loan to the candidates, is available at <br />
						            : https://www.iimj.ac.in/mba-loan-facility.php
						            <br /><br />
						            However, you are free to take loan from any bank. IIM Jammu has nothing to do with any bank for providing
						            educational loan. This is between the candidate & Bank and IIM Jammu is not party to it.
						        </li>

						        <li>
						            Legal issues: All disputes arising out of, or in respect of admissions to the MBA programme shall be
						            referred for the arbitration to the Director of IIM Jammu, or his nominee, whose decision shall be final and
						            binding on the parties. All matters shall be subject to Jammu jurisdiction only.
						        </li>

						        <li>
						            Important: This is a provisional admission subject to the physical verification of the documents submitted
						            as per the eligibility criteria mentioned in IIM Jammu MBA 2026- 28 Admission Policy and CAT 2025 /Ministry
						            of Education, Govt. of India Guidelines.
						        </li>
						    </ol>

						    <p>
						        You may write to the admission office of IIM Jammu at admissions@iimj.ac.in for any clarification and
						        assistance.
						    </p>

						    <p>
						        Please ignore this mail if you initiated the process of Offer withdrawal or if you plan to withdraw the offer.
						    </p>
						</div>

						 `;
                        break;

                    case 3:

                        html += `
						<p>We have received your term- 1 fee for MBA 2026 program.</p>
						`;
                        break;
                    case 4: html += ` 
							<p> Your provisional admission offer has expired. </p> 
							`;
                        break;

                    case 5: html += ` <p> To be used if required. </p> `;
                        break;

                    case 6: html += ` <p> To be used if required. </p> `;
                        break;


                    case 7:

                        html += `
						<p>
						    You have been wait listed for the IIM Jammu MBA 2026 programme. IIM Jammu will come up with another merit list after
						    the offer acceptance date of the declared merit list.
						</p>

						<p>
						    Admissions Office,<br />
						    IIM Jammu
						</p>

						<p>
						    <b>Note:</b> The next merit list may or may not be released. The wait list doesn’t indicate in any way that the
						    admission offer will be made to the candidate. The movement of wait listed candidate to merit list depends on the
						    number of seats and the decline of provisional admissions offer by the selected candidates.
						</p>

						<p>Please address all queries regarding admissions to admissions@iimj.ac.in</p>

						`;
                        break;
                    case 8:

                        if (data.isPaid) {

                            html += `
							<p>We have received your EOI fee for MBA Program, Batch 2026-28.</p>
							`;
                        } else {

                            html += `

							<p>
							We are pleased to let you know that you are in the advanced waitlist for the Admission to Master of Business Administration Programme (MBA) of Indian Institute of Management Jammu, Batch 2026-28, subject to the fulfilment of all the eligibility criteria. It may be noted that IIM Jammu is merely inviting your interest in taking admission into the MBA program of IIM Jammu and shouldn’t be viewed as an offer of admission. You will get a formal provisional admission offer from IIM Jammu only if candidates above you in our admission merit list withdraw or don’t avail admission offer of IIM Jammu.
							</p>

							<p>
							Please confirm your acceptance of this EOI immediately by paying the EOI fees of Rs. 10,000/- latest by 5:30 PM, 14th May 2026. Your EOI will be accepted only on receipt of the EOI fee of Rs. 10,000/- through the online payment link given below:
							</p>

							<p>
							    <a href="/payment-link" target="_blank"><b>PAYMENT LINK</b></a>
							</p>

							<p>
							Failing to pay EOI fees, this EOI offer will automatically stand withdrawn without any further communication to you, and your name will be further removed from the waitlist. The entire WL-EOI fees of Rs. 10,000/- will be refunded.
							</p>

							<p>
							Terms and conditions of Expression of Interest (EOI) will be as given below.
							</p>

							<ol>
							    <li> Candidates who are in the waitlist will be asked to confirm the Expression of Interest (EoI) by accepting the EoI online and by depositing INR 10,000.</li>

							    <li> Those who will not deposit the EoI amount will be removed from the Waitlist for further process.</li>

							    <li> Candidates can ask for the removal from the Waitlist any time. The refund of INR 10,000 will be made without any deduction. Once the candidate is removed from the waitlist, he/she cannot claim the waitlist position or offer in that list.</li>

							    <li> As per the movement of the waitlist and vacancy, candidates (who have deposited INR 10,000 as EoI amount) will be offered the provisional admission offer in the subsequent list.</li>

							    <li> Under no circumstances, offers will be made to the candidates who have not accepted the EoI and not deposited INR 10,000.</li>

							    <li> The candidate who has deposited INR 10,000 as EoI amount and receives the provisional admission offer, will be asked to deposit INR 50,000 as acceptance fee. This fee will be adjusted in the term I fee.</li>

							    <li> If the candidate deposits INR 50,000 (in addition to INR 10,000) and withdraws before due date INR 59,000 will be refunded after deducting INR 1,000.</li>

							    <li> The EOI fee will be refunded to the same account source through which the payment was made via transaction reversal process only after the admission cycle for 2026 is over (tentatively by September 2026)</li>
							</ol>

							<p>
							Once the candidate deposits INR 50,000 (in addition to INR 10,000) towards the offer acceptance fee, the prevailing offer withdrawal policy would be applicable similar to other candidates.
							</p>

							<p>
							In case of any clarifications, please reply to us over email at mba.admissions@iimj.ac.in .
							</p>
							`;
                        }
                        break;

                    case 9:

                        html += `
						<p>We have received your EOI fee for MBA Program, Batch 2026-28.</p>

						<p>
						    You will get a formal provisional admission offer from IIM Jammu only if candidates above you in our admission merit
						    list withdraw or don’t avail admission offer of IIM Jammu.
						</p>
						`;
                        break;

                    default:
                        html += `
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
    document.getElementById("refreshCaptchaBtn").addEventListener("click", function() {
        document.getElementById("captchaImage").src =
            "/captcha?ts=" + new Date().getTime();
    });

});


// ==============================
// PAYMENT SUBMIT
// ==============================
document.addEventListener("click", function(e) {

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
                type: "MBA"

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
document.addEventListener("click", function(e) {
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

