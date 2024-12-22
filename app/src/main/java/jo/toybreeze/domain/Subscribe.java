package jo.toybreeze.domain;

import java.util.Date;

public class Subscribe {
    private Date startDate;
    private Date endDate;
    private Date createdAt;

    public Subscribe() {
    }

    public Subscribe(Date startDate, Date endDate, Date createdAt) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
