import java.util.*;

/**
 * Electric Bill Splitter — Divided Slab Method
 *
 * CONCEPT:
 * Each master slab's units are divided equally by the number of users.
 * Every user climbs their own slab ladder from the cheapest rate upward
 * until their consumed units are exhausted. Users who consume more than
 * the total per-user allocation are charged at the highest slab rate
 * for the excess. Fixed charges are split equally.
 *
 * This is fairer than flat proportional splitting because every user
 * benefits from the cheap lower slabs before hitting higher rates.
 */
public class ElectricBillSplitter {

    // ─── Data Models ────────────────────────────────────────────────────────

    static class Slab {
        int number;
        double totalUnits;
        double rate;
        double perUserUnits;

        Slab(int number, double totalUnits, double rate, int numUsers) {
            this.number = number;
            this.totalUnits = totalUnits;
            this.rate = rate;
            this.perUserUnits = totalUnits / numUsers;
        }
    }

    static class FixedCharge {
        String name;
        double amount;

        FixedCharge(String name, double amount) {
            this.name = name;
            this.amount = amount;
        }
    }

    static class User {
        String name;
        double units;

        User(String name, double units) {
            this.name = name;
            this.units = units;
        }
    }

    static class SlabRow {
        String label;
        double allocated; // per-user slab allocation (master / numUsers)
        double used; // units actually consumed in this slab
        double rate;
        double amount;
        String status; // "full" | "partial" | "unused" | "extra"
    }

    static class UserBill {
        User user;
        List<SlabRow> rows;
        double ecTotal;
        double fixedShare;
        double grandTotal;
    }

    // ─── Entry Point ────────────────────────────────────────────────────────

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        printBanner();

        // ── Number of users ──────────────────────────────────────────────
        int numUsers = promptInt(sc, "Number of users sharing this meter");

        // ── Energy slabs ─────────────────────────────────────────────────
        int numSlabs = promptInt(sc, "Number of energy-charge slabs in the master bill");
        List<Slab> slabs = new ArrayList<>();

        System.out.println();
        printSectionHeader("ENERGY CHARGE SLABS");
        for (int i = 1; i <= numSlabs; i++) {
            System.out.printf("  Slab %-2d  units : ", i);
            double units = sc.nextDouble();
            System.out.printf("  Slab %-2d  rate  : ₹", i);
            double rate = sc.nextDouble();
            sc.nextLine();
            slabs.add(new Slab(i, units, rate, numUsers));
            System.out.printf("           → per-user allocation = %.2f units%n%n",
                    units / numUsers);
        }

        // ── Fixed charges ─────────────────────────────────────────────────
        int numFixed = promptInt(sc, "Number of fixed charge items (e.g. MFC, ED, meter rent)");
        List<FixedCharge> fixed = new ArrayList<>();

        System.out.println();
        printSectionHeader("FIXED CHARGES");
        for (int i = 1; i <= numFixed; i++) {
            System.out.printf("  Item %-2d  name   : ", i);
            String name = sc.nextLine().trim();
            System.out.printf("  Item %-2d  amount : ₹", i);
            double amount = sc.nextDouble();
            sc.nextLine();
            fixed.add(new FixedCharge(name, amount));
            System.out.println();
        }

        double totalFixed = fixed.stream().mapToDouble(f -> f.amount).sum();
        double fixedEach = totalFixed / numUsers;

