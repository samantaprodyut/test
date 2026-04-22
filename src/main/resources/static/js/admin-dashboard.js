document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("uploadForm");
    const modal = new bootstrap.Modal(document.getElementById("uploadModal"));

    form.addEventListener("submit", async function (e) {
        e.preventDefault();

        modal.show();

        try {
            const response = await fetch(form.action, {
                method: "POST",
                body: new FormData(form)
            });

            console.log("STATUS:", response.status);

            const text = await response.text();
            console.log("RESPONSE:", text);

            modal.hide();

            if (response.ok) {
                alert("SUCCESS: " + text);
            } else {
                alert("FAILED: " + text);
            }

        } catch (err) {
            modal.hide();
            console.error("FETCH ERROR:", err);
            alert("FETCH ERROR: " + err.message);
        }
    });

});