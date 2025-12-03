import fs from "fs";

const seats = JSON.parse(fs.readFileSync("./src/assets/seats.json", "utf-8"));

seats.forEach((seat) => {
  if (seat.floor === 2) seat.id += 606;
});

fs.writeFileSync("./src/assets/seats.json", JSON.stringify(seats, null, 4));
