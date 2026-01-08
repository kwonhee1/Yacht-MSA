package HooYah.Yacht.domain;

import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "calendar")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CalendarType type;

    @Column
    private Long partId; // can null

    @Column
    private OffsetDateTime startDate;

    @Column
    private OffsetDateTime endDate;

    @Column
    private Long yachtId;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(nullable = false)
    private boolean byUser = false; // 사용자가 직접 수정했는지 여부

    @Column
    private String content;

    @Column
    private String review;

    @OneToMany(mappedBy = "calendar", cascade = {CascadeType.REMOVE,  CascadeType.PERSIST}, orphanRemoval = true)
    private List<CalendarUser> calendarUsers;

    public void setCalendarUsers(List<CalendarUser> calendarUsers) {
        if (calendarUsers == null || calendarUsers.isEmpty())
            return;

        if (this.calendarUsers == null)
            this.calendarUsers = new ArrayList<>();

        if (!calendarUsers.isEmpty())
            this.calendarUsers.clear();

        this.calendarUsers.addAll(calendarUsers);
    }

    public List<Long> getCalenderUserIdList() {
        return calendarUsers.stream().map(CalendarUser::getUserId).toList();
    }

    public void setByUserTrue() {
        this.byUser = true;
    }

    public void update(
            Long partId,
            String content, String review
    ) {
        if(partId != null) {
            if(this.completed)
                throw new CustomException(ErrorCode.CONFLICT); // can not change part after complete!

            this.partId = partId;
        }

        if(content != null && !content.isEmpty())
            this.content = content;

        if(review != null && !review.isEmpty())
            this.review = review;
    }

    public void updateDate(OffsetDateTime startDate, OffsetDateTime endDate) {
        if(startDate != null && endDate != null) {
            validateDate(startDate, endDate);

            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    private static void validateDate(OffsetDateTime startDate, OffsetDateTime endDate) {
        if(!endDate.isAfter(startDate))
            throw new CustomException(ErrorCode.CONFLICT);
    }

    public void setCompletedTrue() {
        if(this.review == null)
            throw new CustomException(ErrorCode.CONFLICT);
        this.completed = true;
    }

    public static Builder builder() {
        return new Builder();
    }

    static public class Builder {
        private CalendarType type;
        private String content;
        private String review;

        private OffsetDateTime startDate;
        private OffsetDateTime endDate;

        private Long partId;
        private Long yachtId;

        public Calendar buildByUser() {
            return build(true);
        }

        public Calendar buildByAuto() {
            return build(false);
        }

        private Calendar build(boolean byUser) {
            validateDate(startDate, endDate);

            if(type.equals(CalendarType.PART) && partId == null)
                throw new CustomException(ErrorCode.CONFLICT);

            return new Calendar(
                    null, // id
                    type, partId, startDate, endDate, yachtId,
                    false, // completed
                    byUser, // byUser
                    content, review,
                    null // List<CalendarUser>
            );
        }

        // builder functions ...
        public Builder type(CalendarType type) {
            this.type = type; return this;
        }
        public Builder content(String content) {
            this.content = content; return this;
        }
        public Builder review(String review) {
            this.review = review; return this;
        }
        public Builder startDate(OffsetDateTime startDate) {
            this.startDate = startDate; return this;
        }
        public Builder endDate(OffsetDateTime endDate) {
            this.endDate = endDate; return this;
        }
        public Builder partId(Long partId) {
            this.partId = partId; return this;
        }
        public Builder yachtId(Long yachtId) {
            this.yachtId = yachtId; return this;
        }

    }

}
