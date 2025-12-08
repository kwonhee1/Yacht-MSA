package HooYah.Yacht.delete;

import HooYah.Yacht.calendar.repository.CalendarRepository;
import HooYah.Yacht.calendar.service.CalendarService;
import HooYah.Yacht.part.repository.PartRepository;
import HooYah.Yacht.part.service.PartService;
import HooYah.Yacht.repair.repository.RepairRepository;
import HooYah.Yacht.repair.service.RepairService;
import HooYah.Yacht.user.domain.User;
import HooYah.Yacht.user.repository.UserRepository;
import HooYah.Yacht.user.service.UserService;
import HooYah.Yacht.yacht.repository.YachtRepository;
import HooYah.Yacht.yacht.service.YachtService;
import HooYah.Yacht.chat.repository.ChatConversationRepository;
import HooYah.Yacht.chat.repository.ChatMessageRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Disabled
@Transactional
public class DeleteTest {

    @Autowired
    private CalendarService calendarService;
    
    @Autowired
    private CalendarRepository calendarRepository;
    
    @Autowired
    private PartService partService;
    
    @Autowired
    private PartRepository partRepository;
    
    @Autowired
    private RepairService repairService;
    
    @Autowired
    private RepairRepository repairRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private YachtService yachtService;
    
    @Autowired
    private YachtRepository yachtRepository;
    
    @Autowired
    private ChatConversationRepository chatConversationRepository;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @PersistenceContext
    private EntityManager em;

    @Test
    public void deleteNotPartTypeCalendar() {
        // DB에서 확인한 Calendar ID와 User ID를 입력하세요 (PART 타입이 아닌 Calendar)
        Long calendarId = 86L;
        Long userId = 1L;
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Calendar 삭제 (service 메서드 사용)
        calendarService.deleteCalendar(calendarId, user);
        
        em.flush();
        em.clear();
        
        // 삭제 확인
        Optional<?> deletedCalendar = calendarRepository.findById(calendarId);
        Assertions.assertThat(deletedCalendar).isEmpty();
    }

    @Test
    public void deletePartTypeCalendar() {
        // DB에서 확인한 Calendar ID와 User ID를 입력하세요
        Long calendarId = 83L;
        Long userId = 1L;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Calendar 삭제 (service 메서드 사용)
        calendarService.deleteCalendar(calendarId, user);

        em.flush();
        em.clear();

        // 삭제 확인
        Optional<?> deletedCalendar = calendarRepository.findById(calendarId);
        Assertions.assertThat(deletedCalendar).isEmpty();
    }

    @Test
    public void deleteRepair() {
        // DB에서 확인한 Repair ID와 User ID를 입력하세요
        Long repairId = 48L; // 48 49
        Long userId = 1L;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Repair 삭제 (service 메서드 사용)
        repairService.deleteRepair(repairId, user);

        em.flush();
        em.clear();

        // 삭제 확인
        Optional<?> deletedRepair = repairRepository.findById(repairId);
        Assertions.assertThat(deletedRepair).isEmpty();
    }

    @Test
    public void deletePart() {
        // DB에서 확인한 Part ID와 User ID를 입력하세요
        Long partId = 34L; // 34
        Long userId = 1L;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Part 삭제 (service 메서드 사용)
        partService.deletePart(partId, user);

        em.flush();
        em.clear();

        // 삭제 확인
        Optional<?> deletedPart = partRepository.findById(partId);
        Assertions.assertThat(deletedPart).isEmpty();
    }

    @Test
    public void deleteYacht() {
        // DB에서 확인한 Yacht ID와 User ID를 입력하세요
        Long yachtId = 6L; // 6 7
        Long userId = 1L;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Yacht 삭제 (service 메서드 사용)
        yachtService.deleteYacht(user, yachtId);

        em.flush();
        em.clear();

        // 삭제 확인
        Optional<?> deletedYacht = yachtRepository.findById(yachtId);
        Assertions.assertThat(deletedYacht).isEmpty();
    }

    @Test
    public void deleteUser() {
        // DB에서 확인한 User ID를 입력하세요
        Long userId = 1L;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // User 삭제 (service 메서드 사용)
        userService.deleteUser(user);

        em.flush();
        em.clear();

        // 삭제 확인
        Optional<?> deletedUser = userRepository.findById(userId);
        Assertions.assertThat(deletedUser).isEmpty();
    }

    @Test
    public void deleteChatConversation() {
        // DB에서 확인한 ChatConversation ID를 입력하세요
        Long conversationId = 1L;

        // ChatConversation 삭제 (repository 직접 사용)
        chatConversationRepository.findById(conversationId)
                .ifPresent(chatConversationRepository::delete);

        em.flush();
        em.clear();

        // 삭제 확인
        Optional<?> deletedConversation = chatConversationRepository.findById(conversationId);
        Assertions.assertThat(deletedConversation).isEmpty();
    }

}
