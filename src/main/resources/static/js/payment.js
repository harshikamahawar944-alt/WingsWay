const bookingData = JSON.parse(localStorage.getItem("latestBooking") || "null");
const selectedFlightData = JSON.parse(localStorage.getItem("selectedFlight") || "null");
const selectedSeats = JSON.parse(localStorage.getItem("selectedSeats") || "[]");
const paymentStatus = document.getElementById("payment-status");
const confirmationCard = document.getElementById("confirmationCard");
const paymentPanel = document.getElementById("paymentPanel");

function updateBookingDetails() {
  if (!bookingData || !selectedFlightData || !selectedSeats.length) {
    paymentStatus.textContent = "Missing booking details. Please choose a flight and seats first.";
    return false;
  }

  document.getElementById("airline").textContent = selectedFlightData.airline;
  document.getElementById("flight-number").textContent = selectedFlightData.flightNumber;
  document.getElementById("route").textContent = `${selectedFlightData.start} to ${selectedFlightData.end}`;
  document.getElementById("class-type").textContent = bookingData.classType;
  document.getElementById("departure-time").textContent = `${selectedFlightData.departureTime} - ${selectedFlightData.arrivalTime}`;
  document.getElementById("seat-list").textContent = selectedSeats.join(", ");
  document.getElementById("ticket-count").textContent = bookingData.bnumofseat;
  document.getElementById("ticket-price").textContent = `INR ${Number(selectedFlightData.price).toLocaleString()}`;
  return true;
}

function handleExpiredSession(message) {
  paymentStatus.textContent = message || "Your session expired. Please log in again.";
  window.authUtils?.clearAuth();
  window.setTimeout(() => {
    window.location.href = "/api/v1/auth/login";
  }, 1200);
}

function buildConfirmation(cardHolder, bookingReference) {
  const confirmation = {
    bookingReference,
    passengerName: cardHolder,
    airline: selectedFlightData.airline,
    flightNumber: selectedFlightData.flightNumber,
    route: `${selectedFlightData.start} to ${selectedFlightData.end}`,
    classType: bookingData.classType,
    departureTime: `${selectedFlightData.departureTime} - ${selectedFlightData.arrivalTime}`,
    seats: selectedSeats.join(", "),
    price: `INR ${Number(selectedFlightData.price).toLocaleString()}`
  };
  localStorage.setItem("bookingConfirmation", JSON.stringify(confirmation));

  document.getElementById("confirmationMessage").textContent = `Booking reference ${bookingReference} is ready for travel.`;
  document.getElementById("printableTicket").innerHTML = `
    <p><strong>Passenger:</strong> ${confirmation.passengerName}</p>
    <p><strong>Booking Ref:</strong> ${confirmation.bookingReference}</p>
    <p><strong>Airline:</strong> ${confirmation.airline}</p>
    <p><strong>Flight:</strong> ${confirmation.flightNumber}</p>
    <p><strong>Route:</strong> ${confirmation.route}</p>
    <p><strong>Class:</strong> ${confirmation.classType}</p>
    <p><strong>Departure:</strong> ${confirmation.departureTime}</p>
    <p><strong>Seats:</strong> ${confirmation.seats}</p>
    <p><strong>Total Paid:</strong> ${confirmation.price}</p>
  `;
}

document.getElementById("payment-form").addEventListener("submit", async function (event) {
  event.preventDefault();

  const jwtToken = localStorage.getItem("jwtToken");
  if (!jwtToken || window.authUtils?.isTokenExpired(jwtToken)) {
    handleExpiredSession("Please log in again before making a payment.");
    return;
  }

  window.authUtils?.syncAuthCookie();

  paymentStatus.textContent = "Processing payment...";
  const localReference = `JK${Date.now().toString().slice(-8)}`;

  try {
    const response = await fetch("/api/v1/demo-controller/checkPrice/addBooking", {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${jwtToken}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        ...bookingData,
        airline: selectedFlightData.airline,
        flightNumber: selectedFlightData.flightNumber,
        aircraftModel: selectedFlightData.aircraftModel,
        departureTime: selectedFlightData.departureTime,
        arrivalTime: selectedFlightData.arrivalTime,
        seatNumbers: selectedSeats.join(", "),
        price: selectedFlightData.price,
        bookingReference: localReference
      }),
    });

    if (response.status === 401 || response.status === 403 || response.redirected) {
      handleExpiredSession("Your login session expired. Please log in again.");
      return;
    }

    const bookingReference = await response.text();
    if (!response.ok) {
      paymentStatus.textContent = bookingReference || "Unable to complete booking.";
      return;
    }

    buildConfirmation(document.getElementById("card-holder").value, bookingReference || localReference);
    paymentPanel.hidden = true;
    confirmationCard.hidden = false;
    paymentStatus.textContent = "";
    localStorage.removeItem("latestFlightResults");
  } catch (error) {
    paymentStatus.textContent = `Unable to make payment. ${error.message || "Please try again."}`;
  }
});

document.getElementById("printTicketBtn").addEventListener("click", function () {
  window.location.href = "/api/v1/auth/confirmation";
});

updateBookingDetails();
