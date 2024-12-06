package jo.toybreeze.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Payment implements Serializable {
    private Date startDate;
    private Date endDate;
    private PaymentToy paymentToy;
    private String toyId;
    private Date createdAt;

    public Payment() {
    }

    public Payment(Date startDate, PaymentToy paymentToy, String toyId, Date endDate, Date createdAt) {
        this.startDate = startDate;
        this.paymentToy = paymentToy;
        this.toyId = toyId;
        this.endDate = endDate;
        this.createdAt = createdAt;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public PaymentToy getPaymentToy() {
        return paymentToy;
    }

    public String getToyId() {
        return toyId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
