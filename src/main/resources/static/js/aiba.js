document.addEventListener("DOMContentLoaded", function() {



    // ==============================
    // CHECK RESULT
    // ==============================
    document.getElementById("checkResultBtn").addEventListener("click", function() {

        const regNo = document.getElementById("registrationNo").value.trim();
        const email = document.getElementById("email").value.trim();
        const dob = document.getElementById("dob").value;
        const captcha = document.getElementById("captchaInput").value.trim();
        const type = 'AIBA';

        if (!regNo || !email || !dob || !captcha) {
            showAlert("Please fill all fields");
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

                    showAlert(data.message || "Invalid CAPTCHA...!");

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
			        <h5>MBA (AI & BA) 2026–28: Admission Offer Status</h5>
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
                        html += `
                    
                        <p>Congratulations!</p>
                    
                        <p>With reference to your application for admission to IIM Jammu, we are pleased to
                        inform you that you have been selected for admission in the first year of the Master
                        of Business Administration (AI & BA) (MBA (AI & BA)) <b>2026-28</b> of IIM Jammu commencing from <b>July 2026</b>.</p>
                    
                        <p>You are required to pay the Offer Acceptance Amount of <b>INR 50,000/-</b> through online
                        portal latest by <b>5:30 PM by May’ 2026</b>, failing which the offer will stand
                        withdrawn. This amount will be adjusted against the fee payable by you for the first
                        term of the programme.</p>
                    
                        <p>If you subsequently wish to withdraw from the offer within the withdrawal date (dd/mm/yyyy) specified by
                        the Institute, the Offer Acceptance Amount will be refunded after deducting <b>Rs. 1,000</b>
                        as processing charges (the refund of offer fee will be made to the same account source through which the payment was made via transaction reversal process only after the admission cycle for <b>2026</b> is over, tentatively by <b>September 2026</b>). The candidate can withdraw the offer by <b>5:30 PM on 19th June 2026</b>.
                        If a candidate applies for withdrawal after the date specified by the Institute, no
                        refund will be admissible.</p>
                    
                        <p>Your admission will be valid subject to your fulfilling the eligibility conditions stipulated
                        in the <b>CAT-2025 Bulletin</b> and any other conditions conveyed to you through our
                        website communication or other means. You have been offered provisional
                        admission on the basis of the information provided by you, which is subject to
                        verification of original educational transcripts, work experience certificates and category certificate (if applicable) at the
                        time of registration. If at any stage, any information provided by you is found incorrect
                        or false, your admission will be cancelled with immediate effect.</p>
                    
                        <p>You need to read the following instructions carefully and follow the steps accordingly
                        to complete the acceptance process.</p>
                    
                        <p>Visit the link to make the payment of the Offer Acceptance Amount of <b>INR 50,000/-</b>
                        (please do not close the IIM Jammu login page): Click for Payment
                        Click for Payment MBA (AI & BA)</p>
                    
                        <p>The page is an exclusive page of J&K Bank for payment of IIM Jammu fee.
                        You will have to select “Offer Acceptance Deposit” from the scroll down and enter
                        the captcha and press the Continue button to get to the next page (The Payment
                        Page).</p>
                    
                        <p>Provide the information as provided on the CAT Application Form on the payment
                        page followed by the submit button.</p>
                    
                        <p>Follow the payment instructions on the page and complete the payment process.
                        You need to save the receipt generated for your records. It will contain a receipt
                        number which would be required to complete the offer acceptance process.</p>
                    
                        <p>Post the payment, please go back to IIM Jammu login window and provide the
                        receipt number and click on the submit button.</p>
                    
                        <p>For additional queries/ withdrawal of the acceptance, please write to <b>admissions@iimj.ac.in</b></p>
                    
                        <p>For update information, you may visit our website <b>http://www.iimj.ac.in/</b>.</p>
                    
                        <p><b>We look forward to having you with us at IIM Jammu.</b></p>
    `;
                        break;

                    case 2:
                        html += `
                         <h5 class="mb-3">MBA (AI & BA) 2026–28: Admission Status</h5>

                        <p><b>Greetings from IIM Jammu!</b></p>
                        
                        <p>
                           With reference to your provisional admission offer for MBA (AI & BA) and
                           subsequent payment of the Offer Acceptance fee for MBA (AI & BA)
                           <b>Rs. 50,000/-</b>, the registration will take place in the month of
                           <b>July 2026</b>. You are, therefore, advised to follow the instruction given
                           below:
                        </p>
                        
                        <ol>
                           <li>
                              <b>First Term Fees Payment:</b> First Term Fee for MBA (AI & BA) is
                              <b>Rs. _____________</b> plus caution deposit of <b>Rs. 20,000</b>. You
                              are required to deposit the balance amount <b>Rs. XXXXXXX</b> (after
                              adjusting acceptance fee of <b>Rs. 50,000/-</b>) as Term 1 Fee on or
                              before <b>May 21st, 2026, 05:30 PM</b>.
                           </li>
                        
                           <li>
                              <b>Online Payment:</b> Check the payment link at the admission Portal and
                              proceed for the payment (Click on the link named:
                              <b>MBA (AI & BA) Term-I Fees and Caution Deposit -AY 2025-27</b>).
                           </li>
                        
                           <li>
                              <b>Automatic Cancellation:</b> If you fail to make online payment before
                              <b>5:30 pm, 21st May 2026</b>, your provisional admission offer shall
                              automatically stand cancelled.
                           </li>
                        
                           <li>
                              <b>Registration:</b> Registration for the Term - 1 will take place in
                              <b>July 2026</b>. The list of documents/certificate required at the time
                              of registration will be shared after the payment of Term-1 fee (Keep the
                              original copy with you for physical verification).
                           </li>
                        
                           <li>
                              <b>Loan facility:</b> List of Banks, who provide educational loan to the
                              candidates, is available at:<br />
                              <b>https://www.iimj.ac.in/MBA (AI & BA)-loan-facility.php</b><br />
                              However, you are free to take loan from any bank. IIM Jammu has nothing to
                              do with any bank for providing educational loan. This is between the
                              candidate & Bank and IIM Jammu is not party to it.
                           </li>
                        
                           <li>
                              <b>Legal issues:</b> All disputes arising out of, or in respect of
                              admissions to the MBA (AI & BA) programme shall be referred for the
                              arbitration to the Director of IIM Jammu, or his nominee, whose decision
                              shall be final and binding on the parties. All matters shall be subject to
                              <b>Jammu jurisdiction only</b>.
                           </li>
                        
                           <li>
                              <b>Important:</b> This is a provisional admission subject to the physical
                              verification of the documents submitted as per the eligibility criteria
                              mentioned in IIM Jammu MBA (AI & BA) <b>2026-28</b> Admission Policy and
                              <b>CAT 2025</b> / Ministry of Education, Govt. of India Guidelines.
                           </li>
                        </ol>
                        
                        <p>
                           You may write to the admission office of IIM Jammu at
                           <b>admissions@iimj.ac.in</b> for any clarification and assistance.
                        </p>
                        
                        <p>
                           Please ignore this mail if you initiated the process of Offer withdrawal or
                           if you plan to withdraw the offer.
                        </p>

                         `;
                        break;

                    case 3:

                        html += `
						<p>We have received your term- 1 fee for MBA (AI & BA) 2026 program.</p>
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
                            <h5 class="mb-3">MBA (AI & BA) 2026–28: Admission Status</h5>
                        
                            <p>You have been <b>wait listed</b> for the IIM Jammu <b>MBA (AI & BA) 2026 programme</b>. IIM Jammu will come up with another merit list after the offer acceptance date of the declared merit list.</p>
                        
                            <p><b>Admissions Office,<br>IIM Jammu</b></p>
                        
                            <p><b>Note:</b> The next merit list may or may not be released. The wait list doesn’t indicate in any way that the admission offer will be made to the candidate. The movement of wait listed candidate to merit list depends on the number of seats and the decline of provisional admissions offer by the selected candidates.</p>
                        
                            <p>Please address all queries regarding admissions to <b>admissions@iimj.ac.in</b></p>
                         `;
                        break;
                    case 8:
                        html += `
                        <h5 class="mb-3">MBA (AI & BA) 2026–28: Admission Status</h5>
                    
                        <p>We are pleased to let you know that you are in the <b>advanced waitlist</b> for the Admission to Master of Business Administration Programme (AI & BA) (MBA (AI & BA)) of Indian Institute of Management Jammu, Batch <b>2026-28</b>, subject to the fulfilment of all the eligibility criteria. It may be noted that IIM Jammu is merely inviting your interest in taking admission into the MBA (AI & BA) program of IIM Jammu and shouldn’t be viewed as an offer of admission. You will get a formal provisional admission offer from IIM Jammu only if candidates above you in our admission merit list withdraw or don’t avail admission offer of IIM Jammu.</p>
                    
                        <p>Please confirm your acceptance of this <b>EOI</b> immediately by paying the EOI fees of <b>Rs. 10,000/-</b> latest by <b>5:30 PM, 14th May 2026</b>. Your EOI will be accepted only on receipt of the EOI fee of <b>Rs. 10,000/-</b> through the online payment link given below:</p>
                    
                        <p><b>PAYMENT LINK</b></p>
                    
                        <p>Failing to pay EOI fees, this EOI offer will automatically stand withdrawn without any further communication to you, and your name will be further removed from the waitlist. The entire WL-EOI fees of <b>Rs. 10,000/-</b> will be refunded.</p>
                    
                        <p><b>Terms and conditions of Expression of Interest (EOI) will be as given below.</b></p>
                    
                        <ol>
                            <li>Candidates who are in the waitlist will be asked to confirm the Expression of Interest (EoI) by accepting the EoI online and by depositing <b>INR 10,000</b>.</li>
                    
                            <li>Those who will not deposit the EoI amount will be removed from the Waitlist for further process.</li>
                    
                            <li>Candidates can ask for the removal from the Waitlist any time. The refund of <b>INR 10,000</b> will be made without any deduction. Once the candidate is removed from the waitlist, he/she cannot claim the waitlist position or offer in that list.</li>
                    
                            <li>As per the movement of the waitlist and vacancy, candidates (who have deposited <b>INR 10,000</b> as EoI amount) will be offered the provisional admission offer in the subsequent list.</li>
                    
                            <li>Under no circumstances, offers will be made to the candidates who have not accepted the EoI and not deposited <b>INR 10,000</b>.</li>
                    
                            <li>The candidate who has deposited <b>INR 10,000</b> as EoI amount and receives the provisional admission offer, will be asked to deposit <b>INR 50,000</b> as acceptance fee. This fee will be adjusted in the term I fee.</li>
                    
                            <li>If the candidate deposits <b>INR 50,000</b> (in addition to <b>INR 10,000</b>) and withdraws before due date <b>INR 59,000</b> will be refunded after deducting <b>INR 1,000</b>.</li>
                    
                            <li>The EOI fee will be refunded to the same account source through which the payment was made via transaction reversal process only after the admission cycle for <b>2026</b> is over (tentatively by <b>September 2026</b>).</li>
                        </ol>
                    
                        <p>Once the candidate deposits <b>INR 50,000</b> (in addition to <b>INR 10,000</b>) towards the offer acceptance fee, the prevailing offer withdrawal policy would be applicable similar to other candidates.</p>
                    
                        <p>In case of any clarifications, please reply to us over email at <b>MBA (AI & BA).admissions@iimj.ac.in</b>.</p>
    `;
                        break;

                    case 9:
                        html += `
                        <h5 class="mb-3">MBA (AI & BA) 2026–28: Admission Status</h5>
                    
                        <p>We have received your <b>EOI fee</b> for <b>MBA (AI & BA) Program, Batch 2026-28</b>.</p>
                    
                        <p>You will get a formal provisional admission offer from IIM Jammu only if candidates above you in our admission merit list withdraw or don’t avail admission offer of IIM Jammu.</p>
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
                    '<div class="alert alert-danger">No candidate data found...!!!</div>';
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
                type: "AIBA"

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
// SHOW ALERT BUTTON
// ==============================
function showAlert(message) {
    document.getElementById("alertMessage").innerText = message;

    const modal = new bootstrap.Modal(document.getElementById("customAlertModal"));
    modal.show();
}


// ==============================
// BACK BUTTON
// ==============================
function showFormAgain() {
    document.getElementById("mainContainer").style.display = "block";
    document.getElementById("resultSection").innerHTML = "";
    document.getElementById("backButtonContainer").style.display = "none";
}

