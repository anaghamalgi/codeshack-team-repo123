
const display = document.getElementById("display");
const message = document.getElementById("message");
const angleButton = document.getElementById("angle-mode");

let isRadians = false;
let startNewNumber = true;

function show(value) {
    display.value = String(value);
}

function showMessage(text = "") {
    message.textContent = text;
}

function formatNumber(value) {
    if (!Number.isFinite(value)) {
        throw new Error("Result is outside the valid range.");
    }

    if (Object.is(value, -0)) value = 0;
    return Number(value.toPrecision(10)).toString();
}

// Arithmetic parser: supports brackets, unary signs, powers,
// multiplication, division, modulus, addition and subtraction.
function calculate(expression) {
    const input = expression.replace(/\s/g, "");
    const tokens = input.match(/\d+(?:\.\d*)?|\.\d+|[()+\-*/%^]/g);

    if (!tokens || tokens.join("") !== input) {
        throw new Error("Check your expression.");
    }

    let position = 0;

    function primary() {
        const token = tokens[position++];

        if (token === "(") {
            const value = addSubtract();

            if (tokens[position++] !== ")") {
                throw new Error("Check your brackets.");
            }

            return value;
        }

        if (!token || !/^(?:\d+(?:\.\d*)?|\.\d+)$/.test(token)) {
            throw new Error("Enter a valid number.");
        }

        return Number(token);
    }

    function power() {
        const left = primary();

        if (tokens[position] === "^") {
            position++;
            return Math.pow(left, unary());
        }

        return left;
    }

    function unary() {
        if (tokens[position] === "-") {
            position++;
            return -unary();
        }

        if (tokens[position] === "+") {
            position++;
            return unary();
        }

        return power();
    }

    function multiplyDivide() {
        let value = unary();

        while (["*", "/", "%"].includes(tokens[position])) {
            const operator = tokens[position++];
            const right = unary();

            if ((operator === "/" || operator === "%") && right === 0) {
                throw new Error("Cannot divide by zero.");
            }

            if (operator === "*") value *= right;
            if (operator === "/") value /= right;
            if (operator === "%") value %= right;
        }

        return value;
    }

    function addSubtract() {
        let value = multiplyDivide();

        while (tokens[position] === "+" || tokens[position] === "-") {
            const operator = tokens[position++];
            const right = multiplyDivide();
            value = operator === "+" ? value + right : value - right;
        }

        return value;
    }

    const result = addSubtract();

    if (position !== tokens.length) {
        throw new Error("Check your expression.");
    }

    return formatNumber(result);
}

function currentValue() {
    return Number(calculate(
        display.value
            .replace(/×/g, "*")
            .replace(/÷/g, "/")
            .replace(/−/g, "-")
            .replace(/mod/g, "%")
    ));
}

function angleToRadians(value) {
    return isRadians ? value : value * Math.PI / 180;
}

function angleFromRadians(value) {
    return isRadians ? value : value * 180 / Math.PI;
}

function scientificFunction(key) {
    const value = currentValue();
    let result;

    switch (key) {
        case "sin":
            result = Math.sin(angleToRadians(value));
            break;

        case "cos":
            result = Math.cos(angleToRadians(value));
            break;

        case "tan": {
            const angle = angleToRadians(value);

            if (Math.abs(Math.cos(angle)) < 1e-12) {
                throw new Error("Tangent is undefined at this angle.");
            }

            result = Math.tan(angle);
            break;
        }

        case "sin⁻¹":
            if (value < -1 || value > 1) {
                throw new Error("sin⁻¹ input must be between -1 and 1.");
            }
            result = angleFromRadians(Math.asin(value));
            break;

        case "cos⁻¹":
            if (value < -1 || value > 1) {
                throw new Error("cos⁻¹ input must be between -1 and 1.");
            }
            result = angleFromRadians(Math.acos(value));
            break;

        case "tan⁻¹":
            result = angleFromRadians(Math.atan(value));
            break;

        case "√":
            if (value < 0) {
                throw new Error("Square root needs a non-negative number.");
            }
            result = Math.sqrt(value);
            break;

        case "x²":
            result = value ** 2;
            break;

        case "log":
            if (value <= 0) {
                throw new Error("log requires a positive number.");
            }
            result = Math.log10(value);
            break;

        case "ln":
            if (value <= 0) {
                throw new Error("ln requires a positive number.");
            }
            result = Math.log(value);
            break;

        default:
            return;
    }

    show(formatNumber(result));
    startNewNumber = true;
}

// DEG/RAD toggle
angleButton.addEventListener("click", () => {
    isRadians = !isRadians;
    angleButton.textContent = isRadians ? "RAD" : "DEG";
    showMessage(`Angle mode: ${isRadians ? "Radians" : "Degrees"}.`);
});

