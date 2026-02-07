package HooYah.Yacht.domain;

import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import jakarta.persistence.Transient;
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

    @JsonIgnore
    @Transient
    private boolean completedNow = false;

    @Transient
    public boolean isCompletedNow() {
        return completedNow;
    }

    @Transient
    @Deprecated // used by ignore field when read data from db with jpa
    public void setCompletedNow(boolean completedNow) {
        this.completedNow = false;
    }

    public void setCalendarUsers(List<Long> userIdList) {
        List<CalendarUser> calendarUserList = userIdList
                .stream()
                .map(userId->CalendarUser.builder().calendar(this).userId(userId).build())
                .toList();

        if (calendarUserList == null || calendarUserList.isEmpty())
            return;

        if (this.calendarUsers == null)
            this.calendarUsers = new ArrayList<>();

        if (!this.calendarUsers.isEmpty())
            this.calendarUsers.clear();

        this.calendarUsers.addAll(calendarUserList);
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
        updatePartId(partId);

        if(content != null && !content.isEmpty())
            this.content = content;

        if(review != null && !review.isEmpty())
            this.review = review;
    }

    private void updatePartId(Long partId) {
        if(partId == null || partId == this.partId)
            return; // nothing to chage

        if(this.completed)
            throw new CustomException(ErrorCode.CONFLICT); // can not change part after complete!

        this.partId = partId;
    }

    public void updateDate(OffsetDateTime startDate, OffsetDateTime endDate) {
        if(startDate != null && endDate != null) {
            validateDate(startDate, endDate);

            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    private static void validateDate(OffsetDateTime startDate, OffsetDateTime endDate) {
        // if(!endDate.isAfter(startDate))
        if(startDate.isAfter(endDate))
            throw new CustomException(ErrorCode.CONFLICT);
    }

    public void updateComplete(boolean isCompleted) {
        if(!isCompleted)
            return;
        if(this.completed == isCompleted)
            return;

        if(this.review == null)
            throw new CustomException(ErrorCode.CONFLICT);

        this.completed = true;
        this.completedNow = true;
    }

    public static Builder builder() {
        return new Builder();
    }

    static public class Builder {
        private Long id;

        private CalendarType type;
        private String content;
        private String review;

        private OffsetDateTime startDate;
        private OffsetDateTime endDate;

        private Long partId;
        private Long yachtId;

        private boolean isCompleted;

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

            Calendar createdCalendar = new Calendar(
                    id,
                    type, partId, startDate, endDate, yachtId,
                    false, // completed
                    byUser, // byUser
                    content, review,
                    null, // List<CalendarUser>
                    false // isCompleted
            );

            createdCalendar.updateComplete(isCompleted);

            return createdCalendar;
        }

        // builder functions ...
        public Builder id(Long id) {
            this.id = id; return this;
        }
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
        public Builder isCompleted(boolean isCompleted) {
            this.isCompleted = isCompleted; return this;
        }

    }

}