        // ── User details ──────────────────────────────────────────────────
        System.out.println();
        printSectionHeader("USER DETAILS");
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= numUsers; i++) {
            System.out.printf("  User %-2d  name  : ", i);
            String name = sc.nextLine().trim();
            System.out.printf("  User %-2d  units : ", i);
            double units = sc.nextDouble();
            sc.nextLine();
            users.add(new User(name, units));
            System.out.println();
        }

        // ── Compute bills ────────────────────────────────────────────────
        List<UserBill> bills = new ArrayList<>();
        for (User u : users) {
            bills.add(computeBill(u, slabs, fixedEach));
        }

        // ── Print results ─────────────────────────────────────────────────
        System.out.println("\n\n");
        printMasterSummary(slabs, fixed, totalFixed, numUsers);

        for (UserBill bill : bills) {
            printUserBill(bill, numUsers);
        }

        printFinalSummary(bills);

        sc.close();
    }

    // ─── Core Calculation ───────────────────────────────────────────────────

    static UserBill computeBill(User user, List<Slab> slabs, double fixedEach) {
        UserBill bill = new UserBill();
        bill.user = user;
        bill.rows = new ArrayList<>();
        bill.fixedShare = fixedEach;

        double remaining = user.units;
        double highestRate = slabs.get(slabs.size() - 1).rate;

        for (Slab s : slabs) {
            SlabRow row = new SlabRow();
            row.label = "Slab " + s.number;
            row.allocated = s.perUserUnits;
            row.rate = s.rate;

            if (remaining <= 0) {
                row.used = 0;
                row.amount = 0;
                row.status = "unused";
            } else if (remaining >= s.perUserUnits) {
                row.used = s.perUserUnits;
                row.amount = s.perUserUnits * s.rate;
                row.status = "full";
                remaining -= s.perUserUnits;
            } else {
                row.used = remaining;
                row.amount = remaining * s.rate;
                row.status = "partial";
                remaining = 0;
            }
            bill.rows.add(row);
        }

        // Units above total per-user allocation → charged at highest rate
        if (remaining > 0) {
            SlabRow extra = new SlabRow();
            extra.label = "Extra (above allocation)";
            extra.allocated = 0;
            extra.used = remaining;
            extra.rate = highestRate;
            extra.amount = remaining * highestRate;
            extra.status = "extra";
            bill.rows.add(extra);
        }

        bill.ecTotal = bill.rows.stream().mapToDouble(r -> r.amount).sum();
        bill.grandTotal = bill.ecTotal + fixedEach;
        return bill;
    }

    // ─── Print Helpers ───────────────────────────────────────────────────────

    static void printBanner() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          ⚡  ELECTRIC BILL SPLITTER — SLAB METHOD  ⚡       ║");
        System.out.println("║                                                              ║");
        System.out.println("║  Each master slab is divided equally among all users.        ║");
        System.out.println("║  Every user climbs their own rate ladder — cheap first.      ║");
        System.out.println("║  Fixed charges are split equally. Fair for all.              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    static void printSectionHeader(String title) {
        System.out.println("┌─ " + title + " " + "─".repeat(Math.max(0, 58 - title.length())) + "┐");
    }

    static int promptInt(Scanner sc, String prompt) {
        System.out.println();
        System.out.print("  ▶  " + prompt + ": ");
        int val = sc.nextInt();
        sc.nextLine();
        return val;
    }

    static void printMasterSummary(List<Slab> slabs, List<FixedCharge> fixed,
            double totalFixed, int numUsers) {
        double totalEC = slabs.stream().mapToDouble(s -> s.totalUnits * s.rate).sum();
        String line = "═".repeat(72);

        System.out.println(line);
        System.out.println("  MASTER BILL SUMMARY");
        System.out.println(line);
        System.out.printf("  %-32s %8s  %7s  %12s%n",
                "Charge Head", "Units", "Rate ₹", "Amount ₹");
        System.out.println("  " + "─".repeat(68));

        for (Slab s : slabs) {
            System.out.printf("  EC Slab %-2d (÷%d = %-6.2f / user)  %8.2f  %7.2f  %12.2f%n",
                    s.number, numUsers, s.perUserUnits,
                    s.totalUnits, s.rate, s.totalUnits * s.rate);
        }
        System.out.println("  " + "─".repeat(68));
        System.out.printf("  %-32s %8s  %7s  %12.2f%n",
                "Energy Charge Subtotal", "", "", totalEC);
        for (FixedCharge f : fixed) {
            System.out.printf("  %-32s %8s  %7s  %12.2f%n", f.name, "—", "—", f.amount);
        }
        System.out.println("  " + line.substring(2));
        System.out.printf("  %-32s %8s  %7s  %12.2f%n",
                "MASTER BILL TOTAL", "", "", totalEC + totalFixed);
        System.out.println();
    }

    static void printUserBill(UserBill bill, int numUsers) {
        String line = "─".repeat(72);
        System.out.println("\n" + line);
        System.out.printf("  USER : %-20s  |  Units Consumed : %.2f%n",
                bill.user.name.toUpperCase(), bill.user.units);
        System.out.println(line);
        System.out.printf("  %-26s  %10s  %8s  %8s  %12s%n",
                "Slab", "Alloc (÷" + numUsers + ")", "Used", "Rate ₹", "Amount ₹");
        System.out.println("  " + "·".repeat(68));

        for (SlabRow row : bill.rows) {
            String allocStr = row.status.equals("extra") ? "    —" : String.format("%10.2f", row.allocated);
            String tag;
            switch (row.status) {
                case "partial":
                    tag = "  ◀ stops here";
                    break;
                case "extra":
                    tag = "  ◀ above allocation";
                    break;
                case "unused":
                    tag = "  (not reached)";
                    break;
                default:
                    tag = "";
            }
            System.out.printf("  %-26s  %10s  %8.2f  %8.2f  %12.2f%s%n",
                    row.label, allocStr, row.used, row.rate, row.amount, tag);
        }

        System.out.println("  " + "─".repeat(68));
        System.out.printf("  %-26s  %10s  %8s  %8s  %12.2f%n",
                "Energy Charge Total", "", "", "", bill.ecTotal);
        System.out.printf("  %-26s  %10s  %8s  %8s  %12.2f%n",
                "Fixed Charges (1/" + numUsers + ")", "", "", "", bill.fixedShare);
        System.out.println("  " + "═".repeat(68));
        System.out.printf("  %-26s  %10s  %8s  %8s  %12.2f%n",
                "TOTAL BILL — " + bill.user.name.toUpperCase(),
                "", "", "", bill.grandTotal);
    }

    static void printFinalSummary(List<UserBill> bills) {
        String line = "═".repeat(72);
        System.out.println("\n\n" + line);
        System.out.println("  FINAL SUMMARY — AMOUNT EACH USER MUST PAY");
        System.out.println(line);
        System.out.printf("  %-18s  %10s  %12s  %12s  %12s%n",
                "User", "Units", "EC (₹)", "Fixed (₹)", "TOTAL (₹)");
        System.out.println("  " + "─".repeat(68));

        double grandSum = 0;
        for (UserBill bill : bills) {
            System.out.printf("  %-18s  %10.1f  %12.2f  %12.2f  %12.2f%n",
                    bill.user.name, bill.user.units,
                    bill.ecTotal, bill.fixedShare, bill.grandTotal);
            grandSum += bill.grandTotal;
        }

        System.out.println("  " + "═".repeat(68));
        System.out.printf("  %-18s  %10s  %12s  %12s  %12.2f%n",
                "COMBINED TOTAL", "", "", "", grandSum);
        System.out.println(line);
        System.out.println();
    }
}
