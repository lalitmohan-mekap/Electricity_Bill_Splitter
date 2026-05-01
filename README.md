# ⚡ BillSplit — Divided Slab Method

**The fair way to split electricity bills across multiple users sharing a single meter.**


[🚀 **Live Demo**](https://lalitmohan-mekap.github.io/Electricity_Bill_Splitter)
[![BillSplit Preview](preview.png)](https://lalitmohan-mekap.github.io/Electricity_Bill_Splitter)

## 📖 Introduction

In many shared housing situations, multiple tenants share a single electricity meter. A common (but unfair) practice is simply dividing the total bill by the number of residents. This is problematic because electricity is priced progressively—the more you use, the higher the rate.

**BillSplit** solves this using the **Divided Slab Method**. Every user gets an equal share of the cheaper electricity "slabs" first, ensuring that one high-consumption user doesn't push everyone's base rate into a more expensive bracket.

## ✨ Features

- **Divided Slab Logic**: Proportional allocation of energy tiers across all users.
- **Fixed Charge Distribution**: Equally splits meter rent, customer charges, and taxes.
- **Premium UI/UX**: Ultra-modern dark mode with glassmorphism, grid animations, and fluid transitions.
- **Fully Responsive**: Optimized for desktop, tablets, and smartphones.
- **Detailed Breakdowns**: Transparent, line-by-line calculations for every user.

## 🛠️ Tech Stack

- **Frontend**: Vanilla HTML5, CSS3 (Modern Grid & Flexbox), and ES6+ JavaScript.
- **Design**: Premium Glassmorphism, Google Fonts (`Space Mono`, `DM Sans`).
- **Logic**: Implemented in both JavaScript (for the web app) and Java (for backend/CLI processing).

## 🚀 Getting Started

### Web Application

Simply open `index.html` in any modern web browser. No installation or server required.

### Java Logic (CLI)

To run the Java version of the splitter:

1. Ensure you have JDK 11+ installed.
2. Compile the file:
   ```bash
   javac ElectricBillSplitter.java
   ```
3. Run the application:
   ```bash
   java ElectricBillSplitter
   ```

## ⚖️ How the "Divided Slab Method" Works

1. **Slab Allocation**: If a master bill has a 200-unit slab at ₹5/unit and there are 4 users, BillSplit allocates **50 units** at that cheap rate to *each* user.
2. **Individual Progress**: Users only move to the next (more expensive) slab once they have personally consumed their own allocation of the cheaper one.
3. **Difference Reconciliation**: The app automatically detects any small rounding differences and ensures the combined total matches the utility company's master bill.

---

*Built with ❤️ for fair living.*
