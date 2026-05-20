const selectedFlight = JSON.parse(localStorage.getItem("selectedFlight") || "null");
const latestBooking = JSON.parse(localStorage.getItem("latestBooking") || "null");
const selectedSeatSet = new Set(JSON.parse(localStorage.getItem("selectedSeats") || "[]"));
const seatMap = document.getElementById("seatMap");
const seatSummary = document.getElementById("seatSummary");
const seatPrice = document.getElementById("seatPrice");
const ticketForm = document.getElementById("ticket-selection-form");

if (!selectedFlight || !latestBooking) {
  window.location.href = "/api/v1/auth/select";
}

if (selectedFlight && latestBooking) {
  document.getElementById("flightTitle").textContent = `${selectedFlight.airline} ${selectedFlight.flightNumber}`;
  document.getElementById("flightMeta").textContent = `${selectedFlight.start} to ${selectedFlight.end} | ${selectedFlight.departureTime} to ${selectedFlight.arrivalTime} | ${latestBooking.bnumofseat} traveller(s)`;
  document.getElementById("classBadge").textContent = `${latestBooking.classType} CLASS`;
  document.getElementById("cabinHint").textContent = classHint(latestBooking.classType);

  function isTakenSeat(label) {
    const takenIndex = (label.charCodeAt(0) + parseInt(label.slice(1), 10) + selectedFlight.airplaneId) % 7;
    return takenIndex === 0;
  }

  function renderSeats() {
    seatMap.innerHTML = "";
    const layout = classLayout(latestBooking.classType);

    layout.rows.forEach(row => {
      layout.seats.forEach(letter => {
        const label = `${letter}${row}`;
        const seat = document.createElement("button");
        seat.type = "button";
        seat.className = "seat";
        seat.textContent = label;

        if (isTakenSeat(label)) {
          seat.classList.add("seat-taken");
          seat.disabled = true;
        } else if (selectedSeatSet.has(label)) {
          seat.classList.add("seat-selected");
        } else {
          seat.classList.add("seat-free");
        }

        seat.addEventListener("click", () => toggleSeat(label));
        seatMap.appendChild(seat);
      });
    });

    seatSummary.textContent = `Selected seats: ${Array.from(selectedSeatSet).join(", ") || "none yet"} (${selectedSeatSet.size}/${latestBooking.bnumofseat})`;
    seatPrice.textContent = `Fare to pay: INR ${Number(selectedFlight.price).toLocaleString()}`;
  }

  function toggleSeat(label) {
    if (selectedSeatSet.has(label)) {
      selectedSeatSet.delete(label);
    } else {
      if (selectedSeatSet.size >= latestBooking.bnumofseat) {
        seatSummary.textContent = `You can select only ${latestBooking.bnumofseat} seat(s).`;
        return;
      }
      selectedSeatSet.add(label);
    }

    localStorage.setItem("selectedSeats", JSON.stringify(Array.from(selectedSeatSet)));
    renderSeats();
  }

  ticketForm.addEventListener("submit", function(event) {
    if (selectedSeatSet.size !== latestBooking.bnumofseat) {
      event.preventDefault();
      seatSummary.textContent = `Please choose exactly ${latestBooking.bnumofseat} seat(s) before continuing.`;
    }
  });

  renderSeats();
}

function classLayout(classType) {
  if (classType === "FIRST") {
    return { rows: [1, 2], seats: ["A", "C", "D", "F"] };
  }
  if (classType === "BUSINESS") {
    return { rows: [3, 4, 5], seats: ["A", "C", "D", "F"] };
  }
  return { rows: [6, 7, 8, 9, 10, 11, 12], seats: ["A", "B", "C", "D", "E", "F"] };
}

function classHint(classType) {
  if (classType === "FIRST") {
    return "Wide 1-2-1 suite-inspired seating for first class.";
  }
  if (classType === "BUSINESS") {
    return "Comfortable 2-2 recliner seating for business travellers.";
  }
  return "Classic 3-3 seating layout for economy class.";
}
