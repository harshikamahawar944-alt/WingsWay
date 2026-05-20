const bookingData = JSON.parse(localStorage.getItem("latestBooking") || "null");
const cachedFlights = JSON.parse(localStorage.getItem("latestFlightResults") || "[]");
const flightResults = document.getElementById("flightResults");
const flightStatus = document.getElementById("flight-status");
const routeSummary = document.getElementById("routeSummary");

function classMultiplier(classType) {
  if (classType === "FIRST") return 2.1;
  if (classType === "BUSINESS") return 1.45;
  return 1;
}

function applyClassPrice(basePrice, classType) {
  return Math.round(Number(basePrice) * classMultiplier(classType));
}

function renderFlights(flights) {
  flightResults.innerHTML = "";
  if (!flights.length) {
    flightStatus.textContent = "No flights available for this route.";
    return;
  }

  flights.forEach((flight, index) => {
    const article = document.createElement("label");
    article.className = "flight-card";
    article.innerHTML = `
      <input type="radio" name="flight" value="${flight.optionId}" ${index === 0 ? "checked" : ""}>
      <div class="flight-card-top">
        <div>
          <p class="airline">${flight.airline}</p>
          <h3>${flight.flightNumber}</h3>
          <p class="aircraft">${flight.aircraftModel}</p>
        </div>
        <p class="price">INR ${Number(applyClassPrice(flight.price, bookingData.classType)).toLocaleString()}</p>
      </div>
      <div class="flight-timing">
        <div><strong>${flight.departureTime}</strong><span>${flight.start}</span></div>
        <div class="duration">${flight.duration}</div>
        <div><strong>${flight.arrivalTime}</strong><span>${flight.end}</span></div>
      </div>
      <div class="flight-meta">
        <span>${flight.seatsAvailable} seats left</span>
        <span>${bookingData.classType} class for ${bookingData.bnumofseat} traveller(s)</span>
      </div>
    `;
    flightResults.appendChild(article);
  });

  flightStatus.textContent = "Choose an airline to continue to seat selection.";
}

async function ensureFlights() {
  if (!bookingData) {
    window.location.href = "/api/v1/auth/index";
    return [];
  }

  routeSummary.textContent = `${bookingData.bstart} to ${bookingData.bend} for ${bookingData.bnumofseat} traveller(s) in ${bookingData.classType} class`;

  if (cachedFlights.length) {
    return cachedFlights;
  }

  const response = await fetch("/api/v1/auth/flights/search", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(bookingData),
  });
  const flights = await response.json();
  localStorage.setItem("latestFlightResults", JSON.stringify(flights));
  return flights;
}

document.getElementById("flight-chooser-form").addEventListener("submit", function(event) {
  event.preventDefault();
  const selectedFlight = document.querySelector('input[name="flight"]:checked');
  if (!selectedFlight) {
    flightStatus.textContent = "Please select a flight.";
    return;
  }

  const flights = JSON.parse(localStorage.getItem("latestFlightResults") || "[]");
  const flight = flights.find(item => item.optionId === selectedFlight.value);
  flight.price = applyClassPrice(flight.price, bookingData.classType);
  localStorage.setItem("selectedFlight", JSON.stringify(flight));
  localStorage.removeItem("selectedSeats");
  window.location.href = "/api/v1/auth/ticket";
});

ensureFlights()
  .then(flights => {
    localStorage.setItem("latestFlightResults", JSON.stringify(flights));
    renderFlights(flights);
  })
  .catch(() => {
    flightStatus.textContent = "Unable to load flights right now.";
  });
