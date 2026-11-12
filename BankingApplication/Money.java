import java.math.BigDecimal;

public final class Money {
    private final long amountInPaise;
    private final String currency;

    public Money(long amountInPaise, String currency) {
        if (currency == null || !currency.equals("INR")) {
            throw new IllegalArgumentException("Only INR is supported");
        }

        this.amountInPaise = amountInPaise;
        this.currency = currency;
    }

    public static Money ofRupees(long rupees) {
        return new Money(Math.multiplyExact(rupees, 100), "INR");
    }

    public Money add(Money other) {
        checkCurrency(other);
        return new Money(
            Math.addExact(amountInPaise, other.amountInPaise),
            currency
        );
    }

    public Money subtract(Money other) {
        checkCurrency(other);
        return new Money(
            Math.subtractExact(amountInPaise, other.amountInPaise),
            currency
        );
    }

    public Money negate() {
        return new Money(Math.negateExact(amountInPaise), currency);
    }

    public boolean isPositive() {
        return amountInPaise > 0;
    }

    public boolean isNegative() {
        return amountInPaise < 0;
    }

    public boolean isZero() {
        return amountInPaise == 0;
    }

    public int compareTo(Money other) {
        checkCurrency(other);
        return Long.compare(amountInPaise, other.amountInPaise);
    }

    public long getAmountInPaise() {
        return amountInPaise;
    }

    public String getCurrency() {
        return currency;
    }

    private void checkCurrency(Money other) {
        if (other == null || !currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
    }

    @Override
    public String toString() {
        return "INR" + BigDecimal.valueOf(amountInPaise, 2)
                .toPlainString();
    }
}