// Calculator keypad
document.querySelectorAll("[data-key]").forEach(button => {
    button.addEventListener("click", () => {
        const key = button.dataset.key;
        showMessage("");

        try {
            if (key === "AC") {
                show("0");
                startNewNumber = true;
                return;
            }

            if (key === "DEL") {
                if (startNewNumber) {
                    show("0");
                } else {
                    show(display.value.slice(0, -1) || "0");
                }
                startNewNumber = false;
                return;
            }

            if (key === "=") {
                const result = calculate(
                    display.value
                        .replace(/×/g, "*")
                        .replace(/÷/g, "/")
                        .replace(/−/g, "-")
                        .replace(/mod/g, "%")
                );

                show(result);
                startNewNumber = true;
                return;
            }

            if ([
                "sin", "cos", "tan", "sin⁻¹", "cos⁻¹",
                "tan⁻¹", "√", "x²", "log", "ln"
            ].includes(key)) {
                scientificFunction(key);
                return;
            }

            if (/^\d$/.test(key)) {
                if (startNewNumber || display.value === "0") {
                    show(key);
                } else {
                    show(display.value + key);
                }

                startNewNumber = false;
                return;
            }

            if (key === ".") {
                if (startNewNumber) {
                    show("0.");
                    startNewNumber = false;
                } else {
                    const lastNumber = display.value.split(/[+\-*/%^()]/).pop();

                    if (!lastNumber.includes(".")) {
                        show(display.value + ".");
                    }
                }
                return;
            }

            const symbols = {
                "×": "*",
                "÷": "/",
                "−": "-",
                "mod": "%"
            };

            const operator = symbols[key] || key;

            if (["+", "-", "*", "/", "%", "^", "(", ")"].includes(operator)) {
                if (startNewNumber && operator === "-") {
                    show(display.value + "-");
                } else {
                    show(display.value + operator);
                }

                startNewNumber = false;
            }

            if (key === "xʸ") {
                show(display.value + "^");
                startNewNumber = false;
            }
        } catch (error) {
            showMessage(error.message || "Invalid input.");
        }
    });
});

// Allow expressions to be typed in the display too.
display.addEventListener("keydown", event => {
    if (event.key === "Enter" || event.key === "=") {
        event.preventDefault();

        try {
            show(calculate(
                display.value
                    .replace(/×/g, "*")
                    .replace(/÷/g, "/")
                    .replace(/−/g, "-")
                    .replace(/mod/g, "%")
            ));
            showMessage("");
            startNewNumber = true;
        } catch (error) {
            showMessage(error.message);
        }
    }
});

// Arbitrary-base logarithm: log(number) / log(base)
document.getElementById("log-calculate").addEventListener("click", () => {
    try {
        const number = Number(document.getElementById("log-number").value);
        const base = Number(document.getElementById("log-base").value);

        if (number <= 0 || base <= 0 || base === 1 ||
            !Number.isFinite(number) || !Number.isFinite(base)) {
            throw new Error("Number must be positive; base must be positive and not 1.");
        }

        showMessage(`Result: ${formatNumber(Math.log(number) / Math.log(base))}`);
    } catch (error) {
        showMessage(error.message);
    }
});

// Quadratic roots: ax² + bx + c = 0
document.getElementById("quad-calculate").addEventListener("click", () => {
    const a = Number(document.getElementById("quad-a").value);
    const b = Number(document.getElementById("quad-b").value);
    const c = Number(document.getElementById("quad-c").value);
    const output = document.getElementById("quad-result");

    try {
        if (![a, b, c].every(Number.isFinite) || a === 0) {
            throw new Error("a must be a non-zero number.");
        }

        const discriminant = b * b - 4 * a * c;

        if (discriminant >= 0) {
            const root1 = (-b + Math.sqrt(discriminant)) / (2 * a);
            const root2 = (-b - Math.sqrt(discriminant)) / (2 * a);

            output.textContent =
                `Roots: ${formatNumber(root1)}, ${formatNumber(root2)}`;
        } else {
            const real = -b / (2 * a);
            const imaginary = Math.sqrt(-discriminant) / Math.abs(2 * a);

            output.textContent =
                `Roots: ${formatNumber(real)} ± ${formatNumber(imaginary)}i`;
        }
    } catch (error) {
        output.textContent = error.message;
    }
});

// Unit conversion
const units = {
    length: {
        m: 1,
        km: 1000,
        cm: 0.01,
        mm: 0.001,
        mi: 1609.344,
        ft: 0.3048,
        in: 0.0254
    },
    mass: {
        kg: 1,
        g: 0.001,
        mg: 0.000001,
        lb: 0.45359237,
        oz: 0.028349523125
    },
    temperature: {
        C: "C",
        F: "F",
        K: "K"
    }
};

const categorySelect = document.getElementById("unit-category");
const fromSelect = document.getElementById("unit-from");
const toSelect = document.getElementById("unit-to");

function populateUnits() {
    const category = categorySelect.value;
    const options = Object.keys(units[category]);

    fromSelect.replaceChildren();
    toSelect.replaceChildren();

    options.forEach(unit => {
        fromSelect.add(new Option(unit, unit));
        toSelect.add(new Option(unit, unit));
    });

    if (category === "length") {
        fromSelect.value = "m";
        toSelect.value = "km";
    } else if (category === "mass") {
        fromSelect.value = "kg";
        toSelect.value = "g";
    } else {
        fromSelect.value = "C";
        toSelect.value = "F";
    }
}

categorySelect.addEventListener("change", populateUnits);
populateUnits();

function convertTemperature(value, from, to) {
    let celsius;

    if (from === "C") celsius = value;
    if (from === "F") celsius = (value - 32) * 5 / 9;
    if (from === "K") celsius = value - 273.15;

    if (to === "C") return celsius;
    if (to === "F") return celsius * 9 / 5 + 32;
    return celsius + 273.15;
}

document.getElementById("unit-calculate").addEventListener("click", () => {
    const value = Number(document.getElementById("unit-value").value);
    const from = fromSelect.value;
    const to = toSelect.value;
    const category = categorySelect.value;
    const output = document.getElementById("unit-result");

    try {
        if (!Number.isFinite(value)) {
            throw new Error("Enter a valid value.");
        }

        let result;

        if (category === "temperature") {
            result = convertTemperature(value, from, to);

            if (to === "K" && result < 0) {
                throw new Error("Temperature cannot be below absolute zero.");
            }
        } else {
            result = value * units[category][from] / units[category][to];
        }

        output.textContent = `${value} ${from} = ${formatNumber(result)} ${to}`;
    } catch (error) {
        output.textContent = error.message;
    }
});